package net.peihuan.hera.domain

import net.peihuan.hera.constants.ZyOrderSourceEnum
import net.peihuan.hera.domain.annotation.NoArg

@NoArg
class Channel(
    var id: Long,
    var source: ZyOrderSourceEnum,
    var zyAllProductUrl: String,
)