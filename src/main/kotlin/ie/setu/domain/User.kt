package ie.setu.domain

//Blueprint for the API to use
data class User (val id: Int,
                 val name:String,
                 val email:String,
                 val password:String,
                 val age:Int? = null,
                 val gender:String? = null
                )
