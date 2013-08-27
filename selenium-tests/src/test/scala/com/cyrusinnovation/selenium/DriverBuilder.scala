package com.cyrusinnovation.selenium

import org.scalatest.{Suite, BeforeAndAfterEach}
import org.openqa.selenium.WebDriver

trait DriverBuilder extends BeforeAndAfterEach { this: Suite =>

  implicit val webDriver: WebDriver = new WebDriverFactory().driver()
  override def beforeEach() {
  }

  override def afterEach() {
  }
}