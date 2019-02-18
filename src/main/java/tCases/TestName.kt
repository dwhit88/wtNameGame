package tCases

import org.openqa.selenium.WebDriver
import org.testng.annotations.*
import pageObjects.PhotoPage
import setup.RunConfig

class TestName {

    var driver: WebDriver? = null

    /*
    In order to reset the counters, I need to initialize and destroy a new WebDriver for every test. Selenium doesn't
    handle clearing the browser's cache very well and writing some other script may be out of scope for this exercise.
     */
    @BeforeMethod
    fun runConfig(){
        println("Creating new WebDriver instance")
        driver = RunConfig().getWebDriver()

    }

    @AfterMethod
    fun afterTest(){
        println("Destroying WebDriver instance")
        driver!!.quit()
    }

    @Test(priority = 1)
    fun verifyTitle(){
        PhotoPage(driver!!).apply {
            waitForPageLoad(false)
            checkTitle()
        }
    }

    @Test(priority = 2)
    fun verifyStreakOnCorrectSelections(){
        PhotoPage(driver!!).apply {
            waitForPageLoad(false)
            repeat(10){
                selectCorrectName()
                waitForPageLoad(true)
            }
            assertCounters()
        }
    }

    @Test(priority = 3)
    fun verifyStreakReset(){
        PhotoPage(driver!!).apply {
            waitForPageLoad(false)
            repeat(3){
                repeat(5){
                    selectCorrectName()
                    waitForPageLoad(true)
                }
                selectIncorrectName()
                assertCounters()
            }
        }
    }

    @Test(priority = 4)
    fun verifyCountersRandomSelections(){
        PhotoPage(driver!!).apply {
            waitForPageLoad(false)
            repeat(10){
                when ((0..1).shuffled().first()){
                    0 -> {
                        selectIncorrectName()
                        selectCorrectName()
                        waitForPageLoad(true)
                    }
                    1 -> {
                        selectCorrectName()
                        waitForPageLoad(true)
                    }
                }
            }
            assertCounters()
        }
    }

    @Test(priority = 5)
    fun verifyIncorrectAppearMoreFrequently(){
        PhotoPage(driver!!).apply {
            waitForPageLoad(false)
            repeat(50){
                when ((0..1).shuffled().first()){
                    0 -> {
                        selectIncorrectName()
                        capturePerson(false)
                        selectCorrectName()
                        waitForPageLoad(true)
                    }
                    1 -> {
                        selectCorrectName()
                        capturePerson(true)
                        waitForPageLoad(true)
                    }
                }
            }
            printPeopleCollection()
            checkAppearances()
        }
    }


}