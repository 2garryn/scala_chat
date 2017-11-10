import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.io.StdIn

object Initial extends App {
  override def main(args: Array[String]): Unit = {
    val roleObj = ConfigHandler.getString("role", "stewie") match {
      case "maggie" => MaggieRole
    }
    startServer(roleObj.getRoutes())
  }

  def startServer(route: Route) = {
    implicit val system = ActorSystem("actor-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val address = ConfigHandler.getString("server-address", "localhost")
    val port = ConfigHandler.getInt("server-port", 8080)
    val bindingFuture = Http().bindAndHandle(route, address, port)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}



