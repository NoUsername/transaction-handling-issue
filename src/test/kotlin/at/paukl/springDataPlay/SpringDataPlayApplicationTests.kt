package at.paukl.springDataPlay

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import java.util.stream.Collectors
import java.util.stream.IntStream


@TestMethodOrder(MethodOrderer.MethodName::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestAccess {

    val PARALLISM = 4

    @LocalServerPort
    private val port = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate


    @Test
    fun `Test 01 parallel plain`() {
        callParallel("/")
    }

    @Test
    fun `Test 02 parallel nested`() {
        // NOTE: this will fail because of the blocking waiting for a new connection
        //  when the db-pool has actually run out (we try 6 parallel requests, each request uses 2 connections
        callParallel("/nested")
    }

    private fun callParallel(url: String) {
        val results = IntStream.range(0, PARALLISM)
            .parallel()
            .mapToObj {
                println("HTTP call #${it} start")
                // slight artificial delay - so server actually starts executing before next request is triggered
                // not really necessary, but makes the scenario more realistic - might even let the server finish 1 request
                // before it becomes completely blocked in the nested scenario
                Thread.sleep(it * 10L)
                val result = restTemplate.getForEntity("http://localhost:$port$url", String::class.java)
                println("HTTP call #${it} finish")
                return@mapToObj result
            }
            .collect(Collectors.toList())

        assertThat(results)
            .`as`("all requests must succeed with 200 OK")
            .extracting({ it.statusCodeValue })
            .containsOnly(Tuple.tuple(200))
    }

}
