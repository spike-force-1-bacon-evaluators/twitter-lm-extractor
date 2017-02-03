package org.sf1bacon.twitterextractor

import com.danielasfregola.twitter4s.TwitterRestClient
import org.scalatest.FunSuite

/**
  * Created by agapito on 03/02/2017.
  */
class TwitterListTest extends FunSuite {

  val rest: TwitterRestClient = TwitterAuth.rest()
  val list = TwitterList(rest, "team", "twitterapi")

  test("Twitter list data fetched.") {
    assert(list.data.users.nonEmpty)
  }

  test("Usernames correctly extracted."){
    assert(list.usernames.contains("twitterapi"))
  }

  TwitterAuth.terminate()
}
