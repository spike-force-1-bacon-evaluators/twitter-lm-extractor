package org.sf1bacon.twitterextractor

import com.google.maps.model._
import com.google.maps.{GeoApiContext, NearbySearchRequest}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
  * Created by agapito on 09/02/2017.
  */
object GoogleAPI {

  val gContext: GeoApiContext = {

    val config = ConfigFactory.load("google.conf")
    val myAPIKey = config.getString("google.key")
    val queriesPerSecond = 5
    val timeLimit = 30

    new GeoApiContext().setApiKey(myAPIKey)
      .disableRetries()
      .setConnectTimeout(timeLimit, SECONDS)
      .setReadTimeout(timeLimit, SECONDS)
      .setWriteTimeout(timeLimit, SECONDS)
      .setQueryRateLimit(queriesPerSecond)
  }

  def searchFor(text: String,
                // default center is Trafalgar square
                center: (Double, Double) = (51.508052, -0.128037),
                // default search radius is 20 miles
                // scalastyle:off magic.number
                radius: Int = 32000
                // scalastyle:on magic.number
               ): PlacesSearchResponse = {

    val search = new NearbySearchRequest(gContext)
      .language("en")
      .keyword(s"$text")
      .location(new LatLng(center._1, center._2))
      .radius(radius)
      //.`type`(PlaceType.RESTAURANT)
      .rankby(RankBy.PROMINENCE).await()

    search
  }

  def nestedSearch(name: String,
                   address: String,
                   username: String
                  ): Option[PlacesSearchResponse] = {

    //TODO: refactor this ugly code
    // scalastyle:off return
    val search1 = searchFor(s"$name $address restaurant")
    if (!search1.results.isEmpty) {
      return Some(search1)
    }
    val search2 = searchFor(s"$name restaurant")
    if (!search2.results.isEmpty) {
      return Some(search2)
    }
    val search3 = searchFor(s"$username restaurant")
    if (!search3.results.isEmpty) {
      return Some(search3)
    }
    if (Utils.sanitize(address.toLowerCase.replace("london", "").replace("uk", "")).length > 13) {
      val search4 = searchFor(s"$address restaurant")
      if (!search4.results.isEmpty) {
        return Some(search4)
      }
    }
    return None
    // scalastyle:on return
  }

}
