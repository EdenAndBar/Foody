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
    val vicinity: String? = null,
    val types: List<String>? = null,
    val opening_hours: OpeningHours? = null,
    @SerialName("formatted_address")
    val formattedAddress: String? = null
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
    val rating: Float,
    val types: List<String> = emptyList(),
    val isOpenNow: Boolean? = null
)

@Serializable
data class PlaceDetailsResponse(
    val result: PlaceDetailsResult
)

@Serializable
data class PlaceDetailsResult(
    val url: String? = null,
    val website: String? = null,
    val reviews: List<GoogleReview>? = null
)

@Serializable
data class GoogleReview(
    val author_name: String,
    val rating: Int,
    val text: String
)

@Serializable
data class OpeningHours(
    val open_now: Boolean? = null
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
object RestaurantApi {
    suspend fun searchRestaurants(
        location: String? = null,
        city: String? = null
    ): List<Restaurant> {
        val client = getHttpClient()
        val url: String
        val parameters: Map<String, String>

        if (city != null) {
            url = "https://maps.googleapis.com/maps/api/place/textsearch/json"
            parameters = mapOf("query" to "restaurants in $city")
        } else if (location != null) {
            url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
            parameters = mapOf(
                "location" to location,
                "radius" to "1500",
                "type" to "restaurant"
            )
        } else {
            return emptyList()
        }

        val response: HttpResponse = client.get(url) {
            parameters.forEach { (key, value) -> parameter(key, value) }
            parameter("language", "en")
            parameter("key", "AIzaSyD_EBDLvG2nhD9qDkyAp9Tm6k8-fFVfKL0")
        }

        val responseBody = response.bodyAsText()
        val parsed = json.decodeFromString<PlacesResponse>(responseBody)

        return parsed.results.mapNotNull { place ->
            val photoReference = place.photos?.firstOrNull()?.photoReference
            val rating = place.rating ?: 0f
            val name = place.name
            val address = place.vicinity ?: place.formattedAddress ?: "No location info"
            val id = "restaurant-${name.hashCode()}-${photoReference.hashCode()}"
            val placeId = place.place_id

            if (photoReference != null) {
                Restaurant(
                    id = id,
                    placeId = placeId,
                    name = name,
                    photoUrl = buildPhotoUrl(photoReference),
                    address = address,
                    rating = rating,
                    types = place.types ?: emptyList(),
                    isOpenNow = place.opening_hours?.open_now
                )
            } else null
        }
    }
}


//suspend fun searchRestaurants(location: String): List<Restaurant> {
//    val client = getHttpClient()
//
//    val response: HttpResponse =
//        client.get("https://maps.googleapis.com/maps/api/place/nearbysearch/json") {
//            parameter("location", location)
//            parameter("radius", "1500")
//            parameter("type", "restaurant")
//            parameter("language", "en")
//            parameter("key", "AIzaSyD_EBDLvG2nhD9qDkyAp9Tm6k8-fFVfKL0")
//        }
//
//    val responseBody = response.bodyAsText()
//    val parsed = json.decodeFromString<PlacesResponse>(responseBody)
//
//    return parsed.results.mapNotNull { place ->
//        val photoReference = place.photos?.firstOrNull()?.photoReference
//        val rating = place.rating ?: 0f
//        val name = place.name
//        val address = "${place.vicinity ?: "No location info"}"
//        val id = "restaurant-${name.hashCode()}-${photoReference.hashCode()}"
//        val placeId = place.place_id
//        val types = place.types ?: emptyList()
//        val isOpenNow = place.opening_hours?.open_now
//
//        if (photoReference != null) {
//            Restaurant(
//                id = id,
//                placeId = placeId,
//                name = name,
//                photoUrl = buildPhotoUrl(photoReference),
//                address = address,
//                rating = rating,
//                types = types,
//                isOpenNow = isOpenNow
//            )
//        } else {
//            null
//        }
//    }
//}

suspend fun getRestaurantDetails(placeId: String): PlaceDetailsResult? {
    val client = getHttpClient()

    val response: HttpResponse =
        client.get("https://maps.googleapis.com/maps/api/place/details/json") {
            parameter("place_id", placeId)
            parameter("fields", "website,url,reviews")
            parameter("language", "en")
            parameter("key", "AIzaSyD_EBDLvG2nhD9qDkyAp9Tm6k8-fFVfKL0")
        }

    val body = response.bodyAsText()
    val parsed = json.decodeFromString<PlaceDetailsResponse>(body)
    return parsed.result
}

//suspend fun searchRestaurantsByCity(city: String): List<Restaurant> {
//    val client = getHttpClient()
//
//    val response: HttpResponse =
//        client.get("https://maps.googleapis.com/maps/api/place/textsearch/json") {
//            parameter("query", "restaurants in $city")
//            parameter("language", "en")
//            parameter("key", "AIzaSyD_EBDLvG2nhD9qDkyAp9Tm6k8-fFVfKL0")
//        }
//
//    val responseBody = response.bodyAsText()
//    val parsed = json.decodeFromString<PlacesResponse>(responseBody)
//
//    return parsed.results.mapNotNull { place ->
//        val photoReference = place.photos?.firstOrNull()?.photoReference
//        val rating = place.rating ?: 0f
//        val name = place.name
//        val address = "${place.vicinity ?: "No location info"}"
//        val id = "restaurant-${name.hashCode()}-${photoReference.hashCode()}"
//        val placeId = place.place_id
//
//        if (photoReference != null) {
//            Restaurant(
//                id = id,
//                placeId = placeId,
//                name = name,
//                photoUrl = buildPhotoUrl(photoReference),
//                address = address,
//                rating = rating,
//                types = place.types ?: emptyList(),
//                isOpenNow = place.opening_hours?.open_now
//            )
//        } else {
//            null
//        }
//    }
//}



