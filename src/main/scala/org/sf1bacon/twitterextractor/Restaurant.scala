package org.sf1bacon.twitterextractor

import com.danielasfregola.twitter4s.entities.User

/**
  * Created by agapito on 03/02/2017.
  */
case class Restaurant(name: String,
                      username: String,
                      location: String,
                      tweets: Int,
                      followers: Int,
                      url: String,
                      verified: Boolean)

object Restaurant {

  // set constructor for twitter4s User type
  def apply(user: User): Restaurant =
    new Restaurant(
      user.name,
      user.screen_name,
      user.location.getOrElse(""),
      user.statuses_count,
      user.followers_count,
      user.url.getOrElse(""),
      user.verified
    )

}

