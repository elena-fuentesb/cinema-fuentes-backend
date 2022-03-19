package cinema

import cats.effect.ExitCode
import cinema.routes.{CinemaScheduleRoutes, MovieRoutes, ReservationRoutes}
import cinema.services.{AuditoriumRepo, CinemaScheduleRepo, MovieRepo, ReservationRepo}
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.{Task, URIO}


object Main extends CatsApp {

  private val movieRepo: MovieRepo = new MovieRepo.DummyImpl
  private val auditoriumRepo: AuditoriumRepo = new AuditoriumRepo.DummyImpl
  private val scheduledRepo: CinemaScheduleRepo = new CinemaScheduleRepo.DummyImpl(movieRepo, auditoriumRepo)
  private val reservationRepo: ReservationRepo = new ReservationRepo.DummyImpl

  val httpRoutes = Router[Task](
    "/" -> MovieRoutes.routes(movieRepo, scheduledRepo),
    "/admin" -> CinemaScheduleRoutes.routes(auditoriumRepo, movieRepo, scheduledRepo),
    "/booking" -> ReservationRoutes.routes(reservationRepo)
  ).orNotFound

  override def run(args: List[String]): URIO[zio.ZEnv, zio.ExitCode] = {
    BlazeServerBuilder[Task]
      .bindHttp(9000, "0.0.0.0")
      .withHttpApp(httpRoutes)
      .serve
      .compile[Task, Task, ExitCode]
      .drain
      .exitCode

  }
}
