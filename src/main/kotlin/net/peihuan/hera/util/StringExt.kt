package net.peihuan.hera.util


// 完成内容中的 a 标签
fun String.completeALable(url: String): String {
    return replaceFirst("<a>(.*)<\\/a>".toRegex(), buildALabel(url, "$1"))
}

fun buildALabel(url: String, content: String): String {
    return "<a href=\"$url\">$content</a>"
}


/**
 * ====================
 */
// 完成客服消息中的填充
fun String.completeMsgMenu(vararg contents: String): String {
    var ans = this
    contents.forEach {
        ans = ans.completeMsgMenu(it)
    }
    return ans
}

fun String.completeMsgMenu(content: String): String {
    return replaceFirst("<a>(.*)<\\/a>".toRegex(), buildMsgMenuUrl(content, "$1"))
}

fun buildMsgMenuUrl(sendMsg: String, show: String): String {
    return """<a href="weixin://bizmsgmenu?msgmenucontent=${sendMsg}&msgmenuid=1\">${show}</a>"""
}
