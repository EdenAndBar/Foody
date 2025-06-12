package places

import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import places.searchRestaurants

@Serializable
data class PlacesResponse(val results: List<PlaceResult>)

@Serializable
data class PlaceResult(
    val name: String,
    val photos: List<Photo>? = null
)

@Serializable
data class Photo(
    @SerialName("photo_reference") val photoReference: String
)


private val json = Json {
    ignoreUnknownKeys = true
}
fun buildPhotoUrl(photoReference: String): String {
    return "https://maps.googleapis.com/maps/api/place/photo" +
            "?maxwidth=400" +
            "&photo_reference=$photoReference" +
            "&key=AIzaSyD_EBDLvG2nhD9qDkyAp9Tm6k8-fFVfKL0"
}

suspend fun searchRestaurants(): List<Pair<String, String>> {
    val client = getHttpClient()

    val response: HttpResponse =
        client.get("https://maps.googleapis.com/maps/api/place/nearbysearch/json") {
            parameter("location", "32.0853,34.7818")
            parameter("radius", "1500")
            parameter("type", "restaurant")
            parameter("language", "en")
            parameter("key", "AIzaSyD_EBDLvG2nhD9qDkyAp9Tm6k8-fFVfKL0")
        }

    val responseBody = response.bodyAsText()
    val parsed = json.decodeFromString<PlacesResponse>(responseBody)

    return parsed.results.mapNotNull { place ->
        val photoReference = place.photos?.firstOrNull()?.photoReference
        if (photoReference != null) {
            Pair(place.name, buildPhotoUrl(photoReference))
        } else {
            null
        }
    }
}


