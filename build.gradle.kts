import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.6"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.jetbrains.kotlin.plugin.noarg") version "1.4.20"
	kotlin("jvm") version "1.5.31"
	kotlin("plugin.spring") version "1.5.31"
}

noArg {
	annotation("net.peihuan.hera.domain.annotation.NoArg")
}

group = "net.peihuan"
version = "0.21.10"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2020.0.4"


dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	// implementation("org.springframework.cloud:spring-cloud-starter-feign:1.4.7.RELEASE")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

	implementation("commons-codec:commons-codec:1.15")
	implementation("org.apache.commons:commons-compress:1.21")

	implementation("io.jsonwebtoken:jjwt:0.9.0")

	// implementation("org.bytedeco:ffmpeg:4.4-1.5.6")
	// implementation("org.bytedeco:javacv-platform:1.5.6")
	implementation("net.bramp.ffmpeg:ffmpeg:0.6.2")


	implementation("com.aliyun.oss:aliyun-sdk-oss:3.13.2")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("io.github.microutils:kotlin-logging:2.0.11")

	implementation("com.google.code.gson:gson:2.8.8")
	implementation("joda-time:joda-time:2.10.12")
	implementation("org.apache.commons:commons-io:1.3.2")

	implementation("com.github.binarywang:weixin-java-mp:4.1.9.B")
	implementation("com.github.binarywang:weixin-java-pay:4.1.9.B")

	implementation("mysql:mysql-connector-java")
	implementation("p6spy:p6spy:3.9.1")
	implementation("com.baomidou:mybatis-plus-boot-starter:3.4.3.4")


	implementation("xerces:xercesImpl:2.12.0")


	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
