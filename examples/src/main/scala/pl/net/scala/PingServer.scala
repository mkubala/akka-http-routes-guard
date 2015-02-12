package pl.net.scala

import akka.actor.ActorSystem
import akka.http.Http
import akka.http.model.StatusCodes._
import akka.http.server.Directives._
import akka.http.server.{RouteResult, RequestContext, Route}
import akka.stream.FlowMaterializer

import scala.concurrent.{Future, ExecutionContextExecutor}
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
          complete(OK -> "YYY")
        }
    }

    def createRoute = {
      get {
        complete(OK -> "complete GET")
      } ~
        post {
          complete(OK -> "YYY")
        }
    }

    val w = () =>

    val xxx = if (new Random().nextBoolean()) {
      get {
        complete(OK -> "complete GET")
      } ~
      post {
        complete(OK -> "YYY")
      }
    } else {
      get {
        complete(OK -> "complete GET")
      } ~
      post {
        complete(OK -> "YYY")
      }
    }

    println("I love dumplings")

    xxx ~ createRoute ~ getRoute ~ post {
      get {
        complete(OK -> "XXX")
      } ~
        post {
          complete(OK -> "xxx POST")
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

