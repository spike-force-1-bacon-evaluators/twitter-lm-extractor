package org.sf1bacon.twitterextractor

/**
  * Created by agapito on 02/02/2017.
  */
object Main extends App {

  // get data from twitter restaurant list
  val restaurants = TwitterAPI.getMembers("londoneating", "london-restaurants").map(Restaurant(_))
  println(s"[INFO] Got data for ${restaurants.size} restaurants.")

  // insert restaurants in neo4j
  for (place <- restaurants) {
    Neo4jAPI.session.run(Neo4jAPI.cypherRestaurant(place, Utils.timestamp))
    println(s"[INFO] Restaurant '${place.username}' in database.")
  }
  val searchRestaurants = Neo4jAPI.session.run("""MATCH (t:Restaurant) RETURN t""")
  println(s"[INFO] There are now ${searchRestaurants.list.size} restaurants in neo4j database.")

  // insert data in neo4j
  for (place <- restaurants) {
    val mentions = TwitterAPI.getMentions(place.username)
    println(s"[INFO] Got ${mentions.statuses.size} mentions for ${place.username}")
    mentions.statuses.foreach(t => Neo4jAPI.session.run(Neo4jAPI.cypherMention(place, t)))
  }

  // cleanup
  Neo4jAPI.session.close()
  TwitterAPI.terminate()

}

