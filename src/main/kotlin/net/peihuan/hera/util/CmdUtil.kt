package net.peihuan.hera.util

import mu.KotlinLogging
import java.io.*


object CmdUtil {

    private val log = KotlinLogging.logger {}

    fun executeBash(cmd: String) {
        log.info { "------开始执行 $cmd" }
        try {
            val pro = Runtime.getRuntime().exec(cmd)
            pro.waitFor()
            val bufrIn = BufferedReader(InputStreamReader(pro.inputStream, "UTF-8"))
            val bufrError = BufferedReader(InputStreamReader(pro.errorStream, "UTF-8"))
            log.info("执行结果 :${bufrIn.readLines()}  ${bufrError.readLines()}")
        } catch (ex: Exception) {
            log.error("______________ 执行bash脚本出错")
            throw ex
        }
    }

    fun executeBashs(vararg cmd: String) {
        val run = Runtime.getRuntime()
        val wd = File("/bin")
        println(wd)
        var proc: Process? = null
        try {
            proc = run.exec("/bin/bash", null, wd)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (proc != null) {
            val `in` = BufferedReader(InputStreamReader(proc.inputStream))
            val out = PrintWriter(BufferedWriter(OutputStreamWriter(proc.outputStream)), true)
            for (s in cmd) {
                out.println(s)
            }
            out.println("exit") //这个命令必须执行，否则in流不结束。
            try {
                var line: String?
                while (`in`.readLine().also { line = it } != null) {
//                    System.out.println(line);
                    log.info(line)
                }
                proc.waitFor()
                `in`.close()
                out.close()
                proc.destroy()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}