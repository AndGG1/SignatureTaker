import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import com.example.signaturetaker.DrawingViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

//Converteaza imaginea intr-un bitmap
//Intai refacem desenul intr-un alt format si dupa il punem in bitmap
fun convertToPNG(width: Int, height: Int, viewModel: DrawingViewModel): Bitmap {
    val bitMap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitMap)
    canvas.drawColor(Color.Transparent.toArgb(), PorterDuff.Mode.CLEAR)

    val paint = Paint().apply {
        color = Color.Black.toArgb()
        style = Paint.Style.STROKE
        strokeWidth = 7.5f
        isAntiAlias = true
    }

    viewModel.state.value.paths.forEach { pathD ->
        canvas.drawPath(pathD.pathDrawing.asAndroidPath(), paint)
    }

    return bitMap
}

//Aici convertam acel bitmap intr-un alt format(File)
//Cu acest format putem mai departe sa o trimitem intr-un POST request
fun convertToPart(bitmap: Bitmap): MultipartBody.Part {
    // Compress bitmap into a byte array
    val byteOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteOutputStream)
    val byteArr = byteOutputStream.toByteArray()

    val requestBody = byteArr.toRequestBody("image/png".toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        name = "file",
        filename = "image.png",
        body = requestBody
    )
}
