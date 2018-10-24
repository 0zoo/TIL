package xyz.e0zoo.criminalintent.database

import android.database.Cursor
import android.database.CursorWrapper
import xyz.e0zoo.criminalintent.Crime
import xyz.e0zoo.criminalintent.database.CrimeDbSchema.CrimeTable
import java.util.*

class CrimeCursorWrapper(cursor: Cursor?) : CursorWrapper(cursor) {
    fun getCrime(): Crime {
        val uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID))
        val title = getString(getColumnIndex(CrimeTable.Cols.TITLE))
        val date = getLong(getColumnIndex(CrimeTable.Cols.DATE))
        val isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED))

        val crime = Crime(UUID.fromString(uuidString))
        crime.title = title
        crime.date = Date(date)
        crime.solved = isSolved != 0

        return crime
    }
}