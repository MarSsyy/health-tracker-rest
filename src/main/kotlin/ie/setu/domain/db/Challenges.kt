package ie.setu.domain.db

import org.jetbrains.exposed.sql.Table

object Challenges : Table("challenges") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val description = varchar("description", 255)

    override val primaryKey = PrimaryKey(id,name = "PK_Challenges_ID")
}
