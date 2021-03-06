package net.peihuan.hera.feign.dto.bilibili

data class BangumiInfo(
    val activity: Activity,
    val alias: String,
    val areas: List<Area>,
    val bkg_cover: String,
    val cover: String,
    val episodes: List<Episode>?,
    val evaluate: String,
    // val freya: Freya?,
    val jp_title: String,
    val link: String,
    val media_id: Int,
    val mode: Int,
    val new_ep: NewEp,
    // val payment: Payment,
    val positive: Positive?,
    val publish: Publish?,
    val rating: Rating?,
    val record: String?,
    val rights: RightsXX?,
    val season_id: Int,
    val season_title: String,
    val seasons: List<Season>?,
    val section: List<Section>?,
    val series: Series?,
    val share_copy: String,
    val share_sub_title: String,
    val share_url: String,
    val show: Show?,
    val square_cover: String,
    val stat: StatXXX?,
    val status: Int,
    val subtitle: String,
    val title: String,
    val total: Int,
    val type: Int,
    val up_info: UpInfo?,
    val user_status: UserStatus?
)