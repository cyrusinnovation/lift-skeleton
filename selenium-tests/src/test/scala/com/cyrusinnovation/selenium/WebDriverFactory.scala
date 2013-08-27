package com.cyrusinnovation.selenium

import org.openqa.selenium.chrome.{ChromeOptions, ChromeDriverService}
import org.openqa.selenium.remote.{BrowserType, DesiredCapabilities, RemoteWebDriver}
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.android.AndroidDriver
import org.openqa.selenium.ie.{InternetExplorerDriver, InternetExplorerDriverService}
import org.openqa.selenium.remote.service.DriverService
import org.openqa.selenium.WebDriver
import java.io.File
import java.net.URL
import scala.collection.JavaConverters._


/**  A factory for creating web drivers. The default is the HtmlUnit driver.
  *  To use a different driver, set the system property:
  *
  *   web.driver.type
  *
  * The value should be one of the following values of org.openqa.selenium.remote.BrowserType:
  *
  *   "htmlunit"  (BrowserType.HTMLUNIT)
  *   "firefox"   (BrowserType.FIREFOX)
  *   "chrome"    (BrowserType.CHROME)
  *   "safari"    (BrowserType.SAFARI)
  *   "iexplore"  (BrowserType.IEXPLORE)
  *   "android"   (BrowserType.ANDROID)
  *
  * To use Chrome or Internet Explorer, there are several requirements.
  *
  * First, download and install the required "server" driver:
  *   For Chrome:  https://code.google.com/p/selenium/wiki/ChromeDriver
  *   For Internet Explorer: https://code.google.com/p/selenium/wiki/InternetExplorerDriver
  *
  * Second, the test should set one of the corresponding system properties
  * to point to the location of the driver:
  *
  *   chrome.driver.path
  *   ie.driver.path
  *
  * Finally, the Chrome server driver expects Chrome to be installed in a standard
  * location (as described in the ChromeDriver wiki page above). If you have it installed
  * somewhere else, set the following system property to point to Chrome's location:
  *
  *   webdriver.chrome.bin
  *
  * The Firefaox installation path and name of the profile to use can be set using the
  * following system properties, as documented in the FirefoxDriver class:
  *
  *   webdriver.firefox.bin
  *   webdriver.firefox.profile
  */
class WebDriverFactory {

  def driver() = {
    val browserName = Option(System.getProperty("web.driver.type")).getOrElse(BrowserType.HTMLUNIT)
    DriverServiceHolder.useServiceFor(browserName)
    DriverHolder.useDriverFor(browserName)
  }
}

object DriverServiceHolder {
  private var currentBrowserName : String = BrowserType.HTMLUNIT
  private var currentService : DriverService = new NullDriverService()

  def service = currentService

  def useServiceFor(browserName: String) {
    val driverServiceBuilder = serviceBuilderForBrowserNamed(browserName)

    if(currentBrowserName != browserName){
      stopCurrentService()
      currentBrowserName = browserName
      currentService = driverServiceBuilder.build()
    }
    if(! currentService.isRunning)
      currentService.start()
  }

  def stopCurrentService() {
    DriverHolder.stopCurrentDriver()
    currentService.stop()
  }

  override def finalize() {
    stopCurrentService()
  }

  def serviceBuilderForBrowserNamed(browserName: String) = {
    browserName match {
        case BrowserType.HTMLUNIT |
             BrowserType.FIREFOX |
             BrowserType.SAFARI |
             BrowserType.ANDROID      => NullDriverServiceBuilder
        case BrowserType.CHROME       => ChromeDriverServiceBuilder
        case BrowserType.IEXPLORE     => InternetExplorerDriverServiceBuilder
        case _                        => throw new UnsupportedOperationException(s"Browser $browserName not supported.")
    }
  }
}

object DriverHolder {
  val chromeInstallPath = Option(System.getProperty("webdriver.chrome.bin"))
  var currentBrowserName : String = BrowserType.HTMLUNIT
  var currentDriver : WebDriver = new HtmlUnitDriver()

  def useDriverFor(browserName: String) = {
    if(currentBrowserName != browserName) {
      stopCurrentDriver()
      currentBrowserName = browserName
      currentDriver = driverFor(browserName)
    }
    currentDriver
  }

  def stopCurrentDriver() = { currentDriver.quit() }

  def driverFor(browserName: String) = {
    browserName match {
      case BrowserType.HTMLUNIT   => new HtmlUnitDriver()
      case BrowserType.FIREFOX    => new FirefoxDriver()
      case BrowserType.SAFARI     => new SafariDriver()
      case BrowserType.ANDROID    => new AndroidDriver()
      case BrowserType.CHROME     => {
        val desiredCapabilities = DesiredCapabilities.chrome()
        chromeInstallPath.foreach(path => {
          val chromeOptions = Map("binary" -> path).asJava
          desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions)
        })
        new RemoteWebDriver(DriverServiceHolder.service.getUrl, desiredCapabilities)
      }
      case BrowserType.IEXPLORE   => new InternetExplorerDriver(DriverServiceHolder.service.asInstanceOf[InternetExplorerDriverService])
      case _                      => throw new UnsupportedOperationException(s"Browser $browserName not supported.")
    }
  }
}

trait DriverServiceBuilder[DriverServiceType <: DriverService] {
  protected val theService : DriverServiceType
  def build() = { theService }
}

object ChromeDriverServiceBuilder extends DriverServiceBuilder[ChromeDriverService] {
  val chromeDriverPath = Option(System.getProperty("chrome.driver.path"))

  private val driverFile = chromeDriverPath match {
    case Some(path) => new File(path)
    case None => throw new UnsupportedOperationException("Cannot instantiate Chrome driver " +
      "without System property chrome.driver.path pointing to the installation location of the chromedriver executable.")
  }

  protected val theService = new ChromeDriverService.Builder()
    .usingDriverExecutable(driverFile)
    .usingAnyFreePort()
    .build()
}

object InternetExplorerDriverServiceBuilder extends DriverServiceBuilder[InternetExplorerDriverService] {
  val internetExplorerDriverPath = Option(System.getProperty("ie.driver.path"))

  private val driverFile = internetExplorerDriverPath match {
    case Some(path) => new File(path)
    case None => throw new UnsupportedOperationException("Cannot instantiate Chrome driver " +
      "without System property ie.driver.path pointing to the installation location of the IE driver executable.")
  }
  protected val theService = new InternetExplorerDriverService.Builder()
    .usingDriverExecutable(driverFile)
    .usingAnyFreePort()
    .build()
}

object NullDriverServiceBuilder extends DriverServiceBuilder[NullDriverService] {
  protected val theService = new NullDriverService()
}

class NullDriverService extends DriverService(new File("."), 0, null, null) {
  override def getUrl = new URL("file:///dev/null")
  override def isRunning = false
  override def start() {}
  override def stop() { }
}
