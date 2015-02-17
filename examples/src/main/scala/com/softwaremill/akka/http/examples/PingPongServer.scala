package com.softwaremill.akka.http.examples

import akka.actor.ActorSystem
import akka.http.Http
import akka.http.model.StatusCodes._
import akka.http.server.Directives._
import akka.http.server.Route
import akka.stream.FlowMaterializer
import com.softwaremill.akka.http.GuardedRoutes

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

trait PingPongService extends GuardedRoutes {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: FlowMaterializer

  /*
  * Try remove some concatenation tilde operators and see the guard in action
  */

  // guardedRoute is our def macro
  val routes: Route = guardedRoute {

    lazy val pingRoutes = path("ping") {
      get {
        complete(OK -> "Pong! (GET)")
      } ~
      post {
        complete(OK -> "Pong! (POST)")
      }
    }

    pingRoutes ~ complete(OK -> "Wanna play some PingPong? Send request to /ping!")

  }

}

object PingPongServer extends App with PingPongService {

  override implicit val system = ActorSystem("pingServiceActorSys")
  override implicit val executor = system.dispatcher
  implicit val materializer = FlowMaterializer()

  val binding = Http().bind(interface = "localhost", port = 8080)
  val materializedMap = binding.startHandlingWith(routes)

  println("Press enter to stop.")
  StdIn.readLine()
  binding.unbind(materializedMap).onComplete(_ => system.shutdown())

}

