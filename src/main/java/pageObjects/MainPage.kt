package pageObjects

import framework.Commands
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.PageFactory
import org.testng.Assert

class MainPage(val driver: WebDriver) {

    var pageSource: String

    init {
        waitForPageLoad(false)
        PageFactory.initElements(driver, this)
        pageSource = driver.pageSource
    }

    var currentNames: MutableList<String> = mutableListOf()

    @FindBy(className = "text-muted")
    var title: WebElement? = null

    fun checkTitle(){
        Commands.assertTextEquals(title, "name game", "Asserting that the title is 'name game'")
    }

    @FindBy(tagName = "h1")
    var name: WebElement? = null
    var nameText: String = name!!.text

    @FindBy(className = "photo")
    var photos: List<WebElement?>? = null

    fun selectCorrectName(){
        for (photo in photos!!){
            if (photo!!.findElement(By.className("name")).text == nameText){
                photo.click()
                Assert.assertTrue(photo.getAttribute("class").contains("correct"),
                        "The correctly selected image is not displaying the green overlay")
            }
        }
    }

    fun selectIncorrectName(){
        for (photo in photos!!){
            if (photo!!.findElement(By.className("name")).text != nameText
            && !photo.getAttribute("class").contains("wrong")){
                photo.click()
                Assert.assertTrue(photo.getAttribute("class").contains("wrong"),
                        "The incorrectly selected image is not displaying the red overlay")
            }
        }
    }

    @FindBy(className = "attempts")
    var attempts: WebElement? = null

    @FindBy(className = "correct")
    var correct: WebElement? = null

    @FindBy(className = "streak")
    var streak: WebElement? = null

    fun waitForPageLoad(newPage: Boolean){
        Commands.waitForURL(driver, "name-game")

        //Wait until all images have been rendered on the page
        //No images appear immediately on page load
        var i = 0

        do{
            if (!newPage){  //Site loaded first time
                if (!driver.pageSource.contains(".jpg")){  //images are not loaded yet
                    Commands.waitForSeconds(1000)
                    i++
                } else {  //images are loaded
//                    PageFactory.initElements(driver, this::class)
                    pageSource = driver.pageSource  //set pagesource
                    System.out.println(photos)
                    System.out.print("-------------")
                    System.out.println(driver.findElements(By.className("photo"))[2].text)
//                    setCurrentNames()
                    break
                }
            } else {
                if (driver.findElement(By.id("gallery")).text.contains("wrong") ||
                        driver.findElement(By.id("gallery")).text.contains("correct")){
                    Commands.waitForSeconds(1000)
                    i++
                } else {
                    pageSource = driver.pageSource  //set pagesource
                    System.out.println(photos)
                    System.out.print("-------------")
                    System.out.println(driver.findElements(By.className("photo"))[2].text)
//                    setCurrentNames()
                    //Reset page elements
                    break
                }
            }
        } while (i < 10)




//        System.out.println(pageSource)
//        Commands.waitForSeconds(5000)
//        System.out.println(driver.pageSource)



//        Commands.waitForElement(driver, photos!![photos!!.lastIndex])
//        System.out.println(nameText)
//        selectCorrectName()
//        Commands.waitForSeconds(10000)
//        Commands.waitForElement(driver, photos!![photos!!.lastIndex])
//        System.out.println(nameText)

        //wait for name to change
        //wait for images to change
//        var i = 0
//        do{
//            if ()
//            Commands.waitForSeconds(1000)
//            i++
//        } while (i < 10)
    }

    fun setCurrentNames(){
        var i = 0

        for (photo in photos!!){
            currentNames.add(i, photo!!.findElement(By.className("name")).text)
            i++
        }
    }

//    fun reInitElements(): Boolean{
//
//    }
}