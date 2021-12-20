
import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}


import bidding.agent.{BidRequest, BidResponse, BiddingActor, Device, Geo, Impression, Site, User}

class BidSpec extends TestKit(ActorSystem("BidSpec"))
with ImplicitSender with WordSpecLike with BeforeAndAfterAll
{
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A Bidding actor" should{
    val bidActor=system.actorOf(Props[BiddingActor])
    "send back the same message" in{
      val msg="Hi, there"
      bidActor ! msg
      expectMsg(msg)
    }
    "Send bid response when it has valid site, device geo, and banner [w, h]" in{
      val bidRequest=BidRequest(
        id = "reqId-1",
        site = Site(
          id = "0006a522ce0f4bbbbaa6b3c38cafaa0f",
          domain = ""
        ),
        device = Some(Device(
          id = "", geo =Some( Geo(country =Some("LT")))
        )),
        imp = Some(List(
          Impression(
            id = "1", w = Some(300), h = Some(250), bidFloor = Some(3.98786),
            wmin = None, wmax = None, hmax = None, hmin = None
          )
        )),
        user = None
      )
      bidActor ! bidRequest
      val reply=expectMsgType[Option[BidResponse]]
      reply match {
        case Some(value) => assert(value.bidRequestId=="reqId-1" )
      }

    }
    "Send bid response when it has valid site, user geo, and banner [w, hmax]" in{
      val bidRequest=BidRequest(
        id = "reqId-2",
        site = Site(
          id = "0006a522ce0f4bbbbaa6b3c38cafaa0f",
          domain = ""
        ),
        device = Some(Device(
          id = "", geo =Some( Geo(country =Some("LT")))
        )),
        imp = Some(List(
          Impression(
            id = "1", w = Some(300), h = None, bidFloor = Some(3.98786),
            wmin = None, wmax = None, hmax = Some(250), hmin = None
          )
        )),
        user = None
      )
      bidActor ! bidRequest
      val reply=expectMsgType[Option[BidResponse]]
      reply match {
        case Some(value) => assert(value.bidRequestId=="reqId-2" )
      }

    }
    "Send bid response when it has valid site, device geo, and banner [wmin, h]" in{
      val bidRequest=BidRequest(
        id = "reqId-1",
        site = Site(
          id = "0006a522ce0f4bbbbaa6b3c38cafaa0f",
          domain = ""
        ),
        device = Some(Device(
          id = "", geo =Some( Geo(country =Some("LT")))
        )),
        imp = Some(List(
          Impression(
            id = "1", w = None, h = Some(250), bidFloor = Some(3.98786),
            wmin = Some(200), wmax = None, hmax = None, hmin = None
          )
        )),
        user = None
      )
      bidActor ! bidRequest
      val reply=expectMsgType[Option[BidResponse]]
      reply match {
        case Some(value) => assert(value.bidRequestId=="reqId-1" )
      }

    }
    "Send bid response when it has valid site, user geo, and banner [w, hmin, hmax]" in{
      val bidRequest=BidRequest(
        id = "reqId-1",
        site = Site(
          id = "0006a522ce0f4bbbbaa6b3c38cafaa0f",
          domain = ""
        ),
        user = Some(User(
          id = "", geo =Some( Geo(country =Some("LT")))
        )),
        imp = Some(List(
          Impression(
            id = "1", w = Some(300), h = None, bidFloor = Some(3.98786),
            wmin = None, wmax = None, hmax = Some(250), hmin = Some(120)
          )
        )),
        device = None,
      )
      bidActor ! bidRequest
      val reply=expectMsgType[Option[BidResponse]]
      reply match {
        case Some(value) => assert(value.bidRequestId=="reqId-1" )
      }

    }
    "Send bid response when it has valid site, [device geo, user geo - device geo get higher priority], and banner [w, hmin, hmax]" in{
      val bidRequest=BidRequest(
        id = "reqId-1",
        site = Site(
          id = "0006a522ce0f4bbbbaa6b3c38cafaa0f",
          domain = ""
        ),
        user = Some(User(
          id = "", geo =Some( Geo(country =Some("LTxx")))
        )),
        imp = Some(List(
          Impression(
            id = "1", w = Some(300), h = None, bidFloor = Some(3.98786),
            wmin = None, wmax = None, hmax = Some(250), hmin = Some(120)
          )
        )),
        device = Some(Device(
          id = "", geo =Some( Geo(country =Some("LT")))
        )),
      )
      bidActor ! bidRequest
      val reply=expectMsgType[Option[BidResponse]]
      reply match {
        case Some(value) => assert(value.bidRequestId=="reqId-1" )
      }

    }
    "Send bid response none when it has valid site, but no user geo and device geo, and banner [w, hmin, hmax]" in{
      val bidRequest=BidRequest(
        id = "reqId-1",
        site = Site(
          id = "0006a522ce0f4bbbbaa6b3c38cafaa0f",
          domain = ""
        ),
        user = None,
        imp = Some(List(
          Impression(
            id = "1", w = Some(300), h = None, bidFloor = Some(3.98786),
            wmin = None, wmax = None, hmax = Some(250), hmin = Some(120)
          )
        )),
        device = None,
      )
      bidActor ! bidRequest
      val reply=expectMsgType[Option[BidResponse]]
      reply match {
        case None => assert(1==1 )
      }

    }
    "Send bid response none when it has no matched banner [w, hmin, hmax]" in{
      val bidRequest=BidRequest(
        id = "reqId-1",
        site = Site(
          id = "0006a522ce0f4bbbbaa6b3c38cafaa0f",
          domain = ""
        ),
        user = None,
        imp = Some(List(
          Impression(
            id = "1", w = Some(300), h = None, bidFloor = Some(3.98786),
            wmin = None, wmax = None, hmax = Some(200), hmin = Some(120)
          )
        )),
        device = Some(Device(
          id = "", geo =Some( Geo(country =Some("LT")))
        )),
      )
      bidActor ! bidRequest
      val reply=expectMsgType[Option[BidResponse]]
      reply match {
        case None => assert(1==1 )
      }

    }
  }
}


