# 5.1 람다 식과 멤버 참조
## 5.1.1 람다 소개: 코드 블록을 함수 인자로 넘기기
함수형 언어에서는 함수를 직접 다른 함수에 전달할 수 있다.
```java
// java에서의 무명 내부 클래스 
button.setOnclickListener(new OnclickListener(){
    @Override
    public void onClick(View v){
        //...
    }
}); 
```
코틀린에서는 자바8과 마찬가지로 람다를 쓸 수 있다.  
람다를 메소드가 하나뿐인 무명 객체 대신 사용할 수 있다.
```kotlin
//kotlin
button.setOnclickListener{/*...*/}
```

## 5.1.2 람다와 컬렉션

```kotlin
// 람다를 사용해 컬렉션 검색하기
val people = listOf(Person("A",10),Person("B",20))
println( people.maxBy{ it.age } )
```
모든 컬렉션은 _maxBy_ 함수 호출 가능.  

_{ it.age }_ 는 비교에 사용할 값을 돌려주는 함수이다.

이런 식으로 함수나 프로퍼티를 **반환하는 역할만** 수행하는 람다는 **멤버 참조**로 대치할 수 있다.
```kotlin
// 멤버 참조를 사용해 컬렉션 검색하기
people.maxBy(Person::age)
```

## 5.1.3 람다 식의 문법

코틀린 람다식은 항상 중괄호{}로 둘러싸여 있다.
```kotlin
// 람다 식 문법
{ x: Int, y: Int -> x+y }

// { 파라미터 -> 본문 } 의 형식을 가진다.
```
람다식을 변수에 저장할 수도 있다.
```kotlin
val sum = {x: Int, y: Int -> x+y}
```
코드의 일부분을 블록으로 둘러싸 실행할 필요가 있다면 **run**을 사용한다.  
run은 인자로 받은 람다를 실행해주는 라이브러리 함수다.

```kotlin
// {println(42)}()

run{ println(42)} // 람다 본문에 있는 코드를 실행한다.
```
실행 시점에 코틀린 람다 호출에는 아무 부가 비용이 들지 않으며, 프로그램 구성 요소와 비슷한 성능을 낸다.

```kotlin
// 코틀린이 코드를 줄여 쓸 수 있게 제공했던 기능을 제거하고
// 람다로 수정한 예제

val people = listOf(Person("A",10),Person("B",20))
//println( people.maxBy{ it.age } )
//people.maxBy(Person::age)
people.maxBy({p: Person -> p.age})

```
위의 람다 식의 문제점 :  
- 구분자가 많아 가독성이 떨어짐
- 컴파일러가 유추할 수 있는 인자 타입을 굳이 명시할 필요 없음 
- 인자가 하나뿐인 경우에는 이름을 붙이지 않아도 됨


_people.maxBy({p: Person -> p.age})_ 를 **개선**해보자!
1. people.maxBy(){ p: Person -> p.age }  
코틀린에서는 함수 호출 시 맨 뒤에 있는 인자가 람다 식이라면 그 람다를 괄호 밖으로 빼낼 수 있다.
2. people.maxBy{ p: Person -> p.age }   
람다가 함수의 유일한 인자이고, 괄호 뒤에 람다를 썻다면 호출시 빈 괄호를 없애도 된다.


- 람다가 함수의 유일한 인자라면?   
괄호 없이 람다를 바로 쓰는 것이 낫다.
- 둘 이상의 람다를 인자로 받는 경우?  
괄호를 사용하는 일반적인 함수 호출 구문을 사용하는 것이 낫다.

```kotlin
// 이름 붙인 인자를 사용해 람다 넘기기
val names = people.joinToString(separator = " ", transform = {p:Person -> p.name} )
```
```kotlin
// 람다를 괄호 밖에 전달하기
people.joinToString(" "){p:Person -> p.name}
```
```kotlin
// 람다 파라미터 타입 제거하기
people.maxBy{ p:Person -> p.age}
people.maxBy{ p -> p.age}
// 파라미터 타입 컴파일러가 추론 
```
```kotlin
// 디폴트 파라미터 이름 it 사용하기
people.maxBy{it.age}
```
 it 사용 조건
1) 람다 파라미터가 하나
2) 람다 파라미터 이름 지정 x
3) 컴파일러가 타입 추론 가능  

람다를 변수에 저장할 때는 파라미터의 타입을 추론할 문맥이 존재하지 않기 때문에 파라미터 타입을 명시해야 한다.
```kotlin
val getAge = {p: Person -> p.age}
``` 

람다의 본문이 여러 줄로 이뤄진 경우 맨 마지막에 있는 식이 람다의 결과 값이 된다.
```koltin
val sum = { x: Int, y: Int ->
    println("...")
    x+y
}
``` 

## 5.1.4 현재 영역에 있는 변수에 접근

람다를 함수 안에서 정의하면 함수의 파라미터뿐 아니라 람다 정의 앞에 선언된 로컬 변수까지 람다에서 모두 사용 가능.

이런 기능을 사용하기 위해 컬렉션의 모든 원소에 대해 람다를 호출해주는 forEach 표준 함수를 사용해보자.
```kotlin
//함수 파라미터를 람다 안에서 사용하기
fun printMessage(message: Collection<String>, prefix: String){
    message.forEach{ //각 원소에 대해 수행할 작업을 람다로
        println("$prefix $it")
        //람다 안에서 바깥의 prefix 파라미터 사용.
    }
}
```
자바와 다른 점 : 
- 코틀린 람다 안에서는 파이널 변수가 아닌 변수에 접근 가능.
- 람다 안에서 바깥의 변수 변경 가능.

```kotlin
fun printCounts(responses: Collection<String>){
    var count = 0
    responses.forEach{
        count++
    }
}
```
람다 안에서 사용하는 외부 변수를 '**람다가 포획(capture)한 변수**'라고 부른다.

포획한 변수가 있는 람다를 저장해서 함수가 끝난 뒤에 실행해도 람다에서 여전히 포획한 변수를 사용할 수 있다.  
이런 동작이 가능한 이유: 
파이널 변수를 포획한 경우에는 람다 코드를 변수 값과 함께 저장한다.  
파이널이 아닌 변수를 포획한 경우에는 변수를 특별한 래퍼로 감싸서 나중에 변경하거나 읽을 수 있게 한 다음, 래퍼에 대한 참조를 람다 코드와 함께 저장한다.

**함정 주의**  
람다를 이벤트 핸들러나 다른 비동기적으로 실행되는 코드로 활용하는 경우 함수 호출이 끝난 다음에 로컬 변수가 변경될 수도 있다.
```kotlin
// 이 함수는 항상 0을 반환한다.
fun countButtonClicks(button: Button): Int{
    var clicks = 0
    button.onClick{ clicks++ }
    return clicks
}
// 이 함수는 clicks를 먼저 반환하고 onClick 핸들러를 호출한다.
// 문제를 해결하려면,
// 카운터 변수를 함수 내부가 아닌 
// 클래스의 프로퍼티나 전역 프로퍼티의 위치로 빼내
// 변수 변화를 살펴볼 수 있게 해야 한다.
```

### 변경 가능한 변수 포획하기: 자세한 구현 방법
자바에서는 파이널 변수만 포획할 수 있다. 하지만 약간의 속임수를 통해 변경 가능한 변수를 포획할 수 있다. 
1. 원소가 변경 가능 변수 하나뿐인 final 배열 선언
2. 변경 가능 변수를 필드로 가지는 final 클래스 

이런 속임수를 코틀린으로 작성하면,
```kotlin
class Ref<T>(var value: T)
val counter = Ref(0)
val inc = {counter.value++}
// 공식적으로는 변경 불가능한 변수인 val counter를 포획했지만 
// 그 변수가 가리키는 객체의 필드 값을 바꿀 수 있다.
```
실제 코드에서는 이런 래퍼를 만들지 않아도 된다.  
대신, 변수를 직접 바꿈
```kotlin
val counter = 0
val inc = {counter++}
```
위 코드는 어떻게 작동할까?  
람다가 val 변수를 포획하면 변수의 값이 복사됨.  
var 변수를 포획하면 변수를 Ref 클래스 인스턴스에 넣는다.
그 참조를 final로 만들면 쉽게 람다로 포획할 수 있다.

## 5.1.5 멤버 참조

코틀린에서는 자바 8과 마찬가지로 함수를 값으로 바꿀 수 있다.  
이 때 **멤버 참조 ( :: )** 를 사용한다.  

`val getAge = Person :: age`  
`// { person: Person -> person.age }`

멤버 참조는 프로퍼티나 메소드를 단 하나만 호출하는 함수 값을 만들어준다.

> 클래스 이름  :: 멤버 이름  
> Person :: age  

- 멤버 참조 뒤에는 괄호를 넣으면 안된다.

- 멤버 참조는 그 멤버를 호출하는 람다와 같은 타입이다. 따라서 다음 예처럼 그 둘을 바꿔 쓸 수 있다.  
`people.maxBy{Person :: age}`  
`people.maxBy{p -> p.age}`  
`people.maxBy{it.age}`  

- 최상위에 선언되었거나 다른 클래스의 멤버가 아닌 함수나 프로퍼티를 참조할 수도 있다.  
`fun slute() =  println("..")`  
`run(::slute)` // 최상위 함수를 참조한다.
클래스 이름을 생략하고 바로 참조 한다.

- 람다가 인자가 여럿인 다른 함수한테 작업을 위임하는 경우 람다를 정의하지 않고 직접 위임 함수에 대한 참조를 제공하면 편리하다.

```kotlin
val action = {person: Person, message: String 
    -> sendEmail(person, message)
    // 이 람다는 sendEmail 함수에게 작업을 위임한다.
}
val nextAction = ::sendEmail
// 람다 대신 멤버 참조를 쓸 수 있다.
```

- 생성자 참조를 사용하면 클래스 생성 작업을 연기하거나 저장해둘 수 있다. :: 뒤에 클래스 이름을 넣으면 생성자 참조를 만들 수 있다.

```kotlin
data class Person(val name: String, val age: Int)
val createPerson = ::Person
//Person의 인스턴스를 만드는 동작으로 값을 저장한다.
val p = createPerson("Bob",42)
```

- 확장 함수도 멤버 함수와 같은 방식으로 참조할 수 있다.
```kotlin
fun Person.isAdult() = age >= 21  
val predicate = Person::isAdult
```

### 바운드 멤버 참조
코틀린 1.0 에서는 클래스의 메소드나 프로퍼티에 대한 참조를 얻은 다음에 그 참조를 호출할 때 항상 인스턴스 객체를 제공해야 했다.    
**코틀린 1.1 부터는 바운드 멤버 참조를 지원**한다.  
바운드 멤버 참조를 사용하면 멤버 참조를 생성할 때 클래스 인스턴스를 함께 저장한 다음 나중에 그 인스턴스에 대해 멤버를 호출해준다. 따라서 호출 시 수신 대상 객체를 별도로 지정해 줄 필요가 없다.

```kotlin
val p = Person("Amy",23)
val personAgeFunction = Person::age
println(personAgeFunction(p)) // 23

val dmitryAgeFunction = p::age //바운드 멤버 참조
println(dmitryAgeFunction()) // 23

// dmitryAgeFunction은 인자가 없는 함수다.
// (참조를 만들 때 p가 가리키던 사람의 나이를 반환)

// 코틀린 1.0 에서는 p::age 대신
// { p.age } 
// 직접 객체의 프로퍼티를 돌려주는 람다를 만들어야 함. 
```