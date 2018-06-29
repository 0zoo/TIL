# 9.2 실행 시 제네릭스의 동작: 소거된 타입 파라미터와 실체화된 타입 파라미터

JVM의 제네릭스는 보통 **타입 소거(type erasure)** 를 사용해 구현된다.  
(실행 시점에 타입 인자 정보 x)

**실체화 _reify_** : 
코틀린에서는 함수를 `inline`으로 선언함으로써 타입 인자가 지워지지 않게 할 수 있다.  

## 9.2.1 실행 시점의 제네릭 타입: 타입 검사와 캐스트

(자바와 마찬가지로) **코틀린 제네릭 타입 인자 정보는 런타임에 지워진다.**

- 제네릭 타입 소거의 장점: 메모리 사용량이 줄어든다.

예를 들어 `List<String>` 객체를 만들고 그 안에 문자열을 여럿 넣더라도  
실행 시점에는 그 객체를 오직 `List`로만 볼 수 있다.

```kotlin 
val list1: List<String> = listOf("a", "b")
val list2: List<Int> = listOf(1, 2, 3)

// 실행 시점에 둘은 같은 타입의 객체다.
```

타입 인자를 따로 저장하지 않기 때문에 **실행 시점에 타입 인자를 검사할 수 없다.**

일반적으로 말하자면 `is` 검사에서 타입 인자로 지정한 타입을 검사할 수 없다.

`if( value is List<String> )` 코드는 컴파일시 오류.

- `is`를 사용하려면?  
    1. 스타 프로젝션 사용하기  
    `(c: Collection<*>)`
    2. 타입 정보 명시하기  
    `(c: Collection<Int>)`

**스타 프로젝션**
> `if ( value is List<*> )`
>> 타입 파라미터가 2개 이상이라면 모든 타입 파라미터에 `*`을 포함시켜야 한다.


`as`나 `as?` 캐스팅에 제네릭 타입을 사용할 수 있다.  
하지만, 캐스팅은 항상 성공하기 때문에 컴파일러가 **`"unchecked cast"` 경고** 해줌.  
(단순히 경고만 하고 컴파일 진행)

```kotlin
fun printSum(c: Collection<*>) {
    val intList = c as? List<Int> // 여기서 경고
        ?: throw IllegalArgumentException("..")
    
    println(intList.sum())
}

printSum(listOf(1, 2, 3)) // 6
// 컴파일러가 캐스팅 관련 경고 하지만
// 나머지 코드 정상 컴파일.

// 집합:
printSum(setOf(1, 2, 3))
// IllegalArgumentException

// 잘못된 타입의 원소가 들어있는 리스트를 전달:
printSum(setOf("a", "b"))
// ClassCastException
// (sum함수를 호출할 때 예외 발생)

```

코틀린 컴파일러는  
컴파일 시점에 타입 정보가 주어진 경우에는 `is` 검사를 수행하게 허용한다.

```kotlin
// 알려진 타입 인자를 사용해 타입 검사하기
fun printSum(c: Collection<Int>) {
    if( c is List<Int> ){
        println(c.sum())
    }
}

// 컴파일 시점에 
// c 컬렉션이 Int 값을 저장하는 사실을 알기 때문에
// is 검사 ok
```

코틀린 컴파일러는 
- 안전하지 못한 `is` 검사 금지 
- 위험한 `as` 캐스팅은 경고

## 9.2.2 실체화한 타입 파라미터를 사용한 함수 선언

타입 인자 정보가 런타임시 지워지기 때문에  
**제네릭 클래스의 인스턴스가 있어도**  
그 인스턴스를 만들 때 사용한 **타입 인자를 알아낼 수 없다.**

```kotlin
fun <T> isA(value: Any) = value is T
// Error!
// 그 함수의 본문에서도 
// 호출시 쓰인 타입 인자 알 수 없음
```

하지만 이런 제약을 피할 수 있는 경우가 **하나** 있다.  
> **인라인 함수**의 타입 파라미터는 **실체화**되므로  
실행 시점에 인라인 함수의 타입 인자를 알 수 있다.

```kotlin
// 실체화한 타입 파라미터를 사용하는 함수 정의하기

inline fun <reified T> isA(value: Any) = value is T
// 인라인 함수 + 타입 파라미터 reified로 지정
// -> is 검사 ok

println( isA<String>("abc") )
// true

println( isA<String>(123) )
// false
```

_filterIsInstance_ 라이브러리 함수는  
지정한 타입의 원소만을 모아서 만든 리스트를 반환해준다.

```kotlin
val items = listOf("one", 2, "three")

println(items.filterIsInstance<String>())
// [one, three]
// 이 함수의 반환 타입: List<String>
```

- `reified` 키워드는 이 타입 파라미터가 실행 시점에 지워지지 않음을 표시한다.

```kotlin
// filterIsInstance 선언을 간단히 정리한 코드

inline fun <reified T>
    Iterable<*>.filterIsInstance(): List<T> {
    
    val destination = mutableListOf<T>()

    for(element in this){
        if(element is T){ 
        // is 검사 ok
            destination.add(element)
        }
    }
    return destination
}
```

---
### 인라인 함수에서만 실체화한 타입 인자를 쓸 수 있는 이유

컴파일러는 인라인 함수의 본문을 구현한 바이트코드를 그 함수가 호출되는 모든 시점에 삽입한다.

컴파일러는 실체화한 타입 인자를 사용해  
인라인 함수를 호출하는 각 부분의 **정확한 타입 인자를 알 수 있다.**

- `filterIsInstance<String>` 은
    ```kotlin
    for(element in this){
        if(element is String){ 
            destination.add(element)
        }
    }
    ```
    과 같은 코드를 만들어낸다.

타입 파라미터가 아니라 구체적인 타입을 사용하므로  
만들어진 바이트코드는 실행 시점에 벌어지는 **타입 소거의 영향 받지 않음.**

**자바 코드에서 이런 함수 호출 불가!**

자바에서는 코틀린 인라인 함수를 다른 보통 함수처럼 호출한다.

---

인라인 함수에는  
실체화한 타입 파라미터가 여럿 있거나  
실체화된 타입 파라미터와 실체화되지 않은 타입 파라미터가 함께 있을 수도 있음.

람다를 파라미터로 받지 않지만 filterIsInstance를 인라인 함수로 정의했다는 점에 유의하자.

8.2.4에서,   
함수의 파라미터 중에 함수 타입인 파라미터가 있고  
그 파라미터에 해당하는 인자(람다)를 함께 인라이닝함으로써
얻는 이익이 더 큰 경우에만  
함수를 인라인 함수로 만들라고 했음.

함수가 커지면  
실체화한 타입에 의존하지 않는 부분을 별도의 일반 함수로 뽑아내는 편이 낫다.

## 9.2.3 실체화한 타입 파라미터로 클래스 참조 대신

_java.lang.Class_ 타입 인자를 파라미터로 받는 API  
에 대한 코틀린 어댑터를 구축하는 경우  
실체화한 타입 파라미터를 자주 사용함.

_java.lang.Class_ 타입 인자를 파라미터로 받는 API의 예:  
JDK의 `ServiceLoader`  
-> _java.lang.Class_ 를 받아서 그 클래스나 인스턴스를 구현한 인스턴스를 반환한다.

```kotlin
val serviceImpl = ServiceLoader.load(Service::class.java)
```
`::class.java` 는 코틀린 클래스에 대응하는  _java.lang.Class_ **참조**를 얻는 방법을 보여준다.

`Service::class.java`는 `Service.class` 라는 자바 코트와 완전히 같다.  

```kotlin
// 구체화한 타입 파라미터를 사용해 다시 작성.

val serviceImpl = loadService<Service>()
// Service 클래스를 타입 인자로 지정
```

```kotlin
// loadService 함수 정의

inline fun <reified T> loadService() {
    return ServiceLoader.load(T::class.java)
    // T::class로 타입 파라미터의 클래스를 가져온다.
}
```

-----
### 안드로이드의 startActivity 함수 간단하게 만들기

액티비티의 클래스를 _java.lang.Class_ 로 전달하는 대신  
실체화한 타입 파라미터를 사용할 수 있다.

```kotlin
inline fun <reified T : Activity> Context.startActivity() {
    
    val intent = Intent(this, T::class.java)

    startActivity(intent)
}

startActivity<DetailActivity>() 
// startActivity 메소드를 호출
```

-----




## 9.2.4 실체화한 타입 파라미터의 제약

- 실체화한 타입 파라미터 사용 가능한 경우
    -  
    - 타입 검사와 캐스팅  
    (`is`, `!is`, `as`, `as?`)
    - 코틀린 리플렉션  
    (`::clas`)
    - 코틀린 타입에 대응하는 _java.lang.Class_ 얻기  
    (`::clas.java`)
    - 다른 함수를 호출할 때 타입 인자로 사용

- 불가능한 경우
    -
    - 타입 파라미터 클래스의 인스턴스 생성하기

    - 타입 파라미터 클래스의 동반 객체 메소드 호출하기

    - 실체화한 타입 파라미터를 요구하는 함수를 호출시  
    실체화하지 않은 타입 파라미터로 받은 타입을  
    타입 인자로 넘기기
    
    - 클래스, 프로퍼티, 인라인 함수가 아닌 함수의 타입 파라미터를  
    `reified`로 지정하기


마지막 제약으로 인해 흥미로운 파급효과가 생긴다.

**실체화한 타입 파라미터를 인라인 함수에만 사용**할 수 있으므로  
실체화한 타입 파라미터를 사용하는 함수는  
자신에게 전달되는 모든 **람다와 함께 인라이닝**된다.

`notinline` 변경자를 붙이면 인라이닝을 금지 가능.


    



