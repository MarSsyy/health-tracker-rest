package ie.setu.utils

import ie.setu.domain.User
import ie.setu.domain.db.Users
import org.jetbrains.exposed.sql.ResultRow

//Turns db rows to kotlin object
fun mapToUser(it: ResultRow) = User(
    id = it[Users.id],
    name = it[Users.name],
    email = it[Users.email],
    age = it[Users.age],
    gender = it[Users.gender],
    password = it[Users.password]
)
