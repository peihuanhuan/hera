package net.peihuan.hera.domain

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KClass


@Component
class SpringContextHolder : ApplicationContextAware {


    companion object {
        private lateinit var applicationContext: ApplicationContext

        fun <T> getBean(name: String): T {
            return applicationContext.getBean(name) as T
        }

        fun <T: Any> getBean(clazz: KClass<T>): T {
            return applicationContext.getBean(clazz.java)
        }

    }


    override fun setApplicationContext(context: ApplicationContext) {
        applicationContext = context
    }
}