package ie.setu.selenium

import ie.setu.config.JavalinConfig
import ie.setu.domain.db.Users
import ie.setu.domain.repository.UserDAO
import ie.setu.helpers.TestDatabaseConfig
import ie.setu.helpers.users
import io.javalin.Javalin
import io.javalin.testtools.JavalinTest
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.*
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.time.Duration

val user1 = users[0]
val user2 = users[1]
val user3 = users[2]

class AddUserTest {

    private lateinit var driver: WebDriver
    private lateinit var js: JavascriptExecutor
    private var acceptNextAlert = true
    private val verificationErrors = StringBuffer()
    private lateinit var app: Javalin // Javalin app instance


    companion object {
        @BeforeAll
        @JvmStatic
        fun setupInMemoryDatabase() {
            TestDatabaseConfig.connect() // same approach as API tests
        }
    }

    @BeforeEach
    fun setUp() {
        //Reset the h2 database and give it starting users and activities
        TestDatabaseConfig.reset()
        seedTestData()
        val options = ChromeOptions()
        options.addArguments("--headless=new")
        options.addArguments("--no-sandbox")
        options.addArguments("--disable-dev-shm-usage")
        driver = ChromeDriver(options)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60))
        js = driver as JavascriptExecutor
    }


    private fun seedTestData() {
        transaction {
            populateUserTable()
        }
    }

    internal fun populateUserTable(): UserDAO{
        SchemaUtils.create(Users)
        val userDAO = UserDAO()
        userDAO.save(user1)
        userDAO.save(user2)
        userDAO.save(user3)
        return userDAO
    }

    @Test
    fun testAddUser() {

        val app: Javalin = JavalinConfig().getJavalinService()

        JavalinTest.test(app) { _, client ->

            transaction {

                driver.get("${client.origin}/users")

                driver.findElement(By.xpath("//main[@id='main-vue']/div/div/div/div/div/div/div/div[2]/button")).click()

                driver.findElement(By.name("name")).apply {
                    click()
                    clear()
                    sendKeys("test")
                }

                driver.findElement(By.name("email")).apply {
                    click()
                    clear()
                    sendKeys("test@test.com")
                }

                driver.findElement(By.name("password")).apply {
                    click()
                    clear()
                    sendKeys("1234")
                }

                driver.findElement(By.xpath("//main[@id='main-vue']/div/div/div/div/div/div[2]/button")).click()
            }
        }
    }


    @AfterEach
    fun tearDown() {
        driver.quit()
        val verificationErrorString = verificationErrors.toString()
        if (verificationErrorString.isNotEmpty()) {
            throw AssertionError(verificationErrorString)
        }
    }

    private fun isElementPresent(by: By): Boolean =
        try { driver.findElement(by); true } catch (e: NoSuchElementException) { false }

    private fun isAlertPresent(): Boolean =
        try { driver.switchTo().alert(); true } catch (e: NoAlertPresentException) { false }

    private fun closeAlertAndGetItsText(): String {
        val alert = driver.switchTo().alert()
        val alertText = alert.text
        if (acceptNextAlert) alert.accept() else alert.dismiss()
        acceptNextAlert = true
        return alertText
    }
}
