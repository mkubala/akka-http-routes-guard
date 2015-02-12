package pl.net.scala

import akka.http.server.{RouteResult, RequestContext, Route}
import scala.annotation.tailrec
import scala.concurrent.Future
import scala.reflect.macros.blackbox

import scala.language.experimental.macros

trait GuardedRoutes {

  def guardedRoute(routeDef: Route): Route = macro RouteMacro.impl

}

object GuardedRoutes extends GuardedRoutes

object RouteMacro {

  def impl(c: blackbox.Context)(routeDef: c.Expr[Route]): c.Expr[Route] = {
    import c.universe._

    def returnsRoute(tree: Tree): Boolean = {
      val routeType: Type = c.typecheck(q"identity[akka.http.server.Route](null)").tpe
      val typecheckedTreeType: Type = c.typecheck(tree = tree.duplicate).tpe
      typecheckedTreeType != typeOf[Nothing] &&
        typecheckedTreeType != typeOf[Null] &&
        typecheckedTreeType.dealias <:< routeType.dealias
    }

    def verify(subTree: Tree): Unit = {
      subTree match {
        case Block(stmts, expr) if returnsRoute(expr) =>
          if (stmts.exists(returnsRoute)) {
            c.abort(expr.pos, "Missing routes' concatenation tilde (~) operator!")
          }
          (expr :: stmts).foreach(verify)
        case a @ Apply(_, args) if returnsRoute(a) =>
          args.foreach(verify)
        case ValDef(_, _, _, rhs) if returnsRoute(rhs) =>
          verify(rhs)
        case DefDef(_, _, _, _, _, defBody) if returnsRoute(defBody) =>
          verify(defBody)
        case If(_, thenp, elsep) =>
          if (returnsRoute(thenp)) {
            verify(thenp)
          }
          if (returnsRoute(elsep)) {
            verify(elsep)
          }
//        case Match(_, cases) => cases.foreach(verify)
//        case CaseDef(_: Tree, body: Tree) if returnsRoute(body) => verify(body)
        case _ =>
      }
    }

    verify(routeDef.tree)

    routeDef
  }
}


