package cinema.services

import cinema.model.ScheduledMovieModels.getId
import cinema.model.{ScheduledMovie, ScheduledMovieWithId}
import org.joda.time.DateTime
import zio.Task

trait CinemaScheduleRepo {
  def addScheduledMovie(scheduledMovie: ScheduledMovie): Task[ScheduledMovieWithId]

  def getScheduledMovies(date: DateTime): Task[List[ScheduledMovieWithId]]
}

object CinemaScheduleRepo {
  class DummyImpl(movieRepo: MovieRepo, auditoriumRepo: AuditoriumRepo) extends CinemaScheduleRepo {

    import scala.collection.mutable.HashMap

    val storage = HashMap[DateTime, ScheduledMovieWithId]().empty

    override def addScheduledMovie(scheduledMovie: ScheduledMovie): Task[ScheduledMovieWithId] =
      movieRepo.getMovie(scheduledMovie.movie).zip(auditoriumRepo.getAuditorium(scheduledMovie.auditoriumName))
        .flatMap {
          case (Some(movie), Some(auditorium)) =>
            val scheduledMovieId = getId
            val scheduledMovieWithAuditorium = ScheduledMovieWithId(
              scheduledMovieId,
              scheduledMovie.date,
              movie,
              auditorium
            )
            Task {
              storage.put(scheduledMovie.date.withTimeAtStartOfDay(), scheduledMovieWithAuditorium)
              scheduledMovieWithAuditorium
            }
          case _ => throw new Exception("oops")
        }

    override def getScheduledMovies(date: DateTime): Task[List[ScheduledMovieWithId]] = Task {
      storage.get(date.withTimeAtStartOfDay()).toList
    }
  }
}
