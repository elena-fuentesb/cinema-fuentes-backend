package cinema.services

import cinema.model.ReservationModels.{ReservationFeedback, ReservationStatus}
import cinema.model.{InternalReservation, Reservation}
import zio.Task

import scala.collection.mutable.HashMap

trait ReservationRepo {
  def addReservation(reservation: Reservation): Task[ReservationFeedback]

  def getReservationsForMovie(scheduledMovieId: Long): Task[List[Reservation]]
}

object ReservationRepo {
  class DummyImpl() extends ReservationRepo {
    val storage = HashMap[Long, List[InternalReservation]]().empty

    override def addReservation(reservation: Reservation): Task[ReservationFeedback] = Task {
      val internalReservation = InternalReservation(reservation.name, ReservationStatus.confirmed, reservation.seats)
      storage.put(reservation.scheduledMovieId, storage.get(reservation.scheduledMovieId).map(_ :+ internalReservation).getOrElse(List(internalReservation)))
      ReservationFeedback(true, Some("Reservation has been made."))
    }

    override def getReservationsForMovie(scheduledMovieId: Long): Task[List[Reservation]] = Task {
      storage.get(scheduledMovieId).map(list =>
        list.map(internalRes => Reservation(internalRes.name, scheduledMovieId, internalRes.seats))
      ).getOrElse(Nil)
    }
  }
}