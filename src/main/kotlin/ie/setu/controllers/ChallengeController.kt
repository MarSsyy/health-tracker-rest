package ie.setu.controllers

import ie.setu.domain.Challenge
import ie.setu.repository.ChallengeDAO
import ie.setu.utils.jsonObjectMapper
import ie.setu.utils.jsonToObject
import io.javalin.http.Context


object ChallengeController {

    private val challengeDAO = ChallengeDAO()

    fun getAllChallenges(ctx: Context) {
        val mapper = jsonObjectMapper()
        ctx.json(mapper.writeValueAsString(challengeDAO.findAll()))
    }

    fun addChallenge(ctx: Context) {
        val challenge = jsonToObject<Challenge>(ctx.body())
        val saved = challengeDAO.save(challenge)
        ctx.status(201).json(saved)
    }

    fun joinChallenge(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        val challengeId = ctx.pathParam("challenge-id").toInt()

        val joined = challengeDAO.joinChallenge(userId, challengeId)

        if (joined) ctx.status(201).json(mapOf("message" to "Joined"))
        else ctx.status(409).json(mapOf("message" to "Already joined"))
    }


}

