package net.peihuan.hera

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@EnableAsync
@EnableScheduling
@EnableFeignClients
@SpringBootApplication
@ConfigurationPropertiesScan
@MapperScan("net.peihuan.hera.persistent.mapper")
class HeraApplication

fun main(args: Array<String>) {
	runApplication<HeraApplication>(*args)
}
