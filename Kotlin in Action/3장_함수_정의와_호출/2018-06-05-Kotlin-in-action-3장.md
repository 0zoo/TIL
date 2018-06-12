# 3장. 함수 정의와 호출

- 컬렉션, 문자열, 정규식을 다루기 위한 함수
- 이름 붙인 인자, 디폴트 파라미터 값, 중위 호출 문법 사용
- 확장 함수와 확장 프로퍼티를 사용해 자바 라이브러리 사용
- 최상위 및 로컬 함수와 프로퍼티를 사용해 코드 구조화

# 3.1 코틀린에서 컬렉션 만들기

`val map = hashMapOf(1 to "one", 7 to "seven")` 
// to는 키워드가 아니라 일반 함수!

- 코틀린만의 컬렉션 기능은 없다. 기존 자바의 컬렉션과 같음.  
이유: 자바 코드와의 상호작용

하지만, 코틀린이 자바보다 더 많은 기능 제공한다.  
예)  
`val strings = listOf("first","second","third")`  
`println( strings.last() )`  
// third  
`val numbers = setOf(1,14,2)`  
`println( numbers.max() )`  
// 14

3장에서 _last_ 나 _max_ 와 같은 자바 클래스에 없는 메소드를 코틀린이 어디에 정의하는지 살펴본다.

# 3.2 함수를 호출하기 쉽게 만들기

자바 컬렉션에는 디폴트 _toString_ 제공.  
만약 기본 제공 출력 형식을 변경하고 싶다면? 서드파티 프로젝트를 추가하거나 직접 관련 로직을 구현해야 한다.  
코틀린에는 이런 요구 사항을 처리할 수 있는 함수가 표준 라이브러리에 존재한다.

```kotlin
// joinToString 함수 초기 구현
// 컬렉션의 원소를 StringBuilder에 붙인다. 
// 원소 사이에는 구분자 추가
// 맨 앞과 뒤에는 prefix와 postfix 추가
fun <T> joinToString(
    collection: Collection<T>,
    separator: String,
    prefix: String,
    postfix: String
): String {
    val result = StringBuilder(prefix)
    for((index,element) in collection.withIndex()){
        if((index>0)) 
            result.append(separator)
        result.append(element)
    }
    result.append(postfix)
    return result.toString()
}
```

```kotlin
val list = listOf(1,2,3)
println(joinToString(list,";","[","]"))
// [1;2;3]
```
잘 작동하지만, 매번 호출할 때 마다 인자 4개를 모두 전달하지 않을 수 있는 방법을 생각해보자.

## 3.2.1 이름 붙인 인자

해결하고 싶은 문제 : 함수 호출시 가독성  

```kotlin
joinToString(collection, separator=" ", prefix= " ", postfix = ".")
```
호출 시 인자 중 어느 하나라도 이름을 명시하고 나면 혼동을 막기 위해 그 뒤에 오는 모든 인자는 이름을 꼭 명시해야 한다.

* 경고 : 자바로 작성한 코드를 호출할 경우에는 이름 붙인 인자를 사용할 수 없다. (안드로이드 제공 함수도 마찬가지) 

## 3.2.2 디폴트 파라미터 값

자바의 일부 클래스에서는 오버로딩 메소드가 너무 많다는 문제점. 보일러 플레이트 발생.

코틀린에서는 함수 선언에서 파라미터의 디폴트 값을 지정할 수 있어 이런 문제점 상당수 해결. 

```kotlin
// 디폴트 파라미터 값을 사용한 joinToString 정의
fun <T> joinToString(
    collection: Collection<T>,
    separator: String= ",",
    prefix: String= "",
    postfix: String= ""
): String {
    //함수 구현 부분 위와 동일함
}
```

```kotlin
// prefix와 postfix 생략
println(joinToString(list,";"))
// 1;2;3
```

일반 호출 문법을 사용: 함수 선언 순서와 같아야 한다. 만약 일부를 생략했다면, 뒷부분의 인자들은 생략된다.

```kotlin
println(joinToString(list,postfix=";",prefix="#"))
// #1,2,3;
```
이름 붙은 인자 사용: 순서 상관 없음. 

### 디폴트 값과 자바
자바에는 디폴트 파라미터 값이라는 개념이 없다. 자바에서 코틀린 함수를 호출하는 경우에는 코틀린에서 디폴트 값을 설정했더라도 모든 인자를 명시해야 한다.  
*@JvmOverloads* 애노테이션을 추가하면, 코틀린 컴파일러가 자동으로 맨 마지막 파라미터로부터 파라미터를 하나씩 생략한 오버로딩한 자바 메소드를 추가해준다.  

## 3.2.3 정적인 유틸리티 클래스 없애기: 최상위 함수와 프로퍼티

자바에서는 함수를 클래스 안에 선언해야 한다. 무의미한 클래스 생성 문제점. (예: JDK의 *Collections* 클래스)

```java
// java
package strings;
public class JoinKt{
    public static String joinToString(...){...}
}
```

코틀린에서는 함수를 클래스 밖에 위치 시킬 수 있다. 이런 함수들은 여전히 그 파일의 패키지의 멤버 함수이기 때문에 다른 패키지에서 사용하려면 해당 패키지를 import해야 한다. 

```kotlin
//kotlin
package strings
fun joinToString(...): String{...}
```
위 함수가 어떻게 실행이 가능할까?  
    : 컴파일시 새로운 클래스 정의해줌.  
    코틀린 컴파일러가 생성하는 클래스의 이름은 최상위 함수가 들어있던 코틀린 소스 파일의 이름과 대응한다. (join.kt -> JoinKt.Class)

```java
// java에서 joinToString 호출
import strings.JoinKt;

public class Main{
    public static void main(String[] args){
        JoinKt.joinToString(list,",","","");
    }
}
```
### 파일에 대응하는 클래스의 이름 변경하기
최상위 함수가 포함되는 클래스의 이름을 바꾸고 싶다면,   
파일의 맨 앞 (패키지 이름 선언 이전에) *@JvmName* 애노테이션을 추가하라.  

```kotlin
@file: JvmName("StringFunctions")

package strings

fun joinToString(...): String{...}
```
```java
// java
import strings.StringFunctions;

public class Main{
    public static void main(String[] args){
        StringFunctions.joinToString(list,",","","");
    }
}
```

### 최상위 프로퍼티
함수와 마찬가지로 프로퍼티도 파일의 최상위 수준에 놓을 수 있다.  
흔히 사용하지는 않지만 가끔 유용하다. 
예를 들어, 어떤 연산을 수행한 횟수를 저장하는 var 프로퍼티를 예로 들 수 있다.
```kotlin
var opCount= 0 // 최상위 프로퍼티 선언
fun performOperation(){
    opCount++
}
fun reportOperationCount(){
    println("$opCount")
}
```
이런 최상위 프로퍼티의 값은 정적 필드에 저장된다.  
`val LINE_SEPARATOR = "\n" `  
과 같이 상수로 사용 가능하다.

최상위 프로퍼티도 접근자 메소드를 통해 자바 코드에 노출된다. (val은 게터, var은 게터와 세터)  
그런데, 상수의 경우에는 게터로 접근하는 것이 부자연스럽다.  
**const** 키워드를 사용하면 *public static final* 필드로 컴파일된다.  

> const val LINE_SEPARATOR = "\n"   

# 3.3 메소드를 다른 클래스에 추가: 확장 함수와 확장 프로퍼티

**확장 함수(extension function)** 는 기존 자바 API를 재작성 하지 않고도 코틀린이 제공하는 여러 편리한 기능을 사용할 수 있게 해준다.

- 확장 함수는 어떤 클래스의 멤버 메소드처럼 호출할 수 있지만, 그 클래스의 밖에 선언된 함수이다. 

```kotlin
package strings
// 문자열의 마지막 문자를 반환하는 메소드
fun String.lastChar(): Char = this.get(this.length -1)
```
추가하려는 함수 이름 앞에 그 함수가 확장할 클래스의 이름을 덧붙여줘야 한다.  
- **수신 객체 타입 (receiver type)** : 확장이 정의될 클래스의 타입   
- **수신 객체 (receiver object)** : 그 클래스에 속한 인스턴스 객체   

호출 구문은 일반 클래스 멤버 호출 구문과 같다.  
`println( "Kotlin".lastChar() )`  
// String : 수신 객체 타입  
// "Kotlin" : 수신 객체

이런 기능은 어떤 면에서 String 클래스에 원하는 메소드를 추가하는 것과 같다.
자바 클래스로 컴파일한 클래스 파일이 있는 한 그 클래스에 원하는 대로 확장을 추가할 수 있다.

```kotlin
package strings
fun String.lastChar(): Char = get(length -1)
// this 생략 가능
```

확장 함수의 내부에서 수신 객체의 메소드나 프로퍼티를 바로 사용할 수 있다.
하지만, **확장 함수가 캡슐화를 깨지는 않는다!**  
확장 함수 안에서는 클래스 내부에서만 사용할 수 있는 _private_ 나 _protected_ 멤버를 사용할 수 없다.

## 3.3.1 임포트와 확장 함수

확장 함수를 사용하기 위해서는 _import_ 해야만 한다.
만약 한 클래스에 같은 이름의 확장 함수가 둘 이상 있어 이름이 충돌하는 경우가 생긴다면, 코틀린에서는 개별 함수를 임포트 할 수 있다.

```kotlin
import strings.lastChar as last
val c = "Kotlin".last()
```
**as** 키워드를 사용하면 임포트한 클래스나 함수를 다른 이름으로 부를 수 있다.

확장 함수는 코틀린 문법상 반드시 짧은 이름을 써야 한다. 따라서 임포트 시 이름을 바꾸는 것이 확장 함수의 이름 충돌을 해결하는 유일한 방법이다.

## 3.3.2 자바에서 확장 함수 호출

내부적으로 확장 함수는 수신 객체를 첫 번째 인자로 받는 정적 메소드다. 그래서 확장 함수를 호출해도 다른 어댑터 객체나 실행 시점 부가 비용이 들지 않는다.

최상위 함수와 마찬가지로 확장 함수도 위치한 파일의 이름에 따라 자바 클래스의 이름이 정해진다.

확장 함수를 _StringUtil.kt_ 파일에 정의했다면,  
`char c = StringUtilKt.lastChar("Java")`

## 3.3.3 확장 함수로 유틸리티 함수 정의

```kotlin
//joinToString()을 확장으로 정의하기

fun <T> Collection<T>.joinToString(
// Collection<T> 에 대한 확장 함수를 선언
    separator: String= ",",
    prefix: String= "",
    postfix: String= ""
): String {
    val result = StringBuilder(prefix)
    for((index,element) in this.withIndex()){
    // this는 수신객체. Collection<T> 타입인 객체
        if((index>0)) 
            result.append(separator)
        result.append(element)
    }
    result.append(postfix)
    return result.toString()
}
```
확장 함수는 단지 정적 메소드 호출에 대한 문법적인 편의일 뿐이다.  
더 구체적인 타입을 수신 객체 타입으로 지정할 수도 있다. 
```kotlin
fun Collection<String>.join(
    separator: String= ",",
    prefix: String= "",
    postfix: String= ""
)= joinToString(separator, prefix, postfix)

println( listOf("one", "two", "three").join(" "))
// one two three

//listOf(1,2,3).join(" ")
// Type mismatch 에러

```

확장 함수가 정적 메소드와 같은 특징을 가지므로, 확장 함수를 하위 클래스에서 오버라이드할 수는 없다.

## 3.3.4 확장 함수는 오버라이드할 수 없다

```kotlin
open class View{
    open fun click() = println("View Clicked")
}
class Button: View(){ // Button이 View를 확장
    override fun click() = println("Button Clicked")
}

val view: View = Button()
view.click() // Button clicked
// view에 저장된 값의 실제 타입에 따라 호출될 메소드가 결정된다.
```
Button이 View의 하위 타입이기 떄문에 View타입 변수를 선언해도 Button타입 변수를 그 변수에 대입할 수 있다. View 타입 변수에 대해 click과 갗은 일반 메소드를 호출했는데, click을 Button 클래스가 오버라이드 했다면 실제로는 Button이 오버라이드한 click이 호출된다. 


* 동적 디스패치(dynamic dispatch):   
실행 시점에 객체 타입에 따라 동적으로 호출될 대상 메소드를 결정하는 방식. 
* 정적 디스패치(static dispatch):  
컴파일 시점에 알려진 변수 타입에 따라 정해진 메소드를 호출하는 방식 

참고로 프로그래밍 언어 용어에서 '정적'은 컴파일 시점을 의미하고, '동적'은 실행 시점을 의미한다.


## 3.3.5 확장 프로퍼티



# 3.4 컬렉션 처리: 가변 길이 인자, 중위 함수 호출, 라이브러리 지원

## 3.4.1 자바 컬렉션 API 확장

## 3.4.2 가변 인자 함수: 인자의 개수가 달라질 수 있는 함수 정의

## 3.4.3 값의 쌍 다루기: 중위 호출과 구조 분해 선언



# 3.5 문자열과 정규식 다루기

## 3.5.1 문자열 나누기

## 3.5.2 정규식과 3중 따옴표로 묶은 문자열

## 3.5.3 여러 줄 3중 따옴표 문자열



# 3.6 코드 다듬기: 로컬 함수와 확장



# 3.7 요약

