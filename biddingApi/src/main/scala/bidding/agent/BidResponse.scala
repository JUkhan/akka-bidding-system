package bidding.agent
//trait NoContent
case class BidResponse(id: String, bidRequestId: String, price: Double, adid: Option[String], banner: Option[Banner])
//case class HasNoContent(msg:String) extends NoContent
