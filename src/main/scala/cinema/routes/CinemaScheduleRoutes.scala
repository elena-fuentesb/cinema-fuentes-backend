package cinema.routes

import cinema.model.MovieModels.Message
import cinema.model.{Movie, ScheduledMovie}
import cinema.services.{AuditoriumRepo, CinemaScheduleRepo, MovieRepo}
import io.circe.Json
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import zio.Task
import zio.interop.catz._

object CinemaScheduleRoutes {
  private def errorBody(message: Message) = Json.obj(
    ("message", Json.fromString(message))
  )

  def routes(auditoriumRepo: AuditoriumRepo, movieRepo: MovieRepo, scheduleRepo: CinemaScheduleRepo): HttpRoutes[Task] = {
    val dsl = new Http4sDsl[Task] {}
    import JsonHelpers._
    import dsl._

    HttpRoutes.of[Task] {
      case _@GET -> Root / "cinema" =>
        auditoriumRepo.getAuditoriums.flatMap(auditoria => Ok(auditoria))

      case req@POST -> Root / "movies" =>
        req.decode[Movie] { movie =>
          movieRepo.addMovie(movie) flatMap (id =>
            Created(Json.obj(("id", Json.fromString(id))))
            )
        }

      case req@PUT -> Root / "movies" / id =>
        req.decode[Movie] { book =>
          movieRepo.updateMovie(id, book) flatMap {
            case Left(message) => NotFound(errorBody(message))
            case Right(_) => Ok()
          }
        }

      case _@DELETE -> Root / "movies" / id =>
        movieRepo.deleteMovie(id) flatMap {
          case Left(message) => NotFound(errorBody(message))
          case Right(_) => Ok()
        }

      case req@POST -> Root / "schedule" / "movies" =>
        req.decode[ScheduledMovie] { movie =>
          scheduleRepo.addScheduledMovie(movie) flatMap (res => Ok(res))
        }

    }
  }
}
