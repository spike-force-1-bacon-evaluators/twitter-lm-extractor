package org.sf1bacon.twitterextractor

import akka.actor.{ActorSystem, Terminated}
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{AccessToken, ConsumerToken}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future

/**
  * Created by agapito on 03/02/2017.
  */
object TwitterAuth {

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


}
