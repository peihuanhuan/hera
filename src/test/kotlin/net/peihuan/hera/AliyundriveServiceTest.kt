package net.peihuan.hera

import mu.KotlinLogging
import net.peihuan.hera.service.AliyundriveService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path


class AliyundriveServiceTest : HeraApplicationTests() {

    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var aliyundriveService: AliyundriveService


    @Test
    fun testRefresh() {
        aliyundriveService.refreshToken()
    }

    @Test
    fun testUpload() {
        val aliyundriveService1 = aliyundriveService.uploadFile(File("/Users/peihuan/downloads/未命名的设计.png"))
        log.info {aliyundriveService1  }
    }

    @Test
    fun testList() {
        val listFile = aliyundriveService.listFile()
        log.info { listFile }
    }

    @Test
    fun get() {
        aliyundriveService.refreshToken()
        var dto = aliyundriveService.get("61da5bbc425af30397f5451db5ac881b5259e14b")
        log.info { dto }

        dto = aliyundriveService.get("xxxxxxxx")
        log.info { dto }
    }

    @Test
    fun share() {
        aliyundriveService.refreshToken()
        val dto = aliyundriveService.share(listOf("61da5bbc425af30397f5451db5ac881b5259e14b"))
        log.info { dto }
    }

    @Test
    fun testFile() {
        val pathname = "/Users/peihuan/downloads/《这世界那么多人》 这世界有那么个人，活在我飞扬的青春.mp3"

        val path = "/Users/peihuan/downloads/【指弹】全站最还原吉他版《 一直很安静》仙剑奇侠传插曲，听完瞬间被治愈！.wav"
        val file = File(pathname)
        val type = Files.probeContentType(Path(path));
        log.info {  }
    }

}