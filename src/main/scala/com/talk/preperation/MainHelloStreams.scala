package com.talk.preperation

import akka.NotUsed
import akka.actor.{ActorSystem, Cancellable}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl._
import com.typesafe.scalalogging.LazyLogging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

/**
 * This object is a "Source" that generates continues stream of "names".
 * Every 2 second a name is emitted, to downstream,...
 */
object RandomNamesSource {

  sealed trait Tick

  final case object Tick extends Tick

  private[this] val randomNamesFlow =
    Flow[Tick].map { _ =>
      Random.shuffle(List("Mateja", "Jernej", "Blendor", "Oto")).head
    }

  def apply(): Source[String, Cancellable] =
    Source.tick(0.seconds, 2.second, Tick)
      .via(randomNamesFlow)
}

/**
 * The object calls an external "jokes" Web Service with "name"
 * query parameter. The response is JSON that is than parsed and joke is extracted from it.
 * For more information about the API check the documentation:
 * - http://www.icndb.com/api/
 */
object JokesAPI {
  def getJoke(firstName: String)(implicit system: ActorSystem): Future[Option[String]] = {
    import system.dispatcher
    import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
    import io.circe._

    for {
      response <- Http()
        .singleRequest(HttpRequest(uri = Uri("http://api.icndb.com/jokes/random")
          .withQuery(Uri.Query(Map("firstName" -> firstName)))))
      json <- Unmarshal(response).to[Json]
    } yield json.hcursor.downField("value").downField("joke").focus.map(_.toString())
  }
}

object MainHelloStreams extends App with LazyLogging {
  implicit val system: ActorSystem = ActorSystem("streams")

  import system.dispatcher

  /**
   * This Flow gets messages (ignores them) from WebSocket and generates
   * continues stream of "jokes" downstream.
   */
  val wsSocketHandlingFlow: Flow[Message, Message, NotUsed] = Flow[Message].flatMapConcat {
    case TextMessage.Strict(_: String) =>
      RandomNamesSource()
        .mapAsyncUnordered(2)(JokesAPI.getJoke)
        .collect {
          case Some(joke) => joke
          case None => throw new Exception("Failed to fetch a joke!")
        }
        .map(TextMessage(_))
    case _ =>
      Source.single(TextMessage("Just text,..."))
  }

  val route: Route = path("jokes")(handleWebSocketMessages(wsSocketHandlingFlow))
  val serverBindingF: Future[Http.ServerBinding] = Http().newServerAt("0.0.0.0", 7000).bind(route)
  serverBindingF.onComplete(_ => println("Jokes WebSocket Server Started on ws://0.0.0.0:7000/jokes"))
}
