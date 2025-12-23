package ie.setu.controllers

import ie.setu.domain.User
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.jsonToObject
import io.javalin.http.Context

object UserController {

    private val userDAO = UserDAO()

    fun getAllUsers(ctx: Context) {
        val users = userDAO.getAll()
        if (users.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(users)
    }

    fun getUserByUserId(ctx: Context) {
        val user = userDAO.findById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }


    fun addUser(ctx: Context) {
        val user : User = jsonToObject(ctx.body())
        val userId = userDAO.save(user)
        if (userId != null) {
            user.id = userId
            ctx.json(user)
            ctx.status(201)
        }
    }

    fun getUserByEmail(ctx: Context) {
        val user = userDAO.findByEmail(ctx.pathParam("email"))
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }


    fun deleteUserById(ctx: Context){
        if (userDAO.delete(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }
    fun updateUserById(ctx: Context){
        val foundUser : User = jsonToObject(ctx.body())
        if ((userDAO.update(id = ctx.pathParam("user-id").toInt(), user=foundUser)) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    fun getUserBmi(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        val user = userDAO.findById(userId)

        if (user == null) {
            ctx.status(404).json("User not found")
            return
        }

        val height = user.height
        val weight = user.weight

        if (height == null || weight == null) {
            ctx.status(400).json("Height and weight must be set to calculate BMI.")
            return
        }

        val bmi = weight / (height * height)
        val bmiRounded = String.format("%.2f", bmi)

        ctx.status(200).json(
            mapOf(
                "userId" to userId,
                "height" to height,
                "weight" to weight,
                "bmi" to bmiRounded
            )
        )
    }

    fun getUserCalories(ctx: Context) {

        val userId = ctx.pathParam("user-id").toInt()
        val user = userDAO.findById(userId)

        if (user == null) {
            ctx.status(404).json("User not found.")
            return
        }

        val height = user.height
        val weight = user.weight
        val age = user.age
        val gender = user.gender?.lowercase()
        val activity = user.activityLevel?.lowercase()

        if (height == null || weight == null || age == null || gender == null || activity == null) {
            ctx.status(400).json("Height, weight, age, gender and activity level are required.")
            return
        }

        val heightCm = height * 100

        // BMR formula (Mifflin–St Jeor Equation)
        val bmr = when (gender) {
            "male" -> 10 * weight + 6.25 * heightCm - 5 * age + 5
            "female" -> 10 * weight + 6.25 * heightCm - 5 * age - 161
            else -> {
                ctx.status(400).json("Gender must be 'male' or 'female'.")
                return
            }
        }

        val multiplier = when (activity) {
            "light" -> 1.375
            "moderate" -> 1.55
            "active" -> 1.725
            else -> {
                ctx.status(400).json("Invalid activity level.")
                return
            }
        }

        val maintenance = bmr * multiplier
        val gain = maintenance + 300
        val loss = maintenance - 300

        ctx.status(200).json(
            mapOf(
                "bmr" to String.format("%.0f", bmr),
                "maintenanceCalories" to String.format("%.0f", maintenance),
                "surplusCalories" to String.format("%.0f", gain),
                "deficitCalories" to String.format("%.0f", loss)
            )
        )
    }


}

