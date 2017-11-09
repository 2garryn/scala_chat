
import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.ActorMaterializer

import scala.io.StdIn
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import StatusCodes._

import org.json4s.jackson.Serialization
import org.json4s.DefaultFormats

object WebServer {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher
    implicit val formats = DefaultFormats.preservingEmptyValues
    val route = {
      pathPrefix("api") {
        path(Segment) { client =>
          get {
            ClientStore.GetLocation(client) match {
              case Some(status) => complete(Serialization.write(status))
              case other        => complete(HttpResponse(NotFound, entity = "Client Not Found"))
            }
          } ~
          post {
            entity(as[String]) { status => {
              try {
                val new_status = Serialization.read[ClientStatus](status)
                if (client != new_status.client) {
                  complete(HttpResponse(BadRequest, entity = "Client mismatch"))
                } else {
                  ClientStore.Put(new_status)
                  complete("ok")
                }
              } catch {
                case e: Exception => complete(HttpResponse(BadRequest, entity = "Bad JSON"))
              }
            }}
          }

        }

      }
    }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
