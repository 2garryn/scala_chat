import java.nio.file.Paths
import com.lambdista.config.Config
import scala.util.Try

object ConfigHandler {
  val config: Try[Config] = Config.from(Paths.get("application.conf"))

  def getString(key: String, default: String): String = {
    val confValue: Try[String] = for { c <- config; value <- c.getAs[String](key) } yield value
    return confValue.get
  }

  def getInt(key: String, default: Int): Int = {
    val confValue: Try[Int] = for { c <- config; value <- c.getAs[Int](key) } yield value
    return confValue.get
  }

}
