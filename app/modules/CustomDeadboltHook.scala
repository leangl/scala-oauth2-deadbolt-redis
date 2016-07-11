package modules

import be.objectify.deadbolt.scala.cache.HandlerCache
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import security.oauth.deadbolt.{DeadboltHandlerCache, JavaCompat}

class CustomDeadboltHook extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    Seq(
      bind[HandlerCache].to[DeadboltHandlerCache],
      bind[be.objectify.deadbolt.java.cache.HandlerCache].to[JavaCompat]
    )
}