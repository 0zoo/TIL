# 6.1 널 가능성

**nullability** (널 가능성) : _NullPointerException_ 오류를 피할 수 있게 돕기 위한 코틀린 타입 시스템의 특성.

null이 될 수 있는지 여부를 실행 시점이 아닌 컴파일 시점에 알 수 있도록 하여  
실행 시 예외 발생 가능성을 줄일 수 있다.

## 6.1.1 널이 될 수 있는 타입

코틀린 타입 시스템은 널이 될 수 있는 타입을 명시적으로 지원한다.

```java
// java
int strLen(String s){
    return s.length();
}
```
위의 자바 함수는 인자에 null이 들어가면  _NullPointerException_ 을 발생하기 때문에 안전하지 않다.  

```kotlin
// kotlin
// 널 인자 불가능
fun strLen(s: String) = s.length
```
위의 코틀린 코드는 널 가능성이 있는 인자를 넘기면 컴파일 시 오류가 발생한다.

```kotlin
// 널 인자 가능
fun strLenSafe(s: String?) = s.length
```
- 타입 이름 뒤에 물음표룰 붙이면 null 참조를 저장할 수 있다는 뜻.

- 널이 될 수 있는 타입의 변수에 대해 수행할 수 있는 연산이 제한된다.  
    - 메소드 직접 호출 불가  
    _변수.메소드()_
    - 널이 될 수 있는 값을 널이 될 수 없는 변수에 대입 불가
    - 널이 될 수 있는 타입의 값을 널이 될 수 없는 타입을 인자로 받는 함수에 전달 불가
    

## 6.1.2 타입의 의미

> 타입: 분류. 어떤 값들이 가능한지와 그 타입에 대해 수행할 수 있는 연산의 종류를 결정한다.

자바의 타입 시스템은 널을 제대로 다루지 못한다.

**실행 시점**에 널이 될 수 있는 타입이나 널이 될 수 없는 타입의 **객체는 같다**.  
널이 될 수 있는 타입은 널이 될 수 없는 타입을 감싼 래퍼 타입이 아니다.  
모든 검사는 컴파일 시점에 수행되기 때문에 코틀린에서는 널이 될 수 있는 타입을 처리하는 데 별도의 실행 시점 부가 비용이 들지 않는다.

## 6.1.3 안전한 호출 연산자 ?.

`?.` 은 null 검사와 메소드 호출을 한 번의 연산으로 수행한다.  

`s?.toUpperCase()` 는 `if (s != null) s.toUpperCase() else null` 과 같다.

안전한 호출 연산자는 널이 아닌 값에 대해서만 메소드를 호출한다. 호출하려는 값이 null이라면 이 호출은 무시되고 null이 결과 값이 된다.  
-> 안전한 호출의 결과 타입도 널이 될 수 있는 타입이다.

메소드 호출뿐 아니라 프로퍼티를 읽거나 쓸 때도 안전한 호출을 사용할 수 있다. 
```kotlin
// 널이 될 수 있는 프로퍼티를 다루기 위해 안전한 호출 사용하기
class Employee(val name: String, val manager: Employee?)

fun managerName(employee: Employee): String? = employee.manager?.name
```

객체 그래프에서 널이 될 수 있는 중간 객체가 여럿 있다면 한 식 안에서 안전한 호출을 연쇄해서 사용하면 편리하다.
```kotlin
class Address(val streetAddress: String, val zipCode: Int, val city: String, val country: String)

class Company(val name: String, val address: Address?)

class Person(val name: String, val company: Company?)

fun Person.countryName(): String{
    val country = this.company?.address?.country
    //여러 안전한 호출 연산자를 연쇄헤서 사용
    return if(country != null) country else "Unknown"
}
```

## 6.1.4 엘비스 연산자 ?:

**엘비스 연산자**(널 복합 연산자): 이항 연산자. 좌항 값이 널인지 검사하고 널이 아니면 값을 결과로 하고, 좌항 값이 널이면 우항 값을 결과로 한다. 

- `foo ?: bar`
    - foo != null 이라면
        - 결과 값: foo
    - foo == null 이라면 
        - 결과 값: bar

엘비스 연산자를 객체가 널인 경우 널을 반환하는 안전한 호출 연산자와 함께 사용해서 객체가 널인 경우에 대비한 값을 지정하는 경우도 많다.

```kotlin
// 엘비스 연산자를 활용해 널 값 다루기

fun strLenSafe(s: String?): Int = s?.length ?: 0

fun Person.countryName() = company?.address?.country ?: "Unknown"
```

코틀린에서 throw, return 등의 연산도 식이기 때문에 엘비스 연산자의 우항에 넣을 수 있다. 

```kotlin
// throw와 엘비스 연산자 함께 사용하기
fun printShippingLabel(person: Person){
    val address = person.company?.address 
        ?: throw IllegalArgumentException("..")
    // 주소가 없으면 예외 발생

    with(address){ // address는 널이 아니다.
        println(streetAddress)
        // 5장에서 배운 with 함수를 사용해
        // address. 을 반복하지 않아도 된다.
    }
}

```

## 6.1.5 안전한 캐스트 as?

**as?** 연산자는 어떤 값을 지정한 타입으로 캐스트한다. 만약 값을 대상 타입으로 변환할 수 없다면 null을 반환한다.

- `foo as? Type`
    - foo is Type 이면
        - foo as Type
    - foo !is Type 이면 
        - null

```kotlin
// 안전한 연산자를 사용해 equals 구현하기

class Person(val firstName: String, val lastName: String){
    override fun equals(o: Any?): Boolean {
        val other = o as? Person ?: return false
        // 타입이 일치하지 않으면 false 반환
        
        return other.firstName == firstName 
            && other.lastName == lastName
        // 안전한 캐스트를 하면 Person 타입으로 스마트 캐스트 됨.
        
    }

    override fun hashCode(): Int = firstName.hashCode() * 37 + lastName.hashCode()
}
```

## 6.1.6 널 아님 단언 !!

**널 아님 단언 (not-null assertion)**: 느낌표를 이중(!!)으로 사용하면 어떤 값이든 널이 될 수 없는 타입으로 강제로 바꿀 수 있다.  

실제 null에 !!를 적용하면 NPE가 발생한다.  
> !!는 컴파일러에게  " 나는 이 값이 null이 아님을 잘 알고 있어. 내가 틀렸다면 예외가 발생해도 감수할게. " 

- `foo!!`
    - foo != null 이면
        - foo
    - foo == null 이면 
        - NullPointerException 던짐

주의!  
!!를 널에 대해 사영해서 발생하는 예외의 stack trace에는 몇 번째 줄인지에 대한 정보는 들어있지만, 어떤 식에서 예외가 발생했는지에 대한 정보는 들어있지 않다. `person.company!!.address!!.country` <-와 같이 여러 !! 단언문을 한 줄에 함께 쓰는 일은 되도록이면 피하도록 하자

## 6.1.7 let 함수

__let__ 함수를 안전한 호출 연산자와 같이 사용하면 원하는 식을 평가해서 결과가 null인지 검사한 다음에 그 결과를 변수에 넣는 작업을 간단하게 처리할 수 있다.

- 널이 될 수 있는 값을 널이 아닌 값만 인자로 받는 함수에 넘기는 경우에 let 함수를 많이 사용한다.


- let을 안전하게 호출하면 수신 객체가 널이 아닌 경우  
널이 될 수 없는 타입으로 바꿔서 람다에 전달하고 실행해준다.    
    `foo?.let{ ...it... }`  
    - foo가 null이 아니면 
        -> it은 람다 안에서 널이 아니다.
    - foo가 null이면 
        -> 아무 일도 일어나지 않는다.

```kotlin
// email 변수는 String? 타입
// send 메소드의 인자는 널이 될 수 없는 타입

// 방법 1.
if(email != null) send(email)

// 방법 2. 
email?.let{ email -> send(email) }

// 방법 3.
email?.let{ send(it) }

```

- 긴 식의 값이 널이 아닐 때 수행해야 하는 로직이 있을 경우 let을 쓰면 편리하다. let을 쓰면 긴 식의 결과를 저장하는 변수를 따로 만들 필요가 없다.

```kotlin
// 1.
val person: Person? = getBest()
if(person != null) send(person.email)

// 2.
getBest()?.let{ send(it.email) }
```

안전한 호출을 사용하지 않고 nullable 타입에 let을 사용하는 경우  
:  람다의 인자(it)는 널이 될 수 있는 타입으로 추론된다.
 


## 6.1.8 나중에 초기화할 프로퍼티

객체 인스턴스를 일단 생성하고 나중에 초기화하는 프레임워크  
예) 안드로이드의 onCreate에서 액티비티 초기화, JUnit의 @Before ...

코틀린에서는 일반적으로 생성자에서 모든 프로퍼티를 초기화해야 한다.  

프로퍼티가 널이 될 수 없는 타입이라면 반드시 널이 아닌 값으로 초기화해주어야 한다. 만약 널이 아닌 값을 초기화 값으로 제공할 수 없으면 nullable 타입을 사용할 수 밖에 없다. 하지만, nullable 타입을 사용하면 모든 프로퍼티 접근에 널 검사를 넣거나 !! 연산자를 써야 한다.

```kotlin
// 널 아님 단언을 사용해 널이 될 수 있는 프로퍼티 접근하기
class MyService{
    fun performAction(): String = "foo"
}
class MyTest{
    private var myService: MyService? = null
    // null로 초기화하기 위해 nullable 타입인 프로퍼티 선언
    
    @Before fun setUp(){
        myService = MyService()
        // setUp 메소드 안에서 프로퍼티 초기화
    }

    @Test fun testAction(){
        Assert.assertEquals( "foo",
            myService!!.performAction() )
        // 반드시 널 가능성에 신경 써야 한다. 
    }
}

```
- **lateinit** 변경자를 붙이면 프로퍼티를 **나중에 초기화**할 수 있다.

```kotlin
// 나중에 초기화하는 프로퍼티 사용하기

class MyTest{
    private lateinit var myService: MyService?
    // 초기화하지 않고 널이 될 수 없는 프로퍼티를 선언.
    
    @Before fun setUp(){
        myService = MyService()
        // setUp 메소드 안에서 초기화
    }

    @Test fun testAction(){
        Assert.assertEquals( "foo",
            myService.performAction() )
        // 널 검사를 수행하지 않고 프로퍼티 사용
    }
}

```

- 나중에 초기화 하는 프로퍼티는 항상 **var**

lateinit 프로퍼티를 의존관계 주입(DI) 프레임워크와 함께 사용하면, 
DI가 외부에서 프로퍼티의 값을 설정해준다.  
다양한 자바 프레임워크와의 호환성을 위해 
코틀린은 lateinit이 지정된 프로퍼티와 가시성이 같은 필드를 생성해준다. 


## 6.1.9 널이 될 수 있는 타입 확장

nullable 타입에 대한 확장 함수를 정의하면 null 값을 다루는 강력한 도구로 활용할 수 있다.

수신 객체 역할을 하는 변수에 대해 메소드를 호출시 확장 함수가 알아서 널을 처리해줌.  
(확장 함수에서만 가능)

일반 멤버 호출은 객체 인스턴스를 통해 dispatch되므로 
그 인스턴스가 널인지 여부를 검사하지 않는다.   
* dispatch란? 
    * 동적 디스패치: 객체의 동적 타입에 따라 적절한 메소드를 호출해주는 방식.  
    * 직접 디스패치: 컴파일러가 컴파일 시점에 어떤 메소드를 호출될지 결정해서 코드를 생성하는 방식.

```kotlin
// nullable 수신 객체에 대해 확장 함수 호출하기
fun verifyInput(input: String?){
    if(input.isNullOrBlank()){
    // input은 nullable 타입의 값
    // isNullOrBlank()는 nullable 타입의 확장 함수
    // 안전한 호출 안해도 됨. 확장 함수가 알아서 null 처리해줌.
        println("...")
    }

}
verifyInput(null) // null을 수신 객체로 전달해도 예외 발생 안함.
// ...
```
- nullable 타입의 확장 함수는 안전한 호출 없이도 호출 가능하다.

```kotlin
// 널이 될 수 있는 String의 확장
fun String?.isNullOrBlank(): Boolean 
    = this == null || this.isBlank()
// this.isBlank() 에서 스마트 캐스트가 적용되었다. 
// ( isBlank()는 null이 아닐 때만 호출 가능 )

```
- 코틀린 : this는 null이 될 수 있다.  
nullable 타입에 대한 확장 함수 내부에서 명시적으로 널 여부를 검사해야 한다.  

- 자바: this는 항상 null이 아니다.  
자바에서는 메소드 안의 this는 그 메소드가 호출된 수신 객체를 가리키는데,  
수신 객체가 널이였다면 NPE가 발생해서 메소드 안으로 들어가지도 못한다.
 

노트:  
확장 함수 작성시 널이 될 수 있는 타입에 대해 정의할지 여부가 고민된다면  
일단은 널이 될 수 없는 타입에 대한 확장 함수를 정의하자.


## 6.1.10 타입 파라미터의 널 가능성

타입 파라미터 T를 클래스나 함수 안에서 타입 이름으로 사용하면  
이름 끝에 물음표가 없더라도  
T가 널이 될 수 있는 타입이다. 

```kotlin
fun <T> printHashCode(t: T){
    println(t?.hashCode()) 
    // t가 null이 될 수 있으므로 안전한 호출을 써야만 한다.
}
printHashCode(null) // T의 타입은 Any?로 추론된다.
// null
```

타입 파라미터가 널이 아님을 확실히 하려면  
널이 될 수 없는 **타입 상한**(upper bound)를 지정해야 한다.  
타입 상한을 지정하면 nullable인 값을 거부한다.

```kotlin
fun <T: Any> printHashCode(t: T){
// 이제 t는 널이 될 수 없는 타입
    println(t.hashCode()) 
}
printHashCode(null) // 컴파일 되지 않고 예외 발생
```

## 6.1.11 널 가능성과 자바

1. 자바에도 애노테이션으로 표시된 널 가능성 정보가 있다.  
    - @Nullable
    - @NotNull
2. 널 가능성 애노테이션이 없는 경우  
자바의 타입은 **코틀린의 플랫폼 타입**이 된다.

### 플랫폼 타입
플랫폼 타입은 코틀린이 널 관련 정보를 알 수 없는 타입을 말한다. 

플랫폼 타입은 nullable 또는 notNull 타입 모두로 사용 가능하다.  
- 자바: `Type` => 코틀린: `Type?` 또는 `Type` 

```java
// 널 가능성 애노테이션이 없는 자바 클래스
public static class Person{
    private final String name;
    public Person(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
```

```kotlin
// 널 검사 없이 자바 클래스 접근하기
fun yellAt(person: Person){
    println(person.name.toUpperCase())
    // toUpperCase()의 수신 객체 person.name이
    // 널이어서 예외가 발생한다.
}
yellAt(Person(null))
// 예외 발생.
// 발생한 예외가 NPE가 아닌,
// toUpperCase()가 수신 객체로 
// 널을 받을 수 없다는 예외가 발생한다.
```

```kotlin
// 널 검사를 통해 자바 클래스 접근하기
fun yellAtSafe(person: Person){
    println( (person.name ?: "Anyone").toUpperCase())

}
yellAt(Person(null))
// Anyone
```

------
#### 코틀린이 왜 플랫폼 타입을 도입했을까?
모든 자바 타입을 nullable로 다루면 더 안전할 수도 았겠지만,
불필요한 널 검사가 들어갈 수 있기 떄문이다.  
특히 제네릭을 다룰 경우 널 안전성으로 얻는 이익보다 검사에 드는 비용이 훨씬 더 커진다. 
( ArrayList<String?>? 으로 다루면 배열의 원소에 접근할 때마다 널 검사 또는 안전한 캐스트 수행해야 한다. )

그래서 코틀린 설계자들은 프로그래머에게 타입 처리의 책임을 부여하는 실용적인 접근 방법을 택했다.

------

- 코틀린에서 플랫폼 타입을 선언할 수는 없다.  
오직 자바 코드에서 가져온 타입만 플랫폼 타입이 된다.

오류 메세지에서 `String!`의 `!`는 널 가능성에 대해 아무 정보도 없다는 뜻. 

### 상속

코틀린에서 자바 메소드를 오버라이드 할 때  
그 메소드의 파라미터와 반환 타입을  
nullable로 선언할지 notNull로 선언할지 결정해야 한다.

```java
// String 파라미터가 있는 자바 인터페이스
interface StringProcessor{
    void process(String value);
}
```
```kotlin
// 자바 인터페이스를 여러 다른 널 가능성으로 구현하기
class StringPrinter: StringProcessor{
    override fun process(value: String){
        println(value)
    }
}

class NullableStringPrinter: StringProcessor{
    override fun process(value: String?){
        if(value != null){
            println(value)
        }
    }
}
```

코틀린 컴파일러는 널이 될 수 없는 타입으로 선언한 모든 파라미터에 대해 
널이 아님을 검사하는 단언문을 만들어서 주기 때문에   
설령 파라미터를 메소드 안에서 결코 사용하지 않아도 자바 코드가 그 메소드에게 null 값을 넘기면 예외가 발생한다.



