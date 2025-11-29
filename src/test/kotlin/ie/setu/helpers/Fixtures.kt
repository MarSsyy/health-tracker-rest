package ie.setu.helpers

import ie.setu.domain.User

val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
val validName = "Test User 1"
val validEmail = "testuser1@test.com"

val users = arrayListOf<User>(
    User(name = "Alice Wonderland", email = "alice@wonderland.com", id = 1, password = "testpassword"),
    User(name = "Bob Cat", email = "bob@cat.ie", id = 2, password = "testpassword"),
    User(name = "Mary Contrary", email = "mary@contrary.com", id = 3, password = "testpassword"),
    User(name = "Carol Singer", email = "carol@singer.com", id = 4, password = "testpassword"),
)
