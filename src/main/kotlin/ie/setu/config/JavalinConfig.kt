package ie.setu.config

import ie.setu.controllers.ActivityController
import ie.setu.controllers.UserController
import ie.setu.utils.jsonObjectMapper
import io.javalin.Javalin
import io.javalin.json.JavalinJackson

class JavalinConfig {

    val app = Javalin.create(
        { config ->
            config.jsonMapper(JavalinJackson(jsonObjectMapper()))
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
