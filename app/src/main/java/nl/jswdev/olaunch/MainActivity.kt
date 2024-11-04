package nl.jswdev.olaunch

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import nl.jswdev.olaunch.ui.theme.OLaunchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MainWrapper {
                AppLaunchGrid(
                    context = this.applicationContext, this.applicationContext.packageManager
                )
            }
        }
    }
}

fun Context.launchApp(launchInfo: ApplicationLaunchInfo) {
    startActivity(
        Intent()
            .setClassName(launchInfo.packageName, launchInfo.name)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

@Composable
fun MainWrapper(content: @Composable () -> Unit) {
    OLaunchTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) { content() }
    }
}

@Composable
fun AppLaunchGrid(context: Context, pm: PackageManager) {
    val appsIntent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
    val apps: List<Application> = pm.queryIntentActivities(appsIntent, 0).map { app ->
        val actInfo = app.activityInfo
        val icon = actInfo.loadIcon(pm)
        val hasIcon = icon.intrinsicWidth > 0 && icon.intrinsicHeight > 0

        Application(
            name = actInfo.loadLabel(pm).toString(),
            launchInfo = ApplicationLaunchInfo(actInfo.applicationInfo.packageName, actInfo.name),
//            image = if (hasIcon) icon.toBitmap().asImageBitmap() else null
            image = null
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
        items(apps) { app ->
            AppButton(app, onClick = {
                app.launchInfo?.let { context.launchApp(it) }
            })
        }
    }
}

data class ApplicationLaunchInfo(val packageName: String, val name: String)
data class Application(
    val name: String, val image: ImageBitmap?, val launchInfo: ApplicationLaunchInfo? = null
)

@Preview
@Composable
fun AppButton(
    @PreviewParameter(AppButtonParameterProvider::class) app: Application, onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clickable(true, onClick = onClick)
    ) {

        if (app.image != null)
            Image(
                bitmap = app.image, contentDescription = app.name, modifier = Modifier.fillMaxSize()
            )
        else
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground), contentDescription = app.name, modifier = Modifier.fillMaxSize()
            )

        Text(
            text = app.name,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.5f))
                .fillMaxWidth()
                .padding(4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

class AppButtonParameterProvider : PreviewParameterProvider<Application> {
    override val values = sequenceOf(
        Application(
            name = "Jsw", image = createBitmap(512, 512, Bitmap.Config.ARGB_8888).asImageBitmap()
        )
    )
}
