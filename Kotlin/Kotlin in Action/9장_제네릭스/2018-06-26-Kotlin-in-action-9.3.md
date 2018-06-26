# 9.3 변성: 제네릭과 하위 타입

- **변성(variance)** :  
`List<String>`과 `List<Any>`와 같이  
기저 타입이 같고 타입 인자가 다른  
여러 타입이 어떤 관계가 있는지 설명하는 개념.

## 9.3.1 변성이 있는 이유: 인자를 함수에 넘기기

```kotlin
// List<Any>에 문자열이 들어왔을 때
// 안전한 경우
fun printContents(list: List<Any>) {
    println(list.joinToString())
}

printContents(listOf("abc", "bac"))
// abc, bac
```

```kotlin
// MutableList<Any>에 문자열이 들어왔을 때
// 오류 발생하는 경우
fun addAnswer(list: MutableList<Any>) {
    list.add(42)
}

val strings = mutableListOf("abc", "bac")

addAnswer(strings) // 이 줄이 컴파일되면 

// 타입 불일치 가능성이 존재하기에
// 실행 시점에 오류 발생함
println(strings.maxBy{ it.length })
// ClassCastException Error

```

`List<Any>` 대신 `List<String>`을 넘기려면 리스트의 변경 가능성이 없어야한다. 


> 함수가 읽기 전용 리스트를 받는다면  
더 구체적인 타입의 원소를 갖는 리스트를 넘길 수 있다.


## 9.3.2 클래스, 타입, 하위 타입

타입과 클래스의 차이

- 제네릭 클래스가 아닌 클래스에는  
클래스 이름을 바로 타입으로 쓸 수 있다.

예를 들어 `var x: String`이라고 쓰면 `String` 클래스의 인스턴스를 저장하는 변수를 저장할 수 있다.

하지만 `var x: String?`처럼 같은 클래스 이름을 널이 될 수 있는 타입에도 쓸 수 있다는 점을 기억해라

이는 모든 코틀린 클래스가 적어도 둘 이상의 타입을 구성할 수 있다는 뜻.


제네릭 클래스에서는 상황이 더 복잡하다.

올바른 타입을 얻으려면 제네릭 타입의 타입 파라미터를 
구체적인 타입 인자로 바꿔줘야 한다.

- 제네릭 클래스는  
무수히 많은 타입을 만들어낼 수 있다.  
(타입 인자를 치환한 `List<String>`, `List<String?>`, `List<List<String>>` 등)

- `Int`: `Number`의 **하위 타입(subtype)**
- `Number`: `Int`의 **상위 타입(supertype)**

> `A`타입의 값이 필요한 모든 장소에 `B`타입의 값을 넣어도 아무 문제가 없다면  
`B`는 `A`의 **하위 타입** 이다.

모든 타입은 자신의 하위 타입이다.

```kotlin
// 컴파일러는 변수 대입이나 함수 인자 전달 시 
// 하위 타입 검사를 매번 수행한다.

fun test(i: Int){
    val n: Number = i
    // Int가 Number의 하위 타입이기 때문에 
    // 컴파일 ok

    fun f(s: String){...}
    f(i) // 함수 호출 x 컴파일 x
}
```


간단한 경우 하위 타입은 **하위 클래스(subclass)** 와 근본적으로 같다.

`String`은 `CharSequence`의 하위 타입인 것처험  
어떤 인터페이스를 구현하는 클래스의 타입은 그 인터페이스 타입의 하위 타입이다.

널이 될 수 있는 타입은 하위 타입과 하위 클래스가 같지 않은 경우를 보여주는 예다.

> `A`와 `A?`는 같은 클래스  
`A`는 `A?`의 하위 타입 o  
`A?`는 `A`의 하위 타입 x
>> `A` -o-> `A?`  
`Int` -o-> `Int?`  
`Int?` -x-> `Int`  


`MutableList<String>` -x-> `MutableList<Any>`  
`MutableList<Any>` -x-> `MutableList<String>`


제네릭 타입을 인스턴스화할 때  
타입 인자로 서로 다른 타입이 들어가면  
인스턴스 타입 사이의 하위 타입 관계가 성립하지 않으면  
그 제네릭 타입을 **무공변(invariant)** 이라고 말한다.

`MutableList`를 예로 들면  
`A`와 `B`가 서로 다르기만 하면  
`MutableList<A>`는 항상 `MutableList<B>`의 **하위 타입이 아니다.**

자바에서는 모든 클래스가 무공변이다.

## 9.3.3 공변성: 하위 타입 관계를 유지

> `A`가 `B`의 하위 타입일 때  
`Producer<A>`가 `Producer<B>`의 하위 타입이면  
`Producer`는 **공변적(covariant)** 이다.
>> -> 하위 타입 관계가 유지된다.

예) `Cat`이 `Animal`의 하위 타입이기 때문에  
`Producer<Cat>`은 `Producer<Animal>`의 하위 타입이다.

코틀린에서 제네릭 클래스가 타입 파라미터에 대해 공변적임을 표시하려면  
타입 파라미터 이름 앞에 **`out`** 을 넣어야 한다. 

```kotlin
// 클래스가 T에 대해 공변적이라고 선언
interface Producer<out T>{
    fun produce(): T
}
```

클래스의 타입 파라미터를 공변적으로 만들면  
함수 정의에 사용한 파라미터 타입과 타입 인자의 타입이 정확히 일치하지 않더라도  
그 클래스의 인자나 반환값으로 사용 가능함.

```kotlin
// 무공변 컬렉션 역할을 하는 클래스 정의하기
open class Animal{
    fun feed(){...}
}
 
class Herd<T : Animal> {
    val size: Int get() = ...
    operator fun get(i: Int): T {...}
}

fun feedAll(animals: Herd<Animal>) {
    for(i in 0 until animals.size){
        animals[i].feed()
    }
}
```

```kotlin
// 무공변 컬렉션 역할을 하는 클래스 사용하기
class Cat: Animal() {
    fun cleanLitter(){...}
}

fun takeCareOfCats(cats: Herd<Cat>) {
    for(i in 0 until cats.size){
        cats[i].cleanLitter()
        
        // feedAll(cats)
        // 타입 불일치 오류 발생
    }
}
```

`Herd` 클래스의 `T` 타입 파라미터에 대해 아무 변성도 지정하지 않았기 때문에  
고양이 무리는 동물 무리의 하위 클래스가 아니다.

`Herd` 클래스는 `List`와 비슷한 API를 제공하며  
동물을 추가하거나 변경은 불가능하다.  
따라서 `Herd`를 공변적인 클래스로 만들고 호출 코드를 적절히 바꾼다.

```kotlin
// 공변적 컬렉션 역할을 하는 클래스 사용하기

class Herd<out T : Animal> { 
// T는 이제 공변적이다.
    //...
}

fun takeCareOfCats(cats: Herd<Cat>) {
    for(i in 0 until cats.size){
        cats[i].cleanLitter()
    }
    feedAll(cats)
    // 캐스팅 할 필요 없음
}
```

모든 클래스를 공변적으로 만들 수는 없다.

타입 파라미터를 공변적으로 지정하면 클래스 내부에서 그 파라미터를 사용하는 방법을 제한한다.

타입 안전성을 보장하기 위해 공변적 파라미터는 항상 **out** 위치에만 있어야 한다.

이는 클래스가 T 타입의 값을 생산할 수는 있지만 소비할 수는 없다는 뜻.

클래스 멤버를 선언할 때 타입 파라미터를 사용할 수 있는 지점은  
모두 인과 아웃 위치로 나뉜다.




## 9.3.4 반공변성: 뒤집한 하위 타입 관계

**반공변성(contravariance)** : 공변성을 거울에 비친 상.  
반공변 클래스의 하위 타입 관계는 공변 클래스의 경우와 반대다.

`Consumer<A>`가 `Consumer<B>`의 하위 타입인 관계가 성립하면 제네릭 클래스 `Consumer<T>`는 타입 인자 T에 대해 반공변이다.

여기서 A와 B의 위치가 서로 뒤바뀐다.  
따라서 하위 타입 관계가 뒤집힌다고 말한다. 

예) `Consumer<Animal>`은 `Consumer<Cat>`의 하위 타입이다.

> 공변성 타입 `Producer<T>`에서는 타입 인자의 하위 타입 관계가 제네릭 타입에서도 유지되지만  
반공변성 타입 `Consumer<T>`에서는 타입 인자의 하위 타입 관계가 제네릭 타입으로 오면서 뒤집힌다.

**in**이라는 키워드는 그 키워드가 붙은 타입이 이 클래스의 메소드 안으로 전달돼 메소드에 의해 소비된다는 뜻.

`in` 키워드를 타입 인자에 붙이면 그 타입 인자를 오직 인 위치에서만 사용할 수 있다는 뜻.

#### 공변성, 반공변성, 무공변성 클래스
공변성 | 반공변성 | 무공변성
--------- | --------- | ---------
`Producer<out T>` | `Consumer<in T>` | `MutableList<T>`
타입 인자의 하위 타입 관계가 제네릭 타입에서도 유지된다. | 타입 인자의 하위 타입 관계가 제네릭 타입에서 뒤집힌다. | 하위 타입 관계가 성립하지 않는다.
`Producer<Cat>`은 `Producer<Animal>`의 하위 타입 | `Consumer<Animal>`은 `Consumer<Cat>`의 하위 타입 | 
T를 아웃 위치에서만 사용 가능 | T를 인 위치에서만 사용 가능 | T를 아무 위치에서나 사용 가능


```kotlin
interface Functional<in P, out R> {
    operator fun invoke(p: P): R
}
```

> 함수 타입 `(T) -> R`은  
인자의 타입에 대해서는 반공변적이면서  
반환 타입에 대해서는 공변적이다.


## 9.3.5 사용 지정 변성: 타입이 언급되는 지점에서 변성 지정

- **선언 지점 변성** : 클래스를 선언하면서 변성 지정

- **사용 지점 변성** : 타입 파라미터가 있는 타입을 사용할 때마다  
해당 파라미터를 하위 타입이나 상위 타입 중 어떤 타입으로 대치할 수 있는지를 명시

> 코틀린도 사용 지점 변성을 지원한다.

따라서 클래스 안에서 어떤 타입 파라미터가 공변적이거나 반공변적인지 선언할 수 없는 경우에도   
특정 타입 파라미터가 나타나는 지점에서 변성을 정할 수 있다.

```kotlin
// 무공변 파라미터 타입을 사용하는 데이터 복사 함수
fun <T> copyData(s: MutableList<T>, d: MutableList<T>) {
    for( item in s ){
        d.add(item)
    }
}

// 두 컬렉션의 원소 타입이 정확하게 일치하지 않아도 된다.
```

```kotlin
// 타입 파라미터가 2개인 데이터 복사 함수

// s의 원소 타입은 d의 원소 타입의 하위 타입이어야 함.
fun <T: R, R> copyData(s: MutableList<T>, d: MutableList<R>) {
    for( item in s ){
        d.add(item)
    }
}

val ints = mutableListOf(1, 2, 3)

val anyItems = mutableListOf<Any>()

copyData(ints, anyItems)
// Int가 Any의 하위 타입이므로 호출 가능함.

println(anyItems)
// [1, 2, 3]
```

```kotlin
// 아웃-프로젝션 타입 파라미터를 사용하는 데이터 복사 함수

// out 키워드를 타입을 사용하는 위치 앞에 붙이면 
// T 타입을 in 위치에 사용하는 메소드를 호출하지 않는다는 뜻
fun <T> copyData(s: MutableList<out T>, d: MutableList<T>) {
    for( item in s ){
        d.add(item)
    }
}
```

타입 선언에서 타입 파라미터를 사용하는 위치라면 어디에나 변성 변경자를 붙일 수 있다.

따라서 파라미터 타입, 로컬 변수 타입, 함수 반환 타입 등에 타입 파라미터가 쓰이는 경우 `in` 이나 `out` 변경자를 붙일 수 있다. 

이 때 **타입 프로젝션**이 일어난다.

즉, s를 MutableList의 프로젝션을 한(제약을 가한) 타입으로 만든다.


```kotlin
// in 프로젝션 타입 파라미터를 사용하는 데이터 복사 함수

// 원본 리스트 원소 타입의 상위 타입을
// 대상 리스트 원소 타입으로 허용한다.
fun <T> copyData(s: MutableList<T>, d: MutableList<in T>) {
    for( item in s ){
        d.add(item)
    }
}
```

## 9.3.6 스타 프로젝션: 타입 인자 대신 * 사용

`MutableList<*>`는 `MutableList<Any?>`와 같지 않다.  
`MutableList<*>` 타입의 리스트에서 원소를 얻을 수는 있다.  
타입을 모르는 리스트에 원소를 마음대로 넣을 수는 없다.

타입 파라미터를 시그니처에서 언급하지 않거나  
데이터를 읽기는 하지만 그 타입에는 관심이 없는 경우와 같이 타입 인자 정보가 중요하지 않을 때도 스타 프로젝션 구문을 사용할 수 있다.
```kotlin
fun printFirst(list: List<*>) {
// 모든 리스트를 인자로 받을 수 있음
    if(list.isNotEmpty()){
    // isNotEmpty에서는 제네릭 타입 파라미터 사용 안함
        println(list.first())
        // first()는 이제 Any?를 반환하지만
        // 여기서는 그 타입만으로 충분하다.
    }
}
```

사용 지점 변성과 마찬가지로 이런 스타 프로젝션도 우회하는 방법이 있다.
-> 제네릭 타입 파라미터 도입하면 됨.
```kotlin
fun <T> printFirst(list: List<T>) {
// 모든 리스트를 인자로 받을 수 있음
    if(list.isNotEmpty()){
        println(list.first())
        // first()는 이제 T 타입의 값을 반환
    }
}
```

```kotlin
// 입력 검증을 위한 인터페이스

// T에 대해 반공변인 인터페이스를 선언한다.
interface FieldValidator<in T>{
    fun validate(input: T): Boolean
    // T를 in 위치에만 사용한다.
    // 이 메소드는 T 타입의 값을 소비한다.
}

object DefaultStringValidator : FieldValidator<String> {
    override fun validate(input: String) = input.isNotEmpty()
}

object DefaultIntValidator : FieldValidator<Int> {
    override fun validate(input: Int) = input >= 0
}
```

```kotlin
// 검증기를 가져오면서 명시적 타입 캐스팅 사용하기
val stringValidator = validators[String::class] 
    as FieldValidator<String>
    // warning  

println(stringValidator.validate(""))
// false
```
위 코드는  
컴파일러는 타입 캐스팅이 안전하지 못하다고 경고한다.  
값을 검증하는 메소드 안에서 실패한다.  

실행 시점에 모든 제네릭 타입 정보는 사라지므로  
타입 캐스팅은 문제가 없고   
검증 메소드 안에서 값의 메소드나 프로퍼티를 사용할 때 문제 발생

```kotlin
// 검증기를 잘못 가져온 경우

// 검증기를 잘못 가져왔지만 컴파일고 타입 캐스팅 시 아무 문제 없음
val stringValidator = validators[Int::class] 
    as FieldValidator<String>
    // warning

stringValidator.validate("")
// ClassCastException 발생
// 검증기를 사용해야 비로소 오류 발생함
```


```kotlin
// 검증기 컬렉션에 대한 접근 캡슐화하기

object Validators {
    private val validators = 
        mutableMapOf<KClass<*>, FieldValidator<*>>()

        fun <T: Any> registerValidator(
            KClass: KClass<T>, fieldValidator<T>) {
            validators[KClass] = fieldValidator
        }

        @Suppress("UNCHECKED_CAST")
        operator fun <T: Any> get(kClass: KClass<T>): FieldValidator<T> = 
        validators[kClass] as? FieldValidator<T> 
        ?: throw IllegalArgumentException("..")
}

```

안전하지 못한 로직은 클래스 내부에 감춤으로써 이제는 외부에서 그 부분을 잘못 사용하지 않음을 보장할 수 있다. 
 
`Validators` 객체에 있는 제네릭 메소드에서  
검증기(`FieldValidator<T>`)와  
클래스(`KClass<T>`)의 타입 인자가 같기 때문에  
컴파일러가 타입이 일치하지 않는 클래스와 검증기를 등록하지 못하게 한다.


