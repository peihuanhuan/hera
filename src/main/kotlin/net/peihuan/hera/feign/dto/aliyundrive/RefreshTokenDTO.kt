package net.peihuan.hera.feign.dto.aliyundrive

data class RefreshTokenDTO(
    val access_token: String,
    val token_type: String,
    val refresh_token: String,
    val default_drive_id: String,
)