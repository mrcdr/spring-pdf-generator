package com.example.pdfgenerator

import com.microsoft.playwright.Page
import com.microsoft.playwright.Page.WaitForConsoleMessageOptions
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.function.Predicate


@RestController
class APIController(private val browserAdapter: BrowserAdapter) {
    @RequestMapping(value = ["/pdf"])
    private fun getPDF(@RequestParam name: String, @RequestParam email: String): ResponseEntity<ByteArray> {
        browserAdapter.getNewContext().use { browserContext ->
            browserContext.newPage().use { page ->
                /* Prepare page to be loaded */
                page.setViewportSize(400, 800)

                /* Initialize React */
                page.waitForConsoleOutput("INJECTOR-PREPARED") { page.navigate("http://localhost:8080/index.html") }

                /* Inject User data into React */
                val user = User(name, email)
                val json = Json.encodeToString(user)
                page.waitForConsoleOutput("DATA-INJECTED") { page.evaluate("window.injectData($json)") }

                /* Generate PDF */
                val pdfData = page.toPDF()

                /* Return PDF as response */
                val headers = HttpHeaders()
                headers.add("Content-Type", "application/pdf")
                return ResponseEntity(pdfData, headers, HttpStatus.OK)
            }
        }
    }
}

private fun Page.waitForConsoleOutput(expected: String, initiator: Runnable) {
    val options = WaitForConsoleMessageOptions()
    options.predicate = Predicate { output -> output.text() == expected }
    this.waitForConsoleMessage(options, initiator)
}

private fun Page.toPDF(): ByteArray {
    /* Set PDF page size to drawing */
    val wholeHTML = this.locator("html")
    val width = wholeHTML.boundingBox().width
    val height = wholeHTML.boundingBox().height

    val pdfOptions = Page.PdfOptions()
    pdfOptions.printBackground = true
    pdfOptions.width = width.toString() + "px"
    pdfOptions.height = height.toString() + "px"

    /* Generate PDF */
    return this.pdf(pdfOptions)
}