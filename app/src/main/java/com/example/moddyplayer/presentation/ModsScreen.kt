package com.example.moddyplayer.presentation

import Mod.engine.ModLifecycleCoordinator
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moddyplayer.data.db.repo.ModRepository
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModsScreen(
    modRepository: ModRepository = koinInject(),
    modCoordinator: ModLifecycleCoordinator = koinInject()
) {
    // Получаем список модов через метод observeAll() (как в TrackRepository)
    val mods by modRepository.observeAllMods().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Управление модами") }
            )
        }
    ) { paddingValues ->
        if (mods.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Установленных модов пока нет",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mods) { mod ->
                    // Выводим базовую информацию о моде (поля зависят от твоей сущности)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = mod.name, // Или mod.name / mod.title, если появится подсказка
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Switch(
                                checked = mod.enabled,
                                onCheckedChange = { isChecked ->
                                    coroutineScope.launch {
                                        val modsDir = File(context.filesDir, "mods")

                                        val filesList = modsDir.listFiles()?.toList() ?: emptyList()

                                        val matchedFile = filesList.firstOrNull { file ->
                                            val fileName = file.nameWithoutExtension.lowercase()
                                            val modId = mod.id.lowercase()
                                            val modName = mod.name.lowercase()

                                            file.extension == "apk" && (
                                                    fileName.contains(modId) ||
                                                            modId.contains(fileName) ||
                                                            fileName.contains(modName) ||
                                                            modName.contains(fileName)
                                                    )
                                        }

                                        if (isChecked) {
                                            // Если файл не найден ни по ID, ни по маске, берем запасной вариант по умолчанию
                                            val apkFile = matchedFile ?: filesList.firstOrNull { it.extension == "apk" } ?: File(modsDir, "${mod.id}.apk")

                                            // Логируем для себя в консоль, какой путь мы в итоге передаем
                                            android.util.Log.d("ModdyPlayerLaunch", "Пытаемся запустить мод ${mod.id} из файла: ${apkFile.absolutePath}")

                                            if (!apkFile.exists()) {
                                                Toast.makeText(context, "Файл не найден на диске: ${apkFile.name}", Toast.LENGTH_LONG).show()
                                                return@launch
                                            }

                                            val result = modCoordinator.requestEnableMod(
                                                id = mod.id,
                                                apkPath = apkFile.absolutePath
                                            )

                                            if (result.isSuccess) {
                                                modRepository.upsertMod(mod.copy(enabled = true))
                                                Toast.makeText(context, "Мод успешно запущен!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                val errorMessage = result.exceptionOrNull()?.message ?: "Неизвестная ошибка рантайма"
                                                android.util.Log.e("ModdyPlayerLaunch", "Ошибка запуска мода", result.exceptionOrNull())
                                                Toast.makeText(context, "Ошибка запуска: $errorMessage", Toast.LENGTH_LONG).show()
                                            }
                                        } else {
                                            modCoordinator.requestDisableMod(mod.id)
                                            modRepository.upsertMod(mod.copy(enabled = false))
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}