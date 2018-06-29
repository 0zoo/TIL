# 8.1 고차 함수 정의

**고차 함수**: 다른 함수를 인자로 받거나 함수를 반혼하는 함수

## 8.1.1 함수 타입

림다를 로컬 변수에 저장하는 경우
```kotlin
val sum = { x: Int, y: Int -> x+y }
val action = { println(42) }
// 컴파일러가 sum과 action이 함수 타입임을 추론

// 각 변수에 구체적인 타입 선언 추가
val sum: (Int, Int) -> Int = { x, y -> x+y }
// Int 파라미터 두개 받고 Int 값을 반환하는 함수
val action: () -> Unit = { println(42) }
// 인자도 받지 않고 반환값도 없는 함수

```

> (파라미터 타입) -> 반환 타입
>> (Int, String) -> Unit

- 함수 타입을 선언할 때는 반환 타입을 반드시 명시해야 하므로  
Unit도 생략 불가.

- 함수 타입을 지정하면, 람다의 파라미터가 유추 가능하기 때문에  
파라미터의 타입 생략 가능하다.

- 널이 될 수 있는 **반환 타입**:  
`var returnNull: (Int, Int) -> Int? = null`

- 널이 될 수 있는 **함수 타입**:  
`var funOrNull: ((Int, Int) -> Int)? = null`  
(주의! 함수 타입 전체가 널이 될 수 있는 타입임을 선언하기 위해  
함수 타입을 괄호로 감싸고 그 뒤에 물음표를 붙여야 함)


----
### 파라미터 이름과 함수 타입

파라미터 이름은 타입 검사 시 무시되기 때문에  
함수 타입에서 파라미터 이름 지정 가능함.

```kotlin
fun performRequest(
    url: String,
    callback: (code: Int, content: String) -> Unit
    // 함수 타입의 각 파라미터에 이름을 붙인다.
){
    /*..*/
}

val url = "http://..."

performRequest(url) {code, content -> ...}
// API에서 제공하는 이름을 람다에 사용 가능.

performRequest(url) {code, page -> ...}
// 내가 원하는 이름을 붙여도 됨.

```

----

## 8.1.2 인자로 받은 함수 호출

```kotlin
// 간단한 고차함수 정의하기

fun twoAndThree(operation: (Int, Int) -> Int){
// 함수 타입인 파라미터 선언

    val result = operation(2,3) 
    // 함수 타입인 파라미터를 호출

    println(result)
}

twoAndThree{ a,b -> a+b }
// 5

twoAndThree{ a,b -> a*b }
// 6
```

인자로 받은 함수를 호출하는 구문은 일반 함수를 호출하는 구문과 같다.

함수 이름 뒤에 괄호를 붙이고 괄호 안에 인자를 콤마(,)로 구분해 넣는 것.

> `fun String.filter(predicate: (Char) -> Boolean): String`
>> `String`: 수신 객체 타입  
>> `predicate`: 파라미터 이름   
>> `(Char)`: 파라미터로 받는 함수의 파라미터 타입  
>> `Boolean`: 파라미터로 받는 함수의 반환 타입  
>> `(Char) -> Boolean)`: 파라미터 함수 타입  

```kotlin
fun String.filter(predicate: (Char) -> Boolean): String{
    val sb = StringBuilder()

    for(index in 0 until length){
        val element = get(index)

        if(predicate(element)) sb.append(element)
        // predicate 로 전달받은 함수를 호출시킴.
    }

    return sb.toString()
}

println("ab3c".filter{ it in 'a'..'z' })
// 람다를 predicate 로 전달
// abc
```


## 8.1.3 자바에서 코틀린 함수 타입 사용

코틀린 표준 라이브러리는 함수의 인자 개수에 따라  
`Function0<R>` (인자가 없는 함수), `Function1<P1, R>` (인자가 하나인 함수) 등의 인터페이스를 제공한다.  

각 인터페이스에는 `invoke`메소드 정의가 들어 있고, `invoke`를 호출하면 함수를 실행할 수 있다.

함수 타입의 변수는 _FunctionN_ 인터페이스를 구현하는 객체를 저장하며,  
그 클래스의 invoke메소드 본문에 람다의 본문이 들어간다.

---
### 자바 8 에서 함수 타입 사용하는 함수 호출
자바 8 람다를 넘기면 자동으로 함수 타입의 값으로 변환된다.
```kotlin
// kotlin
fun process(f: (Int) -> Int){
    println(42)
}
```
```java
// java
process(number -> number + 1);
// 43
```
---

### 자바 8 이전
필요한 _FunctionN_ 인터페이스의 invoke 메소드를 구현하는 익명 클래스를 넘기면 된다.
```java
// java
process(
    new Function1<Integer, Integer>(){
        @Override
        public Integer invoke(Integer n){
            System.out.println(n);
            return n+1;
        }
    });

// 43
```
---

### 자바에서 코틀린의 람다를 인자로 받는 확장 함수 호출

```java
// java

List<String> strings = new ArrayList();
strings.add("42");
CollectionsKt.forEach(strings, s -> {
// strings는 확장 함수의 수신 객체
// 코틀린 표준 라이브러리에서 가져온 함수를
// 자바 코드에서 호출할 수 있다.
    System.out.println(s);
    return Unit.INSTANCE;
    // Unit 타입의 값을 명시적으로 반환해야 함.
    // (String) -> Unit 처럼
    // 반환 타입이 Unit인 경우
    // void를 반환하는 자바 람다를 넘길 수 없디. 
});

```
---

## 8.1.4 디폴트 값을 지정한 함수 타입 파라미터나 널이 될 수 있는 함수 타입 파라미터

파라미터를 함수 타입으로 선언할 때 디폴트 값 가능.

```kotlin
// 하드 코딩을 통해 toString 사용 관례를 따라는 joinToString
fun <T> Collection<T>.joinToString(
    sep: String = " ",
    pre: String = "",
    pos: String = ""
): String{
    val result = StringBuilder(pre)

    for((idx, elem) in this.withIndex()){
        if(index > 0) result.append(sep)

        result.append(elem)
        // 기본 toString()을 통해 객체를 문자열로 변환

    }
    result.append(pos)

    return result.toString()
}

```

위 코드는 컬렉션의 각 원소를 문자열로 변환하는 방법을 제어할 수 없다는 큰 문제점을 갖고 있음.

StringBuilder.append(o: Any?) 사용시 항상 객체를 toString 메소드를 통해 문자열로 바꾼다.   
원소를 문자열로 바꾸는 방법을 람다로 전달하면 내가 원하는 방식으로 문자열 변환이 가능하지만,  
매번 joinToString 호출 시 람다를 인자로 넘겨야 하는데 더 불편하다.

함수 타입의 파라미터에 대한 디폴트 값으로 문자열을 바꾸는 방법에 대한 람다식을 지정하면 문제 해결.

```kotlin
// 함수 타입의 파라미터에 대한 디폴트 값 지정하기
fun <T> Collection<T>.joinToString(
    sep: String = " ",
    pre: String = "",
    pos: String = "",
    transform: (T) -> String = { it.toString() }
    // 함수 타입 파라미터를 선언하면서
    // 람다를 디폴트 값으로 지정
): String{
    val result = StringBuilder(pre)

    for((idx, elem) in this.withIndex()){
        if(index > 0) result.append(sep)

        result.append(transform(elem))
        // transform 파라미터로 받은 함수를 호출함
    }
    result.append(pos)

    return result.toString()
}

val letters = listOf("A", "B")

// 1. 디폴트 변환 함수 사용
println(letters.joinToString())
// A B

// 2. 람다를 인자로 전달
println(letters.joinToString{ it.toLowerCase() })
// a b

// 3. 이름 붙은 인자 구문을 사용해 람다를 표현하는 여러 인자를 전달
println(letters.joinToString(
    sep = "!", pos = "!",
    transform = { it.toLowerCase() }
))
// a!b!
```

널이 될 수 있는 함수 타입으로 함수를 받으면 함수 직접 호출 불가.  
널 여부를 명시적으로 검사하면 됨.
```kotlin
fun foo( callback: (() -> Unit)? ){
    //...
    if(callback != null){
        callback()
    }
}
```

함수 타입이 invoke를 구현하는 인터페이스이기 때문에  
invoke도 안전 호출 구문으로 `callback?.invoke()`처럼 호출 가능.

```kotlin
fun <T> Collection<T>.joinToString(
    sep: String = " ",
    pre: String = "",
    pos: String = "",
    transform: ((T) -> String)? = null
    // 널이 될 수 있는 함수 타입 파라미터
): String{
    val result = StringBuilder(pre)

    for((idx, elem) in this.withIndex()){
        if(index > 0) result.append(sep)

        val str = transform?.invoke(elem) ?: elem.toString()
        // ?: 로 람다를 인자로 받지 않은 경우 처리

        result.append(transform(elem))
    }
    result.append(pos)
    return result.toString()
}
```

## 8.1.5 함수를 함수에서 반환

```kotlin
// 함수를 반환하는 함수 정의하기

enum class Delivery{ STANDARD, EXPEDITED }

class Order(val itemCount: Int)

fun getShippingCost(delivery: Delivery): 
    (Order) -> Double{ // 함수를 반환하는 함수 선언
        if(delivery == Delivery.EXPEDITED){
            return {order -> 6 + 2.1 * order.itemCount}
        }
        return {order -> 1.2 * order.itemCount}
    }

val calculator = ... // 반환받은 함수를 변수에 저장
getShippingCost(Delivery.EXPEDITED)

println("${calculator(Order(3))}") // 반환받은 함수 호출
// 12.3

```

함수를 반환하려면 return 식에 람다나 멤버 참조나 함수 타입의 값을 계산하는 식 등을 넣으면 됨.


## 8.1.6 람다를 활용한 중복 제거

```kotlin
// class SiteVisit 은 path, duration, os를 멤버로 가짐
// enum class OS
// log = listOf(SiteVisit(,,),...)

// 사이트 방문 데이터를 하드 코딩한 필터를 사용해 분석

val averageWindow = log
    .filter{ it.os == OS.WINDOWS }
    .map(SiteVisit::duration)
    .average()

println(average)
// 23.0
```
```kotlin
// 확장 함수로 정의해 가독성 높임
// 중복 코드를 별도 함수로 추출
fun List<SiteVisit>.averageDurationFor(os: OS) 
    = filter{ it.os == OS.WINDOWS }
    .map(SiteVisit::duration)
    .average()

println(log.averageDurationFor(OS.WINDOWS))
// 23.0
```

```kotlin
// 하드코딩한 필터를 사용해 방문 데이터 분석하기
val averageMobile = log
    .filter{ it.os in setOf(OS.IOS, OS.ANDROID) }
    .map(SiteVisit::duration)
    .average()
```
함수 타입을 사용하면 필요한 조건을 파라미터로 뽑아낼 수 있다.

```kotlin
// 고차 함수를 사용해 중복 제거하기
fun List<SiteVisit>.averageDurationFor
    (predicate: (SiteVisit) -> Boolean) 
    = filter(predicate)
    .map(SiteVisit::duration)
    .average()


println(log.averageDurationFor{it.os in setOf(OS.IOS, OS.ANDROID)})
// 12.15
```

디자인 패턴 중 Strategy 패턴은  
동작을 정의하는 인터페이스를 만들어서  
이를 구현하는 실제 클래스를 만들고 캡슐화해서  
변화에 유연하게 적용되어질 수 있도록 하는 것이다.  
람다나 함수 타입을 사용한다면  
전략(동작) 자체를 파라미터로 넘김으로써 단순화 가능함.  

```java
// 자바8 람다를 사용한 전략 패턴 

// pikachu.setMoveStrategy(new WalkingStrategy()); 
// pigeon.setMoveStrategy(new FlyingStrategy());

pikachu.setMoveStrategy(() -> System.out.println("걸어서 이동 !")); 
pigeon.setMoveStrategy(() -> System.out.println("날아서 이동 !")); 

```

