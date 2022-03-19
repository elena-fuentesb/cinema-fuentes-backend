package cinema.model

import cinema.model.MovieModels.{Genre, Id, Title}

import scala.util.Random

object MovieModels {

  type Title = String
  type Genre = String
  type Id = String
  type MovieId = String
  def getMovieId =  Random.alphanumeric.take(8).foldLeft("")((result, c) => result + c)

  type Message = String
}

case class Movie(title: Title, genre: Genre)
case class MovieWithId(id: Id, title: Title, genre: Genre)