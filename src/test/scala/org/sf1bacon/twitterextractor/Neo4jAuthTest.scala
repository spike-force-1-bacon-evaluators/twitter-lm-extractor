package org.sf1bacon.twitterextractor

import org.neo4j.driver.v1.StatementResult
import org.scalatest.FunSuite

/**
  * Created by agapito on 04/02/2017.
  */
class Neo4jAuthTest extends FunSuite {

  test(s"Neo4J server authentication working (bolt://${Neo4jAuth.host}:${Neo4jAuth.boltport}).") {
    try {
      val neo4j = Neo4jAuth.session()
      neo4j.run(":server status")
    } catch {
      case _: Throwable => fail(s"Could not connect to neo4j server. Exception thrown.")
    }
  }

  test(s"Can run Neo4j queries") {
    val neo4j = Neo4jAuth.session()
    neo4j.run("MERGE (Test:ScalaTest)")
    val test: StatementResult = neo4j.run("MATCH (t:ScalaTest) RETURN t")

    assert(!test.list.isEmpty)

    val del = neo4j.run("MATCH (t:ScalaTest) DETACH DELETE t")
    neo4j.close()
  }

}
