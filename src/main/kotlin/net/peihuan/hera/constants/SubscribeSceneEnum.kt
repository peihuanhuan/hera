package net.peihuan.hera.constants

enum class SubscribeSceneEnum(val code: Int, val msg: String) {
    ADD_SCENE_SEARCH(1, "公众号搜索"),
    ADD_SCENE_ACCOUNT_MIGRATION(2, "公众号迁移"),
    ADD_SCENE_PROFILE_CARD(3, "名片分享"),
    ADD_SCENE_QR_CODE(4, "扫描二维码"),
    ADD_SCENE_PROFILE_LINK(5, "图文页内名称点击"),
    ADD_SCENE_PROFILE_ITEM(6, "图文页右上角菜单"),
    ADD_SCENE_PAID(7, "支付后关注"),
    ADD_SCENE_WECHAT_ADVERTISEMENT(8, "微信广告"),
    ADD_SCENE_OTHERS(9, "其他")
    ;
}