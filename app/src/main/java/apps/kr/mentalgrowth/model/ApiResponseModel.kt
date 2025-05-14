package apps.kr.mentalgrowth.model



import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

class ApiResponseModel {
    data class VersionResponse(
        val flag: FlagInfo
    )

    data class FlagInfo(
        val flag: String,
        val message: String,  // 서버에서 전달하는 버전 정보
        val login_key: String?,
    )

    data class LoginResponse(
        val flag: LoginInfo
    )

    data class LoginInfo(
        val flag: String,
        val message: String,
        val mem_name: String?,
        val mem_phone: String?,
        val mem_level: String?,
        val class_group_id: String?,


    )



    data class MyInfoResponseModel(
        @SerializedName("board_list")
        val boardList: List<MemberInfo>
    )
    data class MemberInfo(
        val mem_id: String,
        val mem_name: String,
        val mem_phone: String,
        val school: String,
        val mem_hack: String,
        val mem_ban: String,
        val class_group_id: String,
        val region: String,
        val mem_img: String,
        val comment_cnt: String?,
        val board_cnt: String?,


    )

    data class GolfListResponseModel(
        @SerializedName("board_list")
        val boardList: List<GolfCourse>
    )

    data class GolfCourse(
        @SerializedName("idx")
        val idx: Int,

        @SerializedName("pid")
        val pid: Int,

        @SerializedName("title")
        val title: String,

        @SerializedName("fee_eighteen")
        val fee_eighteen: String,

        @SerializedName("fee_nine")
        val fee_nine: String,

        @SerializedName("color")
        val color: String,

        @SerializedName("reg_date")
        val reg_date: String,

        @SerializedName("tot_price")
        val tot_price: String?,

        @SerializedName("tot_round")
        val tot_round: String?


    )

    data class CalendarListResponseModel(
        @SerializedName("board_list")
        val boardList: List<GolfCalendar>,

        @SerializedName("sum")
         val sum: String
    )


    data class GolfCalendar(
        @SerializedName("idx")
        val idx: Int,

        @SerializedName("pid")
        val pid: Int,

        @SerializedName("title")
        val title: String,

        @SerializedName("fee_eighteen")
        val fee_eighteen: String,

        @SerializedName("fee_nine")
        val fee_nine: String,

        @SerializedName("golf_name")
        val golf_name: String,

        @SerializedName("golf_color")
        val golf_color: String,

        @SerializedName("cnt_eighteen")
        val cnt_eighteen: Int,

        @SerializedName("cnt_nine")
        val cnt_nine: Int,

        @SerializedName("over_eighteen")
        val over_eighteen: Int,

        @SerializedName("over_nine")
        val over_nine: Int,

        @SerializedName("contents")
        val contents: String?,



    )

    data class HeartDayResponseModel(
        @SerializedName("board_list")
        val board_list:  List<HeartDay>

    )


    data class HeartDay(
        val date: String, // "2025-05-08"
        val ATTEND: Int,
        val TOUCH: Int,
        val CHALLENGE: Int,
        val TALK: Int
    )

    data class RankResponseModel(
        @SerializedName("rank")
        val rank: Int,
        @SerializedName("board_list")
        val board_list:  List<Rank>

    )
    data class RankResponseMyModel(
        @SerializedName("data")
        val data: Rank

    )

    data class Rank(
        @SerializedName("mem_id")
        val mem_id: String,

        @SerializedName("mem_phone")
        val mem_phone: String,

        @SerializedName("board_count")
        val board_count: String,

        @SerializedName("comment_count")
        val comment_count: String,

        @SerializedName("tier")
        val tier: String,

        @SerializedName("level")
        val level: String,
        @SerializedName("school")
        val school: String,
        @SerializedName("mem_img")
        val mem_img: String,


    )


    data class CountResponseModel(
        @SerializedName("count_info")
        val count_info: MonthSummarys

    )

    data class MonthSummarys(
        @SerializedName("h_cnt")
        val h_cnt: String,
        @SerializedName("e_cnt")
        val e_cnt: String,
        @SerializedName("a_cnt")
        val a_cnt: String,
        @SerializedName("r_cnt")
        val r_cnt: String,
        @SerializedName("t_cnt")
        val t_cnt: String,
        )





data class AnnualListResponseModel(
        @SerializedName("board_list")
        val boardList: List<MonthSummary>,


    )

    data class BoardResponseModel(
        @SerializedName("board_list")
        val boardList: List<Board>,

        @SerializedName("titles")
        val titles: String

        )

    data class BoardDetail(
        @SerializedName("board_list")
        val boardList: List<Board>,

        @SerializedName("comment_list")
        val comment_list: List<Comment>,
    )

    data class Comment(
        @SerializedName("idx")
        val idx: Int,

        @SerializedName("pid")
        val pid: Int,

        @SerializedName("contents")
        val contents: String,

        @SerializedName("reg_date")
        val reg_date: String,


        @SerializedName("reg_id_name")
        val reg_id_name: String,

        @SerializedName("reg_id")
        val mem_id: String,
    )

    @Parcelize
    data class Board(
        @SerializedName("idx")
        val idx: Int,

        @SerializedName("pid")
        val pid: Int,

        @SerializedName("title")
        val title: String,

        @SerializedName("noti_flag")
        val noti_flag: String,

        @SerializedName("contents")
        val contents: String,

        @SerializedName("comment_cnt")
        val comment_cnt: String,

        @SerializedName("reg_id_name")
        val reg_id_name: String?,

        @SerializedName("filename")
        val filename: String?,

        @SerializedName("filename2")
        val filename2: String?,

        @SerializedName("addr")
        val addr: String?,

        @SerializedName("hit")
        val hit: String,

        @SerializedName("reg_date")
        val reg_date: String,


        @SerializedName("reg_id")
        val reg_id: String?,

        @SerializedName("reg_img")
        val reg_img: String?,

        @SerializedName("good_cnts")
        val good_cnts: String?,

        @SerializedName("favor_cnt")
        val favor_cnt: String?,

        @SerializedName("cate")
        val cate: String?,

        @SerializedName("point")
        val point: String?,

        @SerializedName("board_category_idx")
        val board_category_idx: String,

        @SerializedName("h_cnt")
        val h_cnt: String?,

        @SerializedName("e_cnt")
        val e_cnt: String?,

        @SerializedName("a_cnt")
        val a_cnt: String?,

        @SerializedName("r_cnt")
        val r_cnt: String?,

        @SerializedName("t_cnt")
        val t_cnt: String?,

        @SerializedName("short_memo")
        val short_memo: String?,



        ) : Parcelable


    // 각 월의 데이터를 표현하기 위한 data class
    data class MonthSummary(
        val month: Int,
        val rounding: Int,    // 라운딩수
        val caddyFee: Int,    // 캐디피
        val overFee: Int,     // 오버비
        val total: Int        // 합계
    )

}