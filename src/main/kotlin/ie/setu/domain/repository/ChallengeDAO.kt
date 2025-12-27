package ie.setu.repository

import ie.setu.domain.Challenge
import ie.setu.domain.db.Challenges
import ie.setu.domain.db.UserChallenges
import ie.setu.utils.mapToChallenge
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


class ChallengeDAO {

    fun findAll(): ArrayList<Challenge> {
        val challengesList: ArrayList<Challenge> = arrayListOf()

        transaction {
            Challenges.selectAll().map {
                challengesList.add(mapToChallenge(it))
            }
        }

        return challengesList
    }


    fun save(challenge: Challenge): Challenge {
        val id = transaction {
            Challenges.insert {
                it[name] = challenge.name
                it[description] = challenge.description
            } get Challenges.id
        }
        return challenge.copy(id = id)
    }

    fun joinChallenge(userId: Int, challengeId: Int): Boolean {
        return transaction {

            val exists = UserChallenges
                .selectAll()
                .where {
                    (UserChallenges.userId eq userId) and
                            (UserChallenges.challengeId eq challengeId)
                }
                .count() > 0L

            if (exists) return@transaction false

            UserChallenges.insert {
                it[UserChallenges.userId] = userId
                it[UserChallenges.challengeId] = challengeId
                it[UserChallenges.status] = "joined"
            }
            true
        }
    }

    fun delete(id: Int): Int {
        return transaction {
            Challenges.deleteWhere { Challenges.id eq id }
        }
    }


}
