package cinema.routes

import cinema.model.ReservationModels.ReservationFeedback
import cinema.model._
import io.circe.Encoder._
import io.circe.Json.{fromString, fromValues}
import io.circe._
import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax.EncoderOps
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.util.Try

object JsonHelpers {
  private lazy val dtf = DateTimeFormat.forPattern("dd-MM-yyyy")

  implicit val decodeSingleSeat: Decoder[SingleSeat] = new Decoder[SingleSeat] {
    final def apply(c: HCursor): Decoder.Result[SingleSeat] =
      for {
        row <- c.downField("row").as[Char]
        column <- c.downField("column").as[Int]
      } yield SingleSeat(row, column)
  }

  implicit val decodeLoveSeat: Decoder[LoveSeat] = new Decoder[LoveSeat] {
    final def apply(c: HCursor): Decoder.Result[LoveSeat] =
      for {
        first <- c.downField("firstSeat").as[SingleSeat]
        second <- c.downField("secondSeat").as[SingleSeat]
      } yield LoveSeat(first, second)
  }

  implicit val decodeSeat: Decoder[Seat] = new Decoder[Seat] {
    final def apply(c: HCursor): Decoder.Result[Seat] =
      decodeLoveSeat(c).orElse(decodeSingleSeat(c))
  }

  implicit val seatListDecoder: Decoder[List[Seat]] = decodeListTolerantly[Seat]

  implicit val decodeReservation: Decoder[Reservation] = new Decoder[Reservation] {
    final def apply(c: HCursor): Decoder.Result[Reservation] =
      for {
        name <- c.downField("name").as[String]
        scheduledMovieId <- c.downField("scheduledMovieId").as[Long]
        seats <- c.downField("seats").as[Seq[Seat]]
      } yield Reservation(name, scheduledMovieId, seats)
  }

  implicit val decodeTestRes: Decoder[TestRes] = new Decoder[TestRes] {
    final def apply(c: HCursor): Decoder.Result[TestRes] =
      for {
        name <- c.downField("name").as[String]
        scheduledMovieId <- c.downField("scheduledMovieId").as[Long]
//        seats <- c.downField("seats").as[Seq[Seat]]
      } yield TestRes(name, scheduledMovieId)
  }

  implicit val decodeDateTime: Decoder[DateTime] = Decoder[String].map(d => dtf.parseDateTime(d))
  implicit val encodeDateTime: Encoder[DateTime] = Encoder.instance(d => dtf.print(d).asJson)

  def decodeListTolerantly[A: Decoder]: Decoder[List[A]] =
    Decoder.decodeList(Decoder[Json]).map(
      _.asInstanceOf[List[A]]
    )

  implicit val encodeSeat: Encoder[Seat] = new Encoder[Seat] {
    override def apply(seat: Seat): Json = {
      Json.obj(("seatName", fromString(seat.seatName)))
    }
  }

  implicit val encodeLayout: Encoder[Layout] = new Encoder[Layout] {
    override def apply(layout: Layout): Json = {
      fromString(layout.print)
    }
  }

  implicit val encodeAuditorium: Encoder[Auditorium] = new Encoder[Auditorium] {
    override def apply(auditorium: Auditorium): Json = {
      val nameJson: (String, Json) = ("name", fromString(auditorium.name))
      val layoutJson: (String, Json) = ("layout", auditorium.layout.asJson)
      Json.obj(nameJson, layoutJson)
    }
  }

  implicit val encodeAuditoriumList: Encoder[List[Auditorium]] = new Encoder[List[Auditorium]] {
    override def apply(list: List[Auditorium]): Json = {
      fromValues(list.map(input => encodeAuditorium(input)))
    }
  }

  implicit val encodeScheduledMovie: Encoder[ScheduledMovie] = new Encoder[ScheduledMovie] {
    override def apply(scheduledMovie: ScheduledMovie): Json = {
      Json.obj(
        ("date", scheduledMovie.date.asJson),
        ("movie", scheduledMovie.movie.asJson),
        ("auditorium", fromString(scheduledMovie.auditoriumName))
      )
    }
  }

  implicit val movieWithIdEncoder: Encoder[MovieWithId] = deriveEncoder

  implicit val encodeScheduledMovieWithId: Encoder[ScheduledMovieWithId] = new Encoder[ScheduledMovieWithId] {
    override def apply(input: ScheduledMovieWithId): Json = {
      val idJson = ("id", input.id.asJson)
      val dateJson = ("date", input.date.asJson)
      val movieJson = ("movie", input.movie.asJson)
      val auditoriumJson = ("auditorium", input.auditorium.asJson)
      if (input.reservations.nonEmpty) {
        Json.obj(idJson, dateJson, movieJson, auditoriumJson, ("reservations", input.reservations.map(seat => Json.obj(("seat", seat.asJson))).asJson))
      } else {
        Json.obj(idJson, dateJson, movieJson, auditoriumJson)
      }
    }
  }

  implicit val encodeScheduledMoviesList: Encoder[List[ScheduledMovieWithId]] = new Encoder[List[ScheduledMovieWithId]] {
    override def apply(list: List[ScheduledMovieWithId]): Json = {
      fromValues(list.map(input => encodeScheduledMovieWithId(input)))
    }
  }
  implicit val encodeFeedback: Encoder[ReservationFeedback] = deriveEncoder

  implicit val encodeInternalReservation: Encoder[Reservation] = new Encoder[Reservation] {
    override def apply(input: Reservation): Json = {
      Json.obj(
        ("name", fromString(input.name)),
        ("seats", fromValues(input.seats.map(_.asJson)))
      )
    }
  }
}