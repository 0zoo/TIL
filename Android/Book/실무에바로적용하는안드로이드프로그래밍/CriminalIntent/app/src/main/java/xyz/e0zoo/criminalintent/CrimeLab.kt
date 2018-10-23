package xyz.e0zoo.criminalintent

import java.util.*

object CrimeLab {
    private var crimes = arrayListOf<Crime>()

    init {
        /*
        for (i in 0..100) {
            val crime = Crime()
            crime.title = "범죄 #$i"
            crime.solved = i % 2 == 0
            crimes.add(crime)
        }
        */
    }

    fun getCrimes(): List<Crime> = crimes

    fun getCrime(id: UUID): Crime? {
        for (crime in crimes) {
            if (crime.id == id)
                return crime
        }
        return null
    }

    fun addCrime(c: Crime){
        crimes.add(c)
    }

}