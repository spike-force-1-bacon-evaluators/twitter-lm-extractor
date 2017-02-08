package org.sf1bacon.twitterextractor

import java.text.SimpleDateFormat

import com.danielasfregola.twitter4s.entities.{ProfileImage, User}
import org.scalatest.FunSuite

/**
  * Created by agapito on 05/02/2017.
  */
class RestaurantTest extends FunSuite {


  test("Can get convert User to Restaurant") {
    val timeString = "01-02-2010 10:20:30"
    val testTime = new SimpleDateFormat("d-MM-yyyy HH:mm:ss").parse(timeString)
    val testProfileImage = ProfileImage(mini = "", normal = "", bigger = "", default = "")
    // scalastyle:off magic.number
    val testUser = User(
      created_at = testTime,
      favourites_count = 1,
      followers_count = 2,
      friends_count = 3,
      id = 4,
      id_str = "",
      lang = "",
      listed_count = 5,
      name = "ScalaTest",
      profile_background_color = "",
      profile_background_image_url = "",
      profile_background_image_url_https = "",
      profile_image_url = testProfileImage,
      profile_image_url_https = testProfileImage,
      profile_link_color = "",
      profile_sidebar_border_color = "",
      profile_sidebar_fill_color = "",
      profile_text_color = "",
      screen_name = "scalatest",
      statuses_count = 6,
      verified = false,
      url = Option("http://localhost"),
      location = Option("here")
    )

    val testRestaurant = Restaurant(testUser)
    // scalastyle:on magic.number

    assert(
        testRestaurant.name == "ScalaTest" &&
        testRestaurant.username == "scalatest" &&
        testRestaurant.location == "here" &&
        testRestaurant.tweets == 6 &&
        testRestaurant.followers == 2 &&
        testRestaurant.url == "http://localhost" &&
        ! testRestaurant.verified
    )
  }
}
