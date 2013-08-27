package code.test.selenium

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.concurrent.Eventually._
import org.scalatest.matchers.ShouldMatchers._
import org.scalatest.selenium.WebBrowser
import org.scalatest.time._
import com.cyrusinnovation.selenium.{DefaultBaseURLProvider, DriverBuilder}

@RunWith(classOf[JUnitRunner])
class HelloWorldIntegrationTests extends FlatSpec with WebBrowser with DriverBuilder with SpanSugar {

  "The test framework" should "be able to access and query pages correctly" in {
    go to "http://www.google.com"
    click on "q"
    textField("q").value = "Cheese!"
    submit()
    // Google's search is rendered dynamically with JavaScript.
    eventually (timeout(2 seconds), interval(250 millis)) { pageTitle should be ("Cheese! - Google Search") }
  }
}

@RunWith(classOf[JUnitRunner])
class WebApplicationIntegrationTests extends FlatSpec with WebBrowser with DriverBuilder with DefaultBaseURLProvider with SpanSugar {
  val applicationHomepage: String = defaultBaseUrl

  "The blank Lift application" should "be accessible" in {
    go to applicationHomepage

    val welcomeText = find(xpath("//*[contains(text(),'Welcome to your project!')]"))
    welcomeText should be ('defined)

    val liftLink = find(linkText("Lift"))
    liftLink should be ('defined)
  }
}

