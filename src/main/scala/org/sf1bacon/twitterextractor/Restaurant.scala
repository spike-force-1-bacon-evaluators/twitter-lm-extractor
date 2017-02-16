package org.sf1bacon.twitterextractor

import com.danielasfregola.twitter4s.entities.User
import com.google.maps.PlaceDetailsRequest
import com.google.maps.model.PlaceDetails
import org.sf1bacon.twitterextractor.GoogleAPI._

/**
  * Created by agapito on 03/02/2017.
  */

/**
  * The Restaurant class stores the twitter data for each restaurant, along with the corresponding
  * geographical data extracted from google places.
  *
  * @param name      name of the restaurant
  * @param username  twitter username of the restaurant
  * @param location  address of the restaurant (as stated in twitter)
  * @param tweets    number of tweets made by this restaurant
  * @param followers number of followers this restaurant's twitter account has
  * @param url       url extracted from twitter
  * @param verified  twitter account verified status
  * @param latlong   geographic coordinates of the restaurant (set to (0.0, 0.0) if not found)
  * @param googleID  google unique placeId matched with this restaurant (set to "noID" if not found)
  * @param twitterID twitter account unique id
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

  /**
    * Constructor for the [[Restaurant]] class from User. The User information obtained from Twitter is used
    * to match this user with the corresponding Google Places data, from which the geographical location
    * and Google places unique id is extracted.
    *
    * @param user `com.danielasfregola.twitter4s.entities.User` object
    * @return new Restaurant
    */
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

