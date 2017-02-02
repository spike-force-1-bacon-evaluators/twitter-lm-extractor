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
    try {
      TwitterList(rest, "team", "twitterapi")
    } catch {
      case _: Throwable => fail("Could not fetch data. Exception thrown.")
    }
  }

  test("Usernames correctly extracted."){
    assert(list.usernames.contains("twitterapi"))
  }

}
