package pl.net.scala

import akka.http.server.Route

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

trait GuardedRoutes {

  def guardedRoute(routeDef: Route): Route = macro RouteMacro.impl

}

object GuardedRoutes extends GuardedRoutes

private object RouteMacro {

  val abortMessage = "Missing routes' concatenation tilde (~) operator!"

  def impl(c: blackbox.Context)(routeDef: c.Expr[Route]): c.Expr[Route] = {
    import c.universe._

    val routeType: Type = c.typecheck(q"identity[akka.http.server.Route](null)").tpe

    def returnsRoute(tree: Tree): Boolean = {

      def performCheck = {
        val typecheckedTreeType: Type = c.typecheck(tree = tree.duplicate).tpe

        if (typecheckedTreeType == null) {
          c.warning(tree.pos, s"GuardedRoutes macro couldn't resolve the type of this expression.")
        }

        typecheckedTreeType != null &&
          typecheckedTreeType != typeOf[Nothing] &&
          typecheckedTreeType != typeOf[Null] &&
          typecheckedTreeType.dealias <:< routeType.dealias
      }

      tree match {
        case Function(_, body) => returnsRoute(body)
        case _ => performCheck
      }
    }

    def verify(subTree: Tree): Unit = {
      subTree match {
        case Block(stmts, expr) if returnsRoute(expr) =>
          val filteredStmts = stmts.filter {
            case Import(_, _) => false
            case _ => true
          }
          if (filteredStmts.exists(returnsRoute)) {
            c.abort(expr.pos, abortMessage)
          }
          (expr :: filteredStmts).foreach(verify)
        case Function(_, body) if returnsRoute(body) => verify(body)
        case a@Apply(_, args) if returnsRoute(a) => args.foreach(verify)
        case ValDef(_, _, _, rhs) if returnsRoute(rhs) => verify(rhs)
        case DefDef(_, _, _, _, _, defBody) if returnsRoute(defBody) => verify(defBody)
        case If(_, thenp, elsep) =>
          if (returnsRoute(thenp)) {
            verify(thenp)
          }
          if (returnsRoute(elsep)) {
            verify(elsep)
          }
        case Match(_, cases) =>
          val caseBodiesReturningRoutes = cases.collect {
            case CaseDef(_, _, body) if returnsRoute(body) =>
              body
          }
          caseBodiesReturningRoutes.foreach(verify)
        case _ => // do nothing
      }
    }

    verify(routeDef.tree)

    routeDef
  }
}


