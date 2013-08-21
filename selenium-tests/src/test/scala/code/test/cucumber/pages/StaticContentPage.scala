package code.test.cucumber.pages

object StaticContentPage extends PageObject {
  protected val namedTexts = Map("static content description" -> "Static content... everything you put in the /static directory will be served without additions to SiteMap")

  def path = "/static/index"
}
