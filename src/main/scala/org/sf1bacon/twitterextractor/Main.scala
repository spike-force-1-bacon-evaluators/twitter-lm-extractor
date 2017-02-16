package org.sf1bacon.twitterextractor

/**
  * Created by agapito on 02/02/2017.
  */

/**
  * Main loop of the Twitter extractor. This does the following:
  *
  * 1. Opens a connection to the Neo4j rest API
  * 2. Gets a list of Restaurants from a specified Twitter list
  * 3. Creates a [[Restaurant]] class for each place
  * 4. Insert each [[Restaurant]]'s data in Neo4j
  * 5. Search for Twitter mentions to each [[Restaurant]]
  * 6. Insert the data of each Twitt in Neo4j
  * 7. Terminate the connection with the Neo4j and the Twitter REST APIs
  *
  */
object Main extends App {

  // start neo4j session
  val neo4j = Neo4jAPI.session()

  // get data from twitter restaurant list
  val restaurants = TwitterAPI.getMembers("londoneating", "london-restaurants").map(Restaurant(_))
  println(s"[INFO] Got data for ${restaurants.size} restaurants.")

  // insert restaurants in neo4j
  for (place <- restaurants) {
    neo4j.run(Neo4jAPI.cypherRestaurant(place, Utils.timestamp))
    println(s"[INFO] Restaurant '${place.username}' in database.")
  }
  val searchRestaurants = neo4j.run("""MATCH (t:Restaurant) RETURN t""")
  println(s"[INFO] There are now ${searchRestaurants.list.size} restaurants in neo4j database.")

  // insert data in neo4j
  for (place <- restaurants) {
    val mentions = TwitterAPI.getMentions(place.username)
    println(s"[INFO] Got ${mentions.statuses.size} mentions for ${place.username}")
    mentions.statuses.foreach(t => neo4j.run(Neo4jAPI.cypherMention(place, t)))
  }

  // cleanup
  neo4j.close()
  TwitterAPI.terminate()

}

