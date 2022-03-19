package cinema.services

import cinema.model.MovieModels._
import cinema.model.{Movie, MovieWithId}
import zio.Task
import cats.implicits._

trait MovieRepo {

  def addMovie(movie: Movie): Task[MovieId]
  def getMovie(id: MovieId): Task[Option[MovieWithId]]
  def deleteMovie(id: MovieId): Task[Either[Message, Unit]]
  def updateMovie(id: MovieId, movie: Movie): Task[Either[Message, Unit]]
  def getMovies(): Task[List[MovieWithId]]
}

object MovieRepo {
  class DummyImpl extends MovieRepo {
    import scala.collection.mutable.HashMap
    val storage = HashMap[MovieId, Movie](("uMSBjKhp", Movie("Eat Pray Love", "inspirational")))

    override def addMovie(movie: Movie): Task[MovieId] = Task {
      val movieId = getMovieId
      storage.put(movieId, movie)
      movieId
    }

    override def getMovie(id: MovieId): Task[Option[MovieWithId]] = Task {
      storage.get(id).map(movie => MovieWithId(id, movie.title, movie.genre))
    }

    override def deleteMovie(id: MovieId): Task[Either[Message, Unit]] =
      for {
        removedMovie <- Task(storage.remove(id))
        result = removedMovie.toRight(s"Movie with ${id} not found").void
      } yield result

    override def updateMovie(id: MovieId, movie: Movie): Task[Either[Message, Unit]] = {
      for {
        movieOpt <- getMovie(id)
        _ <- Task(movieOpt.toRight(s"Movie with ${id} not found").void)
        updatedMovie = storage.put(id, movie)
          .toRight(s"Movie with ${id} not found").void
      } yield updatedMovie
    }

    override def getMovies(): Task[List[MovieWithId]] = Task {
      storage.map {case (id, movie) => MovieWithId(id, movie.title, movie.genre)}.toList
    }
  }
}