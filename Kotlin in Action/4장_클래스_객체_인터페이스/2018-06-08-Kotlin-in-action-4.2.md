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
