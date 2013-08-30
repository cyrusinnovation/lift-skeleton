package com.cyrusinnovation.selenium

import org.openqa.selenium.chrome.{ChromeOptions, ChromeDriverService}
import org.openqa.selenium.remote.{BrowserType, DesiredCapabilities, RemoteWebDriver}
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.android.AndroidDriver
import org.openqa.selenium.ie.{InternetExplorerDriver, InternetExplorerDriverService}
import org.openqa.selenium.remote.service.DriverService
import org.openqa.selenium.{WebElement, By, WebDriver}
import java.io.File
import java.net.URL
import scala.collection.JavaConverters._
import org.openqa.selenium.WebDriver.{TargetLocator, Navigation, Options}
import java.util


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
object WebDriverFactory {

  def driver() = {
    val browserName = Option(System.getProperty("web.driver.type")).getOrElse(BrowserType.HTMLUNIT)
    val service = DriverServiceHolder.useServiceFor(browserName)
    val webDriver: WebDriver = DriverHolder.useDriverFor(browserName, service)
    webDriver
  }

  def deleteAllCookies() = {
    DriverHolder.currentDriver.manage.deleteAllCookies
  }

  def closeAllChildWindows() = {
    val webDriver: WebDriver = DriverHolder.currentDriver
    val mainWindowHandle: String = webDriver.getWindowHandle
    val allWindowHandles: util.Set[String] = webDriver.getWindowHandles
    allWindowHandles.remove(mainWindowHandle)
    while (allWindowHandles.iterator.hasNext) {
      val nextWindow: String = allWindowHandles.iterator.next
      webDriver.switchTo.window(nextWindow)
    }
    webDriver.switchTo.window(mainWindowHandle)
  }
}

private object DriverServiceHolder {
  private var currentBrowserName : String = BrowserType.HTMLUNIT
  private var currentService : DriverService = new NullDriverService()

  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run() {
      stopCurrentService()
    }
  })

  def service = currentService

  def useServiceFor(browserName: String) = {
    val driverServiceBuilder = serviceBuilderForBrowserNamed(browserName)

    if(currentBrowserName != browserName || ! currentService.isRunning){
      stopCurrentService()
      currentBrowserName = browserName
      currentService = driverServiceBuilder.build()
      currentService.start()
    }
    currentService
  }

  def stopCurrentService() = {
    DriverHolder.stopCurrentDriver()
    try {
      currentService.stop()
    } catch {
        case exc: Throwable => {
          System.err.println("Driver service already stopped.")
        }
    }
  }

  private def serviceBuilderForBrowserNamed(browserName: String) = {
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

private object DriverHolder {
  val chromeInstallPath = Option(System.getProperty("webdriver.chrome.bin"))
  var currentBrowserName : String = BrowserType.HTMLUNIT
  var currentDriver : WebDriver = NullDriver

  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run() {
      stopCurrentDriver()
    }
  })

  def useDriverFor(browserName: String, driverService: DriverService) = {
    if(currentBrowserName != browserName || currentDriver == NullDriver ) {
      stopCurrentDriver()
      currentBrowserName = browserName
      currentDriver = driverFor(browserName, driverService)
    }
    currentDriver
  }

  def stopCurrentDriver() = {
    try {
      currentDriver.quit()
    } catch {
        case exc: Throwable => {
            System.err.println("Driver already stopped.")
        }
    }
    currentDriver = NullDriver
  }

  private def driverFor(browserName: String, driverService: DriverService) = {
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
        new RemoteWebDriver(driverService.getUrl, desiredCapabilities)
      }
      case BrowserType.IEXPLORE   => new InternetExplorerDriver(driverService.asInstanceOf[InternetExplorerDriverService])
      case _                      => throw new UnsupportedOperationException(s"Browser $browserName not supported.")
    }
  }
}

private trait DriverServiceBuilder[DriverServiceType <: DriverService] {
  protected val theService : DriverServiceType
  def build() = { theService }
}

private object ChromeDriverServiceBuilder extends DriverServiceBuilder[ChromeDriverService] {
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

private object InternetExplorerDriverServiceBuilder extends DriverServiceBuilder[InternetExplorerDriverService] {
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

private object NullDriverServiceBuilder extends DriverServiceBuilder[NullDriverService] {
  protected val theService = new NullDriverService()
}

private class NullDriverService extends DriverService(new File("."), 0, null, null) {
  override def getUrl = new URL("file:///dev/null")
  override def isRunning = false
  override def start() = {}
  override def stop() = {}
}

private object NullDriver extends WebDriver {
  def get(url: String) {}
  def getCurrentUrl: String = ""
  def getTitle: String = ""

  def findElements(by: By): util.List[WebElement] = null
  def findElement(by: By): WebElement = null
  def getPageSource: String = ""

  def close() {}
  def quit() {}

  def getWindowHandles: util.Set[String] = null
  def getWindowHandle: String = ""

  def switchTo(): TargetLocator = null
  def navigate(): Navigation = null
  def manage(): Options = null
}
