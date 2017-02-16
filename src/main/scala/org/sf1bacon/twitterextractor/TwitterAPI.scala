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

/**
  * Interface for the Twitter REST API, using `com.danielasfregola.twitter4s.TwitterRestClient`
  */
object TwitterAPI {

  /**
    * Akka ActorSystem used by the Twitter API
    */
  val system: ActorSystem = ActorSystem("TwitterSystem")

  /**
    * TwitterRestClient instance. The configuration is done by reading the `twitter.conf` file,
    * located in `src/main/resources`. This file should have the following structure:
    * {{{
    *   twitter {
    *     consumer {
    *         key = "YOUR CONSUMER KEY"
    *         secret = "YOUR CONSUMER SECRET"
    *     }
    *     access {
    *       key = "YOUR ACCESS KEY"
    *       secret = "YOUR ACCESS SECRET"
    *     }
    *   }
    * }}}
    */
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

  /**
    * Terminate the TwitterRestClient Akka ActorSystem
    *
    * @return Future[Terminated] case class
    */
  def terminate(): Future[Terminated] = {
    system.terminate()
  }

  /**
    * Returns mentions to a Twitter username. Twitter's REST API imposes hard limits on the number of searches.
    * The function uses `Try[]` to return the results on `Success`. On `Failure` it forces a `Thread.sleep` for
    * 16 minutes before retrying.
    *
    * @param user      username to search for
    * @param maxTweets the max results returned by the search entrypoint on the REST API (default is 100)
    * @return `com.danielasfregola.twitter4s.entities.StatusSearch` object with results
    */
  @tailrec
  // scalastyle:off magic.number
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

  /**
    * Extracts the members of a Twitter list
    *
    * @param username Twitter username of the list owner
    * @param list name of the list to extract
    * @return list of `com.danielasfregola.twitter4s.entities.User` objects for each member of the list
    */
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
