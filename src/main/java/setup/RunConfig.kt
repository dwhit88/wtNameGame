package setup

import framework.Browsers
import framework.Drivers
import org.openqa.selenium.WebDriver

class RunConfig(var browser: String = Browsers.chrome,
                var url: String = "http://www.ericrochester.com/name-game/",
                var headless: Boolean = true) {

    fun getWebDriver(): WebDriver{
        return Drivers.driverInit(browser, url, headless)
    }
}