package net.peihuan.hera.task

import net.peihuan.hera.feign.service.HealthchecksService
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


/**
 * https://github.com/healthchecks/healthchecks
 * 探活
 */
@Component
class HealthCheckTask(
    val healthchecksService: HealthchecksService
) {

    @Value("\${healthchecks.token}")
    private val token: String? = null

    @Scheduled(fixedDelay = 60_000)
    fun scheduled() {
        healthchecksService.ping(token?:"")
    }

}