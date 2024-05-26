package com.education.ekagratagkquiz.main.presentation.screens


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.contribute_quiz.util.DeleteQuizEvents
import com.education.ekagratagkquiz.core.composables.pdfviewer.HorizontalPdfViewer
import com.education.ekagratagkquiz.core.composables.pdfviewer.VerticalPdfViewer
import com.education.ekagratagkquiz.core.data.UserStore
import com.education.ekagratagkquiz.core.util.admob.interstitialAdsContainer
import com.education.ekagratagkquiz.main.data.parcelable.QuizParcelable
import com.education.ekagratagkquiz.main.presentation.PreparationViewModel
import com.education.ekagratagkquiz.main.presentation.composables.HomeTabTitleBar
import com.education.ekagratagkquiz.main.util.PreparationEvents
import com.education.ekagratagkquiz.main.util.PreparationState
import com.education.ekagratagkquiz.main.util.QuizArrangementStyle
import com.pratikk.jetpdfvue.VerticalVueReader
import com.pratikk.jetpdfvue.state.VerticalVueReaderState
import com.pratikk.jetpdfvue.state.VueFileType
import com.pratikk.jetpdfvue.state.VueLoadState
import com.pratikk.jetpdfvue.state.VueResourceType
import com.pratikk.jetpdfvue.state.rememberHorizontalVueReaderState
import com.pratikk.jetpdfvue.state.rememberVerticalVueReaderState
import com.pratikk.jetpdfvue.util.compressImageToThreshold
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.rememberHorizontalPdfReaderState
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreparationScreen(
    modifier: Modifier = Modifier, navController: NavController,
    viewModel: PreparationViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
    parcelable: QuizParcelable? = null,
    preparationState: PreparationState = PreparationState()
) {

    val store = UserStore(context)
    val isAdmin = store.isAdmin.collectAsState(initial = false)
    val isSubscriptionActive = store.isSubscriptionActive.collectAsState(initial = false)
    val snackBarHostState = remember { SnackbarHostState() }

    val pdfFilePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.onPdfSelectEvents(PreparationEvents.readFile(uri = result.data?.data))
            }
        }
    val pdfVerticalReaderState = rememberVerticalPdfReaderState(
        resource = ResourceType.Remote(parcelable?.pdf ?: ""),
        isZoomEnable = true
    )

    val pdfHorizontalReaderState = rememberHorizontalPdfReaderState(
        resource = ResourceType.Remote(parcelable?.pdf ?: ""),
        isZoomEnable = true
    )

    val scope = rememberCoroutineScope()
    val verticalVueReaderState = rememberVerticalVueReaderState(
        resource = VueResourceType.Remote(
            url = parcelable?.pdf ?: "",
            fileType = VueFileType.PDF
        ),
        cache = 3 // By default 0
    )
    val horizontalVueReaderState = rememberHorizontalVueReaderState(
        resource = VueResourceType.Remote(
            url = parcelable?.pdf ?: "",
            fileType = VueFileType.PDF
        ),
        cache = 3 // By default 0
    )

// .toFile is an util extension function to convert any input stream to a file
    LaunchedEffect(isSubscriptionActive.value) {
        Log.d("TAGAD", "PreparationScreen: isSubscriptionActive = ${isSubscriptionActive.value}")
        interstitialAdsContainer(
            context as Activity,
            isActiveAd = isSubscriptionActive.value
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        floatingActionButton = {

            if (isAdmin.value) {

                ExtendedFloatingActionButton(
                    onClick = {

                        viewModel.onPdfSelectEvents(PreparationEvents.OnRequestPermission)
//                        navController.navigate(NavRoutes.NavCreateQuizRoute.route + "/${quizParcelable.uid}")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = "Upload Pdf"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Upload Pdf")
                }

            }
        }
    ) { padding ->


        if (viewModel.deleteQuizState.value.showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onDeleteQuizEvent(DeleteQuizEvents.OnDeleteCanceled) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.onDeleteQuizEvent(
                                DeleteQuizEvents.OnDeleteConfirmed(
                                    quizPath = viewModel.deleteQuizState.value.quizPath
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(text = "Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.onDeleteQuizEvent(DeleteQuizEvents.OnDeleteCanceled) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text(text = "Cancel")
                    }
                },
                title = { Text(text = "Do you want to delete this quiz") },
                text = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.deleteQuizState.value.isDeleting) {
                            Text(
                                text = "Deleting please wait",
                                color = MaterialTheme.colorScheme.secondary
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.delete_full_quiz_desc),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            )
        }

        if (preparationState.isRequestPermission) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"  // Set the MIME type to filter files
//                type = "application/vnd.ms-excel" // MIME type for Excel files
            }
            pdfFilePicker.launch(intent)
        }

        Column(
            modifier = modifier
                .padding(vertical = 18.dp, horizontal = 16.dp)
                .padding(padding)
                .fillMaxSize()
        ) {
            HomeTabTitleBar(
                title = "Preparation on ${parcelable?.subject}",
                arrangementStyle = viewModel.arrangementStyle.value,
                onListStyle = { viewModel.onArrangementChange(QuizArrangementStyle.ListStyle) },
                onGridStyle = { viewModel.onArrangementChange(QuizArrangementStyle.GridStyle) }
            )

            Text(
                text = stringResource(id = R.string.preparation_tab_info),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                when (viewModel.arrangementStyle.value) {
                    QuizArrangementStyle.GridStyle -> {

//                        PDFWebView(pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
                        /*VerticalPDFReader(
                            state = pdfVerticalReaderState,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.Gray)
                        )*/


                        HorizontalPdfViewer(horizontalVueReaderState)
                    }

                    QuizArrangementStyle.ListStyle -> {
                        VerticalPdfViewer(verticalVueReaderState)
                        /* VerticalVueReader(
                             modifier = Modifier
                                 .fillMaxSize()
                                 .background(color = Color.Gray), // Modifier for pager
                             contentModifier = Modifier, // Modifier for Individual page
                             verticalVueReaderState = verticalVueReaderState
                         )*/


                        /*HorizontalPDFReader(
                            state = pdfHorizontalReaderState,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.Gray)
                        )*/
                    }

                    else -> {}
                }

                /*val quizContent = viewModel.pdfs.value
                if (quizContent.isLoading)
                    CircularProgressIndicator()
                else if (quizContent.content?.isNotEmpty() == true) {
                     AllPdfList(
                         preparationState = viewModel.preparationState.value,
                         navController = navController,
                         arrangementStyle = viewModel.arrangementStyle.value,
                         modifier = Modifier.fillMaxSize(),
                         onUnselect = { },
                         showDialog = viewModel.showDialog.value,
                         selectedQuiz = viewModel.selectedPdfs.value,
                         isAdmin = isAdmin
                     )
                } else Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.quiz),
                        contentDescription = "No contribution",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceTint),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No pdf are available",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "No pdf are added or none of them are  approved",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }*/
            }

        }
    }
}

