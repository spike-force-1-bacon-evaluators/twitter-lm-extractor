package org.sf1bacon.twitterextractor

import java.text.SimpleDateFormat
import java.util.Date

import com.danielasfregola.twitter4s.entities.{Tweet, User}
import org.neo4j.driver.v1.StatementResult
import org.scalatest.FunSuite

/**
  * Created by agapito on 05/02/2017.
  */
class RestaurantTest extends FunSuite {


  val listOfficial = TwitterList("official-twitter-accounts", "Twitter")
  val twitterUser = listOfficial.members.filter(_.screen_name == "Twitter").head

  val listAPITeam = TwitterList("team", "twitterapi")
  val twitterapiUser = listAPITeam.members.filter(_.screen_name == "twitterapi").head

  // scalastyle:off magic.number
  val testRestaurant = Restaurant("ScalaTest", "scalatest", "here", 1, 10, "http://localhost", verified = false)
  // scalastyle:on magic.number

  test("Can get convert User to Restaurant") {
    val test = Restaurant(twitterapiUser)

    assert(
      test.name == "Twitter API" &&
        test.username == "twitterapi" &&
        test.location == "San Francisco, CA" &&
        test.tweets > 3000 &&
        test.followers > 1000000 &&
        test.url.length > 0 &&
        test.verified
    )
  }


  test("Restaurant cypher query created correctly") {

    val time = Utils.timestamp
    val cypherTest = testRestaurant.queryRestaurant(time).replaceAll("\\s", "")
    val correctResult =
      s"""MERGE (r:Restaurant { id: "scalatest", name: "ScalaTest" })
                            ON CREATE SET r.followers = 10,
                                         r.tweets = 1,
                                         r.verified = false,
                                         r.location = "here",
                                         r.url = "http://localhost",
                                         r.added = "$time"
                            ON MATCH SET r.followers = 10,
                                         r.tweets = 1,
                                         r.verified = false,
                                         r.location = "here",
                                         r.url = "http://localhost",
                                         r.updated = "$time"
                        """.replaceAll("\\s", "")
    assert(cypherTest == correctResult)
  }

  test("Can get mentions to user") {
    val test = Restaurant(twitterUser)
    assert(test.getMentions().statuses.nonEmpty)
  }

  test("Mention cypher query created correctly") {
    val timeString = "10-10-2010 10:10:10"
    val time = new SimpleDateFormat("d-MM-yyyy HH:mm:ss").parse(timeString)

    val testTweet = Tweet(created_at = time, id = 1, id_str = "", source = "", text = "Tweet! Tweet!", user = Some(twitterUser))

    val cypherTest = testRestaurant.queryMentions(testTweet).replaceAll("\\s", "")

    val correctResult =
      s"""MERGE (u:User {id: "Twitter",
                         name: "Twitter"})
          MERGE (t:Tweet {id: "Twitter_Sun Oct 10 10:10:10 WEST 2010",
                          text: "Tweet! Tweet!",
                          date: "Sun Oct 10 10:10:10 WEST 2010",
                          written_by:"Twitter"})
          MERGE (d:Date {id: "2010-10-10"})
          MERGE (r:Restaurant {id: "scalatest"})
          MERGE (u)-[:WRITES]->(t)-[:ABOUT]->(r)
          MERGE (t)-[:AT]->(d)
      """.replaceAll("\\s", "")
    assert(cypherTest == correctResult)
  }

  // disable integration tests for now
  //   test(s"Can create restaurant nodes") {
  //     val testRestaurant = Restaurant("ScalaTest", "ScalaTest", "", 0, 0, "", verified = false)
  //     testRestaurant.neo4jMerge()
  //     val search: StatementResult = Neo4jAuth.session.run("""MATCH (t:Restaurant {name:"ScalaTest"}) RETURN t""")
  //     assert(!search.list.isEmpty)
  //     val del = Neo4jAuth.session.run("""MATCH (t:Restaurant {name:"ScalaTest"}) DETACH DELETE t""")
  //   }

}
