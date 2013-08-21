package code.test.cucumber

import cucumber.api.scala.{ScalaDsl, EN}
import org.scalatest.concurrent.Eventually._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.selenium.HtmlUnit
import code.test.cucumber.pages.{StaticContentPage, Homepage}

// TODO: Abstract out the HtmlUnit mixin so that different drivers may be used (for go, find, etc.)
// TODO: Abstract out the host so that tests can be run in different environments
// TODO: Probably need a better PageObject abstraction here
class HelloWorldWebSteps extends ScalaDsl with EN with HtmlUnit with ShouldMatchers {
  private val host = "http://localhost:8080"
  private val pagesByIdentifier = Map("home" -> Homepage,
                                      "Static Content" -> StaticContentPage)
  private val pagesByPath = Map(Homepage.path -> Homepage,
                                StaticContentPage.path -> StaticContentPage)

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