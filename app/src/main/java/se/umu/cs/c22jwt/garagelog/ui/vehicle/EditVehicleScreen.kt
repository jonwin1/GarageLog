package se.umu.cs.c22jwt.garagelog.ui.vehicle

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.text.isDigitsOnly
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.launch
import se.umu.cs.c22jwt.garagelog.R
import se.umu.cs.c22jwt.garagelog.data.Vehicle
import se.umu.cs.c22jwt.garagelog.ui.components.PhotoBottomSheet
import java.io.File
import java.util.Date

/**
 * Compose screen for editing a vehicle.
 *
 * @param viewModel         ViewModel of the vehicle.
 * @param navigateBack      Called to navigate back to the previous page.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVehicleScreen(
    viewModel: VehicleViewModel,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val vehicleState: Vehicle by viewModel.vehicle.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    var name by rememberSaveable { mutableStateOf("") }
    var nameIsValid by rememberSaveable { mutableStateOf(true) }

    var registrationNumber by rememberSaveable { mutableStateOf("") }
    var registrationNumberError: String? by rememberSaveable {
        mutableStateOf(
            null
        )
    }

    var mileage: Int? by rememberSaveable { mutableStateOf(null) }
    var notes by rememberSaveable { mutableStateOf("") }

    val imageFileName by viewModel.imageFileName.collectAsState()
    val imageFile = imageFileName?.let { File(context.filesDir, it) }

    var pendingPhotoName by rememberSaveable { mutableStateOf<String?>(null) }

    /**
     * Camera intent for taking a picture.
     * pendingPhotoName must be set before launching.
     * See requestTakePhoto.
     */
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto) {
            if (pendingPhotoName != null) {
                val file = File(context.filesDir, pendingPhotoName!!)
                viewModel.setImageFile(context, file)
            }
        }
    }

    /**
     * Media picker intent for picking a picture.
     * pendingPhotoName must be set before launching.
     * See requestPickPhoto.
     */
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            if (pendingPhotoName != null) {
                val file = File(context.filesDir, pendingPhotoName!!)
                val inputStream = context.contentResolver.openInputStream(uri)
                val outputStream = file.outputStream()
                inputStream?.use { inputStream ->
                    outputStream.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                viewModel.setImageFile(context, file)
            }
        }
    }

    val requestTakePhoto = {
        pendingPhotoName = "IMG_${Date()}.JPG"
        val photoFile = File(context.filesDir, pendingPhotoName!!)
        val photoUri = FileProvider.getUriForFile(
            context, "se.umu.cs.c22jwt.garagelog.fileprovider", photoFile
        )
        cameraLauncher.launch(photoUri)
    }

    val requestPickPhoto = {
        pendingPhotoName = "IMG_${Date()}.JPG"
        mediaPickerLauncher.launch(
            PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }


    // Warn the user if trying to navigate back without saving.
    BackHandler {
        val vehicle = Vehicle(registrationNumber, name, notes, mileage ?: 0, imageFile?.name)
        if (viewModel.isSaved(vehicle)) {
            navigateBack()
        } else {
            showExitDialog = true
        }
    }

    // Set values when the vehicle has been received from the database without
    // overwriting user entered values.
    LaunchedEffect(vehicleState) {
        if (name.isEmpty()) {
            name = vehicleState.name
        }
        if (registrationNumber.isEmpty()) {
            registrationNumber = vehicleState.registrationNumber
        }
        if (mileage == null) {
            mileage = vehicleState.mileage
        }
        if (notes.isEmpty()) {
            notes = vehicleState.notes
        }
    }

    Scaffold(
        topBar = {
            val vehicle = Vehicle(registrationNumber, name, notes, mileage ?: 0, imageFile?.name)
            val isSaved = viewModel.isSaved(vehicle)

            EditVehicleTopBar(
                scrollBehavior,
                viewModel.isNewVehicle,
                !isSaved && !name.isEmpty() && !registrationNumber.isEmpty() && nameIsValid && registrationNumberError == null,
                { if (isSaved) navigateBack() else showExitDialog = true },
                {
                    viewModel.save(context, vehicle)
                    navigateBack()
                })
        }, modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 16.dp)
        ) {
            item {
                AddImageButton(
                    imageFile,
                    { showBottomSheet = true },
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(1f / 2f)
                        .aspectRatio(1f)
                )
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it; nameIsValid = !name.isEmpty()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    label = { Text("Vehicle name*") },
                    supportingText = { Text("*required") },
                    singleLine = true,
                    isError = !nameIsValid,
                    trailingIcon = {
                        if (!nameIsValid) {
                            Icon(
                                Icons.Default.Warning, "Warning icon"
                            )
                        } else null
                    })
            }

            item {
                OutlinedTextField(
                    value = registrationNumber,
                    onValueChange = {
                        registrationNumber = it.uppercase()
                        registrationNumberError =
                            if (registrationNumber.isEmpty()) "*required" else null
                        viewModel.exists(registrationNumber) { exists ->
                            if (exists) {
                                registrationNumberError = "Must be unique"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    enabled = viewModel.isNewVehicle,
                    readOnly = !viewModel.isNewVehicle,
                    label = { Text("Registration number*") },
                    supportingText = {
                        Text(
                            registrationNumberError ?: "*required"
                        )
                    },
                    singleLine = true,
                    isError = registrationNumberError != null,
                    trailingIcon = {
                        if (registrationNumberError != null) {
                            Icon(Icons.Default.Warning, "Warning icon")
                        } else null
                    })
            }

            item {
                OutlinedTextField(
                    value = (mileage ?: "").toString(),
                    onValueChange = {
                        if (it.isEmpty()) {
                            mileage = null
                        } else if (it.isDigitsOnly() && it.length < 10) {
                            mileage = it.toInt()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    label = { Text("Mileage") },
                    suffix = { Text("km") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = {
                        notes = it
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    },
                    label = { Text("Notes") },
                    singleLine = false,
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .bringIntoViewRequester(bringIntoViewRequester)
                )
            }
        }

        if (showBottomSheet) {
            PhotoBottomSheet(
                onDismiss = { showBottomSheet = false },
                requestTakePhoto = requestTakePhoto,
                requestPickPhoto = requestPickPhoto,
                onRemove = { viewModel.setImageFile(context, null) },
                bottomSheetState = bottomSheetState,
            )
        }

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Discard changes?") },
                confirmButton = {
                    TextButton(onClick = {
                        showExitDialog = false
                        viewModel.resetImageFile(context)
                        navigateBack()
                    }) { Text("Discard") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showExitDialog = false
                    }) { Text("Cancel") }
                },
            )
        }
    }
}

/**
 * A clickable image.
 *
 * @param image     The image file to show.
 * @param onClick   Called when clicking the image.
 * @param modifier  [Modifier] to apply.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Composable
fun AddImageButton(
    image: File?, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(image).crossfade(true).build(),
        contentDescription = if (image == null) {
            "Add image button"
        } else {
            "Image of vehicle"
        },
        modifier = modifier
            .border(
                1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(if (image == null) 16.dp else 0.dp),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(R.drawable.outline_add_a_photo_24),
        error = painterResource(R.drawable.outline_add_a_photo_24),
        colorFilter = if (image == null) {
            ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        } else null,
    )
}

/**
 * Top bar for the edit vehicle screen.
 *
 * @param scrollBehavior    Scroll behaviour of the top bar.
 * @param isNewVehicle      Whether it is a new vehicle or not.
 * @param enableSave        Whether to enable the save button
 * @param onClose           Called when the close button is pressed.
 * @param onSave            Called when the save button is pressed.
 * @param modifier          [Modifier] to be applied.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVehicleTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    isNewVehicle: Boolean,
    enableSave: Boolean,
    onClose: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier, title = {
            if (isNewVehicle) {
                Text("New vehicle")
            } else {
                Text("Edit vehicle")
            }
        }, navigationIcon = {
            IconButton(onClick = { onClose() }) {
                Icon(Icons.Default.Close, "Close")
            }
        }, actions = {
            TextButton(onClick = { onSave() }, enabled = enableSave) {
                Text("Save")
            }
        }, scrollBehavior = scrollBehavior
    )
}