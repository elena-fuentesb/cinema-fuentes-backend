package cinema.routes

import cinema.routes.JsonHelpers._
import cinema.services.{CinemaScheduleRepo, MovieRepo}
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.joda.time.format.DateTimeFormat
import zio.Task
import zio.interop.catz._

object MovieRoutes {

  def routes(movieRepo: MovieRepo, scheduleRepo: CinemaScheduleRepo): HttpRoutes[Task] = {

    val dsl = new Http4sDsl[Task] {}
    import dsl._

    HttpRoutes.of[Task] {
      case _@GET -> Root / "movies" =>
        movieRepo.getMovies().flatMap(movies => Ok(movies))

      case _@GET -> Root / "movies" / id =>
        movieRepo.getMovie(id) flatMap {
          case None => NotFound()
          case Some(book) => Ok(book)
        }

      case _@GET -> Root / "schedule" / dateInput =>
        lazy val dtf = DateTimeFormat.forPattern("dd-MM-yyyy")
        val date = dtf.parseDateTime(dateInput)
        scheduleRepo.getScheduledMovies(date).flatMap(movies => Ok(movies))

    }
  }

}