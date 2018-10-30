package xyz.e0zoo.criminalintent

import java.util.*

data class Crime(val id: UUID = UUID.randomUUID(),
                 var title: String = "",
                 var date: Date = Date(),
                 var solved: Boolean = false,
                 var suspect: String? = null)


