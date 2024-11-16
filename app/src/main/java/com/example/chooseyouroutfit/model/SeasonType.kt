package com.example.chooseyouroutfit.model

enum class SeasonType(val displayName: String) {
    SPRING("Spring"),
    SUMMER("Summer"),
    AUTUMN("Autumn"),
    WINTER("Winter");

    companion object {
        fun getSeasonNames(): List<String> {
            return entries.map { it.displayName }
        }
    }
}