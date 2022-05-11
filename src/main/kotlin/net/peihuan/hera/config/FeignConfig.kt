package net.peihuan.hera.config

import feign.Logger
import feign.Retryer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class FeignConfig(
) {
    /**
     * NONE：默认的，不显示任何日志
     * BASIC：仅记录请求方法、URL、响应状态码及执行时间
     * HEADERS：出了BASIC中定义的信息之外，还有请求和响应的头信息
     * FULL：除了HEADERS中定义的信息之外，还有请求和响应的正文及元素
     */
    @Bean
    fun feginLoggerLevel(): Logger.Level {
        return Logger.Level.BASIC
    }

    @Bean
    fun feignRetryer(): Retryer? {
        return Retryer.Default(100, 1000, 5)
    }
}
