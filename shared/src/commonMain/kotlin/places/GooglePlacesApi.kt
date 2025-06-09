package places

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
data class PlacesResponse(val results: List<PlaceResult>)

@Serializable
data class PlaceResult(val name: String)

private val json = Json {
    ignoreUnknownKeys = true
}

suspend fun searchRestaurants(): List<String> {
    val client = getHttpClient()

    val response: HttpResponse = client.get("https://maps.googleapis.com/maps/api/place/nearbysearch/json") {
        parameter("location", "32.0853,34.7818")
        parameter("radius", "1500")
        parameter("type", "restaurant")
        parameter("key", "AIzaSyD_EBDLvG2nhD9qDkyAp9Tm6k8-fFVfKL0")
    }

    val responseBody = response.bodyAsText()
    val parsed = json.decodeFromString<PlacesResponse>(responseBody)

    return parsed.results.map { it.name }
}
