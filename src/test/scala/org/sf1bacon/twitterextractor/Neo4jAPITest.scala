package org.sf1bacon.twitterextractor

import java.text.SimpleDateFormat
import java.util.{Calendar, TimeZone}

import com.danielasfregola.twitter4s.entities.{ProfileImage, Tweet, User}
import org.neo4j.driver.v1.StatementResult
import org.scalatest.FunSuite

/**
  * Created by agapito on 04/02/2017.
  */
class Neo4jAPITest extends FunSuite {

  ///// disable integration tests for now
  //  test(s"Neo4j server authentication working (bolt://${Neo4jAPI.host}:${Neo4jAPI.boltport}).") {
  //    try {
  //      Neo4jAPI.session.run("""MATCH (n) RETURN count(n)""")
  //    } catch {
  //      case _: Throwable => fail(s"Could not connect to neo4j server. Exception thrown.")
  //    }
  //  }
  //
  //  test(s"Can run Neo4j queries") {
  //    Neo4jAPI.session.run("MERGE (t:ScalaTest)")
  //    val test: StatementResult = Neo4jAPI.session.run("MATCH (t:ScalaTest) RETURN t")
  //
  //    assert(!test.list.isEmpty)
  //
  //  Neo4jAPI.session.run("MATCH (t:ScalaTest) DETACH DELETE t")
  // }

  test("Restaurant cypher query created correctly") {
    // scalastyle:off magic.number
    val testRestaurant = new Restaurant("ScalaTest", "scalatest", "here", 1, 10,
      "http://localhost", verified = false, (1.0, 2.0), "googleID", "twitterID")
    // scalastyle:on magic.number

    val testTime = Utils.timestamp
    val cypherTest = Neo4jAPI.cypherRestaurant(testRestaurant, testTime)
    val correctResult =
      s"""MERGE (r:Restaurant { id: "scalatest" })
         |ON CREATE SET r.followers = 10,
         |              r.name = "ScalaTest",
         |              r.tweets = 1,
         |              r.verified = false,
         |              r.location = "here",
         |              r.url = "http://localhost",
         |              r.added = "$testTime",
         |              r.googleID = "googleID",
         |              r.twitterID = "twitterID",
         |              r.lat = 1.0,
         |              r.lng = 2.0
         |ON MATCH SET r.followers = 10,
         |             r.name = "ScalaTest",
         |             r.tweets = 1,
         |             r.verified = false,
         |             r.location = "here",
         |             r.url = "http://localhost",
         |             r.updated = "$testTime",
         |             r.googleID = "googleID",
         |             r.twitterID = "twitterID",
         |             r.lat = 1.0,
         |             r.lng = 2.0
         |""".stripMargin
    assert(cypherTest == correctResult)
  }

  test("Mention cypher query created correctly") {
    val timeString = "01-02-2010 10:20:30"

    // TODO: Fix SimpleDateFormat time zone
    val timeZone = Calendar.getInstance().getTimeZone.getDisplayName(false, TimeZone.SHORT)

    val testTime = new SimpleDateFormat("d-MM-yyyy HH:mm:ss").parse(timeString)
    val testProfileImage = ProfileImage(mini = "", normal = "", bigger = "", default = "")

    // scalastyle:off magic.number
    val testUser = User(
      created_at = testTime,
      favourites_count = 1,
      followers_count = 2,
      friends_count = 3,
      id = 4,
      id_str = "4",
      lang = "",
      listed_count = 5,
      name = "TestUser",
      profile_background_color = "",
      profile_background_image_url = "",
      profile_background_image_url_https = "",
      profile_image_url = testProfileImage,
      profile_image_url_https = testProfileImage,
      profile_link_color = "",
      profile_sidebar_border_color = "",
      profile_sidebar_fill_color = "",
      profile_text_color = "",
      screen_name = "testuser",
      statuses_count = 6
    )

    val testRestaurant = new Restaurant("ScalaTest", "scalatest", "here", 1, 10,
      "http://localhost", verified = false, (1.0, 2.0), "googleID", "twitterID")
    val testTweet = Tweet(created_at = testTime, id = 1, id_str = "", source = "", text = "Tweet! Tweet!", user = Some(testUser))
    // scalastyle:on magic.number

    val cypherTest = Neo4jAPI.cypherMention(testRestaurant, testTweet)

    //FIXME: Remove timeZone in this string
    val correctResult =
      s"""MERGE (u:User {id: "testuser_4"})
         |ON CREATE SET  u.username = "testuser",
         |               u.name = "TestUser"
         |ON MATCH SET  u.username = "testuser",
         |              u.name = "TestUser"
         |MERGE (t:Tweet {id: "testuser_Mon Feb 01 10:20:30 $timeZone 2010_1",
         |                text: "Tweet Tweet",
         |                date: "Mon Feb 01 10:20:30 $timeZone 2010",
         |                written_by: "testuser"})
         |MERGE (d:Date {id: "2010-02-01"})
         |MERGE (r:Restaurant {id: "scalatest"})
         |MERGE (u)-[:WRITES]->(t)-[:ABOUT]->(r)
         |MERGE (t)-[:AT]->(d)
         |""".stripMargin
    assert(cypherTest == correctResult)
  }

}
