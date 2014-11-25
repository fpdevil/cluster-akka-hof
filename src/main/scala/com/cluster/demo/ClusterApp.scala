package com.cluster.demo

/**
 * @author Sampath
 *
 */

/*
 * The Frontend and Backend Nodes exchange messages in the form of
 * numbers and their factorial computed
 * At a minimum we need 2 Frontend nodes and any number of backend nodes
 * Start Frontend nodes using SBT as follows
 * sbt "run-main com.cluster.demo.ClusterTransformationFrontend 7001"
 * sbt "run-main com.cluster.demo.ClusterTransformationFrontend 7002"
 * 
 * Start the Backend node as follows
 * sbt "run-main com.cluster.demo.ClusterTransformationBackend 0"
 * Here if port if specified as 0 a Random Port will be selected
 */

object ClusterApp {

  def main(args: Array[String]) {
    ClusterTransformationFrontend.main(Seq("7001").toArray)
    ClusterTransformationFrontend.main(Seq("7002").toArray)
    ClusterTransformationBackend.main(Array.empty)
    ClusterTransformationBackend.main(Array.empty)
  }

}