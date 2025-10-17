package com.plcoding.drawinginjetpackcompose

import DrawingCanvas
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.signaturetaker.DrawingViewModel
import com.plcoding.drawinginjetpackcompose.Animation.Animator
import com.plcoding.drawinginjetpackcompose.ImageProcessing.UploadResponse
import com.plcoding.drawinginjetpackcompose.ImageProcessing.useRetrofit
import com.plcoding.drawinginjetpackcompose.ui.theme.DrawingInJetpackComposeTheme
import convertToPNG
import convertToPart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import kotlin.math.abs
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MainActivity : ComponentActivity() {
    val REQUEST_CODE: Int = 13
    var drViewModel: DrawingViewModel? = null

    fun switchToCamera(viewModel: DrawingViewModel) {
        val cameraIntent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 13, null)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val cameraImage: Bitmap = data?.extras?.get("data") as Bitmap
            drViewModel?.id_card_image = cameraImage
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            DrawingInJetpackComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = viewModel<DrawingViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    drViewModel = viewModel

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DrawingCanvas(
                            viewModel,
                            paths = state.paths,
                            currentPath = state.currentPath,
                            onAction = viewModel::onAction,
                            modifier = Modifier
                                .fillMaxWidth(0.95f)   // 95% width
                                .fillMaxHeight(2f / 3f) // 2/3 height
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button (
                                onClick = {viewModel.onClearCanvas()},
                                shape = RectangleShape,
                                modifier = Modifier
                                    .fillMaxWidth(0.48f)
                                    .fillMaxHeight(1.2f / 3f)
                            ) {
                                Text("Resetează")
                            }

                            Button(
                                onClick = {
                                    val currentTime = System.currentTimeMillis()
                                    if (abs(currentTime - viewModel.lastClickedTime) > 1000) {
                                        viewModel.lastClickedTime = currentTime

                                        if (viewModel.id_card_image != null && !viewModel.state.value.paths.isEmpty()) {
                                            viewModel.viewModelScope.launch {

                                            val bitmap: Bitmap = convertToPNG(Resources.getSystem().displayMetrics.widthPixels,
                                                Resources.getSystem().displayMetrics.heightPixels * 2 / 3,
                                                viewModel
                                            )

                                                val uid: String = Uuid.random().toString()
                                                val part1 = convertToPart(bitmap, true, uid)
                                                val part2 =
                                                    convertToPart(viewModel.id_card_image!!, false, uid)
                                                val images = ArrayList<MultipartBody.Part>()
                                                images.addAll(listOf(part2, part1))

                                                val resp: UploadResponse = useRetrofit(images)
                                                var intResp = 1
                                                resp.results.forEach { resp ->
                                                    if (!resp.message.contains("File uploaded successfully"))
                                                        intResp = -1
                                                    Log.d("link", "$resp")
                                                }

                                                viewModel.sent.intValue = intResp
                                            }
                                        } else {
                                            viewModel.sent.intValue = -1
                                        }
                                    }
                                },
                                shape = RectangleShape,
                                modifier = Modifier
                                    .fillMaxWidth(.95f)
                                    .fillMaxHeight(1.2f / 3f)
                            ) {
                                Text("Trimite")
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Left spacer to balance the center alignment
                                Spacer(modifier = Modifier.width(85.dp)) // adjust width to match button size

                                // Centered dialogs
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AlertDialog(viewModel)
                                    AlertDialog_2(viewModel)
                                }

                                // Button aligned to the right
                                AndroidView(
                                    factory = { context ->
                                        Button(context).apply {
                                            background = ContextCompat.getDrawable(
                                                context,
                                                R.drawable.whole_button_drawing
                                            )
                                            scaleX = .65f
                                            setOnClickListener { v ->
                                                switchToCamera(viewModel)
                                            }
                                        }
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlertDialog(viewModel: DrawingViewModel) {

        AndroidView(
            factory = { context ->
                TextView(context).apply {
                    text = "Trimis!"
                    background = ContextCompat.getDrawable(
                        context,
                        R.drawable.success_drawable
                    )
                    visibility = if (viewModel.sent.intValue == 1) View.VISIBLE else View.INVISIBLE
                    textSize = 25f
                    gravity = Gravity.CENTER
                }
            },
            modifier = Modifier.size(150.dp, 50.dp),
            update = { textView ->
                if (viewModel.sent.intValue == 1) {
                    Animator.animate(textView)

                    viewModel.sent.intValue = 0
                }
            }
        )
}

@Composable
fun AlertDialog_2(viewModel: DrawingViewModel) {

    AndroidView(
        factory = {context ->
            TextView(context).apply {
                text = "Respins!"
                background = ContextCompat.getDrawable(
                    context,
                    R.drawable.fail_drawable
                )
                visibility = if (viewModel.sent.intValue == -1) View.VISIBLE else View.INVISIBLE
                textSize = 25f
                gravity = Gravity.CENTER
            }
        },
        modifier = Modifier.size(150.dp, 50.dp),
        update = { textView ->
            if (viewModel.sent.intValue == -1) {
                Animator.animate(textView)

                viewModel.sent.intValue = 0
            }
        }
    )
}
