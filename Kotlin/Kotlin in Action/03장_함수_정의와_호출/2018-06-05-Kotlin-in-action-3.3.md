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



