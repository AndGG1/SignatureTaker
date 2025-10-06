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
import okhttp3.RequestBody
import okio.IOException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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
    val byteOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteOutputStream)

    val byteArr = byteOutputStream.toByteArray()
    val file = File("Imagine")

    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(file)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    try {
        fos?.write(byteArr)
        fos?.flush()
        fos?.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return MultipartBody.Part.createFormData(
        "Imagine",
        file.name,
        RequestBody.create("image/*".toMediaTypeOrNull(),
            file))
}
