package org.sf1bacon.twitterextractor

/**
  * Created by agapito on 02/02/2017.
  */
object Main extends App {

  // get data from twitter restaurant list
  val restaurants = TwitterList("london-restaurants", "londoneating").users.map(Restaurant(_))
  println(s"[INFO] Got data for ${restaurants.size} restaurants.")

  // insert in neo4j db
  val insertResult = restaurants.foreach(_.neo4jMerge)
  val searchResult = Neo4jAuth.session.run("""MATCH (t:Restaurant) RETURN t""")
  println(s"[INFO] There are now ${searchResult.list.size} restaurants in neo4j database.")

  // cleanup
  Neo4jAuth.session.close()
  TwitterAuth.terminate()
}

