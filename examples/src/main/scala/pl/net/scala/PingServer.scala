package pl.net.scala

import akka.actor.ActorSystem
import akka.http.Http
import akka.http.model.StatusCodes._
import akka.http.server.Directives._
import akka.stream.FlowMaterializer

import scala.concurrent.ExecutionContextExecutor

trait PingService extends GuardedRoutes {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: FlowMaterializer

  // guardedRoute is our def macro
  val routes = guardedRoute {
    get {
      complete(OK -> "Hello from PingServer!")
    } ~
    path("ping") {
      (post & entity(as[String])) { msg =>
        complete(OK -> s"Pong, $msg!")
      } ~
      get {
        complete(OK -> "Pong!")
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
