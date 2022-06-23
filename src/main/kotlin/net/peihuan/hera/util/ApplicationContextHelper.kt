package com.alibaba.cola.domain

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

/**
 * ApplicationContextHelper
 *
 * @author Frank Zhang
 * @date 2020-11-14 1:58 PM
 */
@Component
class ApplicationContextHelper : ApplicationContextAware {
    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        Companion.applicationContext = applicationContext
    }

    companion object {
        var applicationContext: ApplicationContext? = null
            private set

        fun <T> getBean(targetClz: Class<T>): T {
            var beanInstance: T? = null
            //优先按type查
            try {
                beanInstance = applicationContext!!.getBean(targetClz)
            } catch (e: Exception) {
            }
            //按name查
            if (beanInstance == null) {
                var simpleName = targetClz.simpleName
                //首字母小写
                simpleName = Character.toLowerCase(simpleName[0]).toString() + simpleName.substring(1)
                beanInstance = applicationContext!!.getBean(simpleName) as T
            }
            if (beanInstance == null) {
                throw RuntimeException("Component $targetClz can not be found in Spring Container")
            }
            return beanInstance
        }

        fun getBean(claz: String?): Any {
            return applicationContext!!.getBean(claz!!)
        }

        fun <T> getBean(name: String?, requiredType: Class<T>): T {
            return applicationContext!!.getBean(name!!, requiredType)
        }

        // fun <T> getBean(requiredType: Class<T>?, vararg params: Any?): T {
        //     return applicationContext!!.getBean(requiredType, *params)
        // }
    }
}