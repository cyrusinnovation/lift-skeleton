package code.test.selenium

import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.remote.{BrowserType, DesiredCapabilities, RemoteWebDriver}
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.android.AndroidDriver
import java.io.File
import org.openqa.selenium.ie.{InternetExplorerDriver, InternetExplorerDriverService}
import org.openqa.selenium.remote.service.DriverService

class WebDriverFactory {
  //TODO These should not be hard-coded
  val chromeDriverPath: String = "/Users/cyrusinnovation/bin/chromedriver"
  val internetExplorerDriverPath: String = "path/to/my/chromedriver"

  object BrowserKind extends Enumeration {
    type BrowserKind = Value
    val HtmlUnit, Firefox, Chrome, Safari, IE, IPhone, IPad, Android = Value
  }

  import BrowserKind._

  def createDriver(capability:DesiredCapabilities) = {
    val browser = browserKind(capability.getBrowserName)
    browser match {
      case HtmlUnit => new HtmlUnitDriver()
      case Firefox => new FirefoxDriver()
      case Safari => new SafariDriver()
      case Android  => new AndroidDriver()
      // case IPhone  => new IPhoneDriver()     //Deprecated. please use ios-driver or appium instead

      case Chrome  => {
        val service = ChromeDriverServiceHolder.service()
        new RemoteWebDriver(service.getUrl, DesiredCapabilities.chrome())
      }

      case IE => {
        val service = InternetExplorerDriverServiceHolder.service()
        new InternetExplorerDriver(service)
      }
    }
  }

  def browserKind(browserName:String) = browserName match {
    case BrowserType.FIREFOX    => Firefox
    case BrowserType.CHROME     => Chrome
    case BrowserType.SAFARI     => Safari
    case BrowserType.IE         => IE
    case BrowserType.HTMLUNIT   => HtmlUnit
    case BrowserType.IPHONE     => IPhone
    case BrowserType.ANDROID    => Android
    case _                      => throw new UnsupportedOperationException(s"Browser $browserName not supported.")
  }

  trait DriverServiceHolder[DriverServiceType <: DriverService] {
    protected val theService : DriverServiceType

    def service() : DriverServiceType = {
      if(! theService.isRunning)
      theService.start()
      theService
    }
  }


  object ChromeDriverServiceHolder extends DriverServiceHolder[ChromeDriverService] {
    protected val theService = new ChromeDriverService.Builder()
      .usingDriverExecutable(new File(chromeDriverPath))
      .usingAnyFreePort()
      .build()
  }

  object InternetExplorerDriverServiceHolder extends DriverServiceHolder[InternetExplorerDriverService] {
    protected val theService = new InternetExplorerDriverService.Builder()
      .usingDriverExecutable(new File(internetExplorerDriverPath))
      .usingAnyFreePort()
      .build()
  }
}