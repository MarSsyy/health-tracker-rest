package ie.setu.controllers

import ie.setu.domain.Activity
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.jsonObjectMapper
import ie.setu.utils.jsonToObject
import io.javalin.http.Context

object ActivityController {

    private val userDao = UserDAO()
    private val activityDAO = ActivityDAO()


    fun getAllActivities(ctx: Context) {
        val mapper = jsonObjectMapper()
        ctx.json(mapper.writeValueAsString(activityDAO.getAll()))
    }


    fun getActivitiesByUserId(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()

        if (userDao.findById(userId) != null) {
            val activities = activityDAO.findByUserId(userId)

            if (activities.isNotEmpty()) {
                val mapper = jsonObjectMapper()
                ctx.json(mapper.writeValueAsString(activities))
            }
        }
    }


    fun addActivity(ctx: Context) {
        val activity = jsonToObject<Activity>(ctx.body())
        val saved = activityDAO.save(activity)     // save returns Activity with id
        ctx.status(201).json(saved)
    }


    fun deleteActivitiesByUserId(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()

        if (userDao.findById(userId) != null) {
            activityDAO.deleteByUserId(userId)
            ctx.status(204)
        }
    }


    fun deleteActivityByActivityId(ctx: Context) {
        val activityId = ctx.pathParam("activity-id").toInt()

        if (activityDAO.findById(activityId) != null) {
            activityDAO.delete(activityId)
            ctx.status(204)
        }
    }


    fun updateActivityByActivityId(ctx: Context) {
        val activityId = ctx.pathParam("activity-id").toInt()
        val existingActivity = activityDAO.findById(activityId)

        if (existingActivity != null) {
            val updatedActivity = jsonToObject<Activity>(ctx.body())
            val finalActivity = updatedActivity.copy(id = activityId)

            activityDAO.update(activityId, finalActivity)
            ctx.status(200).json(finalActivity)
        }
    }


    fun getActivityByActivityId(ctx: Context) {
        val activityId = ctx.pathParam("activity-id").toInt()

        val activity = activityDAO.findById(activityId)
        if (activity != null) {
            ctx.json(activity)
        }
    }
}

