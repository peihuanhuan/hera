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

    BLACK_KEY_WORD("black_key_word", "黑名单关键词"),



    MEDIA_KEFU("media_kefu_weixin", "客服微信二维码素材"),
    BILIBILI_QUN("media_bilibili_weixin_qun", "素材Id-BiliBili玩梗闲聊群活码"),


    MAX_P_LIMIT("max_multiple_p_size", "多p投稿的最大数量限制"),
    MAX_FREE_LIMIT("max_free_size", "自由模式的最大数量限制"),
    VIDEO_LIMIT("video_limit", "提取视频的最大时间限制"),
    MAX_DURATION_MINUTE("max_duration_minute", "允许的最大时长（分钟）"),
    ALARM_TIME("alarm_minutes", "一个视频多久没处理完就报警 - 分钟"),

    BLESS("bless", "虎年祝福语"),

    RED_PACKAGE_NOT_ENOUGH("red_package_not_enough", "红包数量不够"),


    ALI_YUN_DRIVER_REFRESH_TOKEN("ali_yun_driver_refresh_token", "阿里云盘 refresh token"),
    ALI_YUN_DRIVER_DEFAULT_ROOT("ali_yun_driver_default_root", "阿里云盘 上传的根目录"),

    GRAY_USER_PERCENTAGE("gray_user_percentage", "用户灰度比例 0-100"),
    GRAY_USER("gray_user", "指定灰度用户"),


    VIDEO_GRAY_PERCENTAGE("video_gray_user", "视频指定灰度用户"),
    VIDEO_USERS("video_users", "是否能使用视频功能"),

    ALI_DRIVER_BLACK_FILE_NAME("ali_driver_black_file_name", "阿里云盘黑名单 文件名"),

    ORDER_BACK_PERCENT("order_back_percent", "订单返现比例"),

    AUTO_REPLY("auto_reply", "自动回复配置"),
    ;
}