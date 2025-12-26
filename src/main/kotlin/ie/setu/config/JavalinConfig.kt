package ie.setu.config

import ie.setu.controllers.ActivityController
import ie.setu.controllers.AuthController
import ie.setu.controllers.ChallengeController
import ie.setu.controllers.UserController
import ie.setu.utils.Auth
import ie.setu.utils.jsonObjectMapper
import io.javalin.Javalin
import io.javalin.json.JavalinJackson
import io.javalin.vue.VueComponent

class JavalinConfig {

    val app = Javalin.create(
        { config ->
            config.jsonMapper(JavalinJackson(jsonObjectMapper()))
            config.staticFiles.enableWebjars()
            config.vue.vueInstanceNameInJs = "app"

        }
    ).apply {
        exception(Exception::class.java) { e, ctx -> e.printStackTrace() }
        error(404) { ctx -> ctx.json("404 - Not Found") }
    }


    fun startJavalinService(): Javalin {
        app.start(getRemoteAssignedPort())
        registerRoutes(app)
        return app
    }

    private fun registerRoutes(app: Javalin) {

        app.post("/api/auth/login", AuthController::login)
        app.post("/api/auth/logout", AuthController::logout)
        app.get("/api/auth/me", AuthController::me)
        app.before { ctx ->
            val path = ctx.path()
            val isApi = path.startsWith("/api/")
            val isLoginPage = path == "/login"

            if (!isApi && !isLoginPage) {
                Auth.requireLoginPage(ctx)
            }
        }
        app.before("/api/activities") { ctx ->
            if (ctx.method().name == "GET") Auth.requireAdmin(ctx)
            else Auth.requireAuth(ctx)
        }
        app.before("/api/activities/{activity-id}", Auth::requireAdmin)

        app.before("/api/users/{user-id}") { ctx ->
            Auth.requireSelfOrAdmin(ctx, "user-id")
        }

        app.before("/api/users/{user-id}/activities") { ctx ->
            Auth.requireSelfOrAdmin(ctx, "user-id")
        }

        app.before("/api/users/{user-id}/bmi") { ctx ->
            Auth.requireSelfOrAdmin(ctx, "user-id")
        }

        app.before("/api/users/{user-id}/calories") { ctx ->
            Auth.requireSelfOrAdmin(ctx, "user-id")
        }

        app.before("/api/challenges") { ctx ->
            if (ctx.method().name == "POST") Auth.requireAdmin(ctx)
        }



        app.get("/api/users", UserController::getAllUsers)
        app.get("/api/users/{user-id}", UserController::getUserByUserId)
        app.post("/api/users", UserController::addUser)
        app.delete("/api/users/{user-id}", UserController::deleteUserById)
        app.patch("/api/users/{user-id}", UserController::updateUserById)
        app.get("/api/users/email/{email}", UserController::getUserByEmail)

        app.get("/api/activities", ActivityController::getAllActivities)
        app.post("/api/activities", ActivityController::addActivity)
        app.get("/api/users/{user-id}/activities", ActivityController::getActivitiesByUserId)
        app.delete("/api/users/{user-id}/activities", ActivityController::deleteActivitiesByUserId)
        app.delete("/api/activities/{activity-id}", ActivityController::deleteActivityByActivityId)
        app.patch("/api/activities/{activity-id}", ActivityController::updateActivityByActivityId)
        app.get("/api/activities/{activity-id}", ActivityController::getActivityByActivityId)

        app.get("/api/users/{user-id}/bmi", UserController::getUserBmi)
        app.get("/api/users/{user-id}/calories", UserController::getUserCalories)

        app.get("/api/challenges", ChallengeController::getAllChallenges)
        app.post("/api/challenges", ChallengeController::addChallenge)
        app.post("/api/users/{user-id}/challenges/{challenge-id}", ChallengeController::joinChallenge)



        app.before("/users", Auth::requireAdminPage)
        app.before("/activities", Auth::requireAdminPage)

        app.before("/users/{user-id}", Auth::requireLoginPage)
        app.before("/users/{user-id}/activities", Auth::requireLoginPage)
        app.before("/users/{user-id}/calories", Auth::requireLoginPage)
        app.before("/challenges", Auth::requireLoginPage)


        app.get("/", VueComponent("<home-page></home-page>"))
        app.get("/users", VueComponent("<user-overview></user-overview>"))
        app.get("/users/{user-id}", VueComponent("<user-profile></user-profile>"))
        app.get("/users/{user-id}/activities", VueComponent("<user-activity-overview></user-activity-overview>"))
        app.get("/activities", VueComponent("<activity-overview></activity-overview>"))
        app.get("/login", VueComponent("<login-page></login-page>"))
        app.get("/users/{user-id}/bmi", VueComponent("<bmi-page></bmi-page>"))
        app.get("/users/{user-id}/calories", VueComponent("<calorie-page></calorie-page>"))
        app.get("/challenges", VueComponent("<challenge-overview></challenge-overview>"))

    }

    private fun getRemoteAssignedPort(): Int {
        val remotePort = System.getenv("PORT")
        return if (remotePort != null) {
            Integer.parseInt(remotePort)
        } else 8080
    }

    fun getJavalinService(): Javalin {
        registerRoutes(app)
        return app
    }

}
