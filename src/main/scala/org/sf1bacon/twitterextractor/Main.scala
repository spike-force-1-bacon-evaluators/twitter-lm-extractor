package org.sf1bacon.twitterextractor


/**
  * Created by agapito on 02/02/2017.
  */
object Main extends App {

  val restClient = TwitterAuth.rest()

  val restaurants = TwitterList(restClient, "london-restaurants", "londoneating")
  val handles =  restaurants.usernames

  restaurants.printList()
  handles.foreach(println)

  TwitterAuth.terminate()
}

