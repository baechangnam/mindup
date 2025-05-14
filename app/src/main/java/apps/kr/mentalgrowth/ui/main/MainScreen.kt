package apps.kr.mentalgrowth.ui.main


import BoardChalRegisterScreen
import BoardDetailChal
import BoardDetailConsult
import BoardDetailContent
import BoardDetailListen
import BoardDetailPicture
import BoardDetailTouch
import BoardRegisterConsultScreen
import BoardRegisterPictureScreen
import BoardRegisterScreen
import RegisterBoxScreen
import TalkRegisterScreen
import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import apps.kr.mentalgrowth.navigation.BottomNavigationBar

@Composable
fun MainScreen(navController: NavHostController) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = bottomNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home", // 처음에 홈으로 시작
            modifier = Modifier.padding(innerPadding)
        ) {
            // 홈은 처음 진입만 가능, 하단에는 없음
            composable("home") {
                val context = LocalContext.current
                val prefs = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

                HomeScreen(navController = bottomNavController,   onLogout = {
                    // 로그아웃 처리 후 로그인 화면으로 이동
                    // AuthViewModel.logout() 같은 로직 호출
                    prefs.edit().apply {
                        putBoolean("isLoggedIn", false)
                        putString("mem_id", "")
                        putString("mem_level", "")
                        putString("mem_nick",  "")
                        putString("class_group_id", "")

                        apply()
                    }



                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },)
            }

            composable("notice") {
                NoticeListScreen(navController = bottomNavController)
            }

            composable("mind_video") {
                MindVideoListScreen(navController = bottomNavController)
            }

            composable(
                route = "board_detail/{tag}/{boardId}",
                arguments = listOf(
                    navArgument("tag")     { type = NavType.StringType; defaultValue = "notice" },
                    navArgument("boardId") { type = NavType.StringType; defaultValue = "0"      }
                )
            ) { backStackEntry ->
                val tag     = backStackEntry.arguments!!.getString("tag")!!
                val boardId = backStackEntry.arguments!!.getString("boardId")!!
                BoardDetailContent(
                    navController = bottomNavController,
                    tag           = tag,
                    boardId       = boardId
                )
            }




            navigation(
                startDestination = "touch/main",
                route = "touch"
            ) {
                composable("touch/main") {
                    MindTouchScreen(navController = bottomNavController)
                }


                composable(
                    route = "board_reg/{code}?idx={idx}",
                    arguments = listOf(
                        navArgument("code") { type = NavType.StringType },
                        navArgument("idx") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val code = backStackEntry.arguments?.getString("code")!!
                    val idx = backStackEntry.arguments?.getString("idx") // null 가능
                    BoardRegisterScreen(navController = bottomNavController, code = code, idx = idx)
                }


                composable("touchDetail/{code}/{text}") { backStackEntry ->
                    val code = backStackEntry.arguments?.getString("code")!!
                    val text = backStackEntry.arguments?.getString("text")!!
                    MindTouchDetailScreen(navController = bottomNavController,code = code, codeName = text)
                }

                composable(
                    route = "board_detail_touch/{tag}/{boardId}",
                    arguments = listOf(
                        navArgument("tag")     { type = NavType.StringType; defaultValue = "notice" },
                        navArgument("boardId") { type = NavType.StringType; defaultValue = "0"      }
                    )
                ) { backStackEntry ->
                    val tag     = backStackEntry.arguments!!.getString("tag")!!
                    val boardId = backStackEntry.arguments!!.getString("boardId")!!
                    BoardDetailTouch(
                        navController = bottomNavController,
                        tag           = tag,
                        boardId       = boardId
                    )
                }

            }


            navigation(
                startDestination = "challenge/main",
                route = "challenge"
            ) {
                composable("challenge/main") {
                    MindChallengeScreen(navController = bottomNavController)
                }

                composable("chalNote") {
                    MindChallengeNoteScreen(navController = bottomNavController)
                }



                composable("chaDetail/{code}/{text}") { backStackEntry ->
                    val code = backStackEntry.arguments?.getString("code")!!
                    val text = backStackEntry.arguments?.getString("text")!!
                    ChallengeDetailScreen(navController = bottomNavController,code = code, codeName = text)
                }

                composable(
                    route = "board_reg_ch/{code}?idx={idx}",
                    arguments = listOf(
                        navArgument("code") { type = NavType.StringType },
                        navArgument("idx") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val code = backStackEntry.arguments?.getString("code")!!
                    val idx = backStackEntry.arguments?.getString("idx") // null 가능
                    BoardChalRegisterScreen(navController = bottomNavController, code = code, idx = idx)
                }

                composable(
                    route = "board_detail_chal/{tag}/{boardId}",
                    arguments = listOf(
                        navArgument("tag")     { type = NavType.StringType; defaultValue = "notice" },
                        navArgument("boardId") { type = NavType.StringType; defaultValue = "0"      }
                    )
                ) { backStackEntry ->
                    val tag     = backStackEntry.arguments!!.getString("tag")!!
                    val boardId = backStackEntry.arguments!!.getString("boardId")!!
                    BoardDetailChal(
                        navController = bottomNavController,
                        tag           = tag,
                        boardId       = boardId
                    )
                }



            }



            navigation(
                startDestination = "talk/main",
                route = "talk"
            ) {
                composable("talk/main") {
                    MindTalkScreen(navController = bottomNavController)


                }

                composable("talk_write") {
                    MindTalkWriteScreen(navController = bottomNavController)


                }
                composable("talk_box") {
                    MindTalkBoxScreen(navController = bottomNavController)


                }
                composable("talk_recomm") {
                    MindTalkRecomScreen(navController = bottomNavController)


                }
                composable("talk_game") {
                    MindTalkGamecreen(navController = bottomNavController)

                }

                composable("talk_see") {
                    MindTalkSeeScreen(navController = bottomNavController)

                }

                composable("talk_listen") {
                    MindTalkListenScreen(navController = bottomNavController)

                }

                composable("talk_pic") {
                    MindTalkPictureScreen(navController = bottomNavController)

                }

                composable("talk_consult") {
                    MindTalkConsultScreen(navController = bottomNavController)

                }


                composable(
                    route = "board_detail_listen/{tag}/{boardId}",
                    arguments = listOf(
                        navArgument("tag")     { type = NavType.StringType; defaultValue = "notice" },
                        navArgument("boardId") { type = NavType.StringType; defaultValue = "0"      }
                    )
                ) { backStackEntry ->
                    val tag     = backStackEntry.arguments!!.getString("tag")!!
                    val boardId = backStackEntry.arguments!!.getString("boardId")!!
                    BoardDetailListen(
                        navController = bottomNavController,
                        tag           = tag,
                        boardId       = boardId
                    )
                }

                composable(
                    route = "board_detail_picture/{tag}/{boardId}",
                    arguments = listOf(
                        navArgument("tag")     { type = NavType.StringType; defaultValue = "notice" },
                        navArgument("boardId") { type = NavType.StringType; defaultValue = "0"      }
                    )
                ) { backStackEntry ->
                    val tag     = backStackEntry.arguments!!.getString("tag")!!
                    val boardId = backStackEntry.arguments!!.getString("boardId")!!
                    BoardDetailPicture(
                        navController = bottomNavController,
                        tag           = tag,
                        boardId       = boardId
                    )
                }

                composable(
                    route = "board_detail_consult/{tag}/{boardId}",
                    arguments = listOf(
                        navArgument("tag")     { type = NavType.StringType; defaultValue = "notice" },
                        navArgument("boardId") { type = NavType.StringType; defaultValue = "0"      }
                    )
                ) { backStackEntry ->
                    val tag     = backStackEntry.arguments!!.getString("tag")!!
                    val boardId = backStackEntry.arguments!!.getString("boardId")!!
                    BoardDetailConsult(
                        navController = bottomNavController,
                        tag           = tag,
                        boardId       = boardId
                    )
                }



                composable(
                    route = "board_reg_pic/{code}?idx={idx}",
                    arguments = listOf(
                        navArgument("code") { type = NavType.StringType },
                        navArgument("idx") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val code = backStackEntry.arguments?.getString("code")!!
                    val idx = backStackEntry.arguments?.getString("idx") // null 가능
                    BoardRegisterPictureScreen(navController = bottomNavController, code = code, idx = idx)
                }


                composable(
                    route = "board_reg_consult/{code}?idx={idx}",
                    arguments = listOf(
                        navArgument("code") { type = NavType.StringType },
                        navArgument("idx") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val code = backStackEntry.arguments?.getString("code")!!
                    val idx = backStackEntry.arguments?.getString("idx") // null 가능
                    BoardRegisterConsultScreen(navController = bottomNavController, code = code, idx = idx)
                }




                composable(
                    route = "board_reg_talk/{title}?idx={idx}",
                    arguments = listOf(
                        navArgument("title") { type = NavType.StringType },
                        navArgument("idx") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val title = backStackEntry.arguments?.getString("title")!!
                    val idx = backStackEntry.arguments?.getString("idx") // null 가능
                    TalkRegisterScreen(navController = bottomNavController, title = title, idx = idx)
                }

                composable(
                    route = "board_reg_box/{title}?idx={idx}",
                    arguments = listOf(
                        navArgument("title") { type = NavType.StringType },
                        navArgument("idx") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val title = backStackEntry.arguments?.getString("title")!!
                    val idx = backStackEntry.arguments?.getString("idx") // null 가능
                    RegisterBoxScreen(navController = bottomNavController, title = title, idx = idx)
                }


            }



            composable("growth") {
                MindGrowthScreen(navController = bottomNavController)
            }
            composable("profile") {
                val context = LocalContext.current
                val prefs = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

                ProfileEditScreen(navController = bottomNavController,  onLogout = {

                    // 로그아웃 처리 후 로그인 화면으로 이동
                    // AuthViewModel.logout() 같은 로직 호출
                    prefs.edit().apply {
                        putBoolean("isLoggedIn", false)
                        putString("mem_id", "")
                        putString("mem_level", "")
                        putString("mem_nick", "")
                        putString("class_group_id", "")

                        apply()
                    }



                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },)
            }



            composable("rank") {
                MindRankingScreen(navController = bottomNavController)
            }


        }
    }
}



