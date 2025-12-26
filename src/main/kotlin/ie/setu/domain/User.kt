package ie.setu.domain

//Blueprint for the API to use
data class User(
    var id: Int,
    val name: String,
    val email: String,
    val password: String,
    val role: String = "USER",
    val age: Int? = null,
    val gender: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val activityLevel: String? = null)

