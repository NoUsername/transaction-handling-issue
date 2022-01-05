package at.paukl.springDataPlay

import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DemoController(
    private val demoService: DemoService
) {

    @GetMapping("/")
    fun dummy(): String {
        LOG.info("dummy() before service call")
        demoService.requiresNewTransaction()
        LOG.info("dummy() after service call")
        return "also try accessing /nested"
    }

    @Transactional
    @GetMapping("/nested")
    fun nested(): String {
        LOG.info("nested() before service call")
        Thread.sleep(100L) // simulate some work here to make reproducing the scenario easier

        demoService.requiresNewTransaction()
        LOG.info("nested() after service call")
        return "done - try calling me multiple times in parallel"
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(DemoController.javaClass)
    }
}