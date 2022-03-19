package cinema.model

import cinema.model.AuditoriumModels.Name
import cinema.model.MovieModels.MovieId
import org.joda.time.DateTime

import scala.util.Random

object ScheduledMovieModels {
  def getId = Random.nextLong().abs
}

case class ScheduledMovie(date: DateTime,
                          movie: MovieId,
                          auditoriumName: Name)

case class ScheduledMovieWithId(id: Long,
                                date: DateTime,
                                movie: MovieWithId,
                                auditorium: Auditorium,
                                reservations: Seq[Seat] = Nil) {
  def isFull: Boolean = reservations.map {
    case SingleSeat(_, _) => 1
    case LoveSeat(_, _) => 2
  }.sum == auditorium.layout.capacity

  def availableSeats: String = auditorium.layout.availableSeats(takenSeats)

  def takenSeats: Seq[(Char, Int)] = reservations.flatMap(_.occupies)
}


