# 9.3 변성: 제네릭과 하위 타입

- **변성(variance)** :  
`List<String>`과 `List<Any>`와 같이  
기저 타입이 같고(`List`)  
타입 인자가 다른(`<String>`,`<Any>`)  
여러 타입이 어떤 관계가 있는지 설명하는 개념.

## 9.3.1 변성이 있는 이유: 인자를 함수에 넘기기

```kotlin
// List<Any> <- List<String>
// 정상 컴파일
fun printContents(list: List<Any>) {
    println(list.joinToString())
}

printContents(listOf("abc", "bac"))
// abc, bac
```

```kotlin
// MutableList<Any> <- MutableList<String>
// 컴파일 에러
fun addAnswer(list: MutableList<Any>) {
    list.add(42)
}

val strings = mutableListOf("abc", "bac")

addAnswer(strings) // 실행시 에러

println(strings.maxBy{ it.length })
// ClassCastException Error

```

**리스트의 변경 가능성이 없어야한다.** 

> 함수가 읽기 전용 리스트를 받는다면  
더 구체적인 타입의 원소를 갖는 리스트를 넘길 수 있다.


## 9.3.2 클래스, 타입, 하위 타입

타입과 클래스의 차이:  
클래스가 같아도  
둘 이상의 타입을 구성할 수 있음.  
(Nullable, NotNull)

- `String` 클래스 
    - `String` 타입
    - `String?` 타입


올바른 타입을 얻으려면 제네릭 타입의 타입 파라미터를  
구체적인 타입 인자로 바꿔줘야 한다.

 제네릭 클래스는 무수히 많은 타입을 만들어낼 수 있다.  
(타입 인자를 치환한 `List<String>`, `List<String?>`, `List<List<String>>` 등)

- `Int`: `Number`의 **하위 타입(subtype)**
- `Number`: `Int`의 **상위 타입(supertype)**

> `A`타입의 자리에 `B`타입의 값을 넣어도 아무 문제가 없다면  
`B`는 `A`의 **하위 타입** 이다.

(상위 타입의 자리에 하위 타입을 넣어도 문제 발생 x)

>> 모든 타입은 자신의 하위 타입이다.

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

**널이 될 수 있는 타입**은 **하위 타입과 하위 클래스가 같지 않은 경우**를 보여주는 예다.

> `A`와 `A?`는 같은 클래스  
`A`는 `A?`의 하위 타입 o  
`A?`는 `A`의 하위 타입 x
>> `A` -o-> `A?`  
`Int` -o-> `Int?`  
`Int?` -x-> `Int`  

A 타입의 자리에 A? 타입은 올 수 없다.

`MutableList<String>` -x-> `MutableList<Any>`  
`MutableList<Any>` -x-> `MutableList<String>`  
(타입 변경 가능성 존재하기 때문에)

제네릭 타입을 인스턴스화할 때  
타입 인자로 서로 다른 타입이 들어가고  
인자들 간에 **하위 타입 관계가 성립하지 않으면**  
그 제네릭 타입을 **무공변(invariant)** 이라고 말한다.

`MutableList`를 예로 들면  
`A`와 `B`가 서로 다르기만 하면  
`MutableList<A>`는 항상 `MutableList<B>`의 **하위 타입이 아니다.**

> 자바에서는 모든 클래스가 무공변이다.

## 9.3.3 공변성: 하위 타입 관계를 유지

타입의 원소가 변경 불가능함을 보장한다면 공변적??이라는건가..

> `A`가 `B`의 하위 타입일 때  
`Producer<A>`가 `Producer<B>`의 하위 타입이면  
`Producer`는 **공변적(covariant)** 이다.
>> -> 하위 타입 관계가 유지된다.  
> 예) `Producer<Cat>`와 `Producer<Animal>`

코틀린에서 제네릭 클래스가 타입 파라미터에 대해 **공변적**임을 표시하려면  
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
open class Animal{
    fun feed(){...}
}
 
class Herd<T : Animal> {
// 타입 파라미터를 무공변성으로 지정
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
        // Herd 클래스의 T 타입 파라미터에 대해
        // 아무 변성도 지정하지 않았기 때문에
        // 고양이 무리는 동물 무리의 하위 클래스가 아님.
    }
}
```

`Herd` 클래스는 동물을 추가하거나 변경은 불가능하다.  
--> `Herd`를 공변적인 클래스로 만들자

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

**타입 안전성을 보장**하기 위해  
공변적 파라미터는 항상 **out** 위치에만 있어야 한다.  
> 클래스가 T 타입의 값을 **생산**할 수는 있지만 소비할 수는 없다는 뜻.

클래스 멤버를 선언할 때 타입 파라미터를 사용할 수 있는 지점은  
모두 `in`과 `out` 위치로 나뉜다.

`T`라는 타입 파라미터를 선언하고 T를 사용하는 함수가 멤버로 있는 클래스를 생각해보자.  
- T가 함수의 **반환 타입**에 쓰인다면 T는 `out` 위치에 있다.  
그 함수는 T타입의 값을 **생산(produce)** 한다.  
- T가 함수의 **파라미터 타입**에 쓰인다면 T는 `in` 위치에 있다.  
그 함수는 T타입의 값을 **소비(consume)** 한다.  

> 함수 파라미터 타입은 인 위치, 함수 반환 타입은 아웃 위치에 있다.
```kotlin
interface Transformer<T> {
    fun transform(t: T): T
}
```
> `(t: T)` - 인 위치  
`: T` - 아웃 위치

클래스 파라미터 T 앞에 out 키워드를 붙이면  
클래스 안에서 T를 사용하는 메소드가  
아웃 위치에서만 T를 사용하게 허용하고  
인 위치에서는 사용 못하게 막음.

> `out` 키워드는 T의 사용법을 **제한**하며  
T로 인해 생기는 하위 타입 관계의 **타입 안전성을 보장**한다.


```kotlin
// T를 오직 get 메소드의 반환 타입으로만 사용.
class Herd<out T : Animal> {
    val size: Int get() = ...

    operator fun get(i: Int): T {...}
    // T를 반환 타입으로 사용
    // -> 아웃 위치
}
// 따라서 이 함수를 공변적으로 선언해도 안전함.
```
- `T`에 붙은 `out` 키워드의 의미:
    1. **공변성** : 하위 타입 관계가 유지된다.
    2. **사용 제한** : T를 아웃 위치에서만 사용할 수 있다.


코틀린 `List`는 읽기 전용이다.  
-> T 타입의 원소를 반환하는 get 메소드만 있음.  
--> `List`는 `T`에 대해 공변적이다.

```kotlin
interface List<out T> : Collection<T> {
    operator fun get(index: Int): T
    // 읽기 전용 메소드로 T를 반환하는 메소드만 정의
    
    // ...

    // 타입 파라미터를 
    // 함수의 파라미터 타입이나 반환 타입에만
    // 쓸 수 있는 것은 아님.

    // 타입 파라미터를 다른 타입의 타입 인자로 사용 가능함.

    // List<T>를 반환하는 subList가 있다.
    fun subList(from: Int, to: Int): List<T>
    // 여기서도 T는 아웃 위치에 있다.
    // 왜 아웃 위치일까?
    // 정확한 알고리즘은 코틀린 공식 문서 참고.
}
```

`MutableList<T>`를 타입 파라미터 T에 대해 공변적인 클래스로 선언할 수 없다는 점에 유의하자.

```kotlin
// MutableList는 T에 대해 공변적일 수 없다.
interface MutableList<T> 
    : List<T>, MutableCollection<T> {
    
    override fun add(element: T): Boolean
    // 이유는 T가 in 위치에 쓰이기 때문
}
```

`MutableList<T>`에는 T를 인자로 받아서 그 타입의 값을 반환하는 메소드가 있다.  
따라서 T가 인과 아웃 위치에 동시에 쓰인다.

생성자 파라미터는 인이나 아웃 어느쪽도 아니라는 사실에 유의하자.  
타입 파라미터가 out이라고 해도 그 타입을 여전히 생성자 파라미터 선언에 사용 가능하다.
```kotlin
class Herd<out T: Animal>(vararg animals: T){...}
```

변성은 코드에서 위험할 여지가 있는 메소드를 호출 불가능하게 해서 실수를 방지한다.

생성자는 (인스턴스를 생성한 뒤) 나중에 호출할 수 있는 메소드가 아니다.  
따라서 생성자는 위험할 여지가 없다.

하지만 val 이나 var 키워드를 생성자 파라미터에 적는다면  
게터나 세터를 정의하는 것과 같다.  
따라서 읽기 전용 프로퍼티는 아웃 위치,  
변경 가능 프로퍼티는 아웃과 인 위치 모두에 해당한다.

```kotlin
class Herd<T: Animal>(var leadAnimal: T, vararg animals: T){...}

// 여기서는 T 타입인 leadAnimal 프로퍼티가
// 인 위치에 있기 때문에
// T를 out으로 표시 불가
```

이런 위치 규칙은 오직 외부에서 볼 수 있는  
(`public`, `protected`, `internal`)  
클래스 API에만 적용 가능함.

private 메소드의 파라미터는 인도 아니고 아웃도 아닌 위치다.

변성 규칙은 클래스 외부의 사용자가  
클래스를 잘못 사용하는 일을 막기 위한 것이므로  
클래스 내부 구현에는 적용되지 않는다.


## 9.3.4 반공변성: 뒤집힌 하위 타입 관계

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


