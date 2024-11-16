package com.example.chooseyouroutfit.model

enum class ClothesCategoryType(val displayName: String) {
    TOP("Blouse"),
    DRESS("Dress"),
    SHIRT("Shirt"),
    PANTS("Pants"),
    SKIRT("Skirt"),
    SHOES("Shoes"),
    ACCESSORIES("Accessories");

    companion object {
        fun getCategoryNames(): List<String> {
            return entries
                .map { it.displayName }
                .sorted()
        }
    }
}