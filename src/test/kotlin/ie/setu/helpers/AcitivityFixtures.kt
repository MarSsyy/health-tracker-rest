package ie.setu.helpers

import ie.setu.domain.Activity
import org.joda.time.DateTime

val activity1 = Activity(
    id = 1,
    description = "Morning Run",
    duration = 30.5,
    calories = 250,
    started = DateTime.parse("2025-01-10T06:30:00.000Z"),
    userId = 1
)

val activity2 = Activity(
    id = 2,
    description = "Evening Walk",
    duration = 45.0,
    calories = 180,
    started = DateTime.parse("2025-01-11T19:00:00.000Z"),
    userId = 2
)

val activity3 = Activity(
    id = 3,
    description = "Cycling",
    duration = 60.0,
    calories = 600,
    started = DateTime.parse("2025-01-12T14:15:00.000Z"),
    userId = 3
)

val activities = listOf(activity1, activity2, activity3)
