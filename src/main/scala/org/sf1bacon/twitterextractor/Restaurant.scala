package org.sf1bacon.twitterextractor

import com.danielasfregola.twitter4s.entities.{StatusSearch, Tweet, User}

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

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


  def queryRestaurant(modtime: String): String =
    s"""MERGE (r:Restaurant { id: "${this.username}", name: "${this.name}" })
        ON CREATE SET r.followers = ${this.followers},
                      r.tweets = ${this.tweets},
                      r.verified = ${this.verified},
                      r.location = "${this.location}",
                      r.url = "${this.url}",
                      r.added = "$modtime"
        ON MATCH SET r.followers = ${this.followers},
                     r.tweets = ${this.tweets},
                     r.verified = ${this.verified},
                     r.location = "${this.location}",
                     r.url = "${this.url}",
                     r.updated = "$modtime"
    """.stripMargin

  def queryMentions(t: Tweet): String = {

    val user_username = if (t.user.isDefined) t.user.get.screen_name else "bacon_undefined_username"
    val user_name = if (t.user.isDefined) t.user.get.name else "bacon_undefined_name"
    val tweet_text = t.text.replace("\"", "'")

    s"""MERGE (u:User {id: "$user_username",
                       name: "$user_name"})
        MERGE (t:Tweet {id: "${user_username}_${t.created_at}",
                        text: "$tweet_text",
                        date: "${t.created_at}",
                        written_by:"$user_username"})
        MERGE (d:Date {id: "${Utils.twitterDateToDay(t.created_at.toString)}"})
        MERGE (r:Restaurant {id: "${this.username}"})
        MERGE (u)-[:WRITES]->(t)-[:ABOUT]->(r)
        MERGE (t)-[:AT]->(d)
    """.stripMargin
  }

  def neo4jMerge(): Unit = {
    Neo4jAuth.session.run(queryRestaurant(Utils.timestamp))
    println(s"[INFO] Restaurant '${this.username}' in database.")
  }

  // scalastyle:off magic.number
  // The max results returned by the search entrypoint on the REST API is 100
  def getMentions(maxTweets: Int = 100): StatusSearch = {
    // scalastyle:on magic.number

    val result: Try[StatusSearch] = Try(
      Await.result(TwitterAuth.rest.searchTweet(
        query = s"@${this.username}", count = maxTweets),
        atMost = 30.seconds)
    )

    result match {
      case Success(r) => // return r
        r
      case Failure(e) =>
        val snooze = 16
        println(s"[WARN] Rate limit reached. Sleeping for $snooze minutes before retrying.")
        Utils.sleeper(snooze)
        getMentions(maxTweets)
    }
  }

  def neo4jSetMentions(): Unit = {

    val mentions = getMentions()
    println(s"[INFO] Got ${mentions.statuses.size} mentions for ${this.username}")

    mentions.statuses.foreach(t => Neo4jAuth.session.run(queryMentions(t)))
  }
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

