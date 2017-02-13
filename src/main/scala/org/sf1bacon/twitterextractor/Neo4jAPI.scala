package org.sf1bacon.twitterextractor

import org.sf1bacon.twitterextractor.Utils._
import com.danielasfregola.twitter4s.entities.Tweet
import com.typesafe.config.{Config, ConfigFactory}
import org.neo4j.driver.v1.{AuthTokens, GraphDatabase, Session}

/**
  * Created by agapito on 03/02/2017.
  */
object Neo4jAPI {

  val config: Config = ConfigFactory.load("neo4j.conf")
  val host: String = config.getString("neo4j.host")
  val boltport: String = config.getString("neo4j.boltport")
  val username: String = config.getString("neo4j.username")
  val password: String = config.getString("neo4j.password")

  def session(): Session = {

    val tokens = AuthTokens.basic(s"$username", s"$password")

    // Connect to database
    val driver = GraphDatabase.driver(s"bolt://$host:$boltport", tokens)
    driver.session
  }

  def cypherMention(restaurant: Restaurant, tweet: Tweet): String = {

    val user_username = if (tweet.user.isDefined) sanitize(tweet.user.get.screen_name) else "bacon_undefined_username"
    val user_id = if (tweet.user.isDefined) tweet.user.get.id_str else "bacon_undefined_id"
    val user_name = if (tweet.user.isDefined) sanitize(tweet.user.get.name) else "bacon_undefined_name"

    s"""MERGE (u:User {id: "${user_username}_${user_id}",
       |               username: "$user_username",
       |               name: "$user_name"})
       |MERGE (t:Tweet {id: "${user_username}_${tweet.created_at}_${tweet.id}",
       |                text: "${sanitize(tweet.text)}",
       |                date: "${tweet.created_at}",
       |                written_by: "$user_username"})
       |MERGE (d:Date {id: "${twitterDateToDay(tweet.created_at.toString)}"})
       |MERGE (r:Restaurant {id: "${restaurant.username}"})
       |MERGE (u)-[:WRITES]->(t)-[:ABOUT]->(r)
       |MERGE (t)-[:AT]->(d)
       |""".stripMargin

  }

  def cypherRestaurant(restaurant: Restaurant, modtime: String): String =
    s"""MERGE (r:Restaurant { id: "${restaurant.username}", name: "${restaurant.name}" })
       |ON CREATE SET r.followers = ${restaurant.followers},
       |              r.tweets = ${restaurant.tweets},
       |              r.verified = ${restaurant.verified},
       |              r.location = "${restaurant.location}",
       |              r.url = "${restaurant.url}",
       |              r.added = "$modtime"
       |ON MATCH SET r.followers = ${restaurant.followers},
       |             r.tweets = ${restaurant.tweets},
       |             r.verified = ${restaurant.verified},
       |             r.location = "${restaurant.location}",
       |             r.url = "${restaurant.url}",
       |             r.updated = "$modtime"
       |""".stripMargin


}
