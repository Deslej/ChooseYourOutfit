package com.example.chooseyouroutfit.model

enum class ClothesCategoryType(val displayName: String) {
    BLOUSE("Blouse"),
    //DRESS("Dress"),
    SHORTS("Shorts"),
    SHIRT("Shirt"),
    PANTS("Pants"),
    //SKIRT("Skirt"),
    //SHOES("Shoes"),
    //ACCESSORIES("Accessories")
    //Poniewaz reszty raczej uzywac nie bedziemy XDDD
    ;

    companion object {
        fun getCategoryNames(): List<String> {
            return entries
                .map { it.displayName }
                .sorted()
        }
    }
}