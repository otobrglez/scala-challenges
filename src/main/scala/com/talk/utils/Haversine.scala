package com.talk.utils

object Haversine {
  /** Equatorial radius (WGS84) in meters */
  val EARTH_RADIUS = 6378137d

  /**
   * Calculates distance basing on [[EARTH_RADIUS]]. The result is in meters.
   *
   * @param start - a start lon / lat point
   * @param end   - an end lon / lat point
   *
   */
  def apply(start: (Double, Double), end: (Double, Double)): Double =
    apply(start._1, start._2, end._1, end._2, EARTH_RADIUS)

  /**
   * Calculates distance basing on [[EARTH_RADIUS]]. The result is in meters.
   *
   * @param startLon - a start lon
   * @param startLat - a start lan
   * @param endLon   - an end lon
   * @param endLat   - an end lat
   */
  def apply(startLon: Double, startLat: Double, endLon: Double, endLat: Double): Double =
    apply(startLon, startLat, endLon, endLat, EARTH_RADIUS)

  /**
   * Calculates distance, in R units
   *
   * @param start - a start lon / lat point
   * @param end   - an end lon / lat point
   * @param R     - Earth radius, can be in any unit, in fact this value
   *              defines a physical meaning of this function
   *
   */
  def apply(start: (Double, Double), end: (Double, Double), R: Double): Double =
    apply(start._1, start._2, end._1, end._2, R)

  /**
   * Calculates distance, in R units
   *
   * @param startLon - a start lon
   * @param startLat - a start lan
   * @param endLon   - an end lon
   * @param endLat   - an end lat
   * @param R        - Earth radius, can be in any unit, in fact this value
   *                 defines a physical meaning of this function
   */
  def apply(startLon: Double, startLat: Double, endLon: Double, endLat: Double, R: Double): Double = {
    val dLat = math.toRadians(endLat - startLat)
    val dLon = math.toRadians(endLon - startLon)
    val lat1 = math.toRadians(startLat)
    val lat2 = math.toRadians(endLat)

    val a =
      math.sin(dLat / 2) * math.sin(dLat / 2) +
        math.sin(dLon / 2) * math.sin(dLon / 2) * math.cos(lat1) * math.cos(lat2)
    val c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

    R * c
  }
}