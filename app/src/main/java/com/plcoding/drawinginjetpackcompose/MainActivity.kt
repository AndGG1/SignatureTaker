package com.plcoding.drawinginjetpackcompose

import DrawingCanvas
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.signaturetaker.DrawingViewModel
import com.plcoding.drawinginjetpackcompose.ImageProcessing.useRetrofit
import com.plcoding.drawinginjetpackcompose.ui.theme.DrawingInJetpackComposeTheme
import convertToPNG
import convertToPart
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrawingInJetpackComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = viewModel<DrawingViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

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

                                        val bitmap: Bitmap = convertToPNG(
                                            Resources.getSystem().displayMetrics.widthPixels,
                                            Resources.getSystem().displayMetrics.heightPixels,
                                            viewModel
                                        )

                                        viewModel.viewModelScope.launch {
                                            val URL = useRetrofit(convertToPart(bitmap))
                                            Log.d("Post", URL)
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
                    }
                }
            }
        }
    }
}
