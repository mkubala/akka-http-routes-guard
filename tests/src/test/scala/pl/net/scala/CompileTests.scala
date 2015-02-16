package pl.net.scala

import java.io.File

import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source
import scala.tools.reflect.ToolBoxError

/*
* Inspired by Macwire's (https://github.com/adamw/macwire) CompileTests
*/
class CompileTests extends FlatSpec with Matchers {

  trait CompileTestResult {}

  case object Success extends CompileTestResult
  case object Failure extends CompileTestResult

  type CompileTestCase = (File, CompileTestResult)
  type CompileTestsSet = collection.immutable.Seq[CompileTestCase]

  val testCasesDir = new File(getClass.getResource("/testCases").toURI)
  val testCases = testCasesDir.listFiles().collect {
    case f: File if f.getName.endsWith("Fail") =>
      (f, Failure)
    case f: File if f.getName.endsWith("Success") =>
      (f, Success)
  }

  testCases.foreach(runTest)

  def runTest(tc: CompileTestCase): Unit = {
    import scala.reflect.runtime._
    val cm = universe.runtimeMirror(getClass.getClassLoader)

    import scala.tools.reflect.ToolBox
    val tb = cm.mkToolBox()

    val (testCaseFile, expectedResult) = tc

    testCaseFile.getName should testCaseLabel(expectedResult) in {
      val source = loadTest(testCaseFile)

      try {
        tb.eval(tb.parse(source))
        if (expectedResult != Success) {
          fail(s"Expected compile error.")
        }
      } catch {
        case e: ToolBoxError => {
          if (expectedResult == Success) {
            fail(s"Expected compilation & evaluation to be successful, but got an error: ${e.message}", e)
          } else {
            assert(e.getMessage.contains(RouteMacro.abortMessage))
          }
        }
      }
    }
  }

  def testCaseLabel(expectedResult: CompileTestResult): String = expectedResult match {
    case Success => "compile without errors"
    case Failure => "cause a compiler error"
  }

  def wrappedWithImportsAndImplicits(testBody: String) =
    s"""
      |import akka.actor.ActorSystem
      |import akka.http.Http
      |import akka.http.model.StatusCodes._
      |import akka.http.server.Directives._
      |import akka.http.server.Route
      |import akka.stream.FlowMaterializer
      |
      |import scala.concurrent.ExecutionContextExecutor
      |
      |import pl.net.scala.GuardedRoutes
      |
      |trait Test extends GuardedRoutes {
      |
      |     implicit val system: ActorSystem
      |
      |     implicit def executor: ExecutionContextExecutor
      |
      |     implicit val materializer: FlowMaterializer
      |
      |     val route: Route = guardedRoute {
      |         $testBody
      |     }
      |
      |}
    """.stripMargin

  def loadTest(testCaseFile: File) = wrappedWithImportsAndImplicits {
    Source.fromFile(testCaseFile).getLines().mkString("\n")
  }

}
