# WT Name Game

This is automation code to test the WT test project. All of it is written in Kotlin.

Steps to install and run
- Clone the repository and open the project in IntelliJ
- If you see a Gradle prompt, simply check "Use auto-import" and "Use gradle wrapper task configuration" and click OK
- In the IntelliJ terminal, type the following to ensure that the gradle build is complete
    > gradle clean build test
- To run the entire test suite, navigate to src/main/java/tSuite/TestSuite, click the green play button next to the class name in the code, and click "Run 'TestSuite'".

Tips for running the test suite
- Test suite is able to run in Chrome, Firefox, and Safari and can even run headless in Chrome and Firefox
- To adjust these settings, simply navigate to src/main/java/setup/RunConfig and change the following:
    > For Browser -> "Browsers.chrome" or "Browsers.firefox" or "Browsers.safari"

    > For Headless -> true or false
- No need to worry about downloading the right drivers. A depencency within the SeleniumToolkit library already handles it for you by checking the current version of the target browser and downloading the most compatible driver.
