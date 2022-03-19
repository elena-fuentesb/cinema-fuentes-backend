package cinema.model

import cinema.model.AuditoriumModels.Name

object AuditoriumModels {
  type Name = String
}

case class Layout(columnsForRows: Map[Char, Int]) {
  def print: String =
    columnsForRows.map { case (rowLetter, nrColumns) =>
      (1 to nrColumns).map(seatNr => s"$rowLetter$seatNr").mkString(" ")
    }.mkString("\n")

  def capacity: Int = columnsForRows.values.sum

  def allSeats: Seq[(Char, Int)] =
    columnsForRows.flatMap { case (rowLetter, nrColumns) =>
      (1 to nrColumns).map(seatNr => (rowLetter, seatNr))
    }.toSeq

  def availableSeats(taken: Seq[(Char, Int)]): String =
    columnsForRows.map { case (rowLetter, nrColumns) =>
      (1 to nrColumns)
        .map { seatNr =>
          if (taken.contains((rowLetter, seatNr))) {
            "X" //taken
          } else {
            s"$rowLetter$seatNr" //available
          }
        }
        .mkString(" ")
    }.mkString("\n")
}

trait Seat {
  def occupies: Seq[(Char, Int)]

  def seatName: String
}

case class SingleSeat(row: Char, column: Int) extends Seat {
  override def seatName: String = s"$row$column"

  override def occupies: Seq[(Char, Int)] = Seq((row, column))
}

case class LoveSeat(firstSeat: Seat, secondSeat: Seat) extends Seat {
  override def seatName: String = s"Loveseat: ${firstSeat.seatName} & ${secondSeat.seatName}"

  override def occupies: Seq[(Char, Int)] = firstSeat.occupies ++ secondSeat.occupies
}

case class Auditorium(name: Name, layout: Layout) {

}
