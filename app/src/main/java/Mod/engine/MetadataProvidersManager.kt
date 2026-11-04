package Mod.engine

import com.example.player_api.api.MetadataProvider
import com.example.player_api.api.MetadataTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.CancellationException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class MetadataProvidersManager(
    private val timeoutMs: Long = 3000L
) {

    // modId -> список провайдеров
    private val providers =
        ConcurrentHashMap<String, CopyOnWriteArrayList<MetadataProvider>>()


    fun registerProvider(
        modId: String,
        provider: MetadataProvider
    ) {
        providers.computeIfAbsent(modId) {
            CopyOnWriteArrayList()
        }.addIfAbsent(provider)
    }


    fun unregisterProvider(
        modId: String,
        provider: MetadataProvider
    ): Boolean {
        val modProviders = providers[modId] ?: return false

        return modProviders.remove(provider)
    }


    fun unregisterAllForMod(modId: String) {
        providers.remove(modId)
    }


    fun getAllProviders(): List<MetadataProvider> {
        return providers.values.flatMap { it.toList() }
    }

    fun getProvidersForMod(
        modId: String
    ): List<MetadataProvider> {
        return providers[modId]?.toList() ?: emptyList()
    }

    suspend fun fetchExtendedMetadata(
        track: MetadataTrack
    ): List<ProviderMetadataResult> = supervisorScope {

        val registeredProviders = providers.entries.flatMap { entry ->
            entry.value.map { provider ->
                entry.key to provider
            }
        }

        registeredProviders.map { (modId, provider) ->

            async(Dispatchers.IO) {

                try {
                    val metadata = withTimeoutOrNull(timeoutMs) {
                        provider.fetchMetadata(track)
                    }

                    if (metadata != null) {
                        ProviderMetadataResult(
                            modId = modId,
                            provider = provider,
                            metadata = metadata
                        )
                    } else {
                        null
                    }

                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    null
                }
            }

        }.awaitAll().filterNotNull()
    }
}

data class ProviderMetadataResult(
    val modId: String,
    val provider: MetadataProvider,
    val metadata: Map<String, String>
)