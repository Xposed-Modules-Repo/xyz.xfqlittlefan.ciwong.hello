package xyz.xfqlittlefan.ciwong.hello.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.Keep
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import xyz.xfqlittlefan.ciwong.hello.ui.theme.HelloCiwongTheme

@Keep
class SettingsActivity : ComponentActivity() {
    private val viewModel by viewModels<SettingsViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloCiwongTheme {
                Scaffold(topBar = {
                    LargeTopAppBar(title = {
                        Text("HelloCiwong 设置")
                    }, navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack, contentDescription = "返回"
                            )
                        }
                    }, scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                        rememberSplineBasedDecay()
                    )
                    )
                }) {

                }
            }
        }
    }
}