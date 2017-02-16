package org.sf1bacon.twitterextractor

import org.sf1bacon.twitterextractor.Utils._
import com.danielasfregola.twitter4s.entities.Tweet
import com.typesafe.config.{Config, ConfigFactory}
import org.neo4j.driver.v1.{AuthTokens, GraphDatabase, Session}

/**
  * Created by agapito on 03/02/2017.
  */

/**
  * Interface with Neo4j using `org.neo4j.driver.v1`. To use this object you must have
  * a configuration file named `neo4j.conf` located in `src/main/resources/` with the following
  * information:
  * {{{
  *   neo4j {
  *     # the boltport is typically 7687
  *     boltport = "7687"
  *     host = "NEO4J SERVER HOSTNAME OR IP"
  *     username = "YOUR NEO4J USERNAME"
  *     password = "YOUR NEO4J PASSWORD"
  *   }
  * }}}
  *
  */
object Neo4jAPI {

  /**
    * Neo4j config file
    */
  val config: Config = ConfigFactory.load("neo4j.conf")

  /**
    * Neo4j server hostname
    */
  val host: String = config.getString("neo4j.host")

  /**
    * Neo4j server bolt port
    */
  val boltport: String = config.getString("neo4j.boltport")

  /**
    * Neo4j server username
    */
  val username: String = config.getString("neo4j.username")

  /**
    * Neo4j server password
    */
  val password: String = config.getString("neo4j.password")

  /**
    * Authenticates and instantiates a Neo4J driver session
    *
    * @return Neo4J driver session
    */
  def session(): Session = {

    val tokens = AuthTokens.basic(s"$username", s"$password")

    // Connect to database
    val driver = GraphDatabase.driver(s"bolt://$host:$boltport", tokens)
    driver.session
  }

  /**
    * Creates a a correctly formatted cypher query which can be used to insert
    * a Tweet about a [[Restaurant]] in the Neo4j database
    *
    * @param restaurant a [[Restaurant]] mentioned by this Tweet
    * @param tweet      a Tweet object from `import com.danielasfregola.twitter4s.entities.Tweet`
    * @return a string with the corresponding cypher string
    */
  def cypherMention(restaurant: Restaurant, tweet: Tweet): String = {

    val user_username = if (tweet.user.isDefined) sanitize(tweet.user.get.screen_name) else "bacon_undefined_username"
    val user_id = if (tweet.user.isDefined) tweet.user.get.id_str else "bacon_undefined_id"
    val user_name = if (tweet.user.isDefined) sanitize(tweet.user.get.name) else "bacon_undefined_name"

    s"""MERGE (u:User {id: "${user_username}_${user_id}"})
       |ON CREATE SET  u.username = "$user_username",
       |               u.name = "$user_name"
       |ON MATCH SET  u.username = "$user_username",
       |              u.name = "$user_name"
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

  /**
    * Creates a a correctly formatted cypher query which can be used to insert
    * a [[Restaurant]] in the Neo4j database
    *
    * @param restaurant a [[Restaurant]] mentioned by this Tweet
    * @param modtime    a string with the modification/creation time
    * @return a string with the corresponding cypher string
    */
  def cypherRestaurant(restaurant: Restaurant, modtime: String): String =
    s"""MERGE (r:Restaurant { id: "${restaurant.username}" })
       |ON CREATE SET r.followers = ${restaurant.followers},
       |              r.name = "${restaurant.name}",
       |              r.tweets = ${restaurant.tweets},
       |              r.verified = ${restaurant.verified},
       |              r.location = "${restaurant.location}",
       |              r.url = "${restaurant.url}",
       |              r.added = "$modtime",
       |              r.googleID = "${restaurant.googleID}",
       |              r.twitterID = "${restaurant.twitterID}",
       |              r.lat = ${restaurant.latlong._1},
       |              r.lng = ${restaurant.latlong._2}
       |ON MATCH SET r.followers = ${restaurant.followers},
       |             r.name = "${restaurant.name}",
       |             r.tweets = ${restaurant.tweets},
       |             r.verified = ${restaurant.verified},
       |             r.location = "${restaurant.location}",
       |             r.url = "${restaurant.url}",
       |             r.updated = "$modtime",
       |             r.googleID = "${restaurant.googleID}",
       |             r.twitterID = "${restaurant.twitterID}",
       |             r.lat = ${restaurant.latlong._1},
       |             r.lng = ${restaurant.latlong._2}
       |""".stripMargin

}
