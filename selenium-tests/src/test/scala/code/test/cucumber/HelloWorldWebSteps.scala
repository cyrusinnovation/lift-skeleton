package code.test.cucumber

import cucumber.api.scala.{ScalaDsl, EN}
import org.scalatest.concurrent.Eventually._
import org.scalatest.concurrent.Eventually.PatienceConfig
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.selenium.WebBrowser
import code.test.cucumber.pages.{StaticContentPage, Homepage}
import org.scalatest.time.SpanSugar
import org.openqa.selenium.WebDriver
import com.cyrusinnovation.selenium.{DefaultBaseURLProvider, WebDriverFactory}
import cucumber.api.Scenario

class HelloWorldWebSteps extends ScalaDsl with EN with WebBrowser with DefaultBaseURLProvider with ShouldMatchers with SpanSugar {
  private  val host = defaultBaseUrl
  implicit val webDriver: WebDriver = WebDriverFactory.driver()
  implicit val patienceConfig = PatienceConfig(2 seconds, 250 millis)      // Timeout and poll interval for "eventually" method


  private val pagesByIdentifier = Map("home" -> Homepage,
                                      "Static Content" -> StaticContentPage)
  private val pagesByPath = Map(Homepage.path -> Homepage,
                                StaticContentPage.path -> StaticContentPage)


  Before((scenario: Scenario) => {
    WebDriverFactory.deleteAllCookies()
    WebDriverFactory.closeAllChildWindows()
  })

  Given("^I have browsed to the (.*) page") {
    (pageIdentifier: String) => {
      val pagePath = pagesByIdentifier(pageIdentifier).path
      go to s"$host$pagePath"
    }
  }

  When("""^I click the "(.*)" link$""") {
    (theLinkText: String) => {
      val element = eventually { find(linkText(theLinkText)) }
      element match {
        case Some(link) => click on link
        case None => fail(s"Could not find link: $theLinkText.")
      }
    }
  }

  Then("^I should see the (.*) text$") {
    (textIdentifier: String) => {
      val currentPage = pagesByPath(currentPath)
      val actualTextToFind = currentPage.text(textIdentifier)
      val textOnPage = eventually { find(xpath(s"//*[contains(normalize-space(text()), '$actualTextToFind')]")) }
      textOnPage should be ('defined)
    }
  }

  Then("^I should see the (.*) page") {
    (pageIdentifier: String) => {
      val pagePath = pagesByIdentifier(pageIdentifier).path
      currentUrl should be (s"$host$pagePath")
    }
  }

  def currentPath = {
    val regex = host.r
    regex replaceFirstIn(currentUrl, "")
  }
}