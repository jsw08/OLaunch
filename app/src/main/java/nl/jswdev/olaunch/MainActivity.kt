package nl.jswdev.olaunch

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import nl.jswdev.olaunch.ui.theme.OLaunchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MainWrapper {
                AppLaunchGrid(
                    context = this.applicationContext,
                    this.applicationContext.packageManager
                )
            }
        }

    }
}
fun Context.launchApp(activityInfo: ActivityInfo) {
    startActivity(
        Intent()
            .setClassName(
                activityInfo.applicationInfo.packageName,
                activityInfo.name
            )
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
    val appsIntent =
        Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
    val apps: List<ResolveInfo> = pm.queryIntentActivities(appsIntent, 0)

    LazyVerticalGrid(
        columns = GridCells.Adaptive(80.dp)
    ) {
        items(apps) { app ->

            AppLauncher(context, pm, app.activityInfo)
        }
    }
}

@Composable
fun AppLauncher(context: Context, pm: PackageManager, activityInfo: ActivityInfo) {
    Button({
        context.launchApp(activityInfo)
    }) {
        Text(activityInfo.applicationInfo.loadLabel(pm).toString())
        Image(activityInfo.loadIcon(pm).toBitmap().asImageBitmap(), "logo")
    }
}

//data class Application(val name: String, val image: )

@Preview
@Composable
fun App() {

}