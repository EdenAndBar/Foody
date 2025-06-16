package places

import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
data class PlacesResponse(val results: List<PlaceResult>)

@Serializable
data class PlaceResult(
    val place_id: String,
    val name: String,
    val photos: List<Photo>? = null,
    val rating: Float? = null,
    val vicinity: String? = null // ✅ הוספנו כאן את שדה הכתובת/תיאור
)

@Serializable
data class Photo(
    @SerialName("photo_reference") val photoReference: String
)

@Serializable
data class Restaurant(
    val id: String,
    val placeId: String,
    val name: String,
    val photoUrl: String,
    val address: String,
    val rating: Float
)

@Serializable
data class PlaceDetailsResponse(
    val result: PlaceDetailsResult
)

@Serializable
data class PlaceDetailsResult(
    val url: String? = null,
    val reviews: List<GoogleReview>? = null
)

@Serializable
data class GoogleReview(
    val author_name: String,
    val rating: Int,
    val text: String
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

suspend fun searchRestaurants(): List<Restaurant> {
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
        val rating = place.rating ?: 0f
        val name = place.name
        val address = "${place.vicinity ?: "No location info"}"
        val id = "restaurant-${name.hashCode()}-${photoReference.hashCode()}"
        val placeId = place.place_id

        if (photoReference != null) {
            Restaurant(
                id = id,
                placeId = placeId, // ✅ הוספנו את זה
                name = name,
                photoUrl = buildPhotoUrl(photoReference),
                address = address,
                rating = rating
            )
        } else {
            null
        }
    }
}

suspend fun getRestaurantDetails(placeId: String): PlaceDetailsResult? {
    val client = getHttpClient()

    val response: HttpResponse =
        client.get("https://maps.googleapis.com/maps/api/place/details/json") {
            parameter("place_id", placeId)
            parameter("fields", "url,reviews")
            parameter("language", "en")
            parameter("key", "AIzaSyD_EBDLvG2nhD9qDkyAp9Tm6k8-fFVfKL0")
        }

    val body = response.bodyAsText()
    val parsed = json.decodeFromString<PlaceDetailsResponse>(body)
    return parsed.result
}


