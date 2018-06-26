# 7.3 컬렉션 범위에 대해 쓸 수 있는 관례

## 7.3.1 인덱스로 원소에 접근: get과 set

변경 가능 맵에 키/값 쌍을 넣거나,  
이미 맵에 들어있는 키/값 연관 관계를 변경할 수 있다.  
`mutableMap[key] = newValue` 

- 코틀린에서는 인덱스 연산자도 관례를 따른다.  
    - 인덱스 연산자를 사용해 **원소를 읽는 연산**은 **get 메소드**로 변환  
    - **원소를 쓰는 연산**은 **set 메소드**로 변환 

```kotlin
// get 관례 구현하기
operator fun Point.get(index: Int): Int{
    return when(index){
        // 점의 좌표를 읽을 때 인덱스 연산자 사용 가능
        // p[0] =  X 좌표
        // p[1] =  Y 좌표
        0 -> x
        1 -> y
        // 주어진 인덱스에 해당하는 좌표를 찾는다.
        else ->
            throw IndexOutOfBoundException("..")
    }
}

val p = Point(10,20)
// p[1]이라는 식은 
// p가 Point 타입인 경우 get메소드로 변환된다.
println(p[1])
// 20

```

> 각괄호[]를 사용한 접근은 get 함수 호출로 변환된다.  
>> `x[a, b]` ----> `x.get(a,b)`

- get 메소드의 파라미터로 Int가 아닌 타입도 사용할 수 있다.  

- 여러 파라미터를 사용하는 get 가능.  
예) `operator fun get(row: Int, col: Int)` -> matrix[row, col]

- 다양한 파라미터 타입에 대해 오버로딩한 get 여러개 가능.

```kotlin
// 관례를 따르는 set 구현하기
data class MutablePoint(var x: Int, var y: Int)

operator fun MutablePoint.set(index: Int, value: Int){
    while(index){
        0 -> x = value
        1 -> y = value
        // 주어진 인덱스에 해당하는 값을 변경한다.
        else ->
            throw IndexOutOfBoundException("..")
    }
}

val p = MutablePoint(10,20)
p[1] = 42
println(p)
// MutablePoint(x=10, y=42)

```

> 마지막 파라미터 값은 우항에 들어가고,  
나머지 파라미터 값은 인덱스 연산자[]에 들어간다.
>> `x[a, b] = c` ----> `x.set(a, b, c)`


## 7.3.2 in 관례

**in**은 객체가 컬렉션에 들어있는지 검사한다.

in 연산자와 대응하는 함수는  **contains**다.


```kotlin
data class Rectangle(val upperLeft: Point, val lowerRight: Point)

operator fun Rectangle.contains(p: Point): Boolean{
    return p.x in upperLeft.x until lowerRight.x 
        && p.y in upperLeft.y until lowerRight.y 
}

val rect = Rectangle(Point(10,20), Point(50,50))
println(Point(20, 30) in rect)
// true

```

> in의 우항에 있는 객체는 contains 메소드의 수신 객체가 되고,   
in의 좌항에 있는 객체는 contains 메소드에 인자로 전달된다.
>> `a in c` ----> `c.contains(a)`

**열린 범위**란? : 끝 값을 포함하지 않는 범위

- 10 until 20  
    : 10 이상 20 미만  
- 10..20  
    : 10 이상 20 이하

## 7.3.3 rangeTo 관례

> .. 연산자는 rangeTo 함수 호출로 컴파일된다. 
>> `start..end` ----> `start.rangeTo(end)`


코틀린 표준 라이브러리에는 모든 Comparable 객체에 대해 적용 가능한 rangeTo 함수가 들어있기 때문에 Comparable 인터페이스를 구현하면 rangeTo를 정의할 필요 없다.

```kotlin
operator fun <T: Comparable<T>> T.rangeTo(that: T): CloseRange<T>
```

**rangeTo 함수는 범위를 반환**하며  
어떤 원소가 그 범위 안에 들어있는지 **in**을 통해 검사할 수 있다.

```kotlin
val now = LocalDate.now()
val vacation = now..now.plusDays(10) // 오늘 ~ +10일
println(now.plusWeeks(1) in vacation)
// true
```

```kotlin
// 범위 연산자는 우선 순위가 낮아서 
// 범위의 메소드를 호출하려면 범위를 괄호로 둘러싸야 한다.
// 0..n.forEach{} 는 컴파일 에러

val n = 9

(0..n).forEach{ print(it) }
// 0123456789
```


## 7.3.4 for 루프를 위한 iterator 관례

`for(x in list){...}` 와 같은 문장은  
`list.iterator()`를 호출해서 이터레이터를 얻은 다음,  
그 이터레이터에 대해 `hasNext()`와 `next()` 호출을 반복하는 식으로 변환된다.  

```kotlin
// 코틀린 표준 라이브러리는 
// String의 상위 클래스인 CharSequence에 대한
// 문자열을 이터레이션 할 수 있게 해주는 
// iterator 확장 함수를 제공한다. 
operator fun CharSequence.iterator(): CharIterator

for(c in "abc"){...}
```

클래스안에 직접 iterator 메소드를 구현 가능.

```kotlin
// LocalDate에 대한 iterator를 구현
operator fun ClosedRange<LocalDate>.iterator():
    Itertor<LocalDate> = object: Itertor<LocalDate>{

    var current = start
    
    override fun hasNext() <= current endInclusive
    // compareTo 관례를 사용해 날짜를 비교

    override fun next() = current.apply{
    // 현재 날짜를 저장한 뒤에 날짜를 변경.
    // 그 후 저장해둔 날짜를 변경한다.
        current = plusDays(1)
        // 현재 날짜를 1일 뒤로 변경
    }
}

val newYear = LocalDate.ofYearDay(2017, 1)

val daysOff = newYear.minusDays(1)..newYear
// rangeTo 함수는 ClosedRange 인스턴스를 반환한다.
//
// 2016-12-31..2017-01-01

for(dayOff in daysOff){ println(dayOff) }
// ClosedRange<LocalDate>에 대한 iterator를 
// 위에서 정의했기 때문에
// LocalDate의 범위 객체를 for 루프에 사용할 수 있다.
//
// 2016-12-31
// 2017-01-01

```



