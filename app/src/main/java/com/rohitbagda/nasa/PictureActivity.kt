package com.rohitbagda.nasa

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rohitbagda.nasa.ui.theme.NasaAstronomyImagesTheme

class PictureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NasaAstronomyImagesTheme {
                PictureScreen()
            }
        }
    }

    @Composable
    fun PictureScreen(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val fileName = intent.getStringExtra("apod")
        val title = intent.getStringExtra("title")?: "Title"
        val image = if (fileName!= null) {
            BitmapFactory.decodeStream(context.openFileInput(fileName)).asImageBitmap()
        } else {
            BitmapFactory.decodeResource(context.resources, R.drawable.nasa).asImageBitmap()
        }
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(bitmap = image, contentDescription = "Downloaded Bitmap")
            Text(text = title, color = Color.White)
            Button(modifier = modifier.padding(20.dp), onClick = { finish() }) {
                Text(text = "Close")
            }
        }
    }
}
