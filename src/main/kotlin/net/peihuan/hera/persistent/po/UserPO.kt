package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableLogic
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.domain.annotation.NoArg
import java.util.*

@NoArg
@TableName("user")
data class UserPO (
    @TableId
    var id: Long? = null,
    var appid: String? = null,
    val openid: String,
    val unionid: String? = null,
    var nickname: String? = null,
    var sex: Int? = null,
    var province: String? = null,
    var city: String? = null,
    var country: String? = null,
    var headimgurl: String? = null,
    var privilege: String? = null,
    var remark: String? = null,
    var groupid: Int? = null,
    val createTime: Date? = null,
    val updateTime: Date? = null,

    @TableLogic
    val deleted: Boolean? = null
)