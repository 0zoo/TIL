# 9.1 제네릭 타입 파라미터

- **제네릭스**를 사용하면  
**타입 파라미터**를 받는 타입을 정의할 수 있다.  

- 제네릭 타입의 인스턴스를 만들기위해서는  
타입 파라미터를 구체적인 **타입 인자**로 치환해야 한다.

- 코틀린 컴파일라는 타입 인자도 추론할 수 있다.  

```kotlin
// 이 두 선언은 동등함.

val readers: MutableList<String> = mutableListOf()

val readers = mutableListOf<String>()
```

자바에서는 List와 같은 타입 인자가 없는 raw 타입을 허용하지만,  
코틀린에서는 프로그래머가 직접 명시하던 컴파일러 추론에 의해 자동으로 정의되던 항상 정의해야 함.

## 9.1.1 제네릭 함수와 프로퍼티

제네릭 함수를 호출할 때는 반드시 구체적 타입으로 타입 인자를 넘겨야 한다.

> `fun <T> List<T>.slice(indices: IntRange): List<T>`
>> 수신 객체와 반환 타입에 타입 파라미터가 쓰인다. (`List<T>`)

```kotlin
val letters = ('a'..'z').toList()
println(letters.slice<Char>(0..2)) //[a, b, c]
// 타입 인자를 명시적으로 지정.
println(letters.slice(0..2))
// 컴파일러가 T가 Char이라는 사실을 추론함.

// 두 호출 모두 결과 타입이 List<Char>
```

```kotlin
val authors = listOf("Dmitry", "Svetlana")
val readers = mutableListOf<String>(/*..*/)

fun <T> List<T>.filter(predicate: (T) -> Boolean): List<T>
// 수신 객체(List<T>)와 파라미터 타입((T) -> Boolean)에 타입 파라미터 사용함.

readers.filter{ it !in authors }
// it의 타입은 T
```


```kotlin
// 리스트의 뒤에서 두번째 원소를 반환하는 확장 프로퍼티

// 모든 리스트 타입에 이 제네릭 확장 프로퍼티 사용 가능
val <T> List<T>.penultimate: T 
    get() = this[size-2]

println(listOf(1,2,3,4).penultimate)
// 3

```

---
### 확장 프로퍼티만 제네릭하게 만들 수 있다.
일반 프로퍼티는 타입 파라미터를 가질 수 없다.  
`val <T> x: T = ....`  
// 컴파일 오류 발생  

---


## 9.1.2 제네릭 클래스 선언

- 타입 파라미터를 이름 뒤에 붙이고 나면  
클래스 본문 안에서 타입 파라미터를 다른 일반 타입처럼 사용할 수 있다.  
    ```kotlin
    interface List<T>{

        // 인터페이스 안에서 T를 일반 타입처럼 사용 가능
        operator fun get(index: Int): T
        // ...
    }
    ```

- 타입 인자를 지정할 때  
(1) 구체적인 타입을 넘기거나 (2) 타입 파라미터로 받은 타입을 넘길 수 있음.
    ```kotlin
    //1. 구체적인 타입 인자로 String을 지정해 List 구현함
    class StringList: List<String> {
        override fun get(index: Int): String = ...
        // 상위 클래스에 정의된 함수를 오버라이드하거나 사용려면
        // 타입 인자 T를 구체적인 타입 String으로 치환해야 한다.
    }

    //2. ArrayList의 T를 List의 타입 인자로 넘긴다.
    class ArrayList<T>: List<T> {
        override fun get(index: Int): T = ...
    }
    // ArrayList 클래스는 자신만의 타입 파라미터 T를 정의하면서
    // 그 T를 기반 클래스의 타입 인자로 사용한다.
    ```

- 클래스가 자기 자신을 타입 인자로 참조할 수 있다.
    ```kotlin
    interface Comparable<T> {
        fun compareTo(other: T): Int
    }

    class String: Comparable<String> {
        override fun compareTo(other: String): Int = ...
    }
    // String 클래스는 제네릭 compareTo 인터페이스를 구현하면서
    // 그 인터페이스의 타입 파라미터 T로 String 자신을 지정한다.
    ```

## 9.1.3 타입 파라미터 제약

**타입 파라미터 제약**: 클래스나 함수에 사용할 수 있는 타입 인자를 제한하는 기능

**상한(upper bound)** 으로 지정하면  
그 제네릭 타입을 인스턴스화할 때 사용하는 타입 인자는  
반드시 그 상한 타입이거나 상한 타입의 하위 타입이어야 한다.  

제약을 가하려면 타입 파라미터 이름 뒤에 콜론(:)을 표시하고  
그 뒤에 상한 타입을 적으면 된다.

> `fun <T: Number> List<T>.sum(): T`
>> <타입 파라미터: 상한 타입>
>>> T: 타입 파라미터  
Number: 상한

- T에 대한 상한을 정하고 나면 T 타입의 값을 그 상한 타입의 값으로 취급 가능

```kotlin
// 상한 타입에 정의된 메소드를 T 타입 값에 대해 호출할 수 있다.

fun <T: Number> oneHalf(value: T): Double {
    return value.toDouble() / 2.0
    // Number 클래스에 정의된 메소드를 호출한다.
}

println(oneHalf(3))
// 1.5
```

```kotlin
// 이 함수의 인자들은 비교 가능해야 한다.
fun <T: Comparable<T>> max(first: T, second: T): T {
    return if(first > second) first else second
}

println(max("kotlin", "java"))
// kotlin

println(max("kotlin", 42))
// 비교 불가능한 값 사이에 호출하면 컴파일 오류 발생

```

타입 파라미터에 대해 둘 이상의 제약을 가해야 하는 경우도 존재함.
```kotlin
fun <T> ensureTrailingPeriod(seq: T)
    where T: CharSequence, T: Appendable {
    // 타입 파라미터 제약 목록

        // CharSequence 인터페이스의 확장함수 호출
        if(!seq.endsWith('.')){

            // Appendable 인터페이스의 메소드 호출
            seq.append('.')
        }
}

// 타입 인자가 CharSequence와 Appendable 인터페이스를 
// 반드시 구현해야 한다.
// 이는 데이터에 접근하는 연산(endsWith)과  
// 데이터를 변환하는 연산(append)을
// T 타입의 값에게 수행할 수 있다는 뜻.

val helloWorld = StringBuilder("Hello World")
ensureTrailingPeriod(helloWorld)
// Hello World.
```

## 9.1.4 타입 파라미터를 널이 될 수 없는 타입으로 한정

제네릭 타입을 인스턴스화할 때는  
어떤 타입으로 타입 인자를 지정해도  
타입 파라미터를 치환할 수 있다.

- 상한을 정하지 않은 타입 파라미터는 `Any?`를 상한으로 정한 파라미터와 같다.
    ```kotlin
    class Proessor<T>{
        fun process(value: T){
        // T에는 ?가 붙어있지 않지만
        // nullable 타입을 넘길 수 있다.

            value?.hashcode()
            // value는 nullable
        }
    }

    // Processor 클래스를 nullable 타입을 사용해 인스턴스화
    val nullableProcessor = Processor<String?>()
    nullableProcessor.process(null)
    ```

 - 항상 notNull 타입만 받게 만들려면 타입 파라미터에 제약 필수.  

- `<T: Any>` 라는 제약은 T 타입이 항상 notNull 타입임을 보장.
    ```kotlin
    class Processor<T: Any> {
    // notNull 타입 상한을 지정함.
    
        fun process(value: T){
            value.hashcode()
            // value는 notNull
        }
    }
    ``` 


