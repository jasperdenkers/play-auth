package models

import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.Future

case class User(username: String, passwordHash: String) {

  def checkPassword(plaintextPassword: String) =
    BCrypt.checkpw(plaintextPassword, passwordHash)

}

object User {

  def create(username: String, plaintextPassword: String) =
    User(username, hashPassword(plaintextPassword))

  private def hashPassword(plaintextPassword: String) =
    BCrypt.hashpw(plaintextPassword, BCrypt.gensalt())

}

object UserRepository {

  private val users = Set(
    User.create("admin", "123"),
    User.create("user", "321")
  )

  def find(username: String) =
    Future.successful {
      users.find(_.username == username)
    }

}