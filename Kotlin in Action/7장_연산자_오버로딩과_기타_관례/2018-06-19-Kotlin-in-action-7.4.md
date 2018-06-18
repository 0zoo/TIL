# 7.4 구조 분해 선언과 component 함수

**구조 분해 선언 (destructuring declaration)**
: 복합적인 값을 분해해서 여러 다른 변수를 한꺼번에 초기화할 수 있다.


```kotlin
val p = Point(10, 20)

val (x, y) = p
// x와 y 변수를 선언하고 
// p의 여러 컴포넌트로 초기화한다.

println(x)
// 10

println(y)
// 20

```

내부에서 구조 분해 선언은 다시 관례를 사용한다.  


> 구조 분해 선언의 각 변수를 초기화하기 위해 componentN 함수를 호출한다.  
N은 구조 분해 선언에 있는 변수 위치에 따라 붙는 번호
>> `val(a, b) = p` --->  
`val a = p.component1()` , `val a = p.component1()`

data 클래스의 주 생성자에 들어있는 프로퍼티에 대해서는  
컴파일러가 자동으로 component 함수를 만들어준다.

```kotlin
class Point(val x: Int, val y: Int){
    operator fun component1() = x
    operator fun component2() = y
}
```

구조 분해 선언은 함수에서 여러 값을 반환할 때 유용하다. 

```kotlin
// 구조 분해 선언을 사용해 함수에서 여러 값을 반환하기 

data class NameComponents(val name: String, val extension: String)
// 값을 저장하기 위한 데이터 클래스를 선언

fun splitFileName(fullName: String): NameComponents{
    val result = fullName.split('.', limit = 2)

    return NameComponents(result[0], result[1])
    // 함수에서 데이터 클래스의 객체를 반환
}

val (name, ext) = splitFileName("example.kt")
// 구조 분해 선언을 사용해 데이터 클래스를 푼다.

println(name)
// example

```

```kotlin
// 컬렉션에 대해 구조 분해 선언을 사용하기

data class NameComponents(val name: String, val extension: String)

fun splitFileName(fullName: String): NameComponents{
    val (name, extension) = fullName.split('.', limit = 2)

    return NameComponents(name, extension)
}
```

코틀린 표준 라이브러리에서는 맨 앞의 다섯 원소에 대한 componentN을 제공한다.  
여섯개 이상의 변수를 사용하는 구조 분해를 컬렉션에 대해 사용하면 컴파일 오류 발생.


## 7.4.1 구조 분해 선언과 루프

변수 선언이 들어갈 수 있는 곳이면 구조 분해 선언을 사용할 수 있다.  
특히 맵의 원소에 대해 이터레이션할 때 구조 분해 선언이 유용하다. 

```kotlin
// 맵의 모든 원소를 출력하는 함수
fun printEntries(map: Map<String, String>){
    
    for( (key, value) in map ){
        println("$key -> $value")
    }

    // 위의 루프의 내부 구조
    for(entry in map.entries){
        val key = entry.component1()
        val value = entry.component2()
        //...
    }
}

val map = mapOf("A" to "a", "B" to "b")
printEntries(map)
// A -> a
// B -> b
```



