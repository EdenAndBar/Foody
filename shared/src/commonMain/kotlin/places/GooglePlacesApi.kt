//package places
//
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import kotlinx.serialization.*
//import kotlinx.serialization.json.Json
//
//import kotlinx.coroutines.*
//import kotlin.coroutines.CoroutineContext
//import places.searchRestaurants
//
//@Serializable
//data class PlacesResponse(val results: List<PlaceResult>)
//
//@Serializable
//data class PlaceResult(
//    val name: String,
//    val photos: List<Photo>? = null,
//    val rating: Float? = null
//)
//
//@Serializable
//data class Photo(
//    @SerialName("photo_reference") val photoReference: String
//)
//
//@Serializable
//data class Restaurant(
//    val name: String,
//    val photoUrl: String,
//    val description: String, // נכניס ריק כרגע
//    val rating: Float
//)
//
//
//private val json = Json {
//    ignoreUnknownKeys = true
//}
//fun buildPhotoUrl(photoReference: String): String {
//    return "https://maps.googleapis.com/maps/api/place/photo" +
//            "?maxwidth=400" +
//            "&photo_reference=$photoReference" +
//            "&key=AIzaSyD_EBDLvG2nhD9qDkyAp9Tm6k8-fFVfKL0"
//}
//
//suspend fun searchRestaurants(): List<Restaurant> {
//    val client = getHttpClient()
//
//    val response: HttpResponse =
//        client.get("https://maps.googleapis.com/maps/api/place/nearbysearch/json") {
//            parameter("location", "32.0853,34.7818")
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
//
//        if (photoReference != null) {
//            Restaurant(
//                name = name,
//                photoUrl = buildPhotoUrl(photoReference),
//                description = "", // בשלב ראשון נשים ריק
//                rating = rating
//            )
//        } else {
//            null
//        }
//    }
//}
//
//

package places

import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
data class PlacesResponse(val results: List<PlaceResult>)

@Serializable
data class PlaceResult(
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
    val name: String,
    val photoUrl: String,
    val address: String,
    val rating: Float
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

        if (photoReference != null) {
            Restaurant(
                id = id,
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

