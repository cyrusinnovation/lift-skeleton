package code.test.selenium

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.concurrent.Eventually._
import org.scalatest.matchers.ShouldMatchers._
import org.scalatest.selenium.WebBrowser
import org.scalatest.time._
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.WebDriver

@RunWith(classOf[JUnitRunner])
class HelloWorldIntegrationTests extends FlatSpec with WebBrowser with SpanSugar {

  val applicationHomepage: String = "http://localhost:8080/"
  implicit val webDriver: WebDriver = new HtmlUnitDriver()

  "The test framework" should "be able to access and query pages correctly" in {
    go to "http://www.google.com"
    click on "q"
    textField("q").value = "Cheese!"
    submit()
    // Google's search is rendered dynamically with JavaScript.
    eventually (timeout(2 seconds), interval(250 millis)) { pageTitle should be ("Cheese! - Google Search") }
  }

  "The blank Lift application" should "be accessible" in {
    go to applicationHomepage

    val welcomeText = find(xpath("//*[contains(text(),'Welcome to your project!')]"))
    welcomeText should be ('defined)

    val liftLink = find(linkText("Lift"))
    liftLink should be ('defined)
  }
}

