import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import java.io.File

@Composable
fun ReusableImageGrid(
    modifier: Modifier = Modifier,
    imageUris: List<Uri>,
    columns: Int = 2,
    imageSize: Int = 150,
    padding: PaddingValues = PaddingValues(8.dp)
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxWidth(),
        contentPadding = padding
    ) {
        items(imageUris.size) { index ->
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(imageSize.dp)
            ) {
                Image(
                    painter = rememberImagePainter(imageUris[index]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    alignment = Alignment.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewImageGrid() {
    val imageUris = listOf(
        Uri.fromFile(File("src/main/res/drawable/spodnie.jpg"))
    )
    ReusableImageGrid(imageUris = imageUris)
}