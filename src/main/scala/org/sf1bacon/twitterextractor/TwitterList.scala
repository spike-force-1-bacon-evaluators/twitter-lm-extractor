package org.sf1bacon.twitterextractor

import com.danielasfregola.twitter4s.entities.{User, Users}

import scala.concurrent._
import scala.concurrent.duration._

/**
  * Created by agapito on 02/02/2017.
  */
case class TwitterList(list: String, username: String) {

  val data: Users = {

    val maxListElements = 5000

    // This is blocking, but we really need to get this before moving on.
    Await.result(TwitterAuth.rest.listMembersBySlugAndOwnerName(
      slug = list,
      owner_screen_name = username,
      count = maxListElements,
      cursor = -1,
      include_entities = true,
      skip_status = false
    ), atMost = 30.seconds)

  }

  // simplify access to users
  val users: Seq[User] = data.users

  def printList(): Unit = {
    println("---------------------+---------------------")
    println("       name          |       username      ")
    println("---------------------+---------------------")
    users.foreach(u => println(f"${u.name}%-20s | ${u.screen_name}%-20s"))
    println("---------------------+---------------------")
  }

  def usernames: List[String] = users.map(u => u.screen_name).toList

}
