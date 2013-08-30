package com.cyrusinnovation.selenium

import org.scalatest.{BeforeAndAfterEach, Suite}
import org.openqa.selenium.WebDriver

trait DriverBuilder extends BeforeAndAfterEach { this: Suite =>

  implicit val webDriver: WebDriver = WebDriverFactory.driver()

  override def beforeEach() {
    WebDriverFactory.deleteAllCookies()
    WebDriverFactory.closeAllChildWindows()
  }

  override def afterEach() {
  }

}