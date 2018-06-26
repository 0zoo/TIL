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
기존 기능이 필요하다면 데코레이터의 메소드가 기존 클래스의 메소드에게 요청을 **전달(forwarding)** 한다.

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
class CountingSet<T>(val innerSet: MutableCollection<T> 
= HashSet<T>()) : MutableCollection<T> by innerSet{
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
