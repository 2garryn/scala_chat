/**
  * Created by artemgolovinskij on 08/11/17.
  */
import java.util.concurrent.ConcurrentHashMap

case class ClientStatus(client: String, location: String, status: String)

object ClientStore{
  val STATUS_ONLINE = "online"
  val STATUS_OFFLINE = "offline"
  val STATUS_AWAY = "away"

  val map =  new ConcurrentHashMap[String, ClientStatus]

  def Put(client: String, location: String, status: String = STATUS_ONLINE): Unit = {
    val db_status  = new ClientStatus(client = client, location = location, status = status)
    Put(db_status)
  }

  def Put(new_status: ClientStatus) = new_status match {
    case ClientStatus(_, _, STATUS_OFFLINE) => map.remove(new_status.client)
    case _ => map.put(new_status.client, new_status)
  }
  
  def GetLocation(client: String): Option[ClientStatus] = {
    map.get(client) match {
        case status:ClientStatus => Some(status)
        case _ => None
    }
  }
}

