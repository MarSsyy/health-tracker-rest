package ie.setu.controllers

import ie.setu.domain.User
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.jsonToObject
import io.javalin.http.Context

object UserController {

    private val userDAO = UserDAO()

    fun getAllUsers(ctx: Context) {
        ctx.json(userDAO.getAll())
    }

    fun getUserByUserId(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        val user = userDAO.findById(userId)

        if (user != null) {
            ctx.json(user)
        }
    }

    fun addUser(ctx: Context) {
        val user = jsonToObject<User>(ctx.body())
        userDAO.save(user)
        ctx.json(user)
    }

    fun getUserByEmail(ctx: Context) {
        val email = ctx.pathParam("email")
        val user = userDAO.findByEmail(email)

        if (user != null) {
            ctx.json(user)
        }
    }

    fun deleteUserById(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        val user = userDAO.findById(userId)

        if (user != null) {
            userDAO.delete(userId)
            ctx.status(204)
        }
    }

    fun updateUserById(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        val existingUser = userDAO.findById(userId)

        if (existingUser != null) {
            val updatedUser = jsonToObject<User>(ctx.body())
            userDAO.update(userId, updatedUser)
            ctx.status(200).json(updatedUser)
        }
    }
}

