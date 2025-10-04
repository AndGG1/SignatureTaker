import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import com.example.signaturetaker.DrawingViewModel

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
