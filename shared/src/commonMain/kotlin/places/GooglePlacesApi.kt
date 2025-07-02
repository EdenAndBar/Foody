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
    val isOpenNow: Boolean? = null,
    val openingHoursText: List<String>? = null,
    val category: String
)

@Serializable
data class PlaceDetailsResponse(
    val result: PlaceDetailsResult
)

@Serializable
data class PlaceDetailsResult(
    val url: String? = null,
    val website: String? = null,
    val reviews: List<GoogleReview>? = null,
    val opening_hours: OpeningHours? = null
)

@Serializable
data class GoogleReview(
    val author_name: String,
    val rating: Int,
    val text: String
)

@Serializable
data class OpeningHours(
    val open_now: Boolean? = null,
    @SerialName("weekday_text")
    val weekdayText: List<String>? = null
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
        city: String? = null,
        query: String? = null
    ): List<Restaurant> {
        val client = getHttpClient()
        val url: String
        val parameters: MutableMap<String, String> = mutableMapOf()

        when {
            query != null -> {
                url = "https://maps.googleapis.com/maps/api/place/textsearch/json"
                parameters["query"] = "named $query restaurant in Israel"
                parameters["type"] = "restaurant"
            }
            city != null -> {
                url = "https://maps.googleapis.com/maps/api/place/textsearch/json"
                parameters["query"] = "restaurants in $city"
            }
            location != null -> {
                url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                parameters["location"] = location
                parameters["radius"] = "5000"
                parameters["type"] = "restaurant"
            }
            else -> {
                return emptyList()
            }
        }

        val response: HttpResponse = client.get(url) {
            parameters.forEach { (key, value) -> parameter(key, value) }
            parameter("language", "en")
            parameter("region", "il") // ✅ מגביל את התוצאות לישראל
            parameter("key", "AIzaSyD_EBDLvG2nhD9qDkyAp9Tm6k8-fFVfKL0")
        }

        val responseBody = response.bodyAsText()
        println("📦 JSON תגובת ה־API: $responseBody")
        val parsed = json.decodeFromString<PlacesResponse>(responseBody)

        return parsed.results.mapNotNull { place ->
            val photoReference = place.photos?.firstOrNull()?.photoReference
            val rating = place.rating ?: 0f
            val name = place.name
            val address = place.vicinity ?: place.formattedAddress ?: "No location info"
            val id = "restaurant-${name.hashCode()}-${photoReference?.hashCode() ?: 0}"
            val placeId = place.place_id

            val details = getRestaurantDetails(placeId)

            if (photoReference != null) {
                val restaurant = Restaurant(
                    id = id,
                    placeId = placeId,
                    name = name,
                    photoUrl = buildPhotoUrl(photoReference),
                    address = address,
                    rating = rating,
                    types = place.types ?: emptyList(),
                    isOpenNow = place.opening_hours?.open_now,
                    openingHoursText = details?.opening_hours?.weekdayText,
                    category = guessCategoryFromName(place.name ?: "").lowercase()
                )
                if (query == null || restaurant.name.contains(query, ignoreCase = true)) {
                    restaurant
                } else null
            } else null
        }.filter { restaurant ->
            restaurant.photoUrl.isNotBlank() && restaurant.rating > 0
        }
    }
}

suspend fun getRestaurantDetails(placeId: String): PlaceDetailsResult? {
    val client = getHttpClient()

    val response: HttpResponse =
        client.get("https://maps.googleapis.com/maps/api/place/details/json") {
            parameter("place_id", placeId)
            parameter("fields", "website,url,reviews,opening_hours")
            parameter("language", "en")
            parameter("key", "AIzaSyD_EBDLvG2nhD9qDkyAp9Tm6k8-fFVfKL0")
        }

    val body = response.bodyAsText()
    val parsed = json.decodeFromString<PlaceDetailsResponse>(body)
    return parsed.result
}

private fun guessCategoryFromName(name: String): String {
    return when {
        name.contains("pizza", ignoreCase = true) -> "Pizza"
        name.contains("burger", ignoreCase = true) -> "Burger"
        name.contains("sushi", ignoreCase = true) -> "Sushi"
        name.contains("cafe", ignoreCase = true) || name.contains("coffee", ignoreCase = true) -> "Cafe"
        name.contains("bakery", ignoreCase = true) || name.contains("מאפה") -> "Bakery"
        name.contains("asian", ignoreCase = true) -> "Asian"
        name.contains("steak", ignoreCase = true) -> "Steakhouse"
        name.contains("falafel", ignoreCase = true) -> "Falafel"
        name.contains("shawarma", ignoreCase = true) -> "Shawarma"
        name.contains("italian", ignoreCase = true) -> "Italian"
        name.contains("indian", ignoreCase = true) -> "Indian"
        name.contains("chinese", ignoreCase = true) -> "Chinese"
        name.contains("bar", ignoreCase = true) -> "Bar"
        name.contains("vegan", ignoreCase = true) -> "Vegan"
        name.contains("grill", ignoreCase = true) -> "Grill"
        else -> ""
    }
}