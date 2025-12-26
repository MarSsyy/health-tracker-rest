package ie.setu.domain.db

import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one user.
//       Database wise, this is the table object.

//this is a blueprint that tells our kotlin program of what the database table looks like
object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val email = varchar("email", 255)
    val role = varchar("role", 20).default("USER")
    val age = integer("age").nullable()
    val gender = varchar("gender", 10).nullable()
    val password = varchar("password", 255)
    val height = double("height").nullable()
    val weight = double("weight").nullable()
    val activityLevel = varchar("activity_level", 20).nullable()


    override val primaryKey = PrimaryKey(id, name = "PK_Users_ID")
}
