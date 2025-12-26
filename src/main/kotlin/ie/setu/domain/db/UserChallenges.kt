package ie.setu.domain.db

import org.jetbrains.exposed.sql.Table

object UserChallenges : Table("user_challenges") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val challengeId = integer("challenge_id")
    val status = varchar("status", 20).default("joined")

    override val primaryKey = PrimaryKey(id)
}
