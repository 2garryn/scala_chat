import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import StatusCodes._
import akka.http.scaladsl.server.{Route, StandardRoute}
import org.json4s.jackson.Serialization
import org.json4s.DefaultFormats

object MaggieRole {
  implicit val formats = DefaultFormats.preservingEmptyValues
  def getRoutes(): Route = {
    return {
      pathPrefix("api") {
        path(Segment) { client =>
          get {
            getClient(client)
          } ~
            post {
              entity(as[String]) { status => updateClient(client, status)
              }
            }
        }

      }
    }
  }

  private def getClient(client: String): StandardRoute = {
    ClientStore.GetLocation(client) match {
      case Some(status) => complete(Serialization.write(status))
      case other => complete(HttpResponse(NotFound, entity = "Client Not Found"))
    }
  }

  private def updateClient(client: String, json_status: String): StandardRoute = {
    try {
      val new_status = Serialization.read[ClientStatus](json_status)
      if (client != new_status.client) {
        complete(HttpResponse(BadRequest, entity = "Client mismatch"))
      } else {
        ClientStore.Put(new_status)
        complete("ok")
      }
    } catch {
      case e: Exception => complete(HttpResponse(BadRequest, entity = "Bad JSON"))
    }
  }

}
