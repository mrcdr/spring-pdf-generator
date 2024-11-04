package com.example.pdfgenerator

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Playwright
import org.springframework.stereotype.Component

@Component
class BrowserAdapter : AutoCloseable {
    private final val playwright: Playwright
    private final val browser: Browser

    init {
        var playwright: Playwright? = null
        var browser: Browser? = null
        try {
            playwright = Playwright.create()
            browser = playwright.chromium().launch()
        } catch (e: Exception) {
            playwright?.close()
            throw e
        }

        this.playwright = playwright
        this.browser = browser
    }

    fun getNewContext(): BrowserContext = browser.newContext()

    override fun close() {
        browser.close()
        playwright.close()
    }
}