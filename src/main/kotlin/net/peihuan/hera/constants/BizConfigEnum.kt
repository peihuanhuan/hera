package net.peihuan.hera.constants

enum class BizConfigEnum(val key: String, val desc: String) {
    SUBSCRIBE_REPLY_CONTENT("subscribe_reply_content", "关注后回复内容"),
    FIRST_SUBSCRIBE_PRESENT_POINTS("first_subscribe_present_points", "首次关注赠送积分"),
    SIGN_PRESENT_POINTS_EXPECT("sign_present_points_expect", "签到赠送积分期望值"),
    SIGN_PRESENT_POINTS_VARIANCE("sign_present_points_variance", "签到赠送积分波动方差"),

    WAIMAI("waimai", "菜单栏外卖红包点击返回"),
    MEITUAN("meituan", "美团红包推荐语"),
    ELME("elme", "饿了么红包推荐语"),

    MEMBER("member", "多个会员推荐语"),
    HUAFEI("huafei", "话费推荐语"),
    DIANFEI("dianfei", "电费推荐语"),




    MEDIA_KEFU("media_kefu_weixin", "客服微信二维码素材"),
    BILIBILI_QUN("media_bilibili_weixin_qun", "素材Id-BiliBili玩梗闲聊群活码"),


    MAX_P_LIMIT("max_multiple_p_size", "多p投稿的最大数量限制"),
    MAX_FREE_LIMIT("max_free_size", "自由模式的最大数量限制")

    ;
}