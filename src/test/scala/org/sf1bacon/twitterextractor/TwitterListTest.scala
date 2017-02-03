package org.sf1bacon.twitterextractor

import org.scalatest.FunSuite

/**
  * Created by agapito on 03/02/2017.
  */
class TwitterListTest extends FunSuite {

  val list = TwitterList("team", "twitterapi")

  test("Twitter list data fetched.") {
    assert(list.members.nonEmpty)
  }

  test("Usernames correctly extracted."){
    assert(list.usernames.contains("twitterapi"))
  }
}
