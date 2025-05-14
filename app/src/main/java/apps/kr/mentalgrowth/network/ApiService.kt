package apps.kr.mentalgrowth.network

import apps.kr.mentalgrowth.model.ApiRequestModel
import apps.kr.mentalgrowth.model.ApiResponseModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {


    @Multipart
    @POST("api/join_teacher.php")  // 서버의 엔드포인트 URL에 맞게 수정
    suspend fun joinTeacher(
        @Part("region") region: RequestBody,
        @Part("school") school: RequestBody,
        @Part("grade") grade: RequestBody,
        @Part("classNo") classNo: RequestBody,
        @Part("userId") userId: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part("nickName") nickName: RequestBody,
        @Part("memId") memId: RequestBody,
        @Part certificate: MultipartBody.Part?
    ): Response<ApiResponseModel.VersionResponse>

    @Multipart
    @POST("api/mem_update.php")  // 서버의 엔드포인트 URL에 맞게 수정
    suspend fun updateMember(
        @Part("userId") userId: RequestBody,
        @Part("nickName") nickName: RequestBody,
        @Part("memId") memId: RequestBody,
        @Part("isUpdate") isUpdate: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<ApiResponseModel.VersionResponse>

    @Multipart
    @POST("api/join_student.php")  // 서버의 엔드포인트 URL에 맞게 수정
    suspend fun joinStudent(
        @Part consent: MultipartBody.Part?,
        @Part batch: MultipartBody.Part?
    ): Response<ApiResponseModel.VersionResponse>

    @GET("api/version.php")
    suspend fun getVersionInfo(): ApiResponseModel.VersionResponse


    @POST("api/login_proc_sns.php")
    suspend fun login(@Body request: ApiRequestModel.LoginRequest): Response<ApiResponseModel.LoginResponse>

    @POST("api/check_login.php")
    suspend fun checkLogin(@Body request: ApiRequestModel.LoginRequest): Response<ApiResponseModel.LoginResponse>

    @POST("api/register_golf_field.php")
    suspend fun regGolf(@Body request: ApiRequestModel.GolfCourseRegisterRequest): Response<ApiResponseModel.VersionResponse>

    @POST("api/register_golf_data.php")
    suspend fun regGolfData(@Body request: ApiRequestModel.RegistrationData): Response<ApiResponseModel.VersionResponse>

    @POST("api/comm_code_detail.php")
    suspend fun getCommCode(@Query("use_code") use_code: String): Response<ApiResponseModel.VersionResponse>

    @POST("api/comment_register_proc.php")
    suspend fun regComment(@Query("reg_id") reg_id: String,@Query("board_idx") board_idx: String,@Query("contents") contents: String,@Query("pid") pid: String): Response<ApiResponseModel.VersionResponse>

    @GET("api/list_board.php")
    suspend fun getBoardList(@Query("pid") id: String,@Query("group_id") keyword: String,@Query("noti_flag") noti_flag: String): Response<ApiResponseModel.BoardResponseModel>

    @GET("api/list_touch.php")
    suspend fun getBoardListTouch(@Query("pid") id: String,@Query("group_id") keyword: String,@Query("code") code: String,@Query("reg_id") reg_id: String,@Query("tag") tag: String): Response<ApiResponseModel.BoardResponseModel>

    @GET("api/list_talk.php")
    suspend fun getBoardListTalk(@Query("pid") id: String,@Query("group_id") keyword: String,@Query("code") code: String,@Query("reg_id") reg_id: String,@Query("tag") tag: String): Response<ApiResponseModel.BoardResponseModel>


    @GET("api/list_touch_total.php")
    suspend fun getBoardListTouchTotal(@Query("pid") id: String,@Query("group_id") keyword: String,@Query("code") code:
    String,@Query("reg_id") reg_id: String,@Query("tag") tag: String): Response<ApiResponseModel.CountResponseModel>


    @GET("api/my_info.php")
    suspend fun getMyInfo(@Query("mem_id") mem_id: String): Response<ApiResponseModel.MyInfoResponseModel>

    @GET("api/mem_list.php")
    suspend fun getMember(@Query("mem_id") mem_id: String,@Query("group_id") group_id: String): Response<ApiResponseModel.MyInfoResponseModel>


    @GET("api/list_detail.php")
    suspend fun getBoardDetail(@Query("idx") idx: String): Response<ApiResponseModel.BoardDetail>

    @GET("api/list_detail.php")
    suspend fun getBoardDetail(@Query("idx") idx: String,@Query("mem_id") mem_id: String): Response<ApiResponseModel.BoardDetail>


    @GET("api/get_annual_list.php")
    suspend fun getAnnualList(@Query("reg_id") id: String,@Query("year") year: Int): Response<ApiResponseModel.AnnualListResponseModel>


    @GET("api/delete_board.php")
    suspend fun deleteBoard(@Query("idx") id: String): Response<ApiResponseModel.VersionResponse>

    @GET("api/rank_list.php")
    suspend fun getRank(@Query("mem_id") id: String): Response<ApiResponseModel.RankResponseModel>

    @GET("api/rank_list_my.php")
    suspend fun getRankMy(@Query("mem_id") id: String): Response<ApiResponseModel.RankResponseMyModel>

    @GET("api/list_calenadar.php")
    suspend fun getCal(@Query("mem_id") id: String,@Query("yyyy") yyyyy: String,@Query("mm") mm: String): Response<ApiResponseModel.HeartDayResponseModel>

    @GET("api/comment_delete_proc.php")
    suspend fun deleteComment(@Query("idx") id: String): Response<ApiResponseModel.VersionResponse>

    @GET("api/add_board_good_proc.php")
    suspend fun likeBoard(@Query("board_idx") board_idx: String,@Query("mem_id") mem_id: String): Response<ApiResponseModel.VersionResponse>


    @GET("api/comment_update.php")
    suspend fun updateComment(@Query("idx") id: String,@Query("contents") contents: String): Response<ApiResponseModel.VersionResponse>

    @GET("api/register_memo.php")
    suspend fun regNote(@Query("title") title: String,@Query("idx") idx: String,@Query("mem_id") mem_id: String,@Query("short_memo") short_memo: String
                        ,@Query("group_id") group_id: String): Response<ApiResponseModel.VersionResponse>

    @GET("api/register_talk.php")
    suspend fun regTalk(@Query("pid") id: String,@Query("title") title: String,@Query("idx") idx: String,@Query("mem_id") mem_id: String
                        ,@Query("group_id") group_id: String): Response<ApiResponseModel.VersionResponse>

    @Multipart
    @POST("api/register_board.php")  // 서버의 엔드포인트 URL에 맞게 수정
    suspend fun uploadBoardTouch(
        @Part("pid") pid: RequestBody,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("reg_id") reg_id: RequestBody,
        @Part("addr") addr: RequestBody,
        @Part("code") code: RequestBody,
        @Part("rating") rating: RequestBody,
        @Part("group_id") group_id: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<ApiResponseModel.VersionResponse>

    @Multipart
    @POST("api/update_board.php")  // 서버의 엔드포인트 URL에 맞게 수정
    suspend fun upDateBoardTouch(
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("reg_id") reg_id: RequestBody,
        @Part("addr") addr: RequestBody,
        @Part("code") code: RequestBody,
        @Part("rating") rating: RequestBody,
        @Part("group_id") group_id: RequestBody,
        @Part("idx") idx: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<ApiResponseModel.VersionResponse>

    @Multipart
    @POST("api/update_board.php")  // 서버의 엔드포인트 URL에 맞게 수정
    suspend fun updateBoard(
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("reg_id") reg_id: RequestBody,
        @Part("idx") idx: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<ApiResponseModel.VersionResponse>
}