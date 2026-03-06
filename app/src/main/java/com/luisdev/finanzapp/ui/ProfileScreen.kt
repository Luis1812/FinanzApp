package com.luisdev.finanzapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.luisdev.finanzapp.ui.components.CustomTextField
import com.luisdev.finanzapp.ui.theme.*
import com.luisdev.finanzapp.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToFullScreenImage: (String) -> Unit,
    viewModel: UserViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val userPrefs by viewModel.userPreferences.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var showImagePicker by remember { mutableStateOf(false) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(userPrefs) {
        userPrefs?.let {
            firstName = it.firstName
            lastName = it.lastName
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.saveProfilePictureLocally(it.toString())
            showImagePicker = false
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            viewModel.saveProfilePictureLocally(tempUri.toString())
        }
        showImagePicker = false
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createTempPictureUri(context)
            tempUri = uri
            cameraLauncher.launch(uri)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Configuración de Perfil",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Profile Picture Section
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .border(2.dp, BrandGreen.copy(alpha = 0.5f), CircleShape)
                            .background(DarkCardBackground)
                            .clickable(enabled = !userPrefs?.profilePicturePath.isNullOrEmpty()) {
                                userPrefs?.profilePicturePath?.let { onNavigateToFullScreenImage(it) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!userPrefs?.profilePicturePath.isNullOrEmpty()) {
                            AsyncImage(
                                model = userPrefs?.profilePicturePath,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Rounded.Person,
                                contentDescription = null,
                                tint = BrandGreen,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }

                    // Edit Icon Overlay
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BrandGreen)
                            .border(4.dp, DarkBackground, CircleShape)
                            .clickable { showImagePicker = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.CameraAlt,
                            contentDescription = "Edit photo",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Text Fields
                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    CustomTextField(
                        label = "NOMBRE",
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = "Ej: Luis"
                    )
                    CustomTextField(
                        label = "APELLIDO",
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = "Ej: Castillo"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Buttons
                Button(
                    onClick = {
                        if (firstName.isNotBlank() && lastName.isNotBlank()) {
                            viewModel.updateName(firstName, lastName)
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text(
                        "Guardar Cambios",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (showImagePicker) {
                ImagePickerModal(
                    onDismiss = { showImagePicker = false },
                    onTakePhotoClick = {
                        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        if (permission == PackageManager.PERMISSION_GRANTED) {
                            val uri = createTempPictureUri(context)
                            tempUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    onChooseFromGalleryClick = { galleryLauncher.launch("image/*") }
                )
            }
        }
    }
}

@Composable
fun ImagePickerModal(
    onDismiss: () -> Unit,
    onTakePhotoClick: () -> Unit,
    onChooseFromGalleryClick: () -> Unit,
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isVisible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {},
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Selecciona una opción",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    // Take Photo
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onTakePhotoClick() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.PhotoCamera,
                            contentDescription = null,
                            tint = BrandGreen
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Tomar una foto", color = Color.White)
                    }

                    // Choose From Gallery
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onChooseFromGalleryClick() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Image, contentDescription = null, tint = BrandGreen)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Elegir de la galería", color = Color.White)
                    }
                }
            }
        }
    }
}
