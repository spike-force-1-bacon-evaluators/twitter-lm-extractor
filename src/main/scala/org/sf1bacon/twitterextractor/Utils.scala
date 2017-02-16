package org.sf1bacon.twitterextractor

import java.text.SimpleDateFormat
import java.util.Calendar

import scala.concurrent.duration._

/**
  * Created by agapito on 07/02/2017.
  */
object Utils {

  /**
    * Creates a timestamp with the current time
    * @return string timestamp
    */
  def timestamp: String = {
    val format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_z")
    format.format(Calendar.getInstance.getTime)
  }

  /**
    * Converts the timestring format used by Twitter into year/month/day format
    *
    * @param in string with a Twitter timestamp (`EEE MMM d HH:mm:ss Z yyyy`)
    * @return string with a day `yyyy-MM-dd` timestamp
    */
  def twitterDateToDay(in: String): String = {
    val twitterDate = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy").parse(in)
    val dayFormat = new SimpleDateFormat("yyyy-MM-dd")
    dayFormat.format(twitterDate)
  }

  /**
    * Thread.sleep for the specified ammount of minutes, providing visual feedback each minute
    *
    * @param snoozeMinutes number of minutes to sleep
    */
  def sleeper(snoozeMinutes: Int): Unit = {
    (0 to snoozeMinutes).foreach { t =>
      print(s"${snoozeMinutes - t}...")
      Thread.sleep(1.minutes.toMillis)
    }
    println()
  }

  /**
    * Sanitizes a string by removing all characters wich are not letters, numbers, or whitespace
    *
    * @param input
    * @return
    */
  def sanitize(input: String): String = input.replaceAll("[^\\w\\s]+", "")

}
