package com.example.signaturetaker

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

//Modul prin care ViewModel ul transmite activitatii desenul complet
// (liniile trecute + linia curenta desenata)
data class DrawingState(
    val currentPath: PathData? = null,
    val paths: List<PathData> = emptyList()
)

//Reprezinta un singur path(o linie)
data class PathData(
    val id: String,
    val color: Color,
    val path: List<Offset>,
    var pathDrawing: Path
)

//Tine cont de totate stagiile in care utilizatorul se poate afla
//in procesul de desenare
sealed interface DrawingAction {
    data object OnNewPathStart: DrawingAction
    data class OnDraw(val offset: Offset) : DrawingAction
    data object OnPathEnd: DrawingAction
    data object OnClearCanvasClick: DrawingAction
}



class DrawingViewModel: ViewModel() {
    private val _state = MutableStateFlow(DrawingState())
    val state = _state.asStateFlow()

    var currentScreenOrientation = 0
    var lastClickedTime: Long = 0;

    fun onAction(action: DrawingAction) {
        when (action) {
            DrawingAction.OnClearCanvasClick -> onClearCanvas()
            is DrawingAction.OnDraw -> onDraw(action.offset)
            DrawingAction.OnNewPathStart -> startPathOnCanvas()
            DrawingAction.OnPathEnd -> endPathOnCanvas()
        }
    }

    //Sterge tot ce s-a desenat in chenar
    fun onClearCanvas() {
        _state.update {
            it.copy(
                currentPath = null,
                paths = emptyList()
            )
        }
    }

    //Continua sa adauge coordonate noi la pathul curent(linia curenta)
    private fun onDraw(offset: Offset) {
        val currentPathData = state.value.currentPath ?: return

        _state.update {
            it.copy(
                currentPath = currentPathData.copy(
                    path = currentPathData.path + offset
                )
            )
        }
    }

    //Initializeaza tot ce ai nevoie pentru a incepe desenatul
    private fun startPathOnCanvas() {
        _state.update {
            it.copy(
                currentPath = PathData(
                    id = System.currentTimeMillis().toString(),
                    color = Color.Black,
                    path = emptyList(),
                    pathDrawing = Path()
                )
            )
        }
    }

    //Termina procesul de desenare,
    // adica adauga ultimul path(ultima linie) in stocarea temporara
    private fun endPathOnCanvas() {
        val currentPathData = state.value.currentPath
        if (currentPathData == null) return

        _state.update {
            it.copy(
                currentPath = null,
                paths = it.paths + currentPathData
            )
        }
    }
}
