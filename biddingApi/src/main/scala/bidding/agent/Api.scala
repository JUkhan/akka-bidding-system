package bidding.agent


import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import scala.concurrent.duration._


object Api extends App with JsonSupport  with SprayJsonSupport {
  val config = ConfigFactory.load()
  val port = config.getInt("port")

  implicit val system = ActorSystem("demoApi")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher
  implicit val timeout = Timeout(2 seconds)

  val biddingActor = system.actorOf(Props[BiddingActor], "biddingActor")
  val routes=
    pathPrefix("api") {
        get(
          path("hello") {

            complete("hi")
          }
        )~
        post(
          (path("bid") & entity(as[BidRequest])) { request =>
            val data = (biddingActor ? request).mapTo[Option[BidResponse]]
           onSuccess(data){res=>
             res match {
               case Some(value)=>complete(value)
               case None=> complete(StatusCodes.NoContent)
             }
           }

          }

        )

    }
  Http().bindAndHandle(routes, "localhost", port)
  println(s"Server is up and running on port: $port")
}
