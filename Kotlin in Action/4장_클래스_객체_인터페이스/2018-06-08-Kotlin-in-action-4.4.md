# 4.4 object 키워드: 클래스 선언과 인스턴스 생성

object 키워드는 어떤 상황에서 쓸까?

- **객체 선언**은 싱글턴을 정의하는 방법 중 하나이다.
- **동반 객체**(companion object)는 인스턴스 메소드는 아니지만 어떤 클래스와 관련 있는 메소드와 팩토리 메소드를 담을 때 쓰인다. 동반 객체 메소드에 접근할 때는 동반 객체가 포함된 클래스의 이름을 사용할 수 있다.
- 객체 식은 자바의 **무명 내부 클래스**(anonymous inner class) 대신 쓰인다.

## 4.4.1 객체 선언: 싱글턴을 쉽게 만들기
인스턴스를 하나만 생성하고 싶을때 -> 싱글턴 패턴 (singleton pattern)  

코틀린은 **객체 선언** 기능을 통해 싱글턴을 언어에서 기본 지원한다.  
객체 선언 : 클래스 선언 + 클래스에 속한 단일 인스턴스의 선언

```kotlin
// 회사의 급여 대장은 한 개만 필요하기 때문에 싱글턴 적용
object Payroll{
    val allEmployees = arrayListOf<Person>()
    fun calculateSalary(){
        for(person in allEmployees){
            //...
        }
    }
}
```

- 객체 선언은 **object** 키워드로 시작한다.
- 생성자는 객체 선언에 쓸 수 없다.  
코틀린의 싱글턴 객체는 객체 선언문이 있는 위치에서 즉시 생성된다.
- 변수처럼 객체 선언한 이름에 .을 붙이면 메소드와 프로퍼티에 접근 가능하다.    
예) `Payroll.calculateSalary()`
- 객체 선언도 클래스나 인스턴스를 상속할 수 있다.  
(Comparator 인터페이스 구현시 유용)


```kotlin
// 객체 선언을 사용해 Comparator 구현하기
object FileComparator : Comparator<File>{
    override fun compare(f1: File, f2: File): Int{
        return f1.path.compareTo(f2.path, ignoreCase=true)
    }
}
```

- 클래스 안에서 객체 선언 가능. (외부 클래스의 갯수만큼 인스턴스 생기는 것이 아니라 딱 한 개만 생성됨)

```kotlin
// 중첩 객체를 사용해 Comparator 구현하기
data class Person(val name: String){
    object NameComparator : Comparator<Person>{
        override fun compare(p1: Person, p2: Person): Int{
            return p1.name.compareTo(p2.name)
        }
    }
}

```

### 싱글턴과 의존관계 주입
의존관계가 많지 않은 경우에는 싱글턴이나 객체 선언이 유용하지만, 반대의 경우에는 적합하지 않다. 이유: 객체 생성 제어 불가, 생성자 파라미터 지정 불가 -> 단위 테스트, 시스템 설정 변경시 문제  
따라서 이런 경우에는 의존관계 주입 프레임워크 (예: 구글 주스)와 코틀린 클래스를 함께 사용해야 한다.

### 코틀린 객체를 자바에서 사용하기
코틀린 객체 선언은 유일한 인스턴스에 대한 정적인 필드가 있는 자바 클래스 파일로 컴파일된다. 이때 인스턴스 필드의 이름은 항상 _INSTANCE_ 이다.  
자바 코드에서 코틀린 싱글턴 객체를 사용하려면 정적인 _INSTANCE_ 필드를 통하면 된다.
```java
// java
FileComparator.INSTANCE.compare(f1,f2);
```

## 4.4.2 동반 객체: 팩토리 메소드와 정적 멤버가 들어갈 장소

코틀린은 자바의 static을 지원하지 않는다. 그 대신 코틀린에서는 최상위 함수와 객체 선언을 활용한다.  

```kotlin
class A{
    companion object{ // 동반 객체 이름 따로 지정 x
        fun bar(){/*..*/}
    }
}

A.bar() // 동반 객체가 정의된 클래스의 이름을 사용해 접근

```
- 동반 객체는 바깥쪽 클래스의 모든 private 멤버에 접근 가능하다. (물론 private 생성자도) 

```kotlin
// 부 생성자가 여러개 있는 클래스 정의하기
class User{
    val nickname: String

    constructor(email: String){
        nickname = email.substringBefore('@')
    }
    constructor(facebookId: Int){
        nickname = getFacebookName(facebookId)
    }
}
```
```kotlin
//부 생성자를 팩토리 메소드로 대신하기
class User private constructor(val nickname: String){
// 주 생성자를 비공개로 만든다.
    companion object{
        fun newSubscribingUser(email: String) 
        = User(email.substringBefore('@'))

        fun newFacebookUser(id: Int)
        = User(getFacebookName(id))
    }
}

val facebookUser = User.newFacebookUser(4)
```

팩토리 메소드는 매우 유용하다. 
1. 목적에 따라 팩토리 메소드 이름을 정할 수 있다.  
2. 팩토리 메소드가 선언된 클래스의 하위 클래스 객체를 반환할 수 있다.
3. 생성할 필요가 없는 객체를 생성하지 않을 수 있다.

하지만, 클래스를 확장해야만 하는 경우에는 동반 객체 멤버를 하위 클래스에서 오버라이드가 불가능하므로 이때는 여러 생성자를 사용하는 것이 좋다.

## 4.4.3 동반 객체를 일반 객체처럼 사용

동반 객체에 이름 붙이기, 인터페이스 상속하기, 내부에 확장 함수와 프로퍼티 정의하는 것이 모두 가능하다.  

```kotlin
// 동반 객체에 이름 붙이기
class Person(val name: String){
    companion object Loader{
        fun fromJSON(..){...}
    }
}

person = Person.Loader.fromJSON(..)
person = Person.fromJSON(..)
// 두 방법 모두 가능하다.
```
특별히 이름을 지정하지 않으면 동반 객체 이름은 자동으로 _Companion_ 이 된다.
```java
// java
Person.Companion.fromJSON("...");
// 동반 객체에 이름을 붙였다면 Companion 자리에 그 이름이 들어감.
```

### 동반 객체에서 인터페이스 구현

인터페이스를 구현한 동반 객체를 참조할 때 바깥의 클래스의 이름을 바로 사용할 수 있다.
```kotlin
//동반 객체에서 인터페이스 구현하기
interface JSONFactory<T>{
    fun fromJSON(jsonText: String): T
}

class Person(val name: String){
    companion object: JSONFactory<Person>{
        override fun fromJSON(jsonText: String): Person = /*...*/
    }
}
```
동반 객체가 구현한 JSONFactory의 인스턴스를 넘길 때 Person 클래스의 이름을 사용.

### 동반 객체 확장

클래스에 동반 객체가 있다면 그 객체 안에 함수를 정의하여 클래스에 대해 호출할 수 있는 확장 함수를 만들 수 있다.

```kotlin
// 동반 객체에 대한 확장 함수 정의하기
// 비즈니스 모듈이 특정 데이터 타입에 의존하는 것을 원하지 않을 경우

// 비즈니스 로직 모듈
class Person(val firstName: String, val lastName: String){
    companion object{
    }
}

// 클라이언트/서버 통신 모듈
fun Person.Companion.fromJSON(json: String): Person{
    /*...*/
}

val p = Person.fromJSON(json)
```
실제로 fromJSON은 클래스 밖에서 정의한 확장 함수지만, 마치 동반 객체 안에서 함수를 정의한 것처럼 사용 가능하다.

여기서 동반 객체에 대한 확장 함수를 작성할 수 있으려면 원래 클래스에 동반 객체를 **꼭** 선언해야 한다는 점을 주의하자.

## 4.4.4 객체 식: 무명 내부 클래스를 다른 방식으로 작성

**무명 객체**를 정의할 때도 object 키워드를 사용한다.
```kotlin
window.addMouseListener{
    object : MouseAdapter(){ 
    //MouseAdapter를 확장하는 무명 객체 선언
        override fun mouseClicked(e: MouseEvent){
            //..
        }

        override fun mouseEntered(e: MouseEvent){
            //..
        }
    }
}

```

객체에 이름을 붙여야 한다면 변수에 무명 객체를 대입하면 된다.
```kotlin
val listener = object: MouseAdapter(){
    override fun mouseClicked(e: MouseEvent){}
    override fun mouseEntered(e: MouseEvent){}
}
```

- 코틀린의 무명 클래스는 여러 인터페이스를 구현하거나 클래스를 확장하면서 인터페이스를 구현할 수 있다.

- 무명 객체는 싱글턴이 아니다. 

- 그 식이 포함된 함수의 변수에 접근할 수 있다. (자바와 같음)

- 객체 식 안에서 그 변수의 값을 변경할 수 있다. (자바와 다름)

```kotlin
// 무명 객체 안에서 로컬 변수 사용하기

fun countClicks(window: Window){
    var clickCount = 0
    
    window.addMouseListener(object: MouseAdapter(){
        override fun mouseClicked(e: MouseEvent){
            clickCount++
        }
    })
}
```

