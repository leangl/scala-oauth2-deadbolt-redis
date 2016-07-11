package controllers

import play.api.mvc.{Action, Controller}

class Application extends Controller {

  def index = Action { request =>
    Ok("Your new application is ready.")
  }

}
