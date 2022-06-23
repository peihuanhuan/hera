package net.peihuan.hera.annotations

import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.lang.annotation.Inherited

@Inherited
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE)
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
annotation class Entity()
