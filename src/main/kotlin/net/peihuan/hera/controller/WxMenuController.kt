package net.peihuan.hera.controller

import me.chanjar.weixin.common.api.WxConsts
import me.chanjar.weixin.common.api.WxConsts.MenuButtonType
import me.chanjar.weixin.common.bean.menu.WxMenu
import me.chanjar.weixin.common.bean.menu.WxMenuButton
import me.chanjar.weixin.common.error.WxErrorException
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.menu.WxMpGetSelfMenuInfoResult
import me.chanjar.weixin.mp.bean.menu.WxMpMenu
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.net.URL

@RestController
@RequestMapping("/wx/{appid}/menu/")
class WxMenuController(val wxService: WxMpService) {

    /**
     * <pre>
     * 自定义菜单创建接口
     * 详情请见：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141013&token=&lang=zh_CN
     * 如果要创建个性化菜单，请设置matchrule属性
     * 详情请见：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1455782296&token=&lang=zh_CN
    </pre> *
     *
     * @return 如果是个性化菜单，则返回menuid，否则返回null
     */
    @PostMapping("/create")
    @Throws(WxErrorException::class)
    fun menuCreate(@PathVariable appid: String?, @RequestBody menu: WxMenu?): String? {
        return wxService.switchoverTo(appid).menuService.menuCreate(menu)
    }

    @GetMapping("/create")
    fun menuCreateSample(@PathVariable appid: String?): String? {
        val menu = WxMenu()
        val button1 = WxMenuButton()
        button1.type = MenuButtonType.CLICK
        button1.name = "今日歌曲"
        button1.key = "V1001_TODAY_MUSIC"

        val button3 = WxMenuButton()
        button3.name = "菜单"
        menu.buttons.add(button1)
        menu.buttons.add(button3)
        val button31 = WxMenuButton()
        button31.type = MenuButtonType.VIEW
        button31.name = "搜索"
        button31.url = "http://www.soso.com/"
        val button32 = WxMenuButton()
        button32.type = MenuButtonType.VIEW
        button32.name = "视频"
        button32.url = "http://v.qq.com/"
        val button33 = WxMenuButton()
        button33.type = MenuButtonType.CLICK
        button33.name = "赞一下我们"
        button33.key = "V1001_GOOD"
        val button34 = WxMenuButton()
        button34.type = MenuButtonType.VIEW
        button34.name = "获取用户信息"
        val servletRequestAttributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
        if (servletRequestAttributes != null) {
            val request = servletRequestAttributes.request
            val requestURL = URL(request.requestURL.toString())
            val url = wxService.switchoverTo(appid).oAuth2Service.buildAuthorizationUrl(String.format("%s://%s/wx/redirect/%s/greet", requestURL.protocol, requestURL.host, appid),
                    WxConsts.OAuth2Scope.SNSAPI_USERINFO, null)
            button34.url = url
        }
        button3.subButtons.add(button31)
        button3.subButtons.add(button32)
        button3.subButtons.add(button33)
        button3.subButtons.add(button34)
        wxService.switchover(appid)
        return wxService.menuService.menuCreate(menu)
    }

    /**
     * <pre>
     * 自定义菜单创建接口
     * 详情请见： https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141013&token=&lang=zh_CN
     * 如果要创建个性化菜单，请设置matchrule属性
     * 详情请见：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1455782296&token=&lang=zh_CN
    </pre> *
     *
     * @return 如果是个性化菜单，则返回menuid，否则返回null
     */
    @PostMapping("/createByJson")
    @Throws(WxErrorException::class)
    fun menuCreate(@PathVariable appid: String?, @RequestBody json: String?): String {
        return wxService!!.switchoverTo(appid).menuService.menuCreate(json)
    }

    /**
     * <pre>
     * 自定义菜单删除接口
     * 详情请见: https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141015&token=&lang=zh_CN
    </pre> *
     */
    @GetMapping("/delete")
    @Throws(WxErrorException::class)
    fun menuDelete(@PathVariable appid: String?) {
        wxService!!.switchoverTo(appid).menuService.menuDelete()
    }

    /**
     * <pre>
     * 删除个性化菜单接口
     * 详情请见: https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1455782296&token=&lang=zh_CN
    </pre> *
     *
     * @param menuId 个性化菜单的menuid
     */
    @GetMapping("/delete/{menuId}")
    @Throws(WxErrorException::class)
    fun menuDelete(@PathVariable appid: String?, @PathVariable menuId: String?) {
        wxService!!.switchoverTo(appid).menuService.menuDelete(menuId)
    }

    /**
     * <pre>
     * 自定义菜单查询接口
     * 详情请见： https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141014&token=&lang=zh_CN
    </pre> *
     */
    @GetMapping("/get")
    @Throws(WxErrorException::class)
    fun menuGet(@PathVariable appid: String?): WxMpMenu {
        return wxService!!.switchoverTo(appid).menuService.menuGet()
    }

    /**
     * <pre>
     * 测试个性化菜单匹配结果
     * 详情请见: http://mp.weixin.qq.com/wiki/0/c48ccd12b69ae023159b4bfaa7c39c20.html
    </pre> *
     *
     * @param userid 可以是粉丝的OpenID，也可以是粉丝的微信号。
     */
    @GetMapping("/menuTryMatch/{userid}")
    @Throws(WxErrorException::class)
    fun menuTryMatch(@PathVariable appid: String?, @PathVariable userid: String?): WxMenu {
        return wxService!!.switchoverTo(appid).menuService.menuTryMatch(userid)
    }

    /**
     * <pre>
     * 获取自定义菜单配置接口
     * 本接口将会提供公众号当前使用的自定义菜单的配置，如果公众号是通过API调用设置的菜单，则返回菜单的开发配置，而如果公众号是在公众平台官网通过网站功能发布菜单，则本接口返回运营者设置的菜单配置。
     * 请注意：
     * 1、第三方平台开发者可以通过本接口，在旗下公众号将业务授权给你后，立即通过本接口检测公众号的自定义菜单配置，并通过接口再次给公众号设置好自动回复规则，以提升公众号运营者的业务体验。
     * 2、本接口与自定义菜单查询接口的不同之处在于，本接口无论公众号的接口是如何设置的，都能查询到接口，而自定义菜单查询接口则仅能查询到使用API设置的菜单配置。
     * 3、认证/未认证的服务号/订阅号，以及接口测试号，均拥有该接口权限。
     * 4、从第三方平台的公众号登录授权机制上来说，该接口从属于消息与菜单权限集。
     * 5、本接口中返回的图片/语音/视频为临时素材（临时素材每次获取都不同，3天内有效，通过素材管理-获取临时素材接口来获取这些素材），本接口返回的图文消息为永久素材素材（通过素材管理-获取永久素材接口来获取这些素材）。
     * 接口调用请求说明:
     * http请求方式: GET（请使用https协议）
     * https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=ACCESS_TOKEN
    </pre> *
     */
    @GetMapping("/getSelfMenuInfo")
    @Throws(WxErrorException::class)
    fun getSelfMenuInfo(@PathVariable appid: String?): WxMpGetSelfMenuInfoResult {
        return wxService!!.switchoverTo(appid).menuService.selfMenuInfo
    }
}