package ie.setu.controllers

import ie.setu.domain.repository.UserDAO
import ie.setu.utils.Auth
import ie.setu.utils.jsonToObject
import io.javalin.http.Context
import io.javalin.http.UnauthorizedResponse

object AuthController {

    private val userDAO = UserDAO()

    data class LoginDTO(val email: String, val password: String)

    fun login(ctx: Context) {
        val body = jsonToObject<LoginDTO>(ctx.body())

        val user = userDAO.findByEmail(body.email)
            ?: throw UnauthorizedResponse("Invalid email or password")

        if (user.password != body.password) {
            throw UnauthorizedResponse("Invalid email or password")
        }

        ctx.sessionAttribute("authUserId", user.id)
        ctx.sessionAttribute("authRole", user.role)

        ctx.json(
            mapOf(
                "id" to user.id,
                "name" to user.name,
                "email" to user.email,
                "role" to user.role
            )
        )
    }

    fun logout(ctx: Context) {
        ctx.req().session.invalidate()
        ctx.status(204)
    }

    fun me(ctx: Context) {
        val disableAuth =
            (System.getenv("DISABLE_AUTH") == "true") ||
                    (System.getProperty("DISABLE_AUTH") == "true")

        if (disableAuth) {
            ctx.json(mapOf("id" to 1, "name" to "Test Admin", "email" to "admin@test.com", "role" to "ADMIN"))
            return
        }

        Auth.requireAuth(ctx)

        val userId = ctx.attribute<Int>("authUserId")!!
        val user = userDAO.findById(userId) ?: throw UnauthorizedResponse("Invalid session")

        ctx.json(
            mapOf(
                "id" to user.id,
                "name" to user.name,
                "email" to user.email,
                "role" to user.role
            )
        )
    }
}
