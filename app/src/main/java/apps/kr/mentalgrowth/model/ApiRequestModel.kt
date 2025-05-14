package apps.kr.mentalgrowth.model

class ApiRequestModel {
    data class LoginRequest(
        val mem_id: String,
        val mem_pw: String
    )

    data class GolfCourseRegisterRequest(
        val memId: String,
        val courseName: String,
        val caddyFee18: String,
        val caddyFee9: String,
        val color: String // Hex 값 등으로 전달
    )

    // 제출 데이터 클래스 (실제 프로젝트에 맞게 수정)
    data class RegistrationData(
        val reg_id: String,
        val date: String,
        val roundCount18: Int,
        val roundCount9: Int,
        val caddyFee18: String,
        val caddyFee9: String,
        val overFee18: String,
        val overFee9: String,
        val golf_idx: Int,
        val memo: String
    )

}