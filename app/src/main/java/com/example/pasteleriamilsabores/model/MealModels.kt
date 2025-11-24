package com.example.pasteleriamilsabores.model

import com.google.gson.annotations.SerializedName

data class MealResponse(
    @SerializedName("meals") val meals: List<MealApi>
)

data class MealApi(
    @SerializedName("idMeal") val id: String,
    @SerializedName("strMeal") val name: String,
    @SerializedName("strMealThumb") val image: String,

    @SerializedName("strInstructions") val instructions: String?,

    @SerializedName("strIngredient1") val strIngredient1: String?,
    @SerializedName("strIngredient2") val strIngredient2: String?,
    @SerializedName("strIngredient3") val strIngredient3: String?,
    @SerializedName("strIngredient4") val strIngredient4: String?,
    @SerializedName("strIngredient5") val strIngredient5: String?,
    @SerializedName("strIngredient6") val strIngredient6: String?,
    @SerializedName("strIngredient7") val strIngredient7: String?,
    @SerializedName("strIngredient8") val strIngredient8: String?,
    @SerializedName("strIngredient9") val strIngredient9: String?,
    @SerializedName("strIngredient10") val strIngredient10: String?,
    @SerializedName("strIngredient11") val strIngredient11: String?,
    @SerializedName("strIngredient12") val strIngredient12: String?,
    @SerializedName("strIngredient13") val strIngredient13: String?,
    @SerializedName("strIngredient14") val strIngredient14: String?,
    @SerializedName("strIngredient15") val strIngredient15: String?,
    @SerializedName("strIngredient16") val strIngredient16: String?,
    @SerializedName("strIngredient17") val strIngredient17: String?,
    @SerializedName("strIngredient18") val strIngredient18: String?,
    @SerializedName("strIngredient19") val strIngredient19: String?,
    @SerializedName("strIngredient20") val strIngredient20: String?,


    @SerializedName("strMeasure1") val strMeasure1: String?,
    @SerializedName("strMeasure2") val strMeasure2: String?,
    @SerializedName("strMeasure3") val strMeasure3: String?,
    @SerializedName("strMeasure4") val strMeasure4: String?,
    @SerializedName("strMeasure5") val strMeasure5: String?,
    @SerializedName("strMeasure6") val strMeasure6: String?,
    @SerializedName("strMeasure7") val strMeasure7: String?,
    @SerializedName("strMeasure8") val strMeasure8: String?,
    @SerializedName("strMeasure9") val strMeasure9: String?,
    @SerializedName("strMeasure10") val strMeasure10: String?,
    @SerializedName("strMeasure11") val strMeasure11: String?,
    @SerializedName("strMeasure12") val strMeasure12: String?,
    @SerializedName("strMeasure13") val strMeasure13: String?,
    @SerializedName("strMeasure14") val strMeasure14: String?,
    @SerializedName("strMeasure15") val strMeasure15: String?,
    @SerializedName("strMeasure16") val strMeasure16: String?,
    @SerializedName("strMeasure17") val strMeasure17: String?,
    @SerializedName("strMeasure18") val strMeasure18: String?,
    @SerializedName("strMeasure19") val strMeasure19: String?,
    @SerializedName("strMeasure20") val strMeasure20: String?,
){
    fun obtenerIngredientesFormat(): List<String> {
        val lista = mutableListOf<String>()
        fun agregarSiExiste(ing: String?, meas: String?) {
            if (!ing.isNullOrBlank()) {
                val medida = if (!meas.isNullOrBlank()) meas else "Al gusto"
                lista.add("• $ing: $medida")
            }
        }

        agregarSiExiste(strIngredient1, strMeasure1)
        agregarSiExiste(strIngredient2, strMeasure2)
        agregarSiExiste(strIngredient3, strMeasure3)
        agregarSiExiste(strIngredient4, strMeasure4)
        agregarSiExiste(strIngredient5, strMeasure5)
        agregarSiExiste(strIngredient6, strMeasure6)
        agregarSiExiste(strIngredient7, strMeasure7)
        agregarSiExiste(strIngredient8, strMeasure8)
        agregarSiExiste(strIngredient9, strMeasure9)
        agregarSiExiste(strIngredient10, strMeasure10)
        agregarSiExiste(strIngredient11, strMeasure11)
        agregarSiExiste(strIngredient12, strMeasure12)
        agregarSiExiste(strIngredient13, strMeasure13)
        agregarSiExiste(strIngredient14, strMeasure14)
        agregarSiExiste(strIngredient15, strMeasure15)
        agregarSiExiste(strIngredient16, strMeasure16)
        agregarSiExiste(strIngredient17, strMeasure17)
        agregarSiExiste(strIngredient18, strMeasure18)
        agregarSiExiste(strIngredient19, strMeasure19)
        agregarSiExiste(strIngredient20, strMeasure20)


        return lista
    }
}