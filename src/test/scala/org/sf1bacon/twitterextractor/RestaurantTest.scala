package org.sf1bacon.twitterextractor

import org.neo4j.driver.v1.StatementResult
import org.scalatest.FunSuite

/**
  * Created by agapito on 05/02/2017.
  */
class RestaurantTest extends FunSuite {

  // disable integration tests for now
  /***
  test(s"Can create restaurant nodes") {
    val neo4j = Neo4jAuth.session()
    val testRestaurant = Restaurant("ScalaTest", "ScalaTest", "", 0, 0, "", verified = false)

    val create = neo4j.run(testRestaurant.cypherString)
    val search: StatementResult = neo4j.run("""MATCH (t:Restaurant {name:"ScalaTest"}) RETURN t""")

    assert(!search.list.isEmpty)

    val del = neo4j.run("""MATCH (t:Restaurant {name:"ScalaTest"}) DETACH DELETE t""")
  }
  ***/

}
