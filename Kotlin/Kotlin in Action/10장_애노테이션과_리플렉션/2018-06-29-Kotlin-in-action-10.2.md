# 10.2 리플렉션: 실행 시점에 코틀린 객체 내부 관찰

리플렉션이란? 실행 시점에 동적으로 객체의 프로퍼티와 메소드에 접근할 수 있게 해주는 방법.

컴파일러는 이름이 실제로 가리키는 선언을 컴파일 시점에 정적으로 찾아내서 해당하는 선언이 실제로 존재함을 보장함.

(1) 타입과 관계없이 객체를 다뤄야 하거나 
(2) 메소드나 프로퍼티 이름을 실행 시점에만 알 수 있는 경우  
리플렉션 사용하자.



코틀린에서 리플렉션을 사용하려면 두 가지 리플렉션 API를 다뤄야 한다.

1. 자바가 *java.lang.reflect* 패키지를 통해 제공하는 표준 리플렉션.  
리플렉션을 사용하는 자바 라이브러리와 코틀린이 완전히 호환된다.

2. 코틀린이 *kotlin.reflect* 패키지를 통해 제공하는 코틀린 리플렉션 API.  
자바에는 없는 프로퍼티나 코틀린 고유 개념에 대한 리플렉션을 제공함.

하지만,  
현재 코틀린 리플렉션 API는 자바 API를 완전히 대체할 수 있는 복잡한 기능을 제공하지는 않음.

## 10.2.1 코틀린 리플렉션 API: KClass, KCallable, KFunction, KProperty

1. KClass :  
클래스 안에 있는 모든 선언을 열거하고 각 선언에 접근하거나 클래스의 상위 클래스를 얻는 등의 작업이 가능함.  
Java 의 *java.lang.Class* 에 해당.  

KClass 는 `MyClass::class` 의 형태로 얻을 수 있다.

Runtime 에서 얻을 경우에는 javaClass property 를 통해 Java class 를 얻는다.

이는 `Object.getClass()` 와 동일하다.

`.kotlin extension property` 를 통해 Java 에서 Kotlin reflection API 로 옮겨올 수 있다.


```kotlin
class Person(val name: String, val age: Int)

val person = Person(“Alice”, 29)

val kClass = person.javaClass.kotlin

println(kClass.simpleName) // Person

kClass.memberProperties.forEach{ println(it.name) } 
// age 와 name 출력, non-extension property 들만 가져온다.

```


``` kotlin
interface KClass<T : Any>{

    val simpleName: String?

    val qualifiedName: String?

    val members: Collection<KCallable<*>> 

    val constructors: Collection<KFunction<T>>

    val nestedClasses: Collection<KClass<*>>
    ...
}
```

memberProperties 는 extension 으로 정의되어 있다.


2. KCallable :  
function, properties 의 super interface, call 함수를 가지고 있다

```kotlin
interface KCallable<out R> {
    fun call(vararg args: Any?): R
}

fun foo(x:Int) = println(x)

val kFunction = ::foo 
// KFunction1<Int, Unit> 타입, 1 은 param 이 하나라는 의미

kFunction.call(42) 
// argument mismatch case 에는 IllegalArgumentException 이 발생

```

```kotlin
import kotlin.reflect.KFunction2

fun sum(x: Int, y: Int) = x+ y

val kFunction : KFunction2<Int, Int, Int> = ::sum

println(kFunction.invoke(1,2) + kFunction(3, 4))

kFunction(1) // error
```

KFunction 의 경우 invoke 로 수행한다. 
( KFunction 의 interface 에는 invoke 가 있으며, 갯수와 type 이 명시 )

물론 call 로도 가능하다. 그러나 type safe 하지 않다.


---

KFunction2 같은 것은 어디에 정의되어 있을까?

이런 녀석들은 synthetic compiler-generated types 라고 부른다.

compiler 가 필요할 때 정의해서 만드는 녀석이라는 의미로, 이 코드는 package 에서 찾을 수 없다.

이 접근 방법으로 kotlin-runtime.jar 의 사이즈를 줄일 수 있고, 갯수 제한에 대한 문제로 뛰어 넘을 수 있다.

---



KProperty 에도 call 함수를 호출할 수 있다. 이는 getter 를 호출한다.

그러나 getter 를 얻어와서 호출하는 것이 당연히 더 좋다.


top-level property 는 KProperty0 interface 를 상속하며, 이 녀석은 get method 를 가지고 있다.
```kotlin
var counter = 0

val kProperty = ::counter

kProperty.setter.call(21)

println(kProperty.get()) // 21
```


member property 는 KProperty1 이며 한개의 argument 를 갖는 get method 를 가지고 있다.

첫번째 param 에는 접근하려는 object instance 를 전달해야 한다.

```kotlin
val memberProperty = Person::age

println(memberProperty.get(person)) // 29
```

또한 KProperty1 은 generic class 이다.

위의 예에서 memberProperty 는 `KProperty<Person, Int>` 이다.


## 10.2.2 리플렉션을 사용한 객체 직렬화 구현

```kotlin

private fun StringBuilder.serializeObject(obj: Any){

    val kClass = obj.javaClass.kotlin

    val properties = kClass.memberProperties

    properties.jointToStringBuilder(this, prefix = “{“, postfix = “}”) { prop -> // KProperty1<Any, *> type

        serializeString(prop.name) 
        // JSON format 으로 escape 한다

        append(“: “)

        serializePropertyValue(prop.get(obj)) 
        // primitive, string, collection, nested object 로 serialize

}

fun serialize(obj: Any): String = buildString { serializeObject(obj) }
```

buildString 은 StringBuilder 를 만들고 람다를 이용해 fill 하도록 한다.


## 10.2.3 애노테이션을 활용한 직렬화 제어


**KAnnotatedElement interface** 는 annotations 라는 property 를 가지고 있다.

이는 annotation instance collection 이다. (  runtime retention 인 )

```kotlin

inline fun <reified T> KAnnotatedElement.findAnnotation(): T? = annotations.filterIsInstance<T>().firstOrNull()

val properties = kClass.memberProperties.filter{ it.findAnnotation<JSONExclude>() == null }

val jsonNameAnn = prop.findAnnotation<JsonName>()

val propName = jsonNameAnn?.name ?: prop.name
```


```kotlin
annotation class CustomSerializer{
    val serializerClass: KClass<out ValueSerializer<*>>
}

data class Person(val name: String, @CustomSerializer(DateSerializer::class) val birthDate: Date)

fun KProperty<*>.getSerializer(): ValueSerializer<Any?>?{

    val customSerializerAnn = findAnnotation<CustomSerializer>() ?: return null

    val serializerClass = customSerializerAnn.serializerClass

    val valueSerializer = serialzierClass.objectInstance?: serializerClass.createInstance()

    @Suppress(“UNCHECKED_CAST”)
    return valueSerializer as ValueSerializer<Any?>

}
```


class 와 singleton object 는 동일하게 KClass 이다.

objectInstance 로는 singleton object 가 접근된다.

싱글톤이 아닐 경우에는 createInstance 로 인스턴스를 만든다.

```kotlin
private fun StringBuilder.serializeProperty(prop: KProperty1<Any, *>, obj: Any){

    val name = prop.findAnnotation<JsonName>()?.name ?: prop.name

    serializeString(name)

    append(“: “)

    val value = prop.get(obj)

    val jsonValue = prop.getSerializer()?.toJsonValue(value) ?: value

    serializePropertyValue(jsonValue)

}
```

## 10.2.4 JSON 파싱과 객체 역직렬화

```kotlin
inline fun <reified T: Any> deserialize(json: String): T

data class Author(val name:String)

data class Book(val title:String, val author:Author)

val json = “””{“title”: “Catch-22”, “author”: {“name”:”J.Heller”}}”””

val book = deserialize<Book>(json)

println(book)
```

deserializer 는 lexical analyzer(lexer), syntax analyzer, 그리고 parser 로 구성되어 있다.

lexical analysis 는 string 을 token 으로 나눈다.

두가지 형태의 token 이 있는데 character token ( comma, colon, braces, brackets ) 과 value token ( string, number, boolean, null ) 이 있다.

parser 는 plain token list 를 structured form 으로 변경하는 것을 이야기한다.

JKid 는 JSON이 structured form 이다.


## 10.2.5 최종 역직렬화 단계: callBy(), 리플렉션을 사용해 객체 만들기

KCallable.call 은 function 이나 constructor 를 호출할 수 있고, argument 들을 list 형태로 받을 수 있다.  
많은 경우에 잘 작동하지만 제약사항이 있다.  
default parameter value 를 제공하지 않는다.  

```kotlin
interface KCallable<out R> {
    fun callBy(args: Map<KParameter, Any?>): R
    ....
}
```

callBy 는 전달하는 Map 에 빠진 param 이 있다면 default value 가 자동으로 사용된다. 즉 default value 를 잘 지원한다.

Map 으로 전달하니, named-argument 를 사용하는 것처럼 순서에도 영향을 받지 않는다.

Primary constructor 를 호출하는 데에도 사용될 수 있다.
