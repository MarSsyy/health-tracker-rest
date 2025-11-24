package ie.setu.domain

//Blueprint for the API to use
data class User(
    val id: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val age: Int? = null,
    val gender: String? = null
)

