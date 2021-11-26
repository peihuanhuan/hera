package net.peihuan.hera.domain

import net.peihuan.hera.domain.annotation.NoArg

@NoArg
data class ActivityUser (
    val activityId: Int,
    val openidId: String,
)