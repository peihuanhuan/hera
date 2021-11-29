package net.peihuan.hera.persistent.po

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import net.peihuan.hera.domain.annotation.NoArg
import java.util.*

@NoArg
@TableName("user_invitation_ship")
data class UserInvitationShipPO (
    @TableId
    var id: Long? = null,
    var appid: String,
    val openid: String,
    val channel: Long,
    @TableField("`desc`")
    val desc: String,
    val inviter: String,
    val createTime: Date? = null,
)