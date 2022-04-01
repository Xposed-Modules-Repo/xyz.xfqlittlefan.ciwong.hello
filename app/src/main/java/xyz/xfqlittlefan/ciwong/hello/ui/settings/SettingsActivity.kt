package xyz.xfqlittlefan.ciwong.hello.ui.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.Keep
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import xyz.xfqlittlefan.ciwong.hello.ui.base.BaseActivity
import xyz.xfqlittlefan.ciwong.hello.ui.component.HelloCiwongAppBar
import xyz.xfqlittlefan.ciwong.hello.ui.theme.HelloCiwongTheme

@Keep
class SettingsActivity : BaseActivity() {
    private val viewModel by viewModels<SettingsViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloCiwongTheme {
                Scaffold(topBar = {
                    HelloCiwongAppBar(
                        title = {
                            Text("HelloCiwong 设置")
                        }, modifier = Modifier.windowInsetsPadding(
                            WindowInsets.systemBars.only(
                                WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                            )
                        ), navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack, contentDescription = "返回"
                                )
                            }
                        }, scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                            rememberSplineBasedDecay()
                        )
                    )
                }) { paddingValues ->
                    val sharedPreferences =
                        remember { PreferenceManager.getDefaultSharedPreferences(this) }

                    LazyColumn(contentPadding = paddingValues) {
                        items(viewModel.itemList) {
                            var value by remember {
                                mutableStateOf(
                                    sharedPreferences.getBoolean(
                                        it.id, true
                                    )
                                )
                            }
                            Row(modifier = Modifier
                                .clickable {
                                    sharedPreferences.edit(commit = true) {
                                        putBoolean(it.id, !value)
                                    }
                                    value = !value
                                }
                                .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Spacer(
                                    Modifier
                                        .padding(20.dp)
                                        .size(32.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(it.title, style = MaterialTheme.typography.titleMedium)
                                    if (it.description != null) {
                                        Spacer(Modifier.height(10.dp))
                                        Text(
                                            it.description,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .padding(20.dp)
                                        .size(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Checkbox(
                                        checked = value, onCheckedChange = null
                                    ) //TODO: 替换为 Switch
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}