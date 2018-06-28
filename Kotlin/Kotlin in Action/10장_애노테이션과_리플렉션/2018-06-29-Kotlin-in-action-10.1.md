# 10.1 애노테이션 선언과 적용

코틀린에서 애노테이션을 사용하는 문법은 자바와 똑같지만,  
애노테이션을 선언할 때 사용하는 문법은 자바와 약간 다르다.

메타 데이터를 선언에 추가하면 애노테이션을 처리하는 도구가 적절한 처리 해줌.

## 10.1.1 애노테이션 적용

```kotlin
// JUnit 프레임워크 사용하기
import org.junit.*

class MyTest{
    @Test fun testTrue() {
        Assert.assertTrue(true)
    }
}
```

```kotlin
// 코틀린에서는 replaceWith 파라미터를 통해
// 옛 버전을 대신할 수 있는 패턴을 제시.
@Deprecated("...", ReplaceWith("removeAt(index)"))
fun remove(index: Int){...}
```

- 애노테이션을 지정하는 문법은 자바와 약간 다르다.

    - 클래스를 애노테이션 인자로 지정할 때는  
    `@MyAnnotation(MyClass::class)` 처럼  
    `::class`를 클래스 이름 뒤에 넣어야 한다.

    - 다른 애노테이션을 인자로 지정할 때는  
    인자로 들어가는 애노테이션의 이름 앞에 `@`를 사용하지 않는다.  

    - 배열을 인자로 지정하려면  
    `@RequestMapping(path = arrayOf("/foo", "/bar"))`  
    처럼 `arrayOf`함수를 사용한다.  
    자바에서 선언한 애노테이션 클래스를 사용한다면  
    value 라는 이름의 파라미터가 필요에 따라 자동으로 가변 길이 인자로 변환된다.  
    그런 경우에는 `@JavaAnnotationWithArrayValue("foo", "bar")`처럼 사용.


애노테이션 인자를 컴파일 시점에 알 수 있어야한다.  
-> 임의의 프로퍼티를 인자로 지정할 수 없음.  

프로퍼티를 애노테이션 인자로 사용하려면 그 앞에 `const` 변경자를 붙여야 한다.  
컴파일러는 **`const`** 가 붙은 프로퍼티를 **컴파일 시점 상수**로 취급한다. 

`const`가 붙은 프로퍼티는 파일의 맨 위나 object 안에 선언해야 하며,  
원시 타입이나 String으로 초기화해야만 한다.  

```kotlin
// timeout 파라미터를 사용해 밀리초 단위로 타임아웃 시간을 지정하는 예

const val TEST_TIMEOUT = 100L

@Test (timeout = TEST_TIMEOUT) fun testM() {...}
```

## 10.1.2 애노테이션 대상

코틀린 선언과 대응하는 여러 자바 선언에 각각 애노테이션을 붙여야 할 때  
애노테이션을 붙일 때 어떤 요소에 애노테이션을 붙일지 표시할 필요가 있음.

**사용자 지정 대상** 선언으로 애노테이션을 붙일 요소를 정할 수 있다.  

> **`@` 사용자 지점 대상 `:` 애노테이션 이름**  

> `@get:Rule`  
>> `get` - 사용 지점 대상   
`Rule` - 애노테이션 이름  


규칙을 지정하려면 public 프로퍼티 앞에 `@Rule`을 붙여야 하는데,  
코틀린은 기본적으로 private이기 때문에 예외 발생한다.  
-> `@get:Rule`을 사용해야 함.

```kotlin
class HashTempFolder {

    // get은 
    // @Rule 애노테이션을 
    // 프로퍼티 게터에 적용하라는 뜻.
    @get:Rule
    val folder = TemporaryFolder()

    @Test
    fun testUsing() {
        val createdFile = folder.newFile("my.txt")
        val createdFolder = folder.newFolder("subfolder")
        //...
    } 
}
```

코틀린으로 애노테이션을 선언하면 프로퍼티에 직접 적용할 수 잇는 애노테이션을 만들 수 있다.

---
### 사용 지점 대상을 지정할 때 지원하는 대상 목록

- **property** : 프로퍼티 전체. (자바에서 선언된 애노테이션에는 사용 불가)
- **field** : 프로퍼티에 의해 생성되는 backing field
- **get** : 프로퍼티 게터
- **set** : 프로퍼티 세터
- **receiver** : 확장 함수나 프로퍼티의 수신 객체 파라미터
- **param** : 생성자 파라미터
- **setparam** : 세터 파라미터
- **delegate** : 위임 프로퍼티의 위임 인스턴스를 담아둔 필드
- **file** : 파일 안에 선언된 최상위 함수와 프로퍼티를 담아두는 클래스

---

(자바와 달리) 코틀린은 애노테이션 인자로 **임의의 식**을 허용한다. 

```kotlin
// 컴파일러 경고 무시
@Suppress("UNCHECKED_CAST")
```

---
### 자바 API를 애노테이션으로 제어하기

- `@JvmName`은 코틀린 선언이 만들어내는 자바 필드나 메소드 이름을 변경
- `@JvmStatic`을 메소드, 객체 선언, 동반 객체에 적용하면 그 요소가 자바 정적 메소드로 노출
- `@JvmOverloads`를 사용하면 디폴트 파라미터 값이 있는 함수에 대해 컴파일러가 자동으로 오버로딩한 함수를 생성해줌
- `@JvmField`프로퍼티에 사용하면 게터나 세터가 없는 public 자바 필드로 프로퍼티를 노출

---

## 10.1.3 애노테이션을 활용한 JSON 직렬화 제어

- **직렬화**(serialization) : 객체를 저장장치에 저장하거나 네트워크를 통해 전송하기 위해 텍스트나 이진 형식으로 변환하는 것.
- **역직렬화**(deserialization) : 텍스트나 이진 형식으로 된 데이터로부터 원래의 객체를 만들어내는 것.

JSON 변환시 자주 사용하는 라이브러리  
-> Jackson, GSON ...

지금부터 JSON 직렬화를 위한 _제이키드_ 라는 순수 코틀린 라이브러리를 구현하는 과정을 공부해보자.  

10장에서 만들어 볼 순수 코틀린 라이브러리 **_제이키드_**
- `serialize()` : JSON String 반환

- `deserialize()` : 객체 반환.  
`deserialize<Person>(jsonString)` 처럼  
역직렬화할 때 JSON에 객체의 타입이 저장되지 않기 때문에  
인스턴스를 만들려면 타입 인자로 클래스를 명시해야 함.

- `@JsonExclude` : 직렬화나 역직렬화 시 그 프로퍼티를 무시.  
직렬화 대상에서 제외한 프로퍼티는 반드시 디폴트 값을 지정해야 한다.

- `@JsonName` : 프로퍼티를 표현하는 키/값 쌍의 키로 프로퍼티 이름 대신 애노테이션이 지정한 이름 사용 가능.


```kotlin
data class Person(
    // JSON에서 "firstName" 키를 "alias"로 변경
    @JsonName("alias") val firstName: String, 
    @JsonExclude val age: Int? = null
)
```

## 10.1.4 애노테이션 선언

```kotlin
// 아무 파라미터도 없는 가장 단순한 애노테이션
annotation class JsonExclude
```
애노테이션 클래스는 오직 선언이나 식과 관련 있는 metadata의 구조를 정의하기 때문에 내부에 아무 코드도 들어있을 수 없다.


파라미터가 있는 애노테이션을 정의하려면 애노테이션 클래스의 주 생성자에 파라미터 선언해야 함.

```kotlin
// 코틀린의 파라미터가 있는 애노테이션 정의
annotation class JsonName(val name: String)
```
일반 클래스의 주 생성자 선언 구문을 똑같이 사용한다.  
다만 애노테이션 클래스에서는 모든 파라미터 앞에 val을 붙여야 함.

```java
// 자바 애노테이션 선언
public @interface JsonName{
    String value();
}
```

코틀린에서는 name, 자바에서는 `value()`  

자바: 어떤 애노테이션을 적용할 때 value를 제외한 모든 attribute에는 이름을 명시해야 함.

코틀린: 일반적인 생성자 호출과 같다.  
`@JsonName(name = "name")`와 `@JsonName("name")` 둘 다 가능.

코틀린도 자바 애노테이션에 정의된 value를 특별하게 취급하기 때문에,  
자바에서 선언한 애노테이션을 코틀린의 구성 요소에 적용시  
value를 제외한 모든 인자에 대해 이름 붙은 인자 구문을 사용해야만 한다.


## 10.1.5 메타애노테이션: 애노테이션을 처리하는 방법 제어

**메타 애노테이션(meta-annotation)**: 애노테이션 클래스에 적용할 수 있는 애노테이션

- `@Target` : 가장 흔히 쓰이는 메타애노테이션.  
애노테이션을 적용할 수 있는 **요소의 유형을 지정**한다.  
`@Target`을 지정하지 않으면, 모든 선언에 적용할 수 있는 애노테이션 됨.  

- `AnnotationTarget` : 애노테이션이 붙을 수 있는 대상이 정의된 **enum**  

> 메타 애노테이션을 직접 만들어야 한다면  
*ANNOTATION_CLASS* 를 대상으로 지정하라.

```kotlin
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class BindingAnnotation

@BindingAnnotation
annotation class MyBinding
```

대상을 *PROPERTY* 로 지정한 애노테이션을 자바 코드에서 사용 불가.

`AnnotationTarget.FIELD` -> 애노테이션을 코틀린 프로퍼티와 자바 필드에 적용 가능함.

---
### @Retention 
자바의 `@Retention` :   
애노테이션 클래스를 소스 수준에서만 유지할지, .class 파일에 저장할지, 실행 시점에 리플렉션을 사용해 접근할 수 있게 할지를 지정하는 메타애노테이션

**자바** 컴파일러는 기본적으로 애노테이션을 .class 파일에는 저장하지만  
런타임에는 사용할 수 없게 한다.

**코틀린**에서는 기본적으로 애노테이션의 `@Retention`을 *RUNTIME*으로 지정한다.

---

## 10.1.6 애노테이션 파라미터로 클래스 사용

클래스 참조를 파라미터로 하는 애노테이션 클래스를 선언하면  
클래스를 선언 메타데이터로 참조할 수 있다.

```kotlin
interface Company{
    val name: String
}

data class CompanyImpl(override val name: String): Company

data class Person(
    val name: String,
    @DeserializeInterface(CompanyImpl::class) val company: Company
)
```


인터페이스의 인스턴스를 직접 만들 수 없기 때문에  
역직렬화시 어떤 클래스를 사용해 인터페이스를 구현할지를 지정할 수 있어야 함.

역직렬화를 사용할 클래스를 지정하기 위해 `@DeserializeInterface(CompanyImpl::class)`

```kotlin
// 클래스 참조를 인자로 받는 애노테이션 정의
annotation class DeserializeInterface(
    val targetClass: KClass<out Any>
)
```

**`KClass`** 는 *java.lang.Class*와 같은 역할을 하는 코틀린 타입.

코틀린 클래스에 대한 참조를 저장할 때 `KClass` 타입 사용.

> `CompanyImpl::class`의 타입은 `KClass<CompanyImpl>` 이며,  
`KClass<CompanyImpl>`은 `KClass<out Any>`의 **하위 타입**이다.

`KClass<T>`가 `KClass<out Any>`의 하위 타입이 된다.(공변성)  
-> 인자로 Any를 확장하는 모든 클래스에 대한 참조를 전달할 수 있다.


`KClass<out Any>` 에서 out 변경자가 없으면 DeserializeInterface에 오직 `Any::class`만 넘길 수 있다.  


## 10.1.7 애노테이션 파라미터로 제네릭 클래스 받기

```kotlin
interface ValueSerializer<T>{
    fun toJsonValue(value: T): Any?
    fun fromJsonValue(jsonValue: Any?): T
}
```

```kotlin
data class Person(
    val name: String,
    @CustomSerializer(DateSerializer::class) val birthDate: Date
)
```

```kotlin
// 타입 파라미터가 있는 ValueSerializer 타입을 참조하려면
// 항상 타입 인자를 제공해야 한다.
// 하지만,
// 타입 인자 정보를 모르기 때문에
// 스타 프로젝션 사용

annotation class CustomSerializer(
    val serializerClass: KClass<out ValueSerializer<*>>
)
```

> ValueSerializer을 확장하는 클래스에 대한 참조만 올바른 인자로 인정된다.
>> `KClass<out ValueSerializer<*>>`

- `KClass<out` 허용할 클래스 이름`>`  
클래스를 인자로 받아야 한다면

- `KClass<out` 허용할 클래스 이름`<*>>`  
제네릭 클래스를 인자로 받아야 한다면
