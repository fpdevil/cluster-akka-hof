package com.cluster.demo

import language.postfixOps
import scala.concurrent.duration._
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Terminated
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import java.util.concurrent.atomic.AtomicInteger
import akka.actor.ActorLogging

/**
 * @author Sampath
 *
 */

case class ClusterQuery(str: String)
case class ClusterResult(str: String)
case class ClusterCompute(n: Int)
case class ComputationFailure(reason: String, result: ClusterCompute)
// case class ComputeResult(n: BigInt)
case class ComputeResult(str: String, n: BigInt)
case class QueryFailure(reason: String, query: ClusterQuery)
case object BackendRegistration

class ClusterTransformationFrontend extends Actor with ActorLogging {
  var backend = IndexedSeq.empty[ActorRef]
  var counter = 0

  def receive = {
    case query: ClusterQuery if backend.isEmpty => {
      log.error(s"QueryFailure: ${query}")
      sender ! QueryFailure("Failed to Process the Request! Try again Later", query)
    }
    case query: ClusterQuery => {
      log.info(s"Forwarding the Message query ${query}")
      counter += 1
      backend(counter % backend.size).forward(query)
    }
    case n: ClusterCompute if backend.isEmpty => {
      log.error(s"Computation Failure: ${n}")
      sender ! ComputationFailure("Failed to Perform the Computation! Try again Later", n)
    }
    case n: ClusterCompute => {
      log.info(s"Forwarding the Computation of ${n}")
      counter += 1
      backend(counter % backend.size).forward(n)
    }
    case BackendRegistration if !(backend.contains(sender)) => {
      context.watch(sender)
      backend = backend :+ sender
    }
    case Terminated(msg) => {
      log.warning(s"Termination Message received ${msg}")
      backend = backend.filter(_ != msg)
    }
  }
}

object ClusterTransformationFrontend {
  def main(args: Array[String]) {
    args.length match {
      case 1 =>
        val port = args(0)
        println(s"Starting the ClusterTransformationFrontend on port no. ${port}")
        start(port)
      case _ =>
        println("Valid port is not provided")
    }

    def start(port: String) = {
      val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
        withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]")).
        withFallback(ConfigFactory.load())

      val system = ActorSystem("ClusterSystem", config)
      val frontend = system.actorOf(Props[ClusterTransformationFrontend], name = "frontend")

      val counter = new AtomicInteger
      import system.dispatcher
      system.scheduler.schedule(10.seconds, 10.seconds) {
        implicit val timeout = Timeout(5 seconds)
//        (frontend ? ClusterQuery("cluster:" + counter.incrementAndGet())) onSuccess {
//          case result => println(result)
        (frontend ? ClusterCompute(counter.incrementAndGet())) onSuccess {
          case result => println(result)
        }
      }
    }
  }

}