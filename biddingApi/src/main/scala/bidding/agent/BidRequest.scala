package bidding.agent

case class BidRequest(id: String, imp: Option[List[Impression]], site: Site, user: Option[User], device: Option[Device])
case class Impression(id: String, wmin: Option[Int], wmax: Option[Int], w: Option[Int], hmin: Option[Int], hmax: Option[Int], h: Option[Int], bidFloor: Option[Double])
case class Site(id: String, domain: String)
case class User(id: String, geo: Option[Geo])
case class Device(id: String, geo: Option[Geo])
case class Geo(country: Option[String])

