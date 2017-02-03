package org.sf1bacon.twitterextractor

import java.text.SimpleDateFormat
import java.util.Calendar

import scala.concurrent.duration._

/**
  * Created by agapito on 07/02/2017.
  */
object Utils {

  def timestamp: String = {
    val format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_z")
    format.format(Calendar.getInstance.getTime)
  }

  def twitterDateToDay(in:String): String = {
    val twitterDate = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy").parse(in)
    val dayFormat = new SimpleDateFormat("yyyy-MM-dd")
    dayFormat.format(twitterDate)
  }

  def sleeper(snoozeMinutes: Int): Unit = {
    (0 to snoozeMinutes).foreach{ t =>
      print(s"${snoozeMinutes - t}...")
      Thread.sleep(1.minutes.toMillis)
    }
    println()
  }

}
