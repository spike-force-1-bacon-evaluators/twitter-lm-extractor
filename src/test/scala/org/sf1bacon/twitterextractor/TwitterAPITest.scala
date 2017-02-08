package org.sf1bacon.twitterextractor

import org.scalatest.FunSuite
import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by agapito on 03/02/2017.
  */
class TwitterAPITest extends FunSuite {

  test("Twitter REST API autentication working") {
    assert(Await.result(TwitterAPI.rest.verifyCredentials(), 15.seconds).screen_name == "filipe_agapito")
  }

  test("Can get mentions to user") {
    assert(TwitterAPI.getMentions("Twitter").statuses.nonEmpty)
  }

  test("Twitter list data fetched.") {
    assert(TwitterAPI.getMembers("twitterapi", "team").nonEmpty)
  }

  test("Usernames correctly extracted.") {
    assert(TwitterAPI.getMembers("twitterapi", "team").map(u => u.screen_name).contains("twitterapi"))
  }

}
