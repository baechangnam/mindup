
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import apps.kr.mentalgrowth.R
import apps.kr.mentalgrowth.common.CommonView
import apps.kr.mentalgrowth.model.ApiResponseModel
import apps.kr.mentalgrowth.network.NetworkClient
import apps.kr.mentalgrowth.network.NetworkClient.BASE_URL_UPLOAD
import apps.kr.mentalgrowth.network.NetworkClient.apiService
import apps.kr.mentalgrowth.ui.main.viewmodel.MainViewModel
import coil.compose.AsyncImage

import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BoardDetailPicture(
    navController: NavController,
    boardId: String,
    tag: String,
    viewModel: MainViewModel = viewModel()
) {

    val context = LocalContext.current

    val memId = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        .getString("mem_id", "") ?: ""
    LaunchedEffect(boardId) {
        viewModel.fetchBoardDetailTouch(boardId,memId)
    }
    var showVideoFullScreen by remember { mutableStateOf(false) }
    var videoUrl by remember { mutableStateOf("") }
    var showImageFullScreen by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    var commentText by remember { mutableStateOf("") }


    var selCommentText by remember { mutableStateOf("") }
    var selCommentIdx by remember { mutableStateOf("") }


    val isDelete by viewModel.isDelete.collectAsState()
    val isComment by viewModel.isComment.collectAsState()
    val isUpdate by viewModel.isUpdate.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState.flag == "1") {
            Toast.makeText(context, "등록 완료되었습니다.", Toast.LENGTH_SHORT).show()
            viewModel.fetchBoardDetail(boardId)
            commentText = ""                   // ① 입력창 비우기
            keyboardController?.hide()        // ② 키패드 닫기
        }
    }

    LaunchedEffect(key1 = uiState.message) {
        uiState.message?.let { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                viewModel.clearMessage()
            }
        }
    }
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = false
        }
    }


    DisposableEffect(Unit) {
        onDispose {
            player.release()
            Log.d("VideoPlayer", "ExoPlayer released")
        }
    }

    LaunchedEffect(isDelete) {
        if (isDelete == "1") {
            Toast.makeText(context, "삭제 완료되었습니다.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    LaunchedEffect(isComment) {
        if (isComment == "1") {
            Toast.makeText(context, "삭제 완료되었습니다.", Toast.LENGTH_SHORT).show()
            viewModel.fetchBoardDetail(boardId)
        }
    }
    LaunchedEffect(isUpdate) {
        if (isUpdate == "1") {
            Toast.makeText(context, "수정 완료되었습니다.", Toast.LENGTH_SHORT).show()
            viewModel.fetchBoardDetail(boardId)
        }
    }


    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val scope = rememberCoroutineScope()
    var showNameSheet by remember { mutableStateOf(false) }


    val board by viewModel.board.collectAsState()
    val commentList by viewModel.commentList.collectAsState()

    // 댓글 입력 상태
    var menuExpanded by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            if (showNameSheet) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("댓글 수정", style = MaterialTheme.typography.h6)
                    OutlinedTextField(
                        value = selCommentText,
                        onValueChange = { selCommentText = it },
                        label = { Text("수정") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            // 닫기
                            scope.launch { sheetState.hide() }
                            showNameSheet = false
                        }) {
                            Text("취소")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            // 4) API 호출
                          //  viewModel.updateNickname(memId, newName)
                            // 시트 닫기

                            viewModel.updateComment(selCommentIdx,selCommentText)
                            scope.launch { sheetState.hide() }
                            showNameSheet = false
                        }) {
                            Text("수정")
                        }
                    }
                }
            } else {
                Spacer(Modifier.height(1.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = Color(0xFFF8BBD0),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp), // 기본 TopAppBar 높이
                        contentAlignment = Alignment.Center
                    ) {
                        // ✅ 중앙 타이틀 (하트 포함)
                        CommonView.TitleWithHearts("마음그림")

                        // 🔹 왼쪽: 뒤로가기 버튼
                        Row(
                            modifier = Modifier.align(Alignment.CenterStart),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { navController?.popBackStack() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_back_black),
                                    contentDescription = "뒤로가기",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        // 🔹 오른쪽: 홈 버튼
                        Row(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "메뉴열기",
                                    tint = Color.Black
                                )
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                // 내 글이면 수정/삭제/신고
                                if (board?.reg_id == memId) {
                                    DropdownMenuItem(onClick = {
                                        menuExpanded = false

                                        navController?.navigate("board_reg_ch/${board?.cate}?idx=${board?.idx}")
                                        //navController.navigate("board_edit/${board?.idx}")
                                    }) {
                                        Text("수정")
                                    }
                                    DropdownMenuItem(onClick = {
                                        menuExpanded = false
                                        viewModel.deleteBoard(boardId)  // ViewModel에 삭제 메서드 구현
                                    }) {
                                        Text("삭제")
                                    }
                                }
                                // 공통: 신고하기
                                DropdownMenuItem(onClick = {
                                    menuExpanded = false
                                    showReportDialog = true
                                }) {
                                    Text("신고하기")
                                }
                            }
                        }
                    }
                }


            },
            bottomBar = {
                // 하단 고정 댓글 입력 영역
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        placeholder = { Text("댓글 작성") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (memId == "guest") {
                                Toast.makeText(context , "비회원은 등록 불가능합니다." ,Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (commentText.isBlank()) {
                                Toast
                                    .makeText(context, "댓글을 입력해주세요", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                viewModel.registerComment(boardId, memId, commentText,"3")
                            }


                        },
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("전송", style = MaterialTheme.typography.body2)
                    }
                }
            }
        ) { innerPadding ->
            val combinedPadding = PaddingValues(
                start = 16.dp + innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                top = 16.dp + innerPadding.calculateTopPadding(),
                end = 16.dp + innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = 16.dp + innerPadding.calculateBottomPadding()  // innerPadding의 bottom이 0이면, 여기서 16.dp만 적용됨
            )
            // 신고 확인 대화상자
            if (showReportDialog) {
                AlertDialog(
                    onDismissRequest = { showReportDialog = false },
                    title = { Text(text = "신고") },
                    text = { Text("신고가 접수되었습니다. 관리자 확인 후 처리하겠습니다.") },
                    confirmButton = {
                        TextButton(onClick = { showReportDialog = false }) {
                            Text("확인")
                        }
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .padding(
                        bottom = 70.dp,
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp
                    ) // 하단 입력창 높이만큼 여백 추가
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    board?.let { b ->

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // 프로필 이미지 (Coil AsyncImage 사용)
                            val imageData = b.reg_img.takeIf { !it.isNullOrBlank() && it != "null" }
                                ?.let { NetworkClient.BASE_URL_MEMBER + it }

                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imageData)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.thumbnail_no_img1),
                                error       = painterResource(R.drawable.thumbnail_no_img1),
                                contentDescription = "등록자 프로필 이미지",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),      // 둥글게 자르기
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // 등록자명 + 등록일
                            Column {
                                Text(
                                    text = b.reg_id_name ?: "",
                                    style = MaterialTheme.typography.subtitle1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = b.reg_date.takeIf { it.length >= 10 }?.substring(0, 10)
                                        ?: b.reg_date.orEmpty(),
                                    style = MaterialTheme.typography.caption,
                                    color = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f)) // ⬅️ 이게 오른쪽 정렬의 핵심입니다
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(4.dp)
                                    .clickable {
                                        scope.launch {
                                            try {
                                                // 여기에 API 통신 로직을 넣으세요
                                                val response = apiService.likeBoard(b.idx.toString(),memId)
                                                if (response.isSuccessful) {
                                                    Log.d("좋아요", "성공적으로 좋아요 보냄")
                                                    response.body()?.let { list ->
                                                        if (list.flag.flag =="1"){
                                                            Toast.makeText(context, "좋아요를 눌렀습니다", Toast.LENGTH_SHORT).show()
                                                            viewModel.fetchBoardDetailTouch(boardId,memId)
                                                        }else    if (list.flag.flag =="2"){
                                                            Toast.makeText(context, "좋아요를 취소하였습니다", Toast.LENGTH_SHORT).show()
                                                            viewModel.fetchBoardDetailTouch(boardId,memId)
                                                        }else{

                                                        }
                                                    } ?: run {

                                                    }
                                                } else {
                                                    Log.e("좋아요", "실패: ${response.code()}")
                                                }
                                            } catch (e: Exception) {
                                                Log.e("좋아요", "에러 발생: ${e.message}")
                                            }
                                        }
                                    }
                            ) {
                                Icon(
                                    imageVector = if (b.favor_cnt?:"0" == "1") Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "좋아요",
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = "${b.good_cnts?:"0"}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }

                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Divider(color = Color.LightGray)
                        Spacer(modifier = Modifier.height(15.dp))


                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xfffee3ea))
                                .padding(vertical = 16.dp)
                        ) {
                            Text(
                                text = b.title,
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        val imageUrl = b.filename?.takeIf { it.isNotBlank() }?.let {
                            NetworkClient.BASE_URL_UPLOAD + it
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(Color.Black)
                        ) {
                            if (imageUrl != null) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "게시글 이미지",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .wrapContentHeight(),
                                )
                            } else {
                                Text(
                                    text = "이미지 없음",
                                    color = Color.White,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(15.dp))


                        Text(
                            text = "댓글(${b.comment_cnt})",
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                }


                commentList?.let { list ->
                    items(list) { comment ->
                        CommentItem(
                            comment = comment,
                            currentUserId = memId,
                            onEdit = { comment ->
                                selCommentText = comment.contents
                                selCommentIdx = comment.idx.toString()
                                showNameSheet = true
                                scope.launch { sheetState.show() }
                                // navController.navigate("comment_edit/$commentId")
                            },
                            onDelete = { commentId ->
                                viewModel.deleteComment(commentId)
                            },
                            onReport = { commentId ->
                                showReportDialog = true

                                //  viewModel.reportComment(commentId)
                            }
                        )
                        Divider(color = Color.LightGray)
                    }
                }
            }
        }
        if (showImageFullScreen) {
            FullScreenZoomableImages(imageUrl) {
                showImageFullScreen = false
            }
        }

    }

}


//@Composable
//fun CommentList(comments: List<ApiResponseModel.Comment>) {
//    val context = LocalContext.current
//    val memId = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
//        .getString("mem_id", "") ?: ""
//
//    LazyColumn(
//        modifier = Modifier.fillMaxWidth(),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        items(comments) { comment ->
//            CommentItem(comment = comment,currentUserId = memId)
//            Divider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)
//        }
//    }
//}



