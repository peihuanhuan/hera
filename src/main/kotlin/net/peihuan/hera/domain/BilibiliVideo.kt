package net.peihuan.hera.domain

import net.peihuan.hera.domain.annotation.NoArg

@NoArg
data class BilibiliVideo (
    var aid: String? = null,
    var bvid: String,
    var cid: String? = null,
    var sid: String? = null,
    val epid: Int? = null,
    var page: String? = null,
    var title: String? = null,
    var partTitle: String? = null,
    var duration: Int? = null,
    var mid: String? = null
) {
    fun x() {

    }
}