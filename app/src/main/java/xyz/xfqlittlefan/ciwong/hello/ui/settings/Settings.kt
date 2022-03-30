package xyz.xfqlittlefan.ciwong.hello.ui.settings

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import xyz.xfqlittlefan.ciwong.hello.ui.theme.HelloCiwongTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    onClose: () -> Unit
) {
    HelloCiwongTheme {
        Scaffold(topBar = {
            LargeTopAppBar(title = {
                Text("HelloCiwong 设置")
            }, navigationIcon = {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回"
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