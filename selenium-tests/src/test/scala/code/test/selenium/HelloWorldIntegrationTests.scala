package code.test.selenium

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.concurrent.Eventually._
import org.scalatest.matchers.ShouldMatchers._
import org.scalatest.selenium.HtmlUnit
import org.scalatest.time.{Seconds, Span}

@RunWith(classOf[JUnitRunner])
class HelloWorldIntegrationTests extends FlatSpec with HtmlUnit {
  "The test framework" should "be able to access and query pages correctly" in {
    go to "http://www.google.com"
    click on "q"
    textField("q").value = "Cheese!"
    submit()
    implicitlyWait(Span(2, Seconds))
    // Google's search is rendered dynamically with JavaScript.
    eventually { pageTitle should be ("Cheese! - Google Search") }
  }

  "The blank Lift application" should "be accessible" in {
    go to "http://localhost:8080/index"
    implicitlyWait(Span(2, Seconds))

    val welcomeText = find(xpath("//*[contains(text(),'Welcome to your project!')]"))
    welcomeText should be ('defined)

    val liftLink = find(linkText("Lift"))
    liftLink should be ('defined)
  }
}

