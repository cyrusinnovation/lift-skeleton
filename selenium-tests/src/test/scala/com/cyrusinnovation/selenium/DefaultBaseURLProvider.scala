package com.cyrusinnovation.selenium

/** Obtains from a System property the default URL against which tests are run (unless a different
  * one is specified in a particular test). You can modify this by setting the System property
  *
  *   default.base.url
  *
  * It should include protocol and host, and optionally port. If the property is not set, the value
  * defaults to "http://localhost:8080/"
  */
trait DefaultBaseURLProvider {
  protected val defaultBaseUrl = Option(System.getProperty("default.base.url")).getOrElse("http://localhost:8080")
}
