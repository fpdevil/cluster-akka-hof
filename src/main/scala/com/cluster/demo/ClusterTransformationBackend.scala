package com.cluster.demo

import akka.actor.Actor
import akka.cluster.Cluster
import akka.cluster.Member
import akka.cluster.MemberStatus
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.ClusterEvent.CurrentClusterState
import akka.actor.ActorLogging
import akka.actor.RootActorPath
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props
import akka.cluster.ClusterEvent.MemberEvent
import akka.cluster.ClusterEvent.UnreachableMember
import akka.cluster.ClusterEvent.MemberRemoved
import scala.annotation.tailrec

/**
 * @author Sampath
 *
 */

class ClusterTransformationBackend extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    // cluster.subscribe(self, classOf[MemberUp])
    cluster.subscribe(self, classOf[MemberUp], classOf[MemberEvent])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  def factorial(N: Int): Int = {
    require(N >= 0)
    def loop(N: Int, Acc: Int): Int = {
      if (N == 0)
        Acc
      else
        loop(N - 1, Acc * N)
    }
    loop(N, 1)
  }

  def receive = {
    case ClusterQuery(str) => {
      log.info(s"Converting the String ${str} to Upper Case [${sender.path.toString()}]")
      sender ! ClusterResult(str.toUpperCase())
    }
    case ClusterCompute(n) => {
      log.info(s"Computing the Factorial(${n}) = ${factorial(n)} and send to [${sender.path}]")
      sender ! ComputeResult(s"factorial(${n})", factorial(n))
    }
    case status: CurrentClusterState => {
      log.info(s"Member Status ${status} at [${sender.path}]")
      status.members.filter(p => p.status == MemberStatus.Up).foreach(registerMember)
    }
    case MemberUp(member) => {
      log.info("Registering the ClusterMember {} at {}", member, member.address)
      registerMember(member)
    }
    case UnreachableMember(member) => {
      log.info("Member detected as unreachable: {}", member)
    }
    case MemberRemoved(member, previousStatus) => {
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    }
    case _: MemberEvent => // ignore
  }

  def registerMember(member: Member): Unit = {
    if (member.hasRole("frontend"))
      context.actorSelection(RootActorPath(member.address) / "user" / "frontend") ! BackendRegistration
  }
}

object ClusterTransformationBackend {
  def main(args: Array[String]) {
    args.length match {
      case 1 => {
        val port = args(0)
        println(s"Starting the ClusterTransformationBackend on Random Port as port. ${port} is providede")
        start(port)
      }
      case _ => {
        println("Valid port is not provided")
        println("Provide Port number 0 or another port above 1024")
      }
    }
  }

  def start(port: String) = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
      withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[ClusterTransformationBackend], name = "backend")
  }

}