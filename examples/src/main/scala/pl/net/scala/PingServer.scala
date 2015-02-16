package pl.net.scala

import akka.actor.ActorSystem
import akka.http.Http
import akka.http.model.StatusCodes._
import akka.http.server.Directives._
import akka.http.server.Route
import akka.stream.FlowMaterializer

import scala.concurrent.ExecutionContextExecutor
import scala.util.Random

trait PingService extends GuardedRoutes {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: FlowMaterializer

  /*
  * Try remove some concatenation tilde operators and see our macro in action
  */

  // guardedRoute is our def macro
  val routes: Route = guardedRoute {

    lazy val getRoute = path("asd") {
      get {
        complete(OK -> "complete GET")
      } ~
        post {
          complete(OK -> "Complered")
        }
    }

    def createRoute = {
      get {
        complete(OK -> "Completed")
      } ~
        post {
          complete(OK -> "Complered")
        }
    }

    // Pattern matching example
    val patternMatched = "someStr" match {
      case str: String => get {
          complete(OK -> str)
        } ~
        post {
          complete(OK -> str)
        }
      case _ => complete(NotFound)
    }

    // def args example
    def wrapRoute(r: Route): Route = path("somePrefix") {
      r
    }

    val wrappedRouteEx = wrapRoute {
      get {
        complete(OK -> "Completed")
      } ~
      post {
        complete(OK -> "Completed")
      }
    }

    val xxx = if (new Random().nextBoolean()) {
      get {
        complete(OK -> "Completed")
      } ~
      post {
        complete(OK -> "Complered")
      }
    } else {
      get {
        complete(OK -> "Completed")
      } ~
      post {
        complete(OK -> "Complered")
      }
    }

    println("I love dumplings")

    wrappedRouteEx ~ patternMatched ~ xxx ~ createRoute ~ getRoute ~ post {
      get {
        complete(OK -> "Completed")
      } ~
        post {
          complete(OK -> "Completed")
        }
    }
  }

}

object PingServer extends App with PingService {

  override implicit val system = ActorSystem("pingServiceActorSys")
  override implicit val executor = system.dispatcher
  implicit val materializer = FlowMaterializer()

  Http().bind(interface = "localhost", port = 8080).startHandlingWith(routes)

}

