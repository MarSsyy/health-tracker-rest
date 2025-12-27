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


    private val disableAuth = (System.getenv("DISABLE_AUTH") == "true") || (System.getProperty("DISABLE_AUTH") == "true")


    val app = Javalin.create { config ->
        config.jsonMapper(JavalinJackson(jsonObjectMapper()))
        config.staticFiles.enableWebjars()
        config.vue.vueInstanceNameInJs = "app"
    }.apply {
        exception(Exception::class.java) { e, _ -> e.printStackTrace() }
        error(404) { ctx -> ctx.json("404 - Not Found") }
    }

    fun startJavalinService(): Javalin {
        app.start(getRemoteAssignedPort())
        registerRoutes(app)
        return app
    }

    private fun registerRoutes(app: Javalin) {

        // ---------- Auth endpoints ----------
        app.post("/api/auth/login", AuthController::login)
        app.post("/api/auth/logout", AuthController::logout)
        app.get("/api/auth/me", AuthController::me)

        // ---------- Global page guard (non-API pages) ----------
        app.before { ctx ->
            if (disableAuth) return@before

            val path = ctx.path()
            val isApi = path.startsWith("/api/")
            val isLoginPage = path == "/login"
            val isSignupPage = path == "/signup"

            if (!isApi && !isLoginPage && !isSignupPage) {
                Auth.requireLoginPage(ctx)
            }
        }

        // ---------- API guards ----------
        app.before("/api/activities") { ctx ->
            if (disableAuth) return@before

            if (ctx.method().name == "GET") Auth.requireAdmin(ctx)
            else Auth.requireAuth(ctx)
        }

        // method-reference cannot be short-circuited, so wrap it:
        app.before("/api/activities/{activity-id}") { ctx ->
            if (disableAuth) return@before
            Auth.requireAdmin(ctx)
        }

        app.before("/api/users/{user-id}") { ctx ->
            if (disableAuth) return@before
            Auth.requireSelfOrAdmin(ctx, "user-id")
        }

        app.before("/api/users/{user-id}/activities") { ctx ->
            if (disableAuth) return@before
            Auth.requireSelfOrAdmin(ctx, "user-id")
        }

        app.before("/api/users/{user-id}/bmi") { ctx ->
            if (disableAuth) return@before
            Auth.requireSelfOrAdmin(ctx, "user-id")
        }

        app.before("/api/users/{user-id}/calories") { ctx ->
            if (disableAuth) return@before
            Auth.requireSelfOrAdmin(ctx, "user-id")
        }

        app.before("/api/challenges") { ctx ->
            if (disableAuth) return@before
            if (ctx.method().name == "POST") Auth.requireAdmin(ctx)
        }

        // ---------- User routes ----------
        app.get("/api/users", UserController::getAllUsers)
        app.get("/api/users/{user-id}", UserController::getUserByUserId)
        app.post("/api/users", UserController::addUser)
        app.delete("/api/users/{user-id}", UserController::deleteUserById)
        app.patch("/api/users/{user-id}", UserController::updateUserById)
        app.get("/api/users/email/{email}", UserController::getUserByEmail)

        // ---------- Activity routes ----------
        app.get("/api/activities", ActivityController::getAllActivities)
        app.post("/api/activities", ActivityController::addActivity)
        app.get("/api/users/{user-id}/activities", ActivityController::getActivitiesByUserId)
        app.delete("/api/users/{user-id}/activities", ActivityController::deleteActivitiesByUserId)
        app.delete("/api/activities/{activity-id}", ActivityController::deleteActivityByActivityId)
        app.patch("/api/activities/{activity-id}", ActivityController::updateActivityByActivityId)
        app.get("/api/activities/{activity-id}", ActivityController::getActivityByActivityId)

        // ---------- BMI & calories routes ----------
        app.get("/api/users/{user-id}/bmi", UserController::getUserBmi)
        app.get("/api/users/{user-id}/calories", UserController::getUserCalories)

        // ---------- Challenge routes ----------
        app.get("/api/challenges", ChallengeController::getAllChallenges)
        app.post("/api/challenges", ChallengeController::addChallenge)
        app.post("/api/users/{user-id}/challenges/{challenge-id}", ChallengeController::joinChallenge)

        // ---------- Page guards ----------
        app.before("/users") { ctx ->
            if (disableAuth) return@before
            Auth.requireAdminPage(ctx)
        }

        app.before("/activities") { ctx ->
            if (disableAuth) return@before
            Auth.requireAdminPage(ctx)
        }

        app.before("/users/{user-id}") { ctx ->
            if (disableAuth) return@before
            Auth.requireLoginPage(ctx)
        }

        app.before("/users/{user-id}/activities") { ctx ->
            if (disableAuth) return@before
            Auth.requireLoginPage(ctx)
        }

        app.before("/users/{user-id}/calories") { ctx ->
            if (disableAuth) return@before
            Auth.requireLoginPage(ctx)
        }

        app.before("/challenges") { ctx ->
            if (disableAuth) return@before
            Auth.requireLoginPage(ctx)
        }

        // ---------- Vue pages ----------
        app.get("/", VueComponent("<home-page></home-page>"))
        app.get("/users", VueComponent("<user-overview></user-overview>"))
        app.get("/users/{user-id}", VueComponent("<user-profile></user-profile>"))
        app.get("/users/{user-id}/activities", VueComponent("<user-activity-overview></user-activity-overview>"))
        app.get("/activities", VueComponent("<activity-overview></activity-overview>"))
        app.get("/login", VueComponent("<login-page></login-page>"))
        app.get("/users/{user-id}/bmi", VueComponent("<bmi-page></bmi-page>"))
        app.get("/users/{user-id}/calories", VueComponent("<calorie-page></calorie-page>"))
        app.get("/challenges", VueComponent("<challenge-overview></challenge-overview>"))
        app.get("/signup", VueComponent("<signup-page></signup-page>"))
    }

    private fun getRemoteAssignedPort(): Int {
        val remotePort = System.getenv("PORT")
        return if (remotePort != null) Integer.parseInt(remotePort) else 8080
    }

    fun getJavalinService(): Javalin {
        registerRoutes(app)
        return app
    }
}
