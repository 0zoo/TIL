# 2.3 선택 표현과 처리: enum과 when
## 2.3.1 enum 클래스 정의
```kotlin
enum class Color{
    RED,ORANGE,YELLOW,GREEN,BLUE
}
```
enum은 자바 선언보다 코틀린 선언이 더 긴 흔치 않은 경우이다.
코틀린에서 enum은 _soft keyword_라고 불린다.

**enum**은 class앞에 있을 때는 특별한 의미를 지니지만, 다른 곳에서는 이름에 사용할 수 있다.

**class**는 키워드이기 때문에 이름을 사용할 수 없다. (예: clazz, aClass)

```kotlin
//프로퍼티와 메소드가 있는 enum 클래스 선언하기
enum class Color(
    val r: Int. val g: Int, val b: Int
){
    RED(255,0,0), ORANGE(255,165,0), YELLOW(255,255,0), GREEN(0,255,0);
    // 여기에는 반드시 세미콜론(;)을 붙여줘야 한다.

    fun rgb() = (r*256+g)*256+b
    // enum클래스 안에서 메소드 정의
} 
```
enum 클래스 안에 메소드를 정의하는 경우, 반드시 enum 상수 목록과 메소드 정의 사이에 세미콜론(;)을 넣어야 한다.

## 2.3.2 when으로 enum 클래스 다루기
> 자바의 switch에 해당하는 코틀린의 구성요소는 **when**이다.  


식이 본문인 함수에 when을 바로 사용할 수 있다.


```kotlin
fun getMnemonic(color: Color) = when(color){
    Color.RED -> "Richard"
    Color.ORANGE -> "Of"
    Color.YELLOW -> "York"
    Color.GREEN -> "Gave"
    Color.BLUE -> "Battle"
}

println(getMnemonic(Color.BLUE)) //Battle
```

- 자바와는 달리 각 분기의 끝에 break를 삽입하지 않아도 된다.

- 한 분기 안에서 여러 값을 매치 패턴으로 사용하려면 값 사이를 콤마(,)로 분리한다.


```kotlin
fun getWarmth(color: Color) = when(color){
    Color.RED, Color.ORANGE, Color.YELLOW -> "warm"
    Color.GREEN -> "neutral"
}
```

- 상수값을 import하면 Color.RED -> RED로 코드를 더 간결하게 만들 수 있다.


```kotlin
import ch02.colors.Color // 다른 패키지에서 정의한 Color클래스 import
import ch02.colors.Color.* // 짧은 이름으로 사용하기 위해 enum상수를 모두 임포트한다.

fun getWarmth(color: Color) = when(color){
    RED, ORANGE, YELLOW -> "warm"
    GREEN -> "neutral"
}
```

## 2.3.3 when과 임의의 객체를 함께 사용
코틀린의 when의 분기 조건은 임의의 객체를 허용한다. (자바는 상수만 허용)
```kotlin
//리스트 2.15
fun mix(c1: Color, c2: Color) = 
    //when은 인자로 받은 객체가 각 분기 조건에 있는 객체와 같은지 테스트한다. //equality(동등성)을 사용하여 각 객체를 비교함.
    when(setOf(c1,c2)){ 
        setOf(RED,YELLOW) -> ORANGE
        // setOf함수는 각 원소의 순서를 고려하지 않는 집합(Set)객체로 만들어준다. 
        setOf(YELLOW,BLUE) -> GREEN
        else -> throw Exception("Dirty Color")
    }

println(mix(BLUE,YELLOW)) // GREEN

```

## 2.3.4 인자 없는 when 사용
위 함수는 호출될 때마다 _setOf(c1,c2)_ 비교하기 위해 여러 set 인스턴스를 생성한다.

**인자가 없는 when식**을 이용하면 불필요한 객체의 생성을 막을 수 있다.

인자가 없는 when식을 사용하려면, 각 분기의 조건이 Boolean 결과를 계산하는 식이어야 한다.

```kotlin
fun mixOptimized(c1:Color, c2:Color) = when {
    (c1 == RED && c2 == YELLOW) || (c1 == YELLOW && c2 == RED)
    -> ORANGE
    (c1 == BLUE && c2 == YELLOW) || (c1 == YELLOW && c2 == BLUE)
    -> GREEN
    else -> throw Exception("Dirty Color")
}
```
위의 코드는 객체를 만들지 않는다는 장점이 있지만, 가독성이 떨어진다는 단점이 있다.

## 2.3.5 스마트 캐스트: 타입 검사와 타입 캐스트를 조합
식을 트리구조로 저장하는 경우를 생각해보자.
sum은 자식이 둘 있는 중간 노드이다.
num은 항상 leaf노드이다.

Expr은 아무 메소드도 선언하지 않고 여러 타입의 식 객체를 아우르는 공통 타입의 역할을 수행함.
```kotlin
interface Expr
class Num(val value: Int): Expr
//value라는 프로퍼티만 존재하는 단순한 클래스로, Expr 인터페이스를 구현한다.
class Sum(val left: Expr, val right: Expr) : Expr
//Expr 타입의 객체라면 어떤 것이나 Sum연산의 인자가 될 수 있다.
//left와 right는 Num이나 다른 Sum이 인자로 올 수 있다.
```
- 클래스가 구현하는 인터페이스를 지정하기 위해서 콜론(:) 뒤에 인터페이스 이름을 사용한다.

```
// (1+2)+4  -> Sum(Sum(Num(1), Num(2)), Num(4))

                Sum
         Sum         Num(4)
   Num(1)  Num(2) 

```
Expr 인터페이스에는 2가지 구현 클래스가 존재한다.
- 어떤 식이 Num이라면 그 값을 반환한다.
- 어떤 식이 Sum이라면 좌항과 우항의 값을 계산한 다음에 그 두 값을 합한 값을 반환한다.

자바 스타일 : 조건을 검사하기 위해 if문을 사용
-> 코틀린에서 if를 써서 자바 스타일로 함수를 작성해보자.

```kotlin
fun eval(e: Expr): Int{
    if (e is Num) {
        val n = e as Num
        //여기서 Num으로 타입을 변환하는데 이는 보일러 플레이트이다.
        return n.value
    }
    if (e is Sum) {
        return eval(e.right) + eval(e.left)
        //변수 e에 대해 스마트 캐스트를 사용한다.
    }
}
```
> 코틀린에서는 **is**를 사용해 변수 타입을 검사한다.

자바의 instanceof 와 비슷하다. 자바에서 instanceof로 타입을 검사하고, 명시적으로 캐스팅 한 후 멤버에 접근했었다. 이런 멤버 접근을 여러번 한다면, 변수에 따로 캐스팅 결과를 저장하고 사용해야 한다. 

> 코틀린에서는 컴파일러가 캐스팅을 해준다. 이를 **스마트 캐스트**라고 부른다.

```kotlin
if (e is Sum){
    // 스마트 캐스트를 통해 컴파일러는 e의 타입을 Sum으로 해석. 
    return eval(e.right) + eval(e.left)
}
```

- 스마트 캐스트는 is로 변수에 든 값의 타입을 검사한 다음에 그 값이 바뀔 수 없는 경우에만 작동한다.

위의 예제처럼 클래스의 프로퍼티에 접근하는 경우, 반드시 val이어야 하고 커스텀 접근자도 아니어야 한다.

> 원하는 타입으로 타입 캐스팅하려면 **as** 키워드를 사용한다.

```kotlin
val n = e as Num
```

## 2.3.6 리팩토링: if를 when으로 변경

코틀린의 if와 자바의 if는 어떻게 다를까? -> 코틀린의 if는 값 반환이 가능하다는 차이점.

if 식을 자바의 3항 연산자처럼 쓸 수 있다.   
자바: ` a > b ? a : b ` -> 코틀린: ` if ( a>b ) a else b `  

```kotlin
fun eval(e: Expr): Int = 
// if 분기에 식이 하나밖에 없다면 중괄호 생략 가능.
    if (e is Num) {
        e.value
    }else if (e is Sum) {
        eval(e.right) + eval(e.left)
    }else{
        throws IllegalArgumentException("Unknown expression")
    }

```

```kotlin
// if 중첩 대신 when 사용하기
fun eval(e: Expr): Int =
    when(e){
        is Num -> 
            e.value // 스마트 캐스트
        is Sum ->
            eval(e.right) + eval(e.left) // 스마트 캐스트
        else -> 
            throws IllegalArgumentException("Unknown expression")
    }

```

## 2.3.7 if와 when의 분기에서 블록 사용

> **블록의 마지막 식이 블록의 결과** 규칙은 블록이 값을 만들어내야 하는 모든 경우 성립.  
예) when, if, try - catch ..

단, 이 규칙은 함수에 대해서는 성립하지 않음. (2.2 참고)  
식이 본문인 함수는 블록을 본문으로 가질 수 없고, 내부에 return문이 반드시 있어야 한다.

```kotlin
fun evalWithLogging(e: Expr): Int =
    when(e){
        is Num -> {
            println("num: ${e.value}") // 로그
            
            e.value
        }
        is Sum ->{
            val left = evalWithLogging(e.left)
            val right = evalWithLogging(e.right)
            println("num: ${e.value}") // 로그
            
            left + right
        }
        else -> 
            throws IllegalArgumentException("Unknown expression")
    }

```
