package org.sf1bacon.twitterextractor

/**
  * Created by agapito on 02/02/2017.
  */
object Main extends App {

  // open sessions
  val neo4j = Neo4jAuth.session()
  val twitter = TwitterAuth.rest()

  // get data from twitter restaurant list
  val restaurants = TwitterList(twitter, "london-restaurants", "londoneating").users.map(Restaurant(_))
  println(s"[INFO] Got data for ${restaurants.size} restaurants.")

  // insert in neo4j db
  val insertResult = restaurants.map(_.cypherString).foreach(neo4j.run)
  val searchResult = neo4j.run("""MATCH (t:Restaurant) RETURN t""")
  println(s"[INFO] There are now ${searchResult.list.size} restaurants in neo4j database.")

  // cleanup
  neo4j.close()
  TwitterAuth.terminate()
}

