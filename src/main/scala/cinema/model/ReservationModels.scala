package cinema.model

import cinema.model.ReservationModels.ReservationStatus.ReservationStatus

object ReservationModels {
  object ReservationStatus extends Enumeration {
    type ReservationStatus = Value

    val confirmed = Value("confirmed")
    val cancelled = Value("cancelled")
  }

  case class ReservationFeedback(confirmed: Boolean, message: Option[String])
}

case class Reservation(name: String,
                       scheduledMovieId: Long,
                       seats: Seq[Seat])

case class InternalReservation(name: String,
                               reservationStatus: ReservationStatus,
                               seats: Seq[Seat])