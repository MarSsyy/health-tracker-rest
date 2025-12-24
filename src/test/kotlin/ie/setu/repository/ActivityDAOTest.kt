package ie.setu.repository

import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Users
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.helpers.TestDatabaseConfig
import ie.setu.helpers.activities
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test



val activity1 = activities[0]
val activity2 = activities[1]
val activity3 = activities[2]

class ActivityDAOTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setupInMemoryDatabase() {
            TestDatabaseConfig.connect()
        }
    }

    @BeforeEach
    fun resetDatabase() {
        TestDatabaseConfig.reset()
    }

    internal fun populateActivityTable(): ActivityDAO {

        SchemaUtils.create(Users, Activities)

        val userDAO = UserDAO()
        userDAO.save(user1)
        userDAO.save(user2)
        userDAO.save(user3)

        val activityDAO = ActivityDAO()
        activityDAO.save(activity1)
        activityDAO.save(activity2)
        activityDAO.save(activity3)
        return activityDAO
    }

    @Nested
    inner class ReadActivities {

        @Test
        fun `getting all activities from a populated table returns all rows`() {
            transaction {
                val activityDAO = populateActivityTable()
                assertEquals(3, activityDAO.getAll().size)
            }
        }

        @Test
        fun `get activity by id that doesn't exist, results in no activity returned`() {
            transaction {
                val activityDAO = populateActivityTable()
                assertEquals(null, activityDAO.findById(99))
            }
        }

        @Test
        fun `get activity by id that exists, results in correct activity returned`() {
            transaction {
                val activityDAO = populateActivityTable()
                val result = activityDAO.findById(activity1.id)
                assertActivitiesEqualIgnoringTimestamp(activity1, result!!)
            }
        }

        @Test
        fun `get all activities over empty table returns none`() {
            transaction {
                SchemaUtils.create(Users, Activities)
                val activityDAO = ActivityDAO()
                assertEquals(0, activityDAO.getAll().size)
            }
        }
    }

    @Nested
    inner class CreateActivities {

        @Test
        fun `multiple activities added to table can be retrieved successfully`() {
            transaction {
                val activityDAO = populateActivityTable()

                assertEquals(3, activityDAO.getAll().size)
                assertActivitiesEqualIgnoringTimestamp(activity1, activityDAO.findById(activity1.id)!!)
                assertActivitiesEqualIgnoringTimestamp(activity2, activityDAO.findById(activity2.id)!!)
                assertActivitiesEqualIgnoringTimestamp(activity3, activityDAO.findById(activity3.id)!!)
            }
        }
    }


    @Nested
    inner class DeleteActivities {

        @Test
        fun `deleting a non-existant activity in table results in no deletion`() {
            transaction {
                val activityDAO = populateActivityTable()

                assertEquals(3, activityDAO.getAll().size)
                activityDAO.delete(99)
                assertEquals(3, activityDAO.getAll().size)
            }
        }

        @Test
        fun `deleting an existing activity in table results in record being deleted`() {
            transaction {
                val activityDAO = populateActivityTable()

                assertEquals(3, activityDAO.getAll().size)
                activityDAO.delete(activity3.id)
                assertEquals(2, activityDAO.getAll().size)
            }
        }
    }


    @Nested
    inner class UpdateActivities {

        @Test
        fun `updating existing activity in table results in successful update`() {
            transaction {
                val activityDAO = populateActivityTable()

                val updatedActivity = Activity(
                    id = activity3.id,
                    description = "Updated Activity",
                    duration = 99.9,
                    calories = 999,
                    started = activity3.started,   // we ignore started in comparison
                    userId = activity3.userId
                )

                activityDAO.update(activity3.id, updatedActivity)
                val result = activityDAO.findById(activity3.id)

                assertActivitiesEqualIgnoringTimestamp(updatedActivity, result!!)
            }
        }

        @Test
        fun `updating non-existant activity in table results in no updates`() {
            transaction {
                val activityDAO = populateActivityTable()

                val updatedActivity = Activity(
                    id = 99,
                    description = "Should Not Update",
                    duration = 10.0,
                    calories = 10,
                    started = activity1.started,
                    userId = activity1.userId
                )

                activityDAO.update(99, updatedActivity)

                // no row with id 99, and still exactly 3 activities
                assertEquals(null, activityDAO.findById(99))
                assertEquals(3, activityDAO.getAll().size)
            }
        }
    }


    private fun assertActivitiesEqualIgnoringTimestamp(expected: Activity, actual: Activity) {
        assertEquals(expected.id, actual.id)
        assertEquals(expected.description, actual.description)
        assertEquals(expected.duration, actual.duration)
        assertEquals(expected.calories, actual.calories)
        assertEquals(expected.userId, actual.userId)
    }
}


