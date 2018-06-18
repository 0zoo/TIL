# 4장. 클래스, 객체, 인터페이스

# 4.1 클래스 계층 정의

이번 절에서는 클래스 계층 정의하는 방법과 가시성/접근 변경자에 대해 살펴본다. 
또한 클래스의 상속을 제한하는 _sealed_ 변경자에 대해 공부한다. 

## 4.1.1 코틀린 인터페이스

코틀린의 인터페이스는 자바 8 인터페이스와 비슷하다.  
코틀린 인터페이스 안에는 구현이 있는 메소드도 정의할 수 있다.
(자바 8의 디폴트 메소드와 비슷)  
단, 인터페이스에는 아무런 상태(필드)도 들어갈 수 없다.

```kotlin
// 인터페이스 선언
interface Clickable{
    fun click()
}
```

```kotlin
// 인터페이스 구현
class Button : Clickable{
    override fun click() = println("I was clicked")
}

Button().click() 
// I was clicked
```

자바에서는 extends와 implements 키워드를 사용하지만, 코틀린에서는 콜론(:)을 사용하여 확장과 인터페이스 구현을 모두 처리한다. 자바와 마찬가지로 인터페이스 구현은 여러개, 확장은 1개만 가능하다.   
코틀린에서는 _override_ **변경자**를 꼭 사용해야 한다.  
인터페이스 메소드도 디폴트 구현을 제공할 수 있다. 그냥 메소드 본문을 메소드 시그니처 뒤에 추가하면 된다. 

```kotlin
interface Clickable{
    fun click()
    fun showOff() = println("clickable!") // 디폴트 구현
}
```

```kotlin
//동일한 메소드를 구현하는 다른 인터페이스 정의
interface Focusable{
    fun setFocus(b: Boolean) = println("...")
    fun showOff() = println("__")
}
```
만약 두개의 인터페이스에 디폴트 구현이 있는 _showOff()_ 가 존재하고, 한 클래스에서 두 인터페이스를 구현한다면 어떻게 될까?  
-> 만약 _override fun showOff()_ 를 통해 명시적으로 새로운 구현을 해주지 않는다면 컴파일 오류가 발생한다.  
코틀린 컴파일러는 두 메소드를 아우르는 구현을 하위 클래스에 직접 구현하게 강제한다.

```kotlin
class Button: Clickable, Focusable{
    override fun click() = println("...")
    override fun showOff() {
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }
}
```
### 자바에서 코틀린의 메소드가 있는 인터페이스 구현하기
코틀린은 자바 6과 호환되게 설계됬다. 따라서 인터페이스의 디폴트 메소드(자바 8)를 자원하지 않음.   
-> 코틀린은 디폴트 메소드가 있는 인터페이스를 일반 인터페이스와 디폴트 메소드 구현이 정적 메소드로 들어있는 클래스를 조합해 구현. (인터페이스에는 메소드 선언만 들어가며, 인터패이스와 함께 생성되는 클래스에는 모든 디폴트 메소드 구현이 정적 메소드로 들어간다. )   
-> 즉, 자바에서는 코틀린의 디폹트 메소드 구현에 의존할 수 없다.  

## 4.1.2 open, final abstract 변경자: 기본적으로 final

자바에서는 final 키워드로 상속을 금지한 경우를 제외하고는 모두 상속이 가능하다. -> 문제점 발생.

- **취약한 기반 클래스**(fragile base class) 문제 

[Effective Java]에서는 이러한 문제점의 해결 방법을 이렇게 제시했다.  
`"상속을 위한 설계와 문서를 갖추거나, 그럴 수 없다면 상속을 금지하라"`  

코틀린의 클래스와 메소드는 기본적으로 _final_ 이다.  
-> 어떤 클래스의 상속을 허용하려면 **open** 변경자를 붙여햐 한다. 오버라이드를 허용하고 싶은 메소드나 프로퍼티 앞에도 open 변경자를 붙여햐 한다.

```kotlin
// 열린 메소드를 포함하는 열린 클래스 정의하기
open class RichButton: Clickable{
    fun disable() {} // 오버라이드 x

    open fun animate() {} // 오버라이드 o

    override fun click() {} // 오버라이드 o
    // 오버라이드한 메소드는 기본적으로 열려있음
    // 앞에 final을 명시하면 하위 클래스 오버라이드 불가
    // final override fun click() {} 
}
```

### 열린 클래스와 스마트 캐스트
기본 상태를 final로 했을 경우에 얻을 수 있는 가장 큰 이익은 **스마트 캐스트가 가능하다는 점**이다.  
스마트 캐스트는 타입 검사 뒤에 _변경될 수 없는_ 변수에만 적용 가능하다.  
클래스 프로퍼티의 경우 이는 val이면서 커스텀 접근자가 없는 경우에만 가능. 이는 프로퍼티가 final 이어야만 한다는 뜻하기도 한다.  


코틀린에서도 클래스를 **abstract**로 선언할 수 있다.  
abstract로 선언한 클래스는 인스턴스화할 수 없다.  
추상 멤버는 항상 열려있어 open을 붙여줄 필요 없다.

```kotlin
abstract class Animated{ 
    abstract fun animate() // 함수에 구현부 없음. 하위 클래스에서 반드시 오버라이드 해야 함.

    open fun stopAnimating(){} // open으로 오버라이드 허용

    fun animateTwice(){} // 기본적으로 final
}
```

- 인터페이스의 멤버의 경우 final, open, abstract를 사용하지 않는다.  
인터페이스의 멤버는 항상 열려있으며 final로 변경 불가.
- 인터페이스 멤버에게 본문이 없으면 abstract 키워드를 붙여주지 않아도 자동으로 추상 멤버가 됨.


#### 표 4.1 클래스 내에서 상속 제어 변경자의 의미
변경자 | 변경자가 붙은 멤버 | 설명
--- | --- | ---
**final** | 오버라이드 가능 | 클래스 멤버의 기본 변경자 
**open** | 오버라이드 가능 | 반드시 open을 명시해야 오버라이드 가능
**abstract** | 반드시 오버라이드 해야 함 | 추상 클래스의 멤버에만 붙일 수 있음. 구현부 없음.
**override** | 오버라이드하는 중 | 기본적으로 열려있음. final로 오버라이드 금지 가능.


## 4.1.3 가시성 변경자: 기본적으로 공개

- 가시성 변경자(visibility modifier) : 클래스 외부 접근 제어


#### 표 4.2 코틀린의 가시성 변경자
변경자 | 클래스 멤버 | 최상위 선언
--- | --- | ---
**public** | 모든 곳 | 모든 곳 
**internal** | 같은 모듈 안에서만 | 같은 모듈 안에서만
**protected** | 하위 클래스 안에서만 | (최상위 선언에 적용 불가)
**private** | 같은 클래스 안에서만 | 같은 파일 안에서만


* 코틀린의 기본 가시성은 public.  
자바의 기본 가시성인 package-private는 코틀린에 없다. 코틀린은 패키지를 네임스페이스를 관리하기 위한 용도로만 사용한다.
* **internal** : 모듈 내부에서만 볼 수 있다는 뜻. 
    * module : 한 번에 한꺼번에 컴파일되는 파일
    * 모듈 내부 가시성은 진정한 캡슐화를 제공한다는 장점.  
    자바에서는 패키지가 같은 클래스를 선언하기만 하면 어떤 프로젝트의 외부에 있는 코드라도 패키지 내부에 있는 패키지 전용 선언에 쉽게 접근할 수 있다. 그래서 모듈의 캡슐화가 쉽게 깨진다. 
* 코틀린에서는 최상위 선언에 대해 private 가시성을 허용한다. 해당 선언이 들어있는 파일에서만 사용 가능. 자세한 구현 사항을 외부에 감추고 싶을때 유용.

```kotlin
// 가시성 규칙을 위반하는 giveSpeech 함수
internal open class TalkativeButton : Focusable {
    private fun yell() = println("Hey!")
    protected fun whisper() = println("Let's talk")
}

fun TalkativeButton.giveSpeech() {
// 오류. public 멤버가 자산의 internal 수신 타입인 TalkativeButton을 노출함.
    yell() // 접근 불가. 
    whisper() // 접근 불가. 
}
```
1. 코틀린은 public함수인 giveSpeech안에서 가시성이 더 낮은 internal 타입인 TalkativeButton을 참조하지 못하도록 한다.  
오류 해결 방법: giveSpeech를 internal로 바꾸던가 TalkativeButton을 public으로 바꾼다.
2. 클래스를 확장한 **함수**는 상위 클래스의 private와 protected멤버에 접근할 수 없다는 사실을 기억하자. 자바와 달리 코틀린에서는 같은 패키지 안에서 protected 멤버에 접근할 수 없음을 주의하자. 

### 코틀린의 가시성 변경자와 자바
자바에서는 클래스를 private로 만들 수 없으므로 내부적으로 코틀린은 private 클래스를 패키지-전용 클래스로 컴파일한다.  
자바에는 internal에 딱 맞는 가시성이 없다. 바이트 코드상애서 internal은 public이 된다.  

이런 차이점이 존재하기 때문에 코틀린에서는 접근할 수 없는 대상을 자바에서 접근할 수 있는 경우가 생긴다. 예를 들어 다른 모듈에 정의된 internal 클래스나 internal 최상위 선언을 모듈 외부의 자바 코드에서 접근할 수 있다. 또한 코틀린에서 protected로 정의한 멤버를 코틀린 클래스와 같은 패키지에 속한 자바 코드에서 접근할 수 있다.(자바에서 같은 패키지 안의 자바 protected 멤버에 접근하는 경우와 같다.)  

코틀린 컴파일러는 internal 멤버의 이름을 (보기 나쁘게) 바꾼다.  
- 이유 1: 모듈 밖에서 상속한 경우 그 하위 클래스 내부의 메소드 이름이 우연히 상위 클래스의 internal 메소드와 같아져서 내부 메소드를 오버라이드 하는 경우를 방지하기 위해서.  
- 이유 2: 실수로 internal 클래스를 모듈 외부에서 사용하는 일을 막기 위해서.

## 4.1.4 내부 클래스와 중첩된 클래스: 기본적으로 중첩 클래스

중첩 클래스를 사용하면 도우미 클래스를 캡슐화하거나 코드 정의를 사용하는 곳 가까이에 두고 싶을 때 유용하다. 

자바와의 차이는 코틀린은 명시적으로 요청하지 않는 한 바깥쪽 클래스 인스턴스에 대한 접근 권한이 없다는 점이다.

```kotlin
// 직렬화 할 수 있는 상태가 있는 View 선언
interface State: Seriallizable
interface View{
    fun getCurrentState() : State
    fun restoreState(state: State) {}
}
```

```java
// 자바에서 내부 클래스를 사용해 View 구현하기
/* java */
public class Button implements View{
    
    @Override
    public State getCurrentState(){
        // 실제로는 ButtonState에 필요한 모든 정보를 넣어야 한다.
        return new ButtonState();
    }

    @Override
    public void restoreState(State state){ /*..*/ } 

    public class ButtonState implements State{ /*..*/ }
} 

// 이 예제는 Button을 직렬화할 수 없다는 예외를 발생한다.
// ButtonState 클래스는 자동으로 Button 묵시적 참조.
// Button이 참조 불가하기 때문에 ButtonState 직렬화가 불가능한 것.
// ButtonState를 static으로 선언하면 묵시적 참조가 사라진다.
```

#### 표 4.3 자바와 코틀린의 중첩 클래스와 내부 클래스의 관계
B 안에 정의된 A | 자바 | 코틀린
--- | --- | ---
**중첩 클래스** | static class A | class A 
**내부 클래스** | class A | inner class A 


```kotlin
// 중첩 클래스를 사용해 코틀린에서 View 구현하기
Class Button: View{
    
    override fun getCurrentState(): State = ButtonState()

    override fun restoreState(state: State) { /*..*/ }

    class ButtonState : State { /*..*/ } 
    // 자바의 정적 중첩 클래스와 대응함
}
```

```kotlin
class Outer{
    inner class Inner{
        fun getOuterReference(): Outer = this@Outer
    }
}
//내부 클래스에서 바깥쪽 클래스 Outer에 접근하려면 this@Outer라고 써야 한다.
```

## 4.1.5 봉인된 클래스: 클래스 계층 정의 시 계층 확장 제한

```kotlin
interface Expr
class Num(val value: Int): Expr
class Sum(val left: Expr, val right: Expr): Expr

fun eval(e: Expr): Int =
    when(e){
        is Num -> e.value
        is Sum -> eval(e.right) + eval(e.left)
        else -> throws IllegalArgumentException("..")
    }
```
코틀린 컴파일러는 when으로 Expr의 타입을 검사할 때 else로 디폴트 분기를 강제한다.  
하지만 이런 디폴트 분기의 강제는 많은 문제점을 발생시킨다.  

코틀린은 이에 대한 해법으로 **sealed** 클래스를 제공한다.  
상위 클래스에 sealed 변경자를 붙이면 하위 클래스의 정의를 제한할 수 있다.  
- sealed 클래스의 하위 클래스를 정의할 때는 반드시 상위 클래스 안에 중첩시켜야 한다. (봉인된 클래스는 클래스 외부에 자신을 상속한 클래스를 둘 수 없다.)
- sealed로 표시된 클래스는 자동으로 open이다.

```kotlin
sealed class Expr{ //기반 클래스를 sealed로 봉인한다.
    class Num(val value: Int): Expr
    class Sum(val left: Expr, val right: Expr): Expr
    //기반 클래스의 모든 하위 클래스를 중첩 클래스로 나열한다.
}
fun eval(e: Expr): Int =
    when(e){
    // when식이 모든 하위 클래스를 검사하므로 디폴트 분기 없어도 됨.
        is Expr.Num -> e.value
        is Expr.Sum -> eval(e.right) + eval(e.left)
    }
```

when에 디폴트 분기가 없는데 봉인된 클래스에 새로운 하위 클래스를 추가하면 기존의 when식은 컴파일되지 않는다.  

내부적으로 Expr 클래스는 private 생성자를 가진다. 
- sealed 인터페이스를 정의할 수는 없다.  
이유: 봉인된 인터페이스를 만들 수 있다면 그 인터페이스를 자바쪽에서 구현하지 못하게 막을 수 있는 수단이 코틀린 컴파일러에 없기 때문이다.


[노트] 
- 코틀린 1.0:
    - 모든 하위 클래스는 중첩 클래스여야 한다.
    - 데이터 클래스로 봉인된 클래스를 상속할 수 없다.
- 코틀린 1.1:  
    - 봉인된 클래스와 같은 파일이라면 하위 클래스의 생성이 가능하다.
    - 데이터 클래스로 봉인된 클래스를 상속할 수 있다.  

