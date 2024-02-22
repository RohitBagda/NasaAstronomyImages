package com.rohitbagda.nasa

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohitbagda.nasa.ui.theme.NasaAstronomyImagesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.codec.digest.DigestUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("MainActivity", "Starting App")
        setContent {
            NasaAstronomyImagesTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }

    @SuppressLint("MutableCollectionMutableState")
    @Composable
    fun MainScreen(modifier: Modifier = Modifier) {
        val astronomyPictureOfTheDayStates = mutableListOf<MutableState<AstronomyPictureOfTheDay?>>().also { list ->
            // Initialize List of mutable States
            repeat(5 ) { list += remember { mutableStateOf(null) } }
        }
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val service = AstronomyPictureOfTheDayService()
        LaunchedEffect(astronomyPictureOfTheDayStates) {
            coroutineScope.launch {
                service.getLast5().forEachIndexed { index, value -> astronomyPictureOfTheDayStates[index].value = value }
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            val buttonModifier = modifier.padding(vertical = 10.dp)
            Image(painter = painterResource(id = R.drawable.nasa), contentDescription = "nasa", modifier = modifier.padding(30.dp))
            Text(text = "Nasa Astronomy Picture of the Day", style = TextStyle(fontSize = 20.sp), modifier = modifier.padding(30.dp))
            astronomyPictureOfTheDayStates.forEach { CreateClickableText(modifier = buttonModifier, astronomyPictureOfTheDay = it.value) }
        }
    }

    @Composable
    private fun CreateClickableText(modifier: Modifier, astronomyPictureOfTheDay: AstronomyPictureOfTheDay?, ) {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        ClickableText(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(astronomyPictureOfTheDay?.date?.toString() ?: "loading...")
                }
            },
            modifier = modifier,
            style = TextStyle(color = Color.Blue, textAlign = TextAlign.Center, fontSize = 20.sp),
            onClick = onClick(coroutineScope, astronomyPictureOfTheDay, LocalContext.current)
        )
    }

    @Composable
    private fun onClick(
        coroutineScope: CoroutineScope,
        astronomyPictureOfTheDay: AstronomyPictureOfTheDay?,
        applicationContext: Context
    ): (Int) -> Unit = {
        coroutineScope.launch {
            if (astronomyPictureOfTheDay?.url != null) {
                val fileName = DigestUtils.md5Hex(astronomyPictureOfTheDay.title)
                val file = File(applicationContext.filesDir, fileName)
                if (!file.exists()) {
                    val bitmap = downloadImage(astronomyPictureOfTheDay.url)
                    saveImageToDisk(bitmap, applicationContext, fileName)
                }
                val intent = Intent(applicationContext, PictureActivity::class.java)
                intent.putExtra("apod", fileName)
                intent.putExtra("title", astronomyPictureOfTheDay.title)
                applicationContext.startActivity(intent)
            }
        }
    }

    private fun saveImageToDisk(bitmap: Bitmap, applicationContext: Context, fileName: String?) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()
        val fos = applicationContext.openFileOutput(fileName, Context.MODE_PRIVATE)
        fos.write(bytes)
        fos.close()
    }

    private fun downloadImage(urlString: String?): Bitmap {
        try {
            val url = URL(urlString)
            val connection = url.openConnection()
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            return BitmapFactory.decodeStream(inputStream)
        } catch (ioe: IOException) {
            Log.e("IOException", "IO Error downloading image: $ioe")
            throw ioe
        } catch (ex: Exception) {
            Log.e("Exception", "Error downloading image: $ex")
            throw ex
        }
    }

    @Preview
    @Composable
    fun MainScreenPreview(){
        MainScreen()
    }
}