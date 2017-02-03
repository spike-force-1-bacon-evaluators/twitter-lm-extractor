package org.sf1bacon.twitterextractor

import com.typesafe.config.{Config, ConfigFactory}
import org.neo4j.driver.v1.{AuthTokens, GraphDatabase, Session}

/**
  * Created by agapito on 03/02/2017.
  */
object Neo4jAuth {

  val config: Config = ConfigFactory.load("neo4j.conf")
  val host: String = config.getString("neo4j.host")
  val boltport: String = config.getString("neo4j.boltport")
  val username: String = config.getString("neo4j.username")
  val password: String = config.getString("neo4j.password")

  val session: Session = {

    val tokens = AuthTokens.basic(s"$username", s"$password")

    // Connect to database
    val driver = GraphDatabase.driver(s"bolt://$host:$boltport", tokens)
    driver.session
  }


}
