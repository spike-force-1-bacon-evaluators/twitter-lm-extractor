package org.sf1bacon.twitterextractor

import java.text.SimpleDateFormat
import java.util.Calendar

/**
  * Created by agapito on 07/02/2017.
  */
object Utils {

  def timestamp: String = {
    val format = new SimpleDateFormat("yyyy-mm-dd_HH:mm:ss_z")
    format.format(Calendar.getInstance.getTime)
  }

}
