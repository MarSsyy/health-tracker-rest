package ie.setu.helpers

import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.domain.repository.UserDAO
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
val validName = "Test User 1"
val validEmail = "testuser1@test.com"


val users = arrayListOf<User>(
    User(name = "Alice Wonderland", email = "alice@wonderland.com", id = 1, password = "testpassword", height = 1.70, weight = 70.0),
    User(name = "Bob Cat", email = "bob@cat.ie", id = 2, password = "testpassword", height = 1.70, weight = 70.0),
    User(name = "Mary Contrary", email = "mary@contrary.com", id = 3, password = "testpassword", height = 1.70, weight = 70.0),
    User(name = "Carol Singer", email = "carol@singer.com", id = 4, password = "testpassword", height = 1.70, weight = 70.0)
)
