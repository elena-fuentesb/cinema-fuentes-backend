package cinema.routes

import cinema.model.Reservation
import cinema.routes.JsonHelpers._
import cinema.services.ReservationRepo
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import zio.Task
import zio.interop.catz._

object ReservationRoutes {
  def routes(reservationRepo: ReservationRepo): HttpRoutes[Task] = {

    val dsl = new Http4sDsl[Task] {}
    import dsl._

    HttpRoutes.of[Task] {
      case req@POST -> Root =>
        req.decode[Reservation] { reservation =>
          reservationRepo.addReservation(reservation) flatMap (res => Ok(res))
        }

      case req@GET -> Root / "scheduledmovie" / id =>
        reservationRepo.getReservationsForMovie(id.toLong) flatMap (res => Ok(res))
    }
  }
}
