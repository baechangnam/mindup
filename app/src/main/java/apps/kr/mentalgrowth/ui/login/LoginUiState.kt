package apps.kr.mentalgrowth.ui.login




data class LoginUiState(
    val isLoggedIn: Boolean = false,
    val email: String? = null,
    val nickname: String? = null,
    val profileImageUrl: String? = null,
    val errorMessage: String? = null,
    val naverAccessToken: String? = null,
    val naverRefreshToken: String? = null,
    val naverExpiresAt: Long? = null,
    val naverTokenType: String? = null,
    val naverState: String? = null,
    val mem_id: String? = null,
    val mem_level: String? = null,
    val class_group_id: String? = null,
)