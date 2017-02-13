package org.sf1bacon.twitterextractor

import akka.actor.{ActorSystem, Terminated}
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken, StatusSearch, User}
import com.typesafe.config.ConfigFactory

import scala.annotation.tailrec
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}


/**
  * Created by agapito on 03/02/2017.
  */
object TwitterAPI {

  val system: ActorSystem = ActorSystem("TwitterSystem")

  val rest: TwitterRestClient = {
    val config = ConfigFactory.load("twitter.conf")
    val myConsumerKey = config.getString("twitter.consumer.key")
    val myConsumerSecret = config.getString("twitter.consumer.secret")
    val myAccessKey = config.getString("twitter.access.key")
    val myAccessSecret = config.getString("twitter.access.secret")

    val consumerToken = ConsumerToken(key = myConsumerKey, secret = myConsumerSecret)
    val accessToken = AccessToken(key = myAccessKey, secret = myAccessSecret)

    new TwitterRestClient(consumerToken, accessToken)(system)
  }

  def terminate(): Future[Terminated] = {
   system.terminate()
  }

  // scalastyle:off magic.number
  // The max results returned by the search entrypoint on the REST API is 100
  @tailrec
  def getMentions(user: String, maxTweets: Int = 100): StatusSearch = {
    // scalastyle:on magic.number

    val result: Try[StatusSearch] = Try(
      Await.result(TwitterAPI.rest.searchTweet(
        query = s"@$user", count = maxTweets),
        atMost = 30.seconds)
    )

    result match {
      case Success(r) => // return r
        r
      case Failure(e) =>
        val snooze = 16
        println(s"[WARN] Rate limit reached. Sleeping for $snooze minutes before retrying.")
        Utils.sleeper(snooze)
        getMentions(user, maxTweets)
    }
  }

  def getMembers(username: String, list: String): List[User] = {

    val maxListElements = 5000

    Await.result(TwitterAPI.rest.listMembersBySlugAndOwnerName(
      slug = list,
      owner_screen_name = username,
      count = maxListElements,
      cursor = -1,
      include_entities = true,
      skip_status = false
    ), atMost = 30.seconds).users.toList.sortBy(_.screen_name.toLowerCase)
  }

}
