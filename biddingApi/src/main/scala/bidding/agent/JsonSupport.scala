package bidding.agent

import spray.json.DefaultJsonProtocol

trait JsonSupport extends DefaultJsonProtocol {
  implicit val geo = jsonFormat1(Geo)
  implicit val device = jsonFormat2(Device)
  implicit val user = jsonFormat2(User)
  implicit val state = jsonFormat2(Site)
  implicit val impression = jsonFormat8(Impression)
  implicit val bidRequest = jsonFormat5(BidRequest)
  implicit val banner = jsonFormat4(Banner)
  implicit val bidResponse = jsonFormat5(BidResponse)

}
