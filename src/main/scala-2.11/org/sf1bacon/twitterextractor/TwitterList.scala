package org.sf1bacon.twitterextractor

import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.Users

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

/**
  * Created by agapito on 02/02/2017.
  */
case class TwitterList(client: TwitterRestClient, list: String, username: String) {

  // This is blocking, but we really need to get this before moving on.
  val data: Users = Await.result(
    client.listMembersBySlugAndOwnerName(
      slug = list,
      owner_screen_name = username,
      count = 5000,
      cursor = -1,
      include_entities = true,
      skip_status = false
    ), 15 seconds)

  def printList(): Unit = {
    println("---------------------+---------------------")
    println("       name          |       username      ")
    println("---------------------+---------------------")
    data.users.foreach(u => println(f"${u.name}%-20s | ${u.screen_name}%-20s"))
    println("---------------------+---------------------")
  }

  def usernames: List[String] = data.users.map(u => u.screen_name).toList

}
