# Chapter14. SQLite 데이터베이스

안드로이드 장치의 각 애플리케이션은 자신의 **샌드박스(sandbox)** 에 디렉토리를 갖는다.  
`data/data/패키지이름`

샌드박스에 파일을 저장하면 다른 애플리케이션에서 액세스하는 것을 막아준다. (루팅되지 않은 장치에 한하여)

파일을 수정할 경우 파일 전체를 읽고 다시 저장하기 때문에 복잡하고 시간이 많이 걸린다.  
-> SQLite가 필요한 이유

## 스키마 정의하기

```java
package xyz.e0zoo.criminalintent.database;

public class CrimeDbSchema {
    public static final class CrimeTable {
        public static final String NAME = "crimes";
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            // CrimeTable.Cols.TITLE 으로 직접 접근하지 않기 떄문에 코드의 유지보수가 쉬워진다.
        }
    }
}
```

## 데이터베이스 생성하기

`openOrCreateDatabase(...)`와 `databaseList()`: db 파일을 SQLiteDatabase의 인스턴스로 열 수 있는 Context의 메서드

**SQLiteOpenHelper** 클래스

1. 데이터베이스가 이미 있는지 확인
2. 없다면, 데이터베이스와 테이블을 생성하고 초기 데이터 추가
3. 있다면, 데이터베이스 스키마 버전 확인
4. 구버전이라면, 버전 업그레이드

SQLiteOpenHelper는 SQLiteDatabase 인스턴스를 열 때 해야 할 많은 일들을 덜어준다.

```java
public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";
    
    public CrimeBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
```

```kotlin
class CrimeLab private constructor(private val context: Context) {

    companion object Factory {
        fun get(context: Context): CrimeLab = CrimeLab(context)
    }

    private val mContext: Context by lazy {
        context.applicationContext
    }

    private val mDatabase: SQLiteDatabase by lazy {
        CrimeBaseHelper(mContext).writableDatabase
    }
    ...
}
```

`getWritableDatabase()`을 호출하면 CrimeBaseHelper가 다음 일을 해준다.  
1. `data/data/패키지명/database/crimeBase.db`을 연다. (파일이 없다면 생성)
2. 데이터베이스가 최초 생성: `onCreate()` 호출하고 내가 지정한 버전 번호 저장.
3. 최초 생성 아님: 버전 정보 확인하고 저장된 버전보다 높은 경우 `onUpgrade()`호출.

- 데이터베이스 버전: 테이블 구조 변경시 재구성하기 위한 버전을 의미함.

```java
// 테이블 생성 코드 추가
public class CrimeBaseHelper extends SQLiteOpenHelper {
    ...
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ CrimeDbSchema.CrimeTable.NAME);
    }
}
```
`import xyz.e0zoo.criminalintent.database.CrimeDbSchema.CrimeTable;`  
-> `CrimeTable.NAME`으로 사용 가능


```java
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CrimeTable.Cols.UUID + ", " +
                CrimeTable.Cols.TITLE + ", " +
                CrimeTable.Cols.DATE + ", " +
                CrimeTable.Cols.SOLVED +
                ")"
        );
    }
```

데이터베이스 파일 확인하는 방법: 메뉴에서 Tools > Android > Android Device Monitor 에서 File Explore로 들어가 `data/data`폴더 확인


### 데이터베이스 디버깅 고려사항

데이터베이스 구조 변경시 올바른 방법은 버전을 변경하고 `onUpgrade()` 메서드 내부에서 변경하는 것.

변경될 일이 별로 없다면 데이터베이스를 삭제하는 것도 좋은 방법이다.

데이터베이스 삭제하는 가장 쉬운 방법: 장치에서 앱 삭제 

## CrimeLab의 코드 삭제
crimes 관련 코드 모두 삭제

## 데이터베이스에 데이터 쓰기

### ContentValues 사용하기

ContentValues은 키와 값 쌍으로 구성되며 db에 데이터를 추가하거나 갱신하는 것을 도와준다.  

```kotlin
class CrimeLab private constructor(private val context: Context) {

    companion object Factory {.
        ..
        private fun getContentValues(crime: Crime): ContentValues = ContentValues().apply {
            put(CrimeTable.Cols.UUID, crime.id.toString())
            put(CrimeTable.Cols.TITLE, crime.title)
            put(CrimeTable.Cols.DATE, crime.date.time)
            put(CrimeTable.Cols.SOLVED, if (crime.solved) 1 else 0)
        }
    }
    ...
}
```

### 행 추가하기와 갱신하기

```kotlin
// CrimeLab.kt
    fun addCrime(c: Crime) {
        val values = getContentValues(c)
        mDatabase.insert(CrimeTable.NAME, null, values)
    }
```
`insert(테이블 이름, nullColumnHack, ContentValue)`  

nullColumnHack: 행을 추가할 때 최소한 하나의 열을 지정해야 한다. (완전히 비어있는 행을 추가하는 것을 허용하지 않음) nullColumnHack 인자에 null 값이 허용되는 열을 지정해서 Null을 명시적으로 삽입하여 행을 추가할 수 있도록 해준다.

```kotlin
// CrimeLab.kt
    fun updateCrime(crime: Crime){
        val uuidString = crime.id.toString()
        val values = getContentValues(crime)
        mDatabase.update(CrimeTable.NAME, values, 
                "${CrimeTable.Cols.UUID} = ?", arrayOf(uuidString))
    }
```
`update(테이블 이름, ContentValue, 갱신될 행 지정 쿼리, where절에 지정할 값의 String 배열 )`

왜 uuidString을 where 절에 바로 넣지 않을까? -> 안전하지 않기 때문에.   
SQL 쿼리에 String을 직접 넣으면 의미가 변경되거나 데이터베이스가 변경될 위험성이 있기 때문. (SQL 주입 공격)

항상 ?를 사용하는 것이 가장 안전하다.  

범죄 인스턴스는 CrimeFragment에서 변경되므로 일시 중단될 때는 데이터베이스에 저장해야 한다.

```kotlin
// CrimeFragment.kt
    override fun onPause() {
        super.onPause()
        CrimeLab.get(requireActivity()).updateCrime(mCrime)
    }
```


## 데이터베이스의 데이터 읽기

`SQLiteDatabase.query(...)`를 사용해서 데이터베이스에서 데이터를 읽는다.  
이 메서드는 여러 형태로 오버라이딩해서 사용함.

```java
public Cursor query(
    String table,
    String[] columns,
    String where,
    String[] whereArgs,
    String groupBy,
    String having,
    String orderBy,
    String limit
)
```

```kotlin
// CrimeLab.kt
    private fun queryCrimes(whereClause: String, whereArgs: Array<String>): Cursor = mDatabase.query(
            CrimeTable.NAME,
            null, // columns - 널인 경우 테이블의 모든 열을 의미
            whereClause,
            whereArgs,
            null, // groupBy
            null, // having
            null // orderBy
    )
```

### CursorWrapper 사용하기

Cursor는 쿼리된 결과 데이터를 가져오는 데 사용된다.

```java
String title = cursor.getString( cursor.getColumnIndex(CrimeTable.Cols.TITLE));
```

Cursor는 테이블의 각 열에 사용하기 때문에 데이터를 읽을 때마다 매번 이 코드들을 작성해야 하기 때문에  
한 곳에서 이 코드들을 관리해야 한다.  
-> **CursorWrapper** : 커서를 래핑해서 원하는 테이블로부터 데이터를 읽을 수 있고 새로운 메서드도 추가할 수 있다.

```kotlin
class CrimeCursorWrappe(cursor: Cursor?) : CursorWrapper(cursor) {
    fun getCrime(): Crime? {
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
```

### 모델 객체로 변환하기

```kotlin

class CrimeLab private constructor(private val context: Context) {

    ...

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

    /*
    private fun queryCrimes(whereClause: String, whereArgs: Array<String>): Cursor = mDatabase.query(
            CrimeTable.NAME,
            null, // columns - 널인 경우 테이블의 모든 열을 의미
            whereClause,
            whereArgs,
            null, // groupBy
            null, // having
            null // orderBy
    )
    */
    private fun queryCrimes(whereClause: String?, whereArgs: Array<String>?): CrimeCursorWrapper{
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
    ...
}
```


## 애플리테이션 컨텍스트

메모리를 낭비하는 경우를 방지하기 위해서 애플리케이션 컨텍스트를 사용함.

## 챌린지: Crime 데이터 삭제하기

