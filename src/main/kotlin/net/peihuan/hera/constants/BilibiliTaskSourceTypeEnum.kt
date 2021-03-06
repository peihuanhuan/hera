package net.peihuan.hera.constants

enum class BilibiliTaskSourceTypeEnum(val code: Int, val msg: String) {

    FREE(1, "自由模式"),
    MULTIPLE(2, "多p稿件"),
    UP(3, "up主模式")
    ;

    companion object {
        fun getTypeEnum(code: Int): BilibiliTaskSourceTypeEnum? {
            for (value in values()) {
                if (value.code == code) {
                    return value;
                }
            }
            return null
        }
    }
}