package xyz.xfqlittlefan.ciwong.hello.ui.activity.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.res.stringResource
import xyz.xfqlittlefan.ciwong.hello.R
import xyz.xfqlittlefan.ciwong.hello.ui.theme.HelloCiwongTheme

class SettingsActivity : ComponentActivity() {
    private val viewModel = viewModels<SettingsViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloCiwongTheme {
                Scaffold(topBar = {
                    LargeTopAppBar(title = {
                        Text(stringResource(R.string.settings))
                    }, navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(
                                    R.string.back
                                )
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