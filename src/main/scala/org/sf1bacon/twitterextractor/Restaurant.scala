package org.sf1bacon.twitterextractor

import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{StatusSearch, Tweet, User}
import org.neo4j.driver.v1.StatementResult

import scala.concurrent._

/**
  * Created by agapito on 03/02/2017.
  */
case class Restaurant(name: String,
                      username: String,
                      location: String,
                      tweets: Int,
                      followers: Int,
                      url: String,
                      verified: Boolean) {

  def neo4jMerge: StatementResult =
    Neo4jAuth.session.run(
      s"""MERGE (r:Restaurant { id: "${this.username}", name: "${this.name}", username: "${this.username}" })
         ON CREATE SET r.followers = ${this.followers},
                       r.tweets = ${this.tweets},
                       r.verified = ${this.verified},
                       r.location = "${this.location}",
                       r.url = "${this.url}",
                       r.added = "${Utils.timestamp}"
         ON MATCH SET r.followers = ${this.followers},
                      r.tweets = ${this.tweets},
                      r.verified = ${this.verified},
                      r.location = "${this.location}",
                      r.url = "${this.url}",
                      r.updated = "${Utils.timestamp}"
        """.stripMargin)
}

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
