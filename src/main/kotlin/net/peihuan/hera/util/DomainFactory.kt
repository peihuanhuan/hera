package net.peihuan.hera.util

import com.alibaba.cola.domain.ApplicationContextHelper

class DomainFactory {

    companion object fun <T> create(entityClz: Class<T>): T {
        return ApplicationContextHelper.getBean(entityClz)
    }
}