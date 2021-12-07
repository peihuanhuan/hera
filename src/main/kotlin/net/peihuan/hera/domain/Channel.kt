package net.peihuan.hera.domain

import net.peihuan.hera.constants.OrderSourceEnum
import net.peihuan.hera.domain.annotation.NoArg

@NoArg
class Channel(
    var id: Long,
    var source: OrderSourceEnum,
    var zyAllProductUrl: String,
)