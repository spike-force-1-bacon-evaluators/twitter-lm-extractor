package org.sf1bacon.twitterextractor

import org.scalatest.FunSuite
import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by agapito on 03/02/2017.
  */
class TwitterAuthTest extends FunSuite {

  test("Twitter REST API autentication working") {
    val rest = TwitterAuth.rest()
    assert( Await.result(rest.verifyCredentials(),15.seconds).screen_name == "filipe_agapito")
  }

}
