package bidding.agent
import akka.actor.{Actor, ActorLogging}


import scala.util.Random

case class BannerWithBidFloor(banner:Banner, bidFloor: Option[Double])

class BiddingActor extends Actor with ActorLogging{
  override def receive: Receive = {
    case msg:String =>
      sender() ! msg

    case BidRequest(id, imp, site, user, device)=>

      val filteredCampaigns=activeCampaigns
        //filter by site ID
        .filter(cam=>cam.targeting.targetedSiteIds.contains(site.id))
        //filter by country giving higher priority on device.geo.country
        .filter{cam=>
          val _deviceCountry=device match {
            case Some(value) => value.geo.flatMap(_.country)
            case None =>None
          }
          val _userCountry = user match {
            case Some(value) => value.geo.flatMap(_.country)
            case None =>None
          }
          _deviceCountry match {
            case Some(dc) => dc.equals(cam.country)
            case None =>_userCountry match {
              case Some(uc)=>uc.equals(cam.country)
              case None => false
            }
          }

        }
      filteredCampaigns match {
        //sending none if no campaign found
        case Nil => sender() ! None
        case caps =>
          val random = new Random
          //select one campaign randomly
          var resCap=caps(random.nextInt(caps.length))
          findBanners(resCap.banners, imp) match {
            //sending also none if no banner found
            case Nil => sender() ! None
            case banners=>
              //select one banner randomly
              val banner=banners(random.nextInt(banners.length))
              //successfully sending bidding result
              sender() ! Some(BidResponse(
                id=site.id,
                bidRequestId = id,
                price = banner.bidFloor.getOrElse(0.0),
                adid = Some(resCap.id.toString),
                banner = Some(banner.banner)
              ))
          }
      }
  }
  //finding banners based on
  // width and height if provided, otherwise fallback to min/max or min~max
  def findBanners(banners: List[Banner], imp: Option[List[Impression]]):List[BannerWithBidFloor]= {
    val _imp = imp.getOrElse(List())

    banners.foldLeft[List[BannerWithBidFloor]](List()) { (acc, el) =>
      val hasAny = _imp.find { impObj =>

        val widthMatch = impObj.w match {
          //compare with w only
          case Some(w) => w == el.width
          case None => impObj.wmin match {
            case Some(wMin) => impObj.wmax match {
              //compare with (min~max) width
              case Some(wMax) => el.width >= wMin && el.width <= wMax
              //compare with min width only
              case None => el.width >= wMin
            }
            case None => impObj.wmax match {
              //compare with max width only
              case Some(wMax) => el.width <= wMax
              //otherwise return false
              case None => false
            }
          }
        }
        val heightMatch = impObj.h match {
          //compare with h only
          case Some(h) => h == el.height
          case None => impObj.hmin match {
            case Some(hMin) => impObj.hmax match {
              //compare with (min~max) height
              case Some(hMax) => el.height >= hMin && el.height <= hMax
              //compare with min height only
              case None => el.height >= hMin
            }
            case None => impObj.hmax match {
              //compare with max height only
              case Some(hMax) => el.height <= hMax
              //otherwise return false
              case None => false
            }
          }
        }
        widthMatch && heightMatch
      }
      hasAny match {
        case Some(value) => BannerWithBidFloor(banner = el, bidFloor = value.bidFloor) :: acc
        case None => acc
      }
    }
  }
  val activeCampaigns = Seq(
    Campaign(
      id = 1,
      country = "LT",
      targeting = Targeting(
        targetedSiteIds = Seq("0006a522ce0f4bbbbaa6b3c38cafaa0f") // Use collection of your choice
      ),
      banners = List(
        Banner(
          id = 1,
          src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg",
          width = 300,
          height = 250
        )
      ),
      bid = 5d
    )
  )
}
