package xyz.e0zoo.criminalintent

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import xyz.e0zoo.criminalintent.database.CrimeBaseHelper
import xyz.e0zoo.criminalintent.database.CrimeCursorWrapper
import xyz.e0zoo.criminalintent.database.CrimeDbSchema.CrimeTable
import java.util.*

class CrimeLab private constructor(private val context: Context) {

    companion object Factory {
        fun get(context: Context): CrimeLab = CrimeLab(context)

        private fun getContentValues(crime: Crime): ContentValues = ContentValues().apply {
            put(CrimeTable.Cols.UUID, crime.id.toString())
            put(CrimeTable.Cols.TITLE, crime.title)
            put(CrimeTable.Cols.DATE, crime.date.time)
            put(CrimeTable.Cols.SOLVED, if (crime.solved) 1 else 0)
            put(CrimeTable.Cols.SUSPECT, crime.suspect)
        }
    }

    private val mContext: Context by lazy {
        context.applicationContext
    }

    private val mDatabase: SQLiteDatabase by lazy {
        CrimeBaseHelper(mContext).writableDatabase
    }

    fun getCrimes(): List<Crime> {
        val crimes = arrayListOf<Crime>()
        val cursor = queryCrimes(null, null)

        cursor.use {
            it.moveToFirst()
            while (!it.isAfterLast) {
                crimes.add(it.getCrime())
                it.moveToNext()
            }
        }
        return crimes
    }

    fun getCrime(id: UUID): Crime? {
        val cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                arrayOf(id.toString())
        )
        cursor.use {
            if (it.count == 0) return null
            it.moveToFirst()
            return it.getCrime()
        }
    }


    fun addCrime(c: Crime) {
        val values = getContentValues(c)
        mDatabase.insert(CrimeTable.NAME, null, values)
    }

    fun updateCrime(crime: Crime) {
        val uuidString = crime.id.toString()
        val values = getContentValues(crime)
        mDatabase.update(CrimeTable.NAME, values,
                "${CrimeTable.Cols.UUID} = ?", arrayOf(uuidString))
    }

    private fun queryCrimes(whereClause: String?, whereArgs: Array<String>?): CrimeCursorWrapper {
        val cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // columns - 널인 경우 테이블의 모든 열을 의미
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        )
        return CrimeCursorWrapper(cursor)
    }


}