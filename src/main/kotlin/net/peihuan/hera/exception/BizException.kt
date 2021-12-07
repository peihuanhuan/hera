package net.peihuan.hera.exception

class BizException private constructor(val code: Int, val msg: String?) : RuntimeException(msg) {

    companion object {
        fun build(code: Int, msg: String?): BizException {
            return BizException(code, msg)
        }

        fun build(exceptionEnum: CodeEnum): BizException {
            return BizException(exceptionEnum.code, exceptionEnum.msg)
        }

        fun buildBizException(msg: String?): BizException {
            return BizException(CodeEnum.DEFAULT_BIZ_EXCEPTION.code, msg)
        }
    }
}