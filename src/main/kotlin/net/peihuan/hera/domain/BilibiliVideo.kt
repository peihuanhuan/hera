package net.peihuan.hera.domain

import net.peihuan.hera.domain.annotation.NoArg

@NoArg
data class BilibiliVideo (
    val bvid: String,
    val page: String?,
)