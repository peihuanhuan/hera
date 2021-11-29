package net.peihuan.hera.domain

import net.peihuan.hera.domain.annotation.NoArg

@NoArg
data class InviterUser (
    val activityId: Long,
    val openid: String,
)