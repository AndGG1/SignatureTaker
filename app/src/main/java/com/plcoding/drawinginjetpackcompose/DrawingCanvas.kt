import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.util.fastForEach
import com.example.signaturetaker.DrawingAction
import com.example.signaturetaker.DrawingViewModel
import com.example.signaturetaker.PathData

//Facem un Canvas care va folosi un pointerInput in Modifier
//pentru a detecta fiecare miscare, redirectionand pe baza miscarii la
//functiile scrise in viewModel
 var drawingViewModel_t: DrawingViewModel? = null
@Composable
fun DrawingCanvas(
    viewModel: DrawingViewModel,
    paths: List<PathData>,
    currentPath: PathData?,
    onAction: (DrawingAction) -> Unit,
    modifier: Modifier = Modifier
) {
    drawingViewModel_t = viewModel

    Canvas(
        modifier = modifier
            .clipToBounds()
            .background(Color.White)
            .pointerInput(true) {
                detectDragGestures(
                    onDragStart = {
                        onAction(DrawingAction.OnNewPathStart)
                    },
                    onDragEnd = {
                        onAction(DrawingAction.OnPathEnd)
                    },
                    onDrag = { change, _ ->
                        onAction(DrawingAction.OnDraw(change.position))
                    },
                    onDragCancel = {
                        onAction(DrawingAction.OnPathEnd)
                    },
                )
            }
    ) {
        paths.fastForEach { pathData ->
            drawPath(
                pathD = pathData,
                path = pathData.path,
                color = pathData.color,
                alreadyExists =  true
            )
        }
        currentPath?.let {
            drawPath(
                pathD = it,
                path = it.path,
                color = it.color,
                alreadyExists =  false
            )
        }
    }
}

//Deseneaza pathul(linia)
private fun DrawScope.drawPath(
    pathD: PathData,
    path: List<Offset>,
    color: Color,
    thickness: Float = 7.5f,
    alreadyExists: Boolean
) {
    val smoothedPath: Path = if (alreadyExists) {
        pathD.pathDrawing
    } else {
        Path().apply {
            if (path.isNotEmpty()) {
                moveTo(path.first().x, path.first().y)

                for (i in 0..path.lastIndex) {
                    val to = path[i]
                    lineTo(to.x, to.y)
                }
            }
        }
    }

    pathD.pathDrawing = smoothedPath

    drawPath(
        path = smoothedPath,
        color = color,
        style = Stroke(
            width = thickness,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}