package com.luisdev.finanzapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.luisdev.finanzapp.ui.components.CustomTextField
import com.luisdev.finanzapp.ui.theme.*
import com.luisdev.finanzapp.ui.viewmodel.UserViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitialConfigurationScreen(
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userPrefs by viewModel.userPreferences.collectAsState()

    // Estados locales
    var currentStep by remember { mutableIntStateOf(1) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // Inicializar nombres si ya existen
    LaunchedEffect(userPrefs) {
        userPrefs?.let {
            if (firstName.isEmpty()) firstName = it.firstName
            if (lastName.isEmpty()) lastName = it.lastName
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            tempUri = uri
            viewModel.updateProfilePicture(it.toString())
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempUri != null) {
            viewModel.updateProfilePicture(tempUri.toString())
        }
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
            TopAppBar(
                title = { },
                navigationIcon = {
                    if (currentStep > 1) {
                        IconButton(onClick = {
                            var deletePhoto = false
                            if(currentStep == 3) {
                                deletePhoto = true
                            }

                            currentStep--

                            if(deletePhoto) {
                                viewModel.updateProfilePicture("")
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        val isPhotoSelected = !userPrefs?.profilePicturePath.isNullOrEmpty()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Progress Indicators (3 Bars)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                ProgressBar(isActive = true)
                Spacer(modifier = Modifier.width(8.dp))
                ProgressBar(isActive = currentStep >= 2)
                Spacer(modifier = Modifier.width(8.dp))
                ProgressBar(isActive = isPhotoSelected)
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (currentStep == 1) {
                // STEP 1: NAMES
                OnboardingIcon(icon = Icons.Rounded.Person)
                Spacer(modifier = Modifier.height(32.dp))
                Text("Cuéntanos sobre ti", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ingrese su nombre para personalizar su experiencia financiera.", color = TextGray, fontSize = 16.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))

                CustomTextField(label = "Nombre", value = firstName, onValueChange = { firstName = it }, placeholder = "Ej: Luis")
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(label = "Apellido", value = lastName, onValueChange = { lastName = it }, placeholder = "Ej: Castillo")

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        currentStep = 2
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                    enabled = firstName.isNotBlank() && lastName.isNotBlank()
                ) {
                    Text("Continuar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .border(2.dp, BrandGreen.copy(alpha = 0.5f), CircleShape)
                        .background(DarkCardBackground),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPhotoSelected) {
                        AsyncImage(
                            model = userPrefs?.profilePicturePath,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        OnboardingIcon(icon = Icons.Rounded.CameraAlt, size = 48.dp)
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
                Text(if (!isPhotoSelected) "Añade una foto" else "¡Luce muy bien!", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Una foto de perfil ayuda a personalizar tu experiencia de gestión financiera.", color = TextGray, fontSize = 16.sp, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.weight(1f))

                if (!isPhotoSelected) {
                    Button(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                val uri = createTempPictureUri(context)
                                tempUri = uri
                                cameraLauncher.launch(uri)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                    ) {
                        Icon(Icons.Rounded.PhotoCamera, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar una foto", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCardBackground)
                    ) {
                        Icon(Icons.Rounded.Image, contentDescription = null, tint = BrandGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Elegir de la galería", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(onClick = {
                        viewModel.saveProfilePictureLocally(tempUri.toString())
                        viewModel.updateName(firstName, lastName)
                        onFinish()
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)) {
                        Text("Continuar", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { viewModel.updateProfilePicture("") }) {
                        Text("Tomar otra foto", color = BrandGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            if (currentStep == 2 && !isPhotoSelected) {
                TextButton(
                    onClick = {
                        viewModel.updateName(firstName, lastName)
                        onFinish()
                    }
                ) { Text("Omitir por ahora", color = BrandGreen, fontWeight = FontWeight.Bold) }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProgressBar(isActive: Boolean) {
    Box(modifier = Modifier
        .width(32.dp)
        .height(4.dp)
        .clip(CircleShape)
        .background(if (isActive) BrandGreen else DarkCardBackground))
}

@Composable
fun OnboardingIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, size: androidx.compose.ui.unit.Dp = 32.dp) {
    Box(modifier = Modifier
        .size(size * 2)
        .clip(RoundedCornerShape(16.dp))
        .background(BrandGreen.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(size))
    }
}

fun createTempPictureUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    return FileProvider.getUriForFile(context, "com.luisdev.finanzapp.fileprovider", file)
}

