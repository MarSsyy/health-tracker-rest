package ie.setu.utils

import io.javalin.http.Context
import io.javalin.http.ForbiddenResponse
import io.javalin.http.UnauthorizedResponse

object Auth {

    fun requireLoginPage(ctx: Context) {
        val userId = ctx.sessionAttribute<Int>("authUserId")
        if (userId == null) {
            ctx.redirect("/login")
            ctx.skipRemainingHandlers()
            return
        }
    }

    fun requireAdminPage(ctx: Context) {
        val userId = ctx.sessionAttribute<Int>("authUserId")
        val role = ctx.sessionAttribute<String>("authRole")

        if (userId == null) {
            ctx.redirect("/login")
            ctx.skipRemainingHandlers()
            return
        }

        if (role != "ADMIN") {
            ctx.redirect("/users/$userId")
            ctx.skipRemainingHandlers()
            return
        }
    }

    fun requireAuth(ctx: Context) {
        val userId = ctx.sessionAttribute<Int>("authUserId")
            ?: throw UnauthorizedResponse("Not logged in")

        val role = ctx.sessionAttribute<String>("authRole")
            ?: throw UnauthorizedResponse("Not logged in")

        ctx.attribute("authUserId", userId)
        ctx.attribute("authRole", role)
    }

    fun requireAdmin(ctx: Context) {
        requireAuth(ctx)
        val role = ctx.attribute<String>("authRole")
        if (role != "ADMIN") throw ForbiddenResponse("Admin access required")
    }

    fun requireSelfOrAdmin(ctx: Context, paramName: String = "user-id") {
        requireAuth(ctx)

        val role = ctx.attribute<String>("authRole")
        if (role == "ADMIN") return

        val loggedInUserId = ctx.attribute<Int>("authUserId")!!
        val requestedUserId = ctx.pathParam(paramName).toInt()

        if (loggedInUserId != requestedUserId) {
            throw ForbiddenResponse("You can only access your own data")
        }
    }
}
