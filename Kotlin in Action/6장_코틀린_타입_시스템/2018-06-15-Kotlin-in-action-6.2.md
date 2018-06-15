# 6.2 코틀린의 원시 타입
코틀린은 원시 타입과 래퍼 타입을 구분하지 않는다. 
## 6.2.1 원시 타입: Int, Boolean 등

자바는 원시 타입과 참조 타입을 구분한다. 
- 원시 타입(primitive type) : int 등..  
변수에 값이 직접 들어감
- 참조 타입(reference type) : String 등..  
변수에 주소값이 들어감

자바는 참조 타입이 필요한 경우 특별히 래퍼 타입으로 원시 타입 값을 감싸서 사용한다. (Integer 등..)

코틀린은 원시 타입과 래퍼 타입을 구분하지 않으므로 항상 같은 타입을 사용한다.  

코틀린에서는 원시 타입의 값에 대해 메소드를 호출할 수 있다. 

```kotlin
// 원시 타입인 Int에 대해 표준 라이브러리 함수 coreIn을 호출
fun showProgerss(progress: Int){
    val percent = progress.coreIn(0,100)
    println("${percent}% done!")
}
showProgerss(150)
// 100% done!
```

- 코틀린에서 원시 타입과 참조 타입이 같다면 항상 객체로 다룰까?  
-> **NO**

실행 시점에 숫자 타입은 가능한 한 가장 효율적인 방식으로 표현된다.

제네릭 클래스를 사용하는 경우를 제외하고  
대부분의 경우 코틀린의 Int 타입은 자바 int 타입으로 컴파일된다.  

## 6.2.2 널이 될 수 있는 원시 타입: Int?, Boolean? 등

코틀린에서 널이 될 수 있는 원시 타입을 사용하면 그 타입은 자바의 래퍼 타입으로 컴파일된다. 

```kotlin
// 널이 될 수 있는 원시 타입
data class Person(val name: String, val age: Int? = null){
    fun isOlder(other: Person): Boolean? {
        if(age == null || other.age == null)
            return null
        return age > other.age
    }
}

println(Person("A",21).isOrder(Person("B")))
// null

```

어떤 클래스의 타입 인자로 원시 타입을 넘기면  
코틀린은 그 타입에 대한 박스 타입을 사용한다.
`val list = listOf(1,2,3)`  
// 래퍼인 Integer 타입으로 이루어진 리스트

이렇게 컴파일 하는 이유는 JVM은 타입 인자로 원시 타입을 허용하지 않는다.   
따라서 자바, 코틀린 모두에서 제네릭 클래스는 항상 박스 타입을 사용해야 한다.

## 6.2.3 숫자 변환

코틀린은 한 타입의 숫자를 다른 타입의 숫자로 자동 변환하지 않는다.
결과 타입이 허용하는 숫자의 범위가 원래 타입의 범위보다 넒은 경우 조차도 자동 변환은 불가능하다.

```kotlin
val i = 1
val l: Long = i // error! Type mismatch

// 직접 변환 메소드를 호출해야 한다.
val l: Long = i.toLong()
```

코틀린은 Boolean을 제외한 모든 원시 타입에 대한 변환 함수를 제공한다. 양뱡향 변환 함수 모두 제공.

코틀린은 개발자의 혼란을 피하기 위해 타입 변환을 명시하기로 했다.  
두 박스 타입 간의 equals 메소드는 그 안에 들어있는 값이 아니라 박스 타입 객체를 비교한다.  
따라서 자바에서 `new Integer(42).equals(new Long(42))`는 `false`다.

```kotlin
val x = 1 // Int 타입인 변수
val list = listOf(1L, 2L, 3L) // Long값으로 이뤄진 리스트

x in list // 묵시적 타입 변환으로 인해 false
// x in list는 컴파일되면 안 된다.
// 코틀린에서는 타입을 명시적으로 변환해서 같은 타입의 값으로 만든 후 비교해야 한다.

println(x.toLong() in list)
// true
```

코드에 동시에 여러 숫자 타입을 사용하려면 예상치 못한 동작을 피하기 위해 각 변수를 명시적으로 변환해야 한다.

-----
#### 원시 타입 리터럴
코틀린은 소스코드에서 단순한 10진수 외에 다음과 같은 숫자 리터럴을 허용한다.
- Long 타입 리터럴:
    123L
- Double 타입 리터럴:
    2.0, 1.2e10
- Float 타입 리터럴:
    123.4f, 456F, 1e3f
- 16진 리터럴:
    0xCAFEBABE
- 2진 리터럴:
    0b000000101

코틀린 1.1부터 숫자 리터럴 사이에 밑줄_ 을 넣을 수 있다.  
(1_234, 1_000.123_456)

-----

숫자 리터럴을 사용할 때는 보통 변환 함수를 호출할 필요가 없다.  

```kotlin
fun foo(l: Long) = println(l)

val b: Byte = 1
// 상수 값은 적절한 타입으로 해석된다.

// 산술 연산자는 적당한 타입의 값을 받아들일 수 있게 이미 오버로드되어 있다.
val l = b + 1L
// +는 Byte와 Long을 인자로 받을 수 있다.


foo(42)
//42
// 타입이 알려진 변수에 대입하거나 함수에게 인자로 넘기면 
// 컴파일러가 필요한 변환을 자동으로 넣어준다.
// 컴파일러는 42를 Long으로 해석한다.
```

코틀린 산술 연산자에서도 숫자 연산시 overflow가 발생할 수 있다.  


## 6.2.4 Any, Any?: 최상위 타입

코틀린에서는 **Any** 타입이 모든 **널이 될 수 없는 타입(원시 타입을 포함한)의 조상 타입**이다.

자바와 마찬가지로 코틀린에서도 원시 타입 값을 Any 타입의 변수에 대입하면 자동으로 값을 객체로 감싼다.

`val answer: Any = 42` // Any가 참조 타입이기 때문에 42가 박싱된다.

내부에서 Any 타입은 *java.lang.Object*에 대응한다.  
자바 메소드에서 Object는 코틀린에서는 (플랫폼 타입인) Any로 그 타입을 취급한다.

모든 코틀린 클래스에는 toString, equals, hashCode 세 메소드가 들어있다. 이 세 메소드는 Any에 정의된 메소드를 상속한 것이다.  
하지만 *java.lang.Object*의 다른 메소드(wait, notify 등)은 Any에서 사용할 수 없다. 만약 이 메소드들을 사용하고 싶다면 Object 타입으로 캐스트해야 한다.

## 6.2.5 Unit 타입: 코틀린의 void

코틀린 Unit 타입은 자바 void와 같은 기능을 한다. 

`fun f(): Unit {...}`  
// 반환 타입 선언 없이 정의한 블록이 본문인 함수와 같음.  
`fun f() {...}`

함수의 반환 타입이 Unit이고 그 함수가 제네릭 함수를 오버라이드하지 않는다면  
그 함수는 내부에서 자바 void 함수로 컴파일된다.

코틀린의 Unit과 자바의 void의 차이점은?  

- Unit은 모든 기능을 갖는 일반적인 타입이며, void와 달리 Unit을 타입 인자로 쓸 수 있다.  
- Unit 타입의 함수는 Unit 값을 묵시적으로 반환한다.  
- 이 두 특성은 제네릭 파라미터를 반환하는 함수를 오버라이드하면서 반환 타입으로 Unit을 쓸 때 유용하다.

```kotlin
interface Processor<T>{
    fun process(): T
}

class NoResultProcessor: Processor<Unit>{
// Unit을 반환하지만 타입을 지정할 필요 없음    
    override fun process(){
        // return을 명시할 필요 없음
        // 컴파일러가 묵시적으로 return Unit을 넣어줌
    }
}
```

왜 코틀린에서 Void가 아닌 Unit이라는 이름을 골랐을까?  
함수형 프로그래밍에서 전통적으로 Unit은 `단 하나의 인스턴스만 갖는 타입 `을 의미해 왔고, 바로 그 유일한 인스턴스의 유무가 자바 void와 코틀린 Unit을 구분하는 가장 큰 차이다.

## 6.2.6 Nothing 타입: 이 함수는 결코 정상적으로 끝나지 않는다.

코틀린에는 `반환 값`이라는 개념 자체가 의미 없는 함수가 일부 존재한다. (예_ 테스트 라이브러리의 fail 함수는 예외를 던져 테스트를 실패시킴. 무한 루프인 함수..)

함수가 정상적으로 끝나지 않는 경우를 표현하기 위해 코틀린에서는 **Nothing**이라는 특별한 타입이 있다.

```kotlin
fun fail(message: String): Nothing{
    throw IllegalStateException(message)
}
```
Nothing 타입은 아무 값도 포함하지 않는다. 

Nothing을 반환하는 함수를 엘비스 연산자의 우항에 사용해서 전제 조건을 검사할 수 있다.
```kotlin
val address = company.address ?: fail("No Address")
```
컴파일러는 Nothing이 반환 타입인 함수가 결코 정상 종료되지 않음을 알고 그 함수를 호출하는 코드를 분석할 때 사용한다. 