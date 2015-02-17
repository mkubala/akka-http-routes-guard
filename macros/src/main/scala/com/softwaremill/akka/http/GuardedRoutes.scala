package com.softwaremill.akka.http

import akka.http.server.Route
import scala.language.experimental.macros

object GuardedRoutes extends GuardedRoutes

trait GuardedRoutes {

  def guardedRoute(routeDef: Route): Route = macro RouteMacro.impl

}