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
    HUAFEI("huafei", "话费推荐语")
    ;
}