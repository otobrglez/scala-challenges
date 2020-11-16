package com.talk.show

import akka.actor._

object MainHelloAkka extends App {
  var system = ActorSystem("hello")

  class Device(id: String) extends Actor with ActorLogging {

    var messagesCount: Int = 0

    override def receive: Receive = {
      case s: String if s.contains("boom") =>
        throw new Exception("boom 💥💥💥💥")
      case s: String =>
        log.info(s"${id} at ${self.path} got => ${s} [${messagesCount + 1}]")
        messagesCount += 1

        sender() ! "Thanks for telling me"
    }

    override def preStart(): Unit = {
      log.info(s"${id} is up at ${self.path}")
    }
  }

  class DeviceManager extends Actor with ActorLogging {
    val phone = context.actorOf(Props(new Device("☎️")), "phone")
    val car = context.actorOf(Props(new Device("🚗️")), "car")

    override def receive: Receive = {
      case s: String if s.contains("car") =>
        car.forward(s)
      case s: String if s.contains("phone") =>
        phone.forward(s)



      case s: String =>


        log.info(s"Got ${s}")
    }
  }

  val dm = system.actorOf(Props(new DeviceManager), "dm")
  dm ! "car test"
  dm ! "car test"
  dm ! "phone test some more"
  dm ! "car boom"
  // dm.tell("car boom")



}
