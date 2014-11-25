cluster-akka-hof
================

Scala Akka cluster for message exchange in the form of numbers and their functional result.
It uses SBT 0.13.7 as build tool. This version supports the latest Akka and Scala libraries
The System needs at a minimum 3 nodes for testing the akka actor message exchange. Cluster monitoring ensures that messages are received whenever
a node either joins or leaves the cluster system. All the events would be recorded in the logs. For logging logback with slf4j is used.
One of the Node sends messaged in the form of numbers and the other node caculated its factorial and send back as a response. The response is a function worked on the number so it could be anything other than a factorial also the messages can be anything other than numbers.

Here is how it works
On Terminal 1 start a Frontend Node as follows
sbt "run-main com.cluster.demo.ClusterTransformationFrontend 7001"
On Terminal 2 start a second Frontend Node
sbt "run-main com.cluster.demo.ClusterTransformationFrontend 7002"
On Terminal 3 start a Backend Node as follows
sbt "run-main com.cluster.demo.ClusterTransformationBackend 0"
Here when port number is specified as 0 a Random port is automatically allocated by the Akka kernel system.

Once all 3 are started we can see message exchange similar to following on Frontend Nodes

Frontend Node Output
ComputeResult(factorial(1),1)
ComputeResult(factorial(2),2)
ComputeResult(factorial(3),6)
ComputeResult(factorial(4),24)