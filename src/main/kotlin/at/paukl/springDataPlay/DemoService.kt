package at.paukl.springDataPlay

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class DemoService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun requiresNewTransaction() {
        LOG.info("simulated db work - before")
        // simulate db work
        Thread.sleep(3000)
        LOG.info("simulated db work - after")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(DemoController.javaClass)
    }
}