package Mod.engine

import Mod.api.ModAPI
import Mod.manifest.ModManifest
import android.content.Context
import dalvik.system.DexClassLoader
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile

internal class ModEngineRealization: ModEngine {

    // По id храним манифест и конкретно класс
    private val mods: MutableMap<String, Pair<ModManifest, ModAPI>> = mutableMapOf()

    private val loadedMods: MutableMap<String, ModManifest> = mutableMapOf()

    override fun readManifest(path: String?): ModManifest {
        ZipFile(path).use { apk ->
            val entry = apk.getEntry("assets/modManifest.json")
                ?: throw IllegalArgumentException("assets/modManifest.json at $path is not found")

            apk.getInputStream(entry).use { inputStream ->
                return Json.decodeFromString<ModManifest>(inputStream.readBytes().decodeToString())
            }
        }
    }


    override fun loadMod(context: Context, assetName: String) {
        val modsDir = File(context.filesDir, "mods").apply { mkdirs() }
        val file = File(modsDir, assetName)

        context.assets.open(assetName).use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        file.setReadOnly()

        val modManifest = readManifest(file.absolutePath)
        loadedMods[modManifest.id] = modManifest
    }

    override fun unloadMod(context: Context, id: String): Boolean {
        if (mods.contains(id))
            disableMod(id)

        val modManifest = loadedMods[id] ?: return false

        val modsDir = File(context.filesDir, "mods")
        val modFile = File(modsDir, modManifest.apkName)

        if (!modFile.delete()) {
            return false
        }
        else
        {
            loadedMods.remove(id)
            return true;
        }
//        mods.remove(id)
    }


    override fun enableMod(path: String?, parentLoader: ClassLoader?)
    {
        val dexLoader = DexClassLoader(
            path,
            null,
            null,
            parentLoader
        )
        val modManifest: ModManifest = readManifest(path)

        // Не знаю нужна ли эта проверка, логичнее просто запуск устроить так чтобы не было повторно
        if (mods.containsKey(modManifest.id))
            throw IllegalStateException("Mod is already enabled")

        val modMainClass = dexLoader.loadClass(modManifest.entryPoint)

        val modInstance = modMainClass.getDeclaredConstructor().newInstance() as ModAPI

        mods[modManifest.id] = Pair(modManifest, modInstance)

        // быть может, нужно просто дернуть флажок а там уже самому приложению
        // на другом уровне синхронизировать взаимодействие
        // Чисто теоретически - есть мод, он когда подрубается мы должны провести целый процесс:
        //
        // Проверить мод на обновления, если включено автообновление
        // Загрузить всё, если обнова нужна (или прописать обновления..)
        // Запустить мод. Тут сложно:
        // У нас есть процесс самого плеера, отвечающий за трек
        // И есть параллельно работа с самим модом
        // Моды меняют как визуал так и вводят свои приколы. Много делают
        // Нужно чтобы мы условно запустили сам мод и пошли в самую глубину того, как запускается мод
        // То есть сам запуск - операция на другом уровне

    }

    override fun disableMod(id: String)
    {
        mods.remove(id)
    }

    // Точно ли это должно быть методом в составе интерфейса?
//    fun listMods(path: String): List<ModManifest>
//    {
//        // Бежим по папке, находим APK файлы
//        // Читаем из них ТОЛЬКО modManifest.json через readManifest
//        // И просто складываем в карту:
//        // registeredMods[manifest.id] = Pair(manifest, apkPath)
//    }

    override fun getModApi(id: String): ModAPI
    {
        val modPair = mods[id] ?: throw IllegalArgumentException("Mod with id $id is not enabled or not found")
        return modPair.second
    }


/* нейрослоп
    // Твой движок при старте находит или создает эту папку
    val modsFolder = File(context.getExternalFilesDir(null), "mods")
    if (!modsFolder.exists()) {
        modsFolder.mkdirs() // Создали папку, если её ещё нет
    }

    fun scanLocalMods() {
        val modsFolder = File(context.getExternalFilesDir(null), "mods")

        // Берем все файлы с расширением .apk из нашей папки
        val apkFiles = modsFolder.listFiles { file -> file.extension == "apk" } ?: return

        for (file in apkFiles) {
            // Вот он, наш чистый Этап 1!
            val manifest = readManifest(file.absolutePath)

            // Складываем в карту: ID -> (Манифест, Путь к файлу на диске)
            registeredMods[manifest.id] = Pair(manifest, file.absolutePath)
        }
    }

    fun setupFolderForCollegeProject(context: Context) {
    // Наша целевая папка во внутренней памяти приложения
    val targetFolder = File(context.filesDir, "mods")
    if (!targetFolder.exists()) targetFolder.mkdirs()

    // Берем список файлов из папки assets/mods/
    val assetManager = context.assets
    val modFiles = assetManager.list("mods") ?: return

    for (fileName in modFiles) {
        val targetFile = File(targetFolder, fileName)

        // Переписываем байты из ресурсов проекта во внутреннюю память смартфона
        assetManager.open("mods/$fileName").use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}
 */


}
