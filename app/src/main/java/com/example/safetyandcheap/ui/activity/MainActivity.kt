package com.example.safetyandcheap.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.safetyandcheap.R
import com.example.safetyandcheap.auth.TokenManager
import com.example.safetyandcheap.service.CurrentUserState
import com.example.safetyandcheap.service.PropertyType
import com.example.safetyandcheap.ui.UiState
import com.example.safetyandcheap.ui.screens.FillPersonalDataScreen
import com.example.safetyandcheap.ui.screens.list.FilterScreen
import com.example.safetyandcheap.ui.screens.list.ListScreen
import com.example.safetyandcheap.ui.screens.ProfileScreen
import com.example.safetyandcheap.ui.screens.list.PropertyScreen
import com.example.safetyandcheap.ui.screens.add_property.AddApartmentPart
import com.example.safetyandcheap.ui.screens.add_property.AddHousePart
import com.example.safetyandcheap.ui.screens.add_property.AddPropertyDealTypeScreen
import com.example.safetyandcheap.ui.screens.add_property.AddPropertyScreen
import com.example.safetyandcheap.ui.screens.add_property.AddPropertyTypeScreen
import com.example.safetyandcheap.ui.screens.add_property.AddRoomPart
import com.example.safetyandcheap.ui.screens.add_property.YourAnnouncementsScreen
import com.example.safetyandcheap.ui.screens.list.CartScreen
import com.example.safetyandcheap.ui.screens.phone.AddPhoneNumberScreen
import com.example.safetyandcheap.ui.screens.phone.PhoneCodeVerificationScreen
import com.example.safetyandcheap.ui.theme.SafetyAndCheapTheme
import com.example.safetyandcheap.ui.util.AuthChangeScreenTextButton
import com.example.safetyandcheap.ui.util.BottomMenuButton
import com.example.safetyandcheap.ui.util.MainNavGroups
import com.example.safetyandcheap.ui.util.MainNavRoutes
import com.example.safetyandcheap.viewmodel.AddAnnouncementViewModel
import com.example.safetyandcheap.viewmodel.AddPhoneNumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var currentUserState: CurrentUserState

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        lifecycleScope.launch {
            if (tokenManager.getToken() == null) {
                Log.d("NAV", "Running Auth from Activity")
                navigateToAuth()
            } else {
                Log.d("NAV", "Running Main from Activity")
                currentUserState.refresh()
                setContent {
                    SafetyAndCheapTheme {
                        Main(
                            currentUserState = currentUserState,
                            onLogout = ::navigateToAuth
                        )
                    }
                }
            }
        }
    }

    private fun navigateToAuth() {
        startActivity(Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(
    currentUserState: CurrentUserState,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val currentRoute = rememberSaveable { mutableStateOf<MainNavRoutes?>(null) }
    val userState =
        currentUserState.userState.collectAsState(initial = CurrentUserState.UserState.Loading)
    val context = LocalContext.current
    Log.d("NAV", "Starting main")

    when (userState.value) {
        is CurrentUserState.UserState.Loading -> {
            Log.d("NAV", "Loading")
            LoadingDialog()
        }

        is CurrentUserState.UserState.Unauthenticated -> {
            Log.d("NAV", "Logout")
            LaunchedEffect(Unit) {
                (context as? ComponentActivity)?.finish()
                onLogout()
            }
        }

        is CurrentUserState.UserState.Error -> {
            Log.d("NAV", "Error")
            ErrorMessage(currentUserState)
        }

        else -> {}
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            if (currentRoute.value == MainNavRoutes.PropertyScreen) {
                return@Scaffold
            }
            MainTopBar(
                navController = navController,
                currentRoute = currentRoute
            )
        },
        bottomBar = {
            if (currentRoute.value?.group == MainNavGroups.List
                || currentRoute.value?.group == MainNavGroups.Profile
                || currentRoute.value?.group == MainNavGroups.Cart
            ) {
                MainBottomBar(
                    onListClick = {
                        navController.navigate(MainNavRoutes.ListMenu.route) {
                            launchSingleTop = true
                        }
                    },
                    onCartClick = {
                        navController.navigate(MainNavRoutes.Cart.route)
                    },
                    onProfileClick = {
                        navController.navigate(MainNavRoutes.Profile.route) {
                            launchSingleTop = true
                        }
                    },
                    currentRoute = currentRoute
                )
            }
        }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainNavRoutes.Profile.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainNavRoutes.Profile.route) {
                ProfileScreen(
                    modifier = Modifier,
                    onPersonalDataPressed = {
                        navController.navigate(MainNavRoutes.PersonalDataFill.route) {
                            launchSingleTop = true
                        }
                    },
                    onAddAnnouncementPressed = {
                        if ((userState.value as CurrentUserState.UserState.Loaded).user.phoneNumber != null) {
                            navController.navigate(MainNavRoutes.NewAnnouncementChooseDealType.route) {
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate(MainNavRoutes.AddPhoneNumber.route) {
                                launchSingleTop = true
                            }
                        }
                    },
                    onYourAnnouncementPressed = {
                        navController.navigate(MainNavRoutes.YourAnnouncements.route) {
                            launchSingleTop = true
                        }
                    },
                    onExitPressed = {
                        currentUserState.clearUser()
                    }
                )
            }

            composable(MainNavRoutes.PersonalDataFill.route) {
                FillPersonalDataScreen(
                    onSavePressed = {}
                )
            }

            navigation(
                startDestination = MainNavRoutes.ListMenu.route,
                route = MainNavRoutes.ListFlow.route
            ) {
                composable(MainNavRoutes.ListMenu.route) {
                    val parentEntry = remember {
                        navController.getBackStackEntry(MainNavRoutes.ListFlow.route)
                    }
                    ListScreen(
                        onAnnouncementPressed = {
                            navController.navigate(MainNavRoutes.PropertyScreen.route) {
                                launchSingleTop = true
                            }
                        },
                        viewModel = hiltViewModel(parentEntry)
                    )
                }
                composable(
                    route = MainNavRoutes.Filtration.route,
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    }
                ) {
                    val parentEntry = remember {
                        navController.getBackStackEntry(MainNavRoutes.ListFlow.route)
                    }
                    FilterScreen(
                        viewModel = hiltViewModel(parentEntry),
                        onApply = {
                            navController.navigate(MainNavRoutes.ListMenu.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(MainNavRoutes.Cart.route) {
                    val parentEntry = remember {
                        navController.getBackStackEntry(MainNavRoutes.ListFlow.route)
                    }
                    CartScreen(
                        viewModel = hiltViewModel(parentEntry),
                        onAnnouncementPressed = {
                            navController.navigate(MainNavRoutes.PropertyScreen.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(MainNavRoutes.PropertyScreen.route) {
                    val parentEntry = remember {
                        navController.getBackStackEntry(MainNavRoutes.ListFlow.route)
                    }
                    PropertyScreen(
                        viewModel = hiltViewModel(parentEntry),
                        onBackPressed = {
                            if (navController.previousBackStackEntry != null) {
                                navController.popBackStack()
                            }
                        }
                    )
                }

            }

            navigation(
                startDestination = MainNavRoutes.NewAnnouncementChooseDealType.route,
                route = MainNavRoutes.NewAnnouncementFlow.route
            ) {
                composable(MainNavRoutes.YourAnnouncements.route) {
                    val parentEntry = remember {
                        navController.getBackStackEntry(MainNavRoutes.NewAnnouncementFlow.route)
                    }
                    val announcementViewModel: AddAnnouncementViewModel = hiltViewModel(parentEntry)
                    LaunchedEffect(Unit) {
                        if (announcementViewModel.uiState.value !is UiState.Success)
                            announcementViewModel.setupUser()
                    }
                    YourAnnouncementsScreen(
                        viewModel = announcementViewModel,
                        onAnnouncementPressed = {
                            navController.navigate("${MainNavRoutes.NewAnnouncement.route}/true")
                        }
                    )
                }
                composable(MainNavRoutes.NewAnnouncementChoosePropertyType.route) {
                    val parentEntry = remember {
                        navController.getBackStackEntry(MainNavRoutes.NewAnnouncementFlow.route)
                    }
                    val announcementViewModel: AddAnnouncementViewModel = hiltViewModel(parentEntry)
                    AddPropertyTypeScreen(
                        onTypePressed = {
                            navController.navigate("${MainNavRoutes.NewAnnouncement.route}/false")
                        },
                        viewModel = announcementViewModel
                    )
                }
                composable(MainNavRoutes.NewAnnouncementChooseDealType.route) {
                    val parentEntry = remember {
                        navController.getBackStackEntry(MainNavRoutes.NewAnnouncementFlow.route)
                    }
                    val announcementViewModel: AddAnnouncementViewModel = hiltViewModel(parentEntry)
                    LaunchedEffect(Unit) {
                        if (announcementViewModel.uiState.value !is UiState.Success)
                            announcementViewModel.setupUser()
                    }
                    AddPropertyDealTypeScreen(
                        onSaleTypePressed = {
                            navController.navigate(MainNavRoutes.NewAnnouncementChoosePropertyType.route) {
                                launchSingleTop = true
                            }
                        },
                        viewModel = announcementViewModel
                    )
                }
                composable(
                    route = "${MainNavRoutes.NewAnnouncement.route}/{isUpdate}",
                    arguments = listOf(navArgument("isUpdate") { type = NavType.BoolType })
                ) { backStackEntry ->
                    val isUpdate = backStackEntry.arguments!!.getBoolean("isUpdate")
                    val parentEntry = remember {
                        navController.getBackStackEntry(MainNavRoutes.NewAnnouncementFlow.route)
                    }
                    val announcementViewModel: AddAnnouncementViewModel = hiltViewModel(parentEntry)
                    val onApplyPressed = {
                        navController.navigate(MainNavRoutes.Profile.route) {
                            popUpTo(MainNavRoutes.Profile.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                    when (announcementViewModel.selectedPropertyType) {
                        PropertyType.Room -> AddPropertyScreen(
                            onApplyPressed = onApplyPressed,
                            viewModel = announcementViewModel,
                            specificPart = {
                                AddRoomPart(
                                    viewModel = announcementViewModel
                                )
                            },
                            isLoaded = isUpdate
                        )

                        PropertyType.House -> AddPropertyScreen(
                            onApplyPressed = onApplyPressed,
                            viewModel = announcementViewModel,
                            specificPart = {
                                AddHousePart(
                                    viewModel = announcementViewModel
                                )
                            },
                            isLoaded = isUpdate
                        )

                        PropertyType.Apartment -> AddPropertyScreen(
                            onApplyPressed = onApplyPressed,
                            viewModel = announcementViewModel,
                            specificPart = {
                                AddApartmentPart(
                                    viewModel = announcementViewModel
                                )
                            },
                            isLoaded = isUpdate
                        )

                        null -> {}
                    }
                }
            }

            navigation(
                startDestination = MainNavRoutes.AddPhoneNumber.route,
                route = MainNavRoutes.PhoneNumberFlow.route
            ) {
                composable(MainNavRoutes.AddPhoneNumber.route) { backStackEntry ->
                    val parentEntry = remember {
                        navController.getBackStackEntry(MainNavRoutes.PhoneNumberFlow.route)
                    }
                    val phoneNumberViewModel: AddPhoneNumberViewModel = hiltViewModel(parentEntry)
                    AddPhoneNumberScreen(
                        onBackPressed = { navController.popBackStack() },
                        onSubmitNumberPressed = {
                            navController.navigate(MainNavRoutes.VerifyPhoneNumber.route) {
                                launchSingleTop = true
                            }
                        },
                        viewModel = phoneNumberViewModel
                    )
                }

                composable(MainNavRoutes.VerifyPhoneNumber.route) { backStackEntry ->
                    val parentEntry = remember {
                        navController.getBackStackEntry(MainNavRoutes.PhoneNumberFlow.route)
                    }
                    val phoneNumberViewModel: AddPhoneNumberViewModel = hiltViewModel(parentEntry)
                    phoneNumberViewModel.resetState()

                    PhoneCodeVerificationScreen(
                        onBackPressed = { navController.popBackStack() },
                        onCodeVerified = {
                            currentUserState.refresh()
                            navController.navigate(MainNavRoutes.NewAnnouncementChooseDealType.route) {
                                launchSingleTop = true
                                popUpTo(MainNavRoutes.Profile.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        viewModel = phoneNumberViewModel
                    )
                }
            }
        }
    }
    DisposableEffect(key1 = navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentRoute.value = MainNavRoutes.getByRoute(destination.route)
        }
        onDispose {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navController: NavController,
    currentRoute: MutableState<MainNavRoutes?>,
) {
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        modifier = modifier.height(92.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.tertiary,
            actionIconContentColor = MaterialTheme.colorScheme.tertiary,
        ),
        navigationIcon = {
            Box(Modifier.fillMaxHeight()) {
                IconButton(
                    onClick = {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(
                        painterResource(id = R.drawable.arrow_right),
                        contentDescription = "Back"
                    )
                }
            }
        },
        title = {
            Box(
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    modifier = Modifier.align(alignment = Alignment.Center),
                    text = currentRoute.value?.group?.localName ?: "",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight(600),
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                )
            }
        },
        actions = {
            if (currentRoute.value?.group == MainNavGroups.List) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter),
                        contentDescription = "Filter",
                        modifier = Modifier
                            .width(14.77049.dp)
                            .height(13.93443.dp)
                            .clickable(onClick = {
                                navController.navigate(MainNavRoutes.Filtration.route)
                            })
                    )
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.frame),
                        contentDescription = "Sort",
                        modifier = Modifier
                            .width(17.dp)
                            .height(17.dp)
                    )
                }

            }
        }
    )
}

@Composable
fun MainBottomBar(
    modifier: Modifier = Modifier,
    onListClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    currentRoute: MutableState<MainNavRoutes?>
) {
    BottomAppBar(
        modifier = modifier.height(84.dp),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomMenuButton(
                onClick = onListClick,
                currentRoute = currentRoute.value?.route,
                routeToItem = MainNavRoutes.ListMenu.route
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painterResource(id = R.drawable.note_text),
                        contentDescription = "List",
                        modifier = Modifier
                    )
                    Text(text = "List", style = MaterialTheme.typography.bodySmall)
                }
            }
            BottomMenuButton(
                onClick = onCartClick,
                currentRoute = currentRoute.value?.route,
                routeToItem = MainNavRoutes.Cart.route
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painterResource(id = R.drawable.bag),
                        contentDescription = "Shop",
                        modifier = Modifier
                    )
                    Text(text = "Cart", style = MaterialTheme.typography.bodySmall)
                }
            }
            BottomMenuButton(
                onClick = onProfileClick,
                currentRoute = currentRoute.value?.route,
                routeToItem = MainNavRoutes.Profile.route
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painterResource(id = R.drawable.iconprofile),
                        contentDescription = "Shop",
                        modifier = Modifier
                    )
                    Text(text = "Profile", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun LoadingDialog() {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Loading") },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator()
                Spacer(Modifier.width(16.dp))
                Text("Please, wait...")
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun ErrorMessage(currentUserState: CurrentUserState) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Network Error") },
        confirmButton = {
            AuthChangeScreenTextButton(
                onClick = { currentUserState.refresh() },
                text = "Try again"
            )
        }
    )
}