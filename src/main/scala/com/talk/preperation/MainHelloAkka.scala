package com.talk.preperation

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object MainHelloAkka extends App with LazyLogging {
  implicit val system = ActorSystem("hello")

  import system.dispatcher


  class Device(id: String) extends Actor with ActorLogging {
    var messagesCount: Int = 0

    override def receive: Receive = {
      case s: String if s.toLowerCase.contains("crash") =>
        throw new Exception("Boom! 💥")

      case s: String if s.contains("Stats") =>
        sender() ! (id, messagesCount)

      case s: String =>
        log.info(s"$id at ${self.path} ~> ${s} [${messagesCount}]")
        messagesCount += 1
    }

    override def preStart(): Unit = {
      log.info(s"${id} at ${self.path} is starting up,..")
    }
  }


  class DeviceManager() extends Actor with ActorLogging {
    val (
      phone, car
      ) = (
      context.actorOf(Props(new Device("☎️")), "phone"),
      context.actorOf(Props(new Device("🚗")), "car")
    )

    override def receive: Receive = {
      case s: String if s.contains("car") =>
        car.forward(s)
      case s: String if s.contains("phone") =>
        phone.forward(s)

      case s: String if s.contains("stats") =>
        implicit val timeout: Timeout = 3.seconds

        val r = for {
          phoneStats <- (phone ? "Stats").mapTo[(String, Int)]
          carStats <- (car ? "Stats").mapTo[(String, Int)]
        } yield Map(phoneStats, carStats)

        r pipeTo (sender())

      case s: String =>
        log.info(s"Got ${s} at ${self.path}")
    }
  }

  // val d1 = system.actorOf(Props(new Device("☎️")))
  // val d2 = system.actorOf(Props(new Device("🚗")))
  // d1 ! "Do it!"
  // d1 ! "Do something else"
  // d2 ! "Call me, maybe!"
  // d1 ! "crash"
  // d1 ! "how about I"
  // d1 ! "how about II."

  val dm = system.actorOf(Props(new DeviceManager), "device-manager")

  dm ! "Do it"
  dm ! "For car. Say hello to people!"
  dm ! "For car. Crash!"
  dm ! "Is my car up?"

  val f = ((dm ? "Show me stats") (3.seconds)).mapTo[Map[String, Int]]
  f.onComplete {
    case Success(value) =>
      println(value)
    case Failure(exception) =>
      System.err.println(exception)
  }

}
