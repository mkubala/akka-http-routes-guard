package pl.net.scala

import akka.http.server.Route
import scala.reflect.macros.blackbox

import scala.language.experimental.macros

trait GuardedRoutes {

  def guardedRoute(routeDef: Route): Route = macro RouteMacro.impl

}

object GuardedRoutes extends GuardedRoutes

object RouteMacro {
  def impl(c: blackbox.Context)(routeDef: c.Expr[Route]): c.Expr[Route] = {
    import c.universe._

    // just print wrapped route definition tree.
    println(showRaw(routeDef))

    routeDef
  }
}
