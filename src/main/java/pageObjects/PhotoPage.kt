package pageObjects

import framework.Commands
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.testng.Assert

class PhotoPage(private val driver: WebDriver) {
    private var pageSource: String = driver.pageSource

    private var nameText: String = ""                           //The name presented in the question
    private var photos: List<WebElement>? = null                //List of photos as divs
    private var photoNames: MutableList<String> = ArrayList()   //List of names from each image group

    //Counters according to the page
    private var numOfAttempts: Int = 0
    private var numOfCorrect: Int = 0
    private var numOfStreak: Int = 0

    //Counters according to the math
    private var countAttempts: Int = 0
    private var countCorrect: Int = 0
    private var countStreak: Int = 0

    private var peopleCollection: MutableList<People> = ArrayList()     //List of all people asked in the question

    /*
    Typically, I would initialize web elements using the @FindBy attribute in TestNG (it's more elegant in my
    opinion). However, because the page never changes or redirects, I need to re-initialize the elements in order for
    them to be accurate.  If I don't do this, the elements will become stale when the next question appears.
     */
    private fun initElements(){
        Commands.printStep("initElements", "Re-initializing page elements")
        pageSource = driver.pageSource
        nameText = driver.findElement(By.id("name")).text
        photos = driver.findElements(By.className("photo"))
        setPhotoNames()
        initCounters(true)
    }

    private fun initCounters(allElements: Boolean){
        if (!allElements){
            Commands.printStep("initCounters", "Re-initializing counters")
        }
        numOfAttempts = driver.findElement(By.className("attempts")).text.toInt()
        numOfCorrect = driver.findElement(By.className("correct")).text.toInt()
        numOfStreak = driver.findElement(By.className("streak")).text.toInt()
    }

    fun checkTitle(){
        Commands.assertTextEquals(driver.findElement(By.className("text-muted")),
                "name game", "Asserting that the title is 'name game'")
    }

    fun selectCorrectName(){
        var clicked = false         //flag if correct name was actually clicked
        for (photo in photos!!){
            if (photo.findElement(By.className("name")).text == nameText){
                Commands.printStep("selectCorrectName", "Selecting the correct image")
                photo.click()
                Commands.waitForSeconds(500)
                Assert.assertTrue(photo.getAttribute("class").contains("correct"),
                        "The correctly selected image is not displaying the green overlay")
                updateCounters(true)
                clicked = true
                break
            }
        }

        if (!clicked){ Assert.fail("Unable to click on the correct person in the image group") }
    }

    fun selectIncorrectName(){
        var clicked = false         //flag if an incorrect name was actually clicked
        for (photo in photos!!){
            if (photo.findElement(By.className("name")).text != nameText
                    && !photo.getAttribute("class").contains("wrong")){
                Commands.printStep("selectIncorrectName", "Selecting an incorrect image")
                photo.click()
                Commands.waitForSeconds(500)
                Assert.assertTrue(photo.getAttribute("class").contains("wrong"),
                        "The incorrectly selected image is not displaying the red overlay")
                updateCounters(false)
                clicked = true
                break
            }
        }

        if (!clicked){ Assert.fail("Unable to click on an incorrect person in the image group") }
    }

    private fun updateCounters(correctAnswer: Boolean){
        Commands.printStep("updateCounters", "Updating programmatic counters")
        if (correctAnswer){  //answered correctly
            //adjust counters
            countCorrect++
            countAttempts++
            countStreak++
        } else if (!correctAnswer) {  //answered incorrectly
            //adjust counters
            countAttempts++  //May need to be adjusted based on definition of incorrect answer
            countStreak = 0
        } else {
            Assert.fail("Counters cannot be updated. Answer must be correct or incorrect (true or false)")
        }
    }

    fun assertCounters(){
        Commands.printStep("assertCounters", "Asserting that page counters are correct")
        initCounters(false)
        Assert.assertTrue(countCorrect == numOfCorrect, "The page displays '$numOfCorrect' for the " +
                "Correct counter but it should be '$countCorrect'")
        Assert.assertTrue(countAttempts == numOfAttempts, "The page displays '$numOfAttempts' for " +
                "the Attempts counter but it should be '$countAttempts'")
        Assert.assertTrue(countStreak == numOfStreak, "The page displays '$numOfStreak' for the " +
                "Streak counter but it should be '$countStreak'")
    }

    fun waitForPageLoad(nextPage: Boolean){
        if (!nextPage) { //page loaded for the first time
            var i = 0

            do {
                if (!driver.pageSource.contains(".jpg")){  //images are not loaded yet on the page
                    Commands.waitForSeconds(1000)
                    i++
                } else {  //images are loaded on the page
                    initElements()
                    printCurrentNames()
                    break
                }
            } while (i < 10)  //Wait up to 10 seconds

            failOnImages(i)
        } else { //new set of photos
            var i = 0

            do {
                var samePagePhotoNames: MutableList<String> = ArrayList()

                if (driver.pageSource.contains(".jpg")){  //images are loaded on the page
                    //Check that name in question has changed AND that photos have changed
                    val samePhotos: List<WebElement> = driver.findElements(By.className("photo"))
                    for (samePagePhoto in samePhotos){
                        samePagePhotoNames.add(samePagePhoto.findElement(By.className("name")).text)
                    }

                    if (nameText == driver.findElement(By.id("name")).text || photoNames == samePagePhotoNames){
                        Commands.waitForSeconds(1000)
                        i++
                    } else {
                        initElements()
                        printCurrentNames()
                        break
                    }
                } else {  //images are not loaded yet on the page
                    Commands.waitForSeconds(1000)
                    i++
                }
            } while (i < 10)  //Wait up to 10 seconds

            failOnImages(i)
        }
    }

    fun capturePerson(correctAnswer: Boolean){
        //Keep track of info for each person (name, appearances, number correct, and percentage correct)
        var found = false
        for (person in peopleCollection){
            if (nameText == person.name){
                person.appearances++
                if (correctAnswer){
                    person.correct++
                }
                person.percentage = getPercentageCorrect(person)
                found = true
                break
            }
        }

        if (!found){
            val thisPerson = People()
            thisPerson.name = nameText
            thisPerson.appearances++
            if (correctAnswer){
                thisPerson.correct++
            }
            thisPerson.percentage = getPercentageCorrect(thisPerson)
            peopleCollection.add(thisPerson)
        }
    }

    fun checkAppearances(){
        /*Asserting that the person with both the highest percentage correct and most appearances appear less than the
        person with the lowest percentage correct and the least appearances */
        var highestPercentageCorrect = peopleCollection[0]
        var lowestPercentageCorrect = peopleCollection[0]


        for (person in peopleCollection){
            if (person.percentage >= highestPercentageCorrect.percentage &&
                    person.appearances >= highestPercentageCorrect.appearances){
                highestPercentageCorrect = person
            } else if (person.percentage <= lowestPercentageCorrect.percentage &&
                    person.appearances <= lowestPercentageCorrect.appearances){
                lowestPercentageCorrect = person
            }
        }

        println("Highest percentage correct: \n" +
                "   Name: ${highestPercentageCorrect.name}\n" +
                "   Correct: ${highestPercentageCorrect.correct}\n" +
                "   Appearances: ${highestPercentageCorrect.appearances}\n" +
                "   Percentage Correct: ${highestPercentageCorrect.percentage}")

        println("Lowest percentage correct: \n" +
                "   Name: ${lowestPercentageCorrect.name}\n" +
                "   Correct: ${lowestPercentageCorrect.correct}\n" +
                "   Appearances: ${lowestPercentageCorrect.appearances}\n" +
                "   Percentage Correct: ${lowestPercentageCorrect.percentage}")

        if (highestPercentageCorrect.appearances >= lowestPercentageCorrect.appearances){
            Assert.fail("Images for correctly selected people are being displayed as or more frequently than " +
                    "images for incorrectly selected people.")
        }
    }

    fun printPeopleCollection(){
        for (person in peopleCollection){
            println("{\n   Name: ${person.name}\n   Appearances: ${person.appearances}\n   " +
                    "Correct: ${person.correct}\n}")
        }
    }

    private fun setPhotoNames(){
        for (photo in photos!!){
            photoNames.add(photo.findElement(By.className("name")).text)
        }
    }

    private fun printCurrentNames(){
        println("Current names on the page: ")
        for (photo in photos!!){
            println(photo.findElement(By.className("name")).text)
        }
    }

    private fun getPercentageCorrect(thisPerson: People): Double{
        when (thisPerson.correct){
            0 -> return 0.0
            else -> return (thisPerson.correct.toDouble() / thisPerson.appearances.toDouble()) * 100
        }
    }

    private fun failOnImages(i: Int){
        if (i >= 10){ Assert.fail("Images are not being loaded on the page correctly") }
    }
}

class People{
    var name: String = ""
    var correct: Int = 0
    var appearances: Int = 0
    var percentage: Double = 0.0
}