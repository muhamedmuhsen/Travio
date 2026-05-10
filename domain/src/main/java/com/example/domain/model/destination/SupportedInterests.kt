package com.example.domain.model.destination

object SupportedInterests {
    val all: List<Interest> = listOf(
        Interest(1, "Historical"),
        Interest(2, "Architecture"),
        Interest(3, "Cultural"),
        Interest(4, "Art"),
        Interest(5, "Religious"),
        Interest(6, "Adventure"),
        Interest(7, "Relaxation"),
        Interest(8, "Coastal"),
        Interest(9, "Shopping"),
        Interest(10, "City View"),
        Interest(11, "Entertainment"),
        Interest(12, "Family"),
        Interest(13, "Nature"),
        Interest(14, "Nightlife"),
        Interest(15, "Photography")
    )

    fun isValid(id: Int): Boolean {
        return all.any { it.interestID == id }
    }
}
