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

# 4.2 뻔하지 않은 생성자와 프로퍼티를 갖는 클래스 연산

코틀린의 생성자 
- 주(primary) 생성자 : 클래스 본문 밖에서 정의한다.
- 부(secondary) 생성자 : 클래스 본문 안에서 정의한다.

## 4.2.1 클래스 초기화: 주 생성자와 초기화 블록

중괄호{}가 없고 클래스 이름 뒤에 괄호로 둘러싸인 코드를 **주 생성자**라고 한다. 

주 생성자의 목적  
- 생성자 파라미터 지정  
- 생성자 파라미터에 의해 초기화되는 프로퍼티를 정의

`class User(val nickname: String)`

```kotlin
class User constructor(_nickname: String){
    val nickname: String
    
    init{ 
        nickname = _nickname
    }
}
```
- _constructor_ 키워드 : 주 생성자나 부 생성자 정의를 시작할 때 사용
- _init_ 키워드 : 초기화 블록

주 생성자는 별도의 코드를 포함시킬 수 없기 때문에 초기화 블록이 필요하다. 필요하다면 클래스 안에 여러개의 초기화 블록을 선언할 수 있다.

위의 예제와 같이 _를 통해 구분해줘도 되고, 자바처럼 this를 써도 된다. 

```kotlin
// 주 생성자 앞에 별다른 애노테이션이나 가시성 변경자가 없다면 
// constructor 키워드는 생략 가능하다. 
class User (_nickname: String){
    val nickname = _nickname
}
```
- 프로퍼티를 초기화하는 식이나 초기화 블록 안에서만 주 생성자의 파라미터에 접근 가능함을 유의하자.

`class User(val nickname: String)`
// 생성자 파라미터 이름 앞에 val을 추가하는 방식으로 정의와 초기화를 간략하게 쓸 수 있다.

`class User(val nickname: String = "Bob")` // 디폴트값 지정

만약 모든 생성자 파라미터가 디폴트 값을 갖고 있다면 컴파일러가 자동으로 파라미터가 없는 생성자를 만들어준다.

클래스의 인스턴스 생성시 new 키워드 없이 생성자 직접 호출  
`val tom = User("Tom")`

클래스에 기반 클래스가 있다면, 주 생성자에서 기반 클래스의 생성자를 호출해야 할 필요가 있다. 기반 클래스를 초기화하려면 기반 클래스 이름 뒤에 괄호를 치고 생성자 인자를 넘긴다. 

`open class User(val nickname: String){...}`  
`class TwitterUser(nickname: String): User(nickname) {...}`

별도로 생성자를 정의하지 않으면 컴파일러가 자동으로 디폴트 생성자를 만들어준다.  
`open class Button`

위의 Button 클래스를 상속한 하위 클래스는 반드시 Button 클래스의 생성자를 호출해야 한다.  
`class RadioButton: Button()`  
이 규칙으로 인해 기반 클래스의 이름 뒤에는 인자가 있는 괄호가 들어간다.  
인터페이스의 경우에는 생성자가 없기 때문에 상위 인터페이스의 이름 뒤에 괄호가 붙지 않는다.   
-> 이름 뒤에 괄호가 붙었는지 살펴보면 기반 클래스와 인터페이스를 쉽게 구분할 수 있다. 

`class Secretive private constructor() {}`  
생성자에 private을 붙이면 외부에서 인스턴스화 불가능.  
동반 객체 안에서 이런 비공개 생성자를 호출하면 좋다. 

### 비공개 생성자에 대한 대안
유틸리티 함수를 담아두는 역할만을 하는 클래스는 인스턴스화할 필요가 없고, 싱글턴인 클래스는 미리 정한 팩토리 메소드 등의 생성 방법을 통해서만 객체를 생성해야 한다.  
자바에서는 이런 경우 어쩔 수 없이 private 생성자를 사용하지만,    
코틀린은 그런 경우 정적 유틸리티 함수 대신 최상위 함수를 사용할 수 있고(3.2.3), 싱글턴을 사용하고 싶으면 객체를 선언하면 된다.(4.4.1) 

## 4.2.2 부 생성자: 상위 클래스를 다른 방식으로 초기화

- 팁: 인자에 대한 디폴트 값을 제공하기 위해 부 생성자를 여러개 만들지 말라. 대신 파라미터의 디폴트 값을 생성자 시그니처에 직접 명시하라. 

코틀린에서 생성자가 여러개 필요한 경우가 가끔 있음.  

```kotlin
open class View{
// 주 생성자를 선언하지 않고 부 생성자만 2개 생성하는 클래스
    constructor(ctx: Context){
        //...
    }
    constructor(ctx: Context, attr: AttributeSet){
        //...
    }
}

class MyButton: View{
    constructor(ctx: Context) : super(ctx){
        //...
    }
    constructor(ctx: Context, attr: AttributeSet : super(ctx,attr){
        //...
    }
}
```
**super**키워드를 통해 자신에 대응하는 상위 클래스 생성자를 호출하고 상위 클래스의 생성자에게 객체 생성을 위임한다. 

**this()**를 통해 클래스 자신의 다른 생성자를 호출할 수 있다.

```kotlin
class MyButton: View{
    constructor(ctx: Context) : this(ctx,MY_STYLE){
    // 이 클래스의 다른 생성자에게 위임한다.
        //...
    }
    constructor(ctx: Context, attr: AttributeSet : super(ctx,attr){
        //...
    }
}
```
- 클래스에 주 생성자가 없다면 모든 부 생성자는 반드시 상위 클래스를 초기화하거나 다른 생성자에게 생성을 위임해야 한다.
- 부 생성자가 필요한 주된 이유는 **자바 상호운용성** 이다.

## 4.2.3 인터페이스에 선언된 프로퍼티 구현

코틀린에서는 인터페이스에 추상 프로퍼티 선언을 넣을 수 있다. 
```kotlin
interface User{
    val nickname: String
}
```
이는 User 인터페이스를 구현하는 클래스가 nickname의 값을 얻을 수 있는 방법을 제공해야 한다는 뜻.

```kotlin
// 인터페이스의 프로퍼티 구현하기

class PrivateUser(override val nickname: String) : User
// 주 생성자 안에 프로퍼티 직접 선언. 
// 이 프로퍼티는 User의 추상 프로퍼티를 구현하기 때문에 
// override를 표시해야 한다.

class SubscribingUser(val email: String) : User {
    override val nickname: String
        get () = email.subscribingBefore('@')
}
// custom getter로 nickname 구현.
// 이 프로퍼티는 뒷받침하는 필드에 값을 저장하지 않고
// 매번 이메일 주소에서 닉네임을 계산해 반환한다.

class FacebookUser(val accountId: Int): User {
    override val nickname = getFacebookName(accountId)
}
// 초기화 식으로 nickname 초기화
```
SubscribingUser 와 FacebookUser의 초기화 방식에는 차이점이 있음을 주의하자.
SubscribingUser는 호출시 커스텀 게터를 이용하여 매번 계산하고, 
FacebookUser는 객체를 초기화 시 계산한 데이터를 backing field에 저장했다가 불러오는 방식을 활용한다.

인터페이스에는 추상 프로퍼티뿐 아니라 게터/세터가 있는 프로퍼티를 선언할 수 있다. (이런 게터/세터는 뒷받침 필드를 참조할 수 없다. 뒷받침 필드가 있다면 인터페이스에 상태를 추가하는 셈인데 인터페이스는 상태를 저장할 수 없다.)

```kotlin
// 추상 프로퍼티인 email과 커스텀 게터를 가진 nickname이 함께 있는 인터페이스
interface User {
    val email: String
    val nickname: String
        get() = email.subscribingBefore('@')
        // 프로퍼티에 backing field가 없다. 매번 계산      
}
// 이 인터페이스를 구현하는 하위 클래스는
// 추상 프로퍼티인 email을 반드시 오버라이드해야 한다.
// nickname은 오버라이드 하지 않고 상속 가능하다.
```

## 4.2.4 게터와 세터에서 뒷받침하는 필드에 접근

1. 값을 저장하는 프로퍼티
2. 커스텀 접근자에서 매번 값을 계산하는 프로퍼티

이 두가지 유형을 조합하여 어떤 값을 저장하되 그 값을 변경하거나 읽을 때마다 정해진 로직을 실행하는 유형의 프로퍼티를 만드는 방법을 살펴보자. 

값을 저장하는 동시에 로직을 실행할 수 있게 하려면 접근자 안에서 프로퍼티를 뒷받침하는 필드에 접근할 수 있어야 한다.

```kotlin
// 세터에서 뒷받침하는 필드 접근하기
class User(val name: String){
    var address: String = "unknown"
        set(value: String){
            println("""Address was changed for $name: "$field" -> "$value".""".trimIndent())
            //뒷받침 필드 값 읽기

            field = value //뒷받침 필드 값 변경
        }
}

val user = User("Alice")
user.address = "@@"
//Address was changed for Alice: 
//"unknown" -> "@@"
```
`user.address = "new value"`로 프로퍼티의 값을 바꾸고, 이 구문은 내부적으로 address의 세터를 호출한다.  

접근자의 본문에서는 **field**라는 뒷받침 필드에 접근 가능하다.   
게터에서는 field 값을 읽을 수만 있고,  
세터에서는 field 값을 읽거나 쓸 수 있다.

뒷받침 필드가 있는 프로퍼티와 그런 필드가 없는 프로퍼티에 어떤 차이가 있을까?  
컴파일러는 게터나 세터에서 field를 사용하는 프로퍼티에 대해 뒷받침하는 필드를 생성해준다. 다만 field를 사용하지 않는 커스텀 접근자 구현을 정의한다면 뒷받침하는 필드는 존재하지 않는다. (프로퍼티가 val인 경우에는 게터에 field가 없으면 안되지만, var인 경우에는 게터나 세터에 모두 field가 존재해야 한가.)

## 4.2.5 접근자의 가시성 변경

접근자의 가시성은 기본적으로는 프로퍼티의 가시성과 같다.
```kotlin
// 비공개 세터가 있는 프로퍼티 선언하기
class LengthCounter{
    var counter: Int = 0
        private set // 외부에서 값 변경 불가
    
    fun addWord(word: String){
        counter += word.length
    }
}
```

# 4.3 컴파일러가 생성한 메소드: 데이터 클래스와 클래스 위임

## 4.3.1 모든 클래스가 정의해야 하는 메소드 

자바와 마찬가지로 코틀린 클래스도 toString, equals, hashcode 등을 오버라이드할 수 있고, 코틀린은 이런 메소드 구현을 자동으로 생성해줄 수 있다.

```kotlin
class Client(val name: String, val postalCode: Int)
```
### 문자열 표현: toString()
기본 제공되는 문자열 표현은 `Client@5e9f23b4`같은 방식인데,  
이 기본 구현을 바꾸려면 toString메소드를 오버라이드해야 한다.
```kotlin
class Client(val name: String, val postalCode: Int){

    override fun toString() = "Client[$name,$postalCode]"

}
```
### 객체의 동등성: equals()
> 동등성 연산에 == 를 사용함.
코틀린에서는 == 연산자는 내부적으로 equals를 호출해서 객체를 비교한다.  
참조 비교를 위해서는 === 연산자를 사용할 수 있다.
```kotlin
class Client(val name: String, val postalCode: Int){

    override fun equals(other: Any?):Boolean{
    // Any는 모든 클래스의 최상위 클래스이다.
        if(other !is Client)
        // is는 타입을 검사해준다.
            return false
        return name == other.name && postalCode == other.postalCode
    }

}
```

### 해시 컨테이너: hashCode()
자바에서는 equals를 오버라이드할 때 반드시 hashCode도 오버라이드 해야 한다.  

예)   
`val processed = hashSetOf(Client("영주",1234))`  
`println(processed.contains(Client("영주",1234)))`
출력값은 false가 나온다.   
이유: HashSet은 원소를 비교할 때 먼저 객체의 해쉬 코드를 비교하고 같은 경우에만 실제 값을 비교하는데 Client는 hashCode 정의하지 않았기 때문.

JVM 언어에서 hashCode 규칙:
> "equals()가 true를 반환하는 두 객체는 반드시 같은 hashCode()를 반환해야 한다."

```kotlin
// Client에 hashCode 구현
class Client(val name: String, val postalCode: Int){

    override fun hashCode(): Int = name.hashCode()*31 + postalCode

}
```

## 4.3.2 데이터 클래스: 모든 클래스가 정의해야 하는 메소드 자동 생성 

**data** 변경자를 붙여주면 필요한 메소드(toString, equals, hashCode, ...)를 컴파일러가 자동으로 만들어준다. 

```kotlin
data class Client(val name: String, val postalCode: Int)
```

- 주 생성자 밖에 정의된 프로퍼티는 equals나 hashCode를 계산할 때 고려 대상이 아님을 유의하자.

### 데이터 클래스와 불변성: copy() 메소드
val인 프로퍼티를 사용해 불변 클래스로 만드는 것을 권장한다. (HashMap, 다중스레드 사용시 편리)

코틀린 컴파일러는 객체를 복사하면서 일부 프로퍼티를 바꿀 수 있게 해주는 **copy** 메소드를 제공한다. 복사본을 수정/삭제 해도 원본을 참조하는 다른 부분에는 영행을 끼치지 않는다. 
```kotlin
val lee = Client("이영주",1234)
println(lee.copy(postalCode=4000))
//출력결과: Client("이영주",4000)
```

## 4.3.3 클래스 위임: by 키워드 사용
대규모 객체 지향 시스템을 설계할 때 시스템을 취약하게 만드는 문제는 보통 **구현 상속**에 의해 발생한다. 하위 클래스가 상위 클래스의 메소드를 오버라이드하면 하위 클래스는 상위 클래스의 세부 구현 사항에 의존하게 된다.  
코틀린은 open을 통해서만 클래스의 확장이 가능하기 떄문에, 상위 클래스의 구현 사항을 변경할 경우에 좀 더 조심할 수 있다.

상속을 허용하지 않는 클래스에 새로운 동작을 추가해야 한다면 일반적으로 **데코레이터 패턴**을 사용한다.  
이 패턴의 핵심은 상속을 허용하지 않는 기존의 클래스 대신 사용할 수 있는 새로운 클래스(데코레이터)를 만들되  
데코레이터가 기존 클래스와 같은 인터페이스를 제공하게 만들고,  
기존 클래스를 데코레이터 내부에 필드로 유지한다.  
새로 정의해야 하는 기능을 데코레이터의 메소드에 정의한다.  
기존 기능이 필요하다면 데코레이터의 메소드가 기존 클래스의 메소드에게 요청을 **전달(forwarding)**한다.

이런 접근 방법의 단점은 준비 코드가 상당히 많다는 점.  

코틀린은 이런 위임을 **일급 시민 기능**으로 제공한다.  
인터페이스를 구현할 때 **by** 키워드를 통해 그 인터페이스의 구현을 다른 객체에 위임 중이라는 사실을 명시할 수 있다.

```kotlin
// Collection 인터페이스를 구현하면서 아무 동작도 변경하지 않는 데코레이터 생성
class DelegatingCollection<T> : Collection<T>{
    private val innerList = arrayListOf<T>()

    override val size: Int get() = innerList.size
    override fun isEmpty(): Boolean = innerList.isEmpty()
    override fun contains(element: T): Boolean = innerList.contains(element)
    override fun iterator(): Iterator<T> = innerList.iterator()
    override fun containsAll(elements: Collection<T>): Boolean = innerList.containsAll(elements)
}
```

```kotlin
// 위임을 사용해 재작성한 코드
class DelegatingCollection<T> (innerList: Collection<T> = ArrayList<T>()) : Collection<T> by innerList {}
// 컴파일러가 자동 생성한 코드의 구현은 위의 DelegatingCollection 구현과 비슷하다.
```
기존 클래스의 메소드에 위임하는 기본 구현으로 충분한 메소드는 따로 오버라이드할 필요가 없다. 

```kotlin
// 클래스 위임을 사용한 원소 추가 시도 횟수 기록하는 컬렉션 구현
class CountingSet<T>(val innerSet: MutableCollection<T> = HashSet<T>()) : MutableCollection<T> by innerSet{
// MutableCollection의 구현을 innerSet에게 위임한다.
    var objectsAdded = 0

    override fun add(element: T): Boolean{
        objectAdded++
        return innerSet.add(element)
    }

    override fun addAll(c: Collection<T>): Boolean {
        objectsAdded += c.size
        return innerSet.addAll(c)
    }

    //add 와 addAll 메소드는 위임하지 않고 새로운 구현을 제공한다.
}

val cset = CountingSet<Int>()
cset.addAll(listOf(1,1,2))
println("${cset.objectsAdded}")
// 3
println("${cset.size}")
// 2

```

CountingSet에 MutableCollection의 구현 방식에 대란 의존관계가 생기지 않는다는 점이 중요하다. 


# 4.4 object 키워드: 클래스 선언과 인스턴스 생성

## 4.4.1 객체 선언: 싱글턴을 쉽게 만들기

## 4.4.2 동반 객체: 팩토리 메소드와 정적 멤버가 들어갈 장소

## 4.4.3 동반 객체를 일반 객체처럼 사용

## 4.4.4 객체 식: 무명 내부 클래스를 다른 방식으로 작성

# 4.5 요약