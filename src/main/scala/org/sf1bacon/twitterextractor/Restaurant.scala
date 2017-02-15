package org.sf1bacon.twitterextractor

import com.danielasfregola.twitter4s.entities.User
import com.google.maps.PlaceDetailsRequest
import com.google.maps.model.PlaceDetails
import org.sf1bacon.twitterextractor.GoogleAPI._

/**
  * Created by agapito on 03/02/2017.
  */
case class Restaurant(name: String,
                      username: String,
                      location: String,
                      tweets: Int,
                      followers: Int,
                      url: String,
                      verified: Boolean,
                      latlong: (Double, Double),
                      googleID: String,
                      twitterID: String)

object Restaurant {

  // set constructor for twitter4s User type
  def apply(user: User): Restaurant = {

    val name = user.name
    val username = user.screen_name
    val address = user.location.getOrElse("")

    //println(s"############### $username #################")
    println(s"[INFO] Searching for'$name ($username), $address'")

    val searchResults = nestedSearch(name, address, username)

    val latlong = searchResults match {
      case Some(data) =>
        val googlePlace = data.results.head
        val placeInfo: PlaceDetails = new PlaceDetailsRequest(gContext).placeId(googlePlace.placeId).await()
        val geolocation = (placeInfo.geometry.location.lat, placeInfo.geometry.location.lng)
        //println(s"[INFO] Searched for '$name ($username), $address' -> Found '${googlePlace.name}, ${googlePlace.vicinity}' @ $geolocation")
        println(s"[INFO] Found '${googlePlace.name}, ${googlePlace.vicinity}' @ $geolocation")
        geolocation
      case None =>
        //println(s"[INFO] Searched for '$name ($username), $address -> Not found. Returning (0.0,0.0)")
        println(s"[INFO] Not found. Using (0.0,0.0)")
        (0.0, 0.0)
    }

    val googleID = searchResults match {
      case Some(data) => data.results.head.placeId
      case None => "noID"
    }

    new Restaurant(
      user.name,
      user.screen_name,
      user.location.getOrElse(""),
      user.statuses_count,
      user.followers_count,
      user.url.getOrElse(""),
      user.verified,
      latlong,
      googleID,
      user.id_str
    )

  }

}

