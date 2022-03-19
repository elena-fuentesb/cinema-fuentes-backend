package cinema.services

import cinema.model.AuditoriumModels.Name
import cinema.model.{Auditorium, Layout}
import zio.Task

trait AuditoriumRepo {
  def getAuditoriums: Task[List[Auditorium]]

  def getAuditorium(name: Name): Task[Option[Auditorium]]
}

object AuditoriumRepo {
  class DummyImpl extends AuditoriumRepo {
    val standardLayout100Seats = Layout(
      Map(
        'A' -> 10,
        'B' -> 10,
        'C' -> 10,
        'D' -> 10,
        'E' -> 10,
        'F' -> 10,
        'G' -> 10,
        'H' -> 10,
        'I' -> 10,
        'J' -> 10
      )
    )

    val largeLayout = Layout(
      Map(
        'A' -> 20,
        'B' -> 20,
        'C' -> 18,
        'D' -> 16,
        'E' -> 16,
        'F' -> 14,
        'G' -> 12,
        'H' -> 12,
        'I' -> 10,
        'J' -> 10
      )
    )

    val enigma = Auditorium("The Enigma Room", standardLayout100Seats)
    val trinity = Auditorium("Trinity Hall", standardLayout100Seats)
    val imperial = Auditorium("Imperial", largeLayout)

    val storage = Seq(enigma, trinity, imperial)

    override def getAuditoriums: Task[List[Auditorium]] = Task {
      storage.toList
    }

    override def getAuditorium(name: Name): Task[Option[Auditorium]] = Task {
      storage.find(_.name == name)
    }
  }
}
