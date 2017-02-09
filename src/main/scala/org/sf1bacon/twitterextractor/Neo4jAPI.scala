package org.sf1bacon.twitterextractor

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

    val user_username = if (tweet.user.isDefined) tweet.user.get.screen_name else "bacon_undefined_username"
    val user_name = if (tweet.user.isDefined) tweet.user.get.name else "bacon_undefined_name"
    val tweet_text = tweet.text.replace("\"", "'")
                               .replace("\\","")

    s"""MERGE (u:User {id: "$user_username",
       |               name: "$user_name"})
       |MERGE (t:Tweet {id: "${user_username}_${tweet.created_at}",
       |                text: "$tweet_text",
       |                date: "${tweet.created_at}",
       |                written_by: "$user_username"})
       |MERGE (d:Date {id: "${Utils.twitterDateToDay(tweet.created_at.toString)}"})
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
