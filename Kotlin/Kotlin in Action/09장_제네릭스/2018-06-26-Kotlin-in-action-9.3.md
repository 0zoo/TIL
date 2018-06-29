# 9.3 변성: 제네릭과 하위 타입

- **변성(variance)** :  
`List<String>`과 `List<Any>`와 같이  
기저 타입이 같고(`List`)  
타입 인자가 다른(`<String>`,`<Any>`)  
여러 타입이 어떤 관계가 있는지 설명하는 개념.

## 9.3.1 변성이 있는 이유: 인자를 함수에 넘기기

`(list: List<Any>)` 에 `List<String>` 넘기기  
-> 정상 컴파일

`(list: MutableList<Any>)` 에 `MutableList<String>` 넘기기  
-> 컴파일 에러 (_ClassCastException_)

**리스트의 변경 가능성이 없어야한다.** 

> 함수가 읽기 전용 리스트를 받는다면  
더 구체적인 타입의 원소를 갖는 리스트를 넘길 수 있다.


## 9.3.2 클래스, 타입, 하위 타입

클래스가 같아도 둘 이상의 타입을 구성할 수 있다. (Nullable, NotNull)
- `String` 클래스 
    - `String` 타입
    - `String?` 타입


제네릭 클래스는 무수히 많은 타입을 만들어낼 수 있다.  
(타입 인자를 치환한 `List<String>`, `List<String?>`, `List<List<String>>` 등)

- `Int`: `Number`의 **하위 타입(subtype)**
- `Number`: `Int`의 **상위 타입(supertype)**

> `A`타입의 자리에 `B`타입의 값을 넣어도 아무 문제가 없다면  
`B`는 `A`의 **하위 타입** 이다.

(상위 타입의 자리에 하위 타입을 넣어도 문제 발생 x)

- 모든 타입은 자신의 하위 타입이다.

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

간단한 경우,  
하위 타입은 **하위 클래스(subclass)** 와 근본적으로 같다.

**인터페이스를 구현하는 클래스의 타입**은  
그 **인터페이스 타입의 하위 타입**이다.

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

**무공변(invariant)** 타입 조건
1. 타입 인자로 서로 다른 타입이 들어가고
2. 인자들 간에 하위 타입 관계가 성립하지 않으면

- 자바에서는 모든 클래스가 무공변이다.

## 9.3.3 공변성: 하위 타입 관계를 유지

`Producer`가 **공변적(covariant)** 일 조건
1. `A`가 `B`의 하위 타입
2. `Producer<A>`가 `Producer<B>`의 하위 타입  

-> 하위 타입 관계가 유지된다.  
예) `Producer<Cat>` - `Producer<Animal>`

> 제네릭 클래스가 타입 파라미터에 대해 **공변적**임을 표시하려면  
타입 파라미터 이름 앞에 **`out`** 을 넣어야 한다. 

```kotlin
// 클래스가 T에 대해 공변적이라고 선언
interface Producer<out T>{
    fun produce(): T
}
```

클래스의 타입 파라미터를 공변적으로 만들면  
함수 정의에 사용한 파라미터 타입과   
타입 인자의 타입이  
정확히 일치하지 않더라도  
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

`T`라는 타입 파라미터를 선언하고 `T`를 사용하는 함수가 멤버로 있는 클래스  
- T가 함수의 **반환 타입**으로 사용.  
T는 `out` 위치  
그 함수는 T 타입의 값을 **생산(produce)** 한다.  

- T가 함수의 **파라미터 타입**으로 사용.  
T는 `in` 위치  
그 함수는 T 타입의 값을 **소비(consume)** 한다.  

```kotlin
interface Transformer<T> {
    fun transform(t: T): T
}
```

> `out` 키워드는 아웃 위치에서만 T를 사용하게 **제한**하며  
T로 인해 생기는 하위 타입 관계의 **타입 안전성을 보장**한다.


```kotlin
// T를 오직 get 메소드의 반환 타입으로만 사용.
class Herd<out T : Animal> {
    val size: Int get() = ...

    operator fun get(i: Int): T {...}
    // T를 반환 타입으로 사용
    // -> 아웃 위치
}
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
`MutableList<T>`에서는 T가 인과 아웃 위치에 동시에 쓰인다.

**생성자 파라미터는 인이나 아웃 어느쪽도 아니라는 사실**에 유의하자.  

타입 파라미터가 `out`이라고 해도  
그 타입을 여전히 생성자 파라미터 선언에 사용 가능하다.

```kotlin
class Herd<out T: Animal>(vararg animals: T){...}
```

- 읽기 전용 프로퍼티: 아웃 위치  
- 변경 가능 프로퍼티: 아웃, 인 위치 모두

```kotlin
class Herd<T: Animal> 
    (var leadAnimal: T, vararg animals: T){...}

// 생성자 파라미터에 var
// 게터, 세터

// 여기서는 T 타입인 leadAnimal 프로퍼티가
// 인 위치에 있기 때문에 
// (함수의 파라미터 타입)
// T를 out으로 표시 불가
```

이런 위치 규칙은 오직 외부에서 볼 수 있는  
(`public`, `protected`, `internal`)  
클래스 API에만 적용 가능함.

private 메소드의 파라미터는  in도 아니고 out도 아닌 위치.


out 위치 | in 위치 
--------- | --------- 
`: T` | `(t: T)`
함수의 반환 타입 | 함수의 파라미터 타입
T 타입 생산 | T 타입 소비 
읽기 전용, 변경 가능 프로퍼티 | 변경 가능 프로퍼티


## 9.3.4 반공변성: 뒤집힌 하위 타입 관계

**반공변성(contravariance)** : 반공변 클래스의 하위 타입 관계는 공변 클래스와 **반대**

```kotlin
interface Comparator<in T>{
    
    fun compare(e1: T, e2: T): Int{...}
    // T 타입의 값을 소비만 함
    // T가 `in` 위치에서만 쓰인다는 뜻
}
```

```kotlin
val anyComparator = Comparator<Any> {
        e1, e2 -> e1.hashcode() - e2.hashcode()
    }

val strings: List<String> = ...

strings.sortedWith(anyComparator)
// 문자열과 같은 구체적인 타입의 객체 비교 가능
```
`sortedWith` 함수는 `Comparator<String>`을 요구하므로,  
`String`보다 더 일반적인 타입을 비교할 수 있는 `Comparator`를 넘기는 것은 안전하다.

- `Comparator<Any>` ---> `Comparator<String>`
- (타입 인자) `Any` <--- `String` 

-> **`Comparator`** 는 **타입 인자**의 하위 타입 관계와 **정반대**

`Consumer<T>`가 타입 인자 T에 대해 **반공변**이 될 조건 :
1. 타입 `B`가 타입 `A`의 하위 타입  
2. `Consumer<A>`가 `Consumer<B>`의 하위 타입인 관계

---

> `Cat` ----> `Animal`   
> `Producer<Cat>` --공변성--> `Producer<Animal>`   
> `Consumer<Cat>` <--반공변성-- `Consumer<Animal>`   

- 공변성 타입 `Producer<T>`:  
    - 타입 인자의 하위 타입 관계 유지됨. 

- 반공변성 타입 `Consumer<T>`:  
    - 타입 인자의 하위 타입 관계 뒤집힘.
---

> **in** 키워드: 키워드가 붙은 타입이 메소드에 의해 소비된다는 뜻.

공변성의 경우와 마찬가지로 `in` 위치에서만 사용할 수 있게 하여서 타입 파라미터의 사용을 제한함.

---
---
#### 표 9.1 공변성, 반공변성, 무공변성 클래스 요약

공변성 | 반공변성 | 무공변성
--------- | --------- | ---------
`Producer<out T>` | `Consumer<in T>` | `MutableList<T>`
타입 인자의 하위 타입 관계가 제네릭 타입에서도 유지된다. | 타입 인자의 하위 타입 관계가 제네릭 타입에서 뒤집힌다. | 하위 타입 관계가 성립하지 않는다.
`Producer<Cat>`은 `Producer<Animal>`의 하위 타입 | `Consumer<Animal>`은 `Consumer<Cat>`의 하위 타입 | 
T를 out 위치에서만 사용 가능 | T를 in 위치에서만 사용 가능 | T를 아무 위치에서나 사용 가능

---
---

```kotlin
interface Function1<in P, out R> {
    operator fun invoke(p: P): R
}

// 함수의 하위 타입 관계는
// 첫 번째 타입 인자의 하위 타입 관계와는 반대지만
// 두번째 타입 인자의 하위 타입 관계와는 같음을 뜻한다.
```

> `(P) -> R` 와 `Function1<P, R>`은 같음
>> P : 함수 파라미터의 타입  
R : 함수 반환 타입



예를 들어 동물을 인자로 받아서 정수를 반환하는 람다를 고양이에게 번호를 붙이는 고차 함수에 넘길 수 있다.

```kotlin
// 동물을 인자로 받아서 정수를 반환하는 람다를
// 고양이에게 번호를 붙이는 고차 함수에 넘길 수 있다.

fun enumerateCats(f: (Cat) -> Number) {...}
fun Animal.getIndex(): Int = ...

enumerateCats(Animal::getIndex)
```

> 함수 타입 `(T) -> R`은  
인자의 타입에 대해서는 반공변적(*in*)이면서  
반환 타입에 대해서는 공변적(*out*)이다.

> `Animal` ----> `Cat`   
> `Int` ----> `Number`   
> `(Animal) -> Int` ----> `(Cat) -> Number`   


## 9.3.5 사용 지정 변성: 타입이 언급되는 지점에서 변성 지정

- **선언 지점 변성** (*declaration site variannce*) : 클래스를 선언하면서 변성을 지정해 그 클래스를 사용하는 모든 장소에 변성 지정자가 영향을 끼친다.

- **사용 지점 변성** (*use-site variannce*) : 타입 파라미터가 있는 타입을 사용할 때마다 해당 파라미터를 어떤 타입으로 대치할 수 있는지를 명시해야 한다. (자바)

---
### 코틀린 선언 지점 변성과 자바 와일드 카드 비교

선언 지점 변성을 사용하면 클래스 선언 지점에서 변성을 한 번만 지정하고  
클래스를 사용하는 쪽에서는 변성에 대해 신경 쓸 필요가 없기 때문에 코드 간결해짐.

자바에서 사용자의 예상대로 작동하는 API를 만들기 위해서는  
`Function< ? super T, ? extends R>` 같은 와일드 카드 사용해야 함.

---

> 코틀린도 사용 지점 변성을 지원한다.

`MutableList` 같은 상당수의 인터페이스는 타입 파라미터를 소비와 생산을 둘 다 할수 있어 공변적이지도 반공변적이지도 않다.  
하지만 이런 인터페이스 타입의 변수가 한 함수 안에서 생산자나 소비자 중 단 한가지 역할만을 하는 경우가 자주 있다.

```kotlin
// 무공변 파라미터 타입을 사용하는 데이터 복사 함수
fun <T> copyData(s: MutableList<T>, d: MutableList<T>) {
    for( item in s ){
        d.add(item)
    }
}
// 두 컬렉션의 원소 타입이 정확하게 일치하지 않아도 된다.
```
위 함수가 여러 다른 리스트 타입에 대해 작동하게 만들기 위해 
두 번째 제네릭 타입 파라미터를 도입

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

copyData 함수처럼 아웃 위치 또는 인 위치에 있는 타입 파라미터를 사용하는 메소드만 호출한다면  
함수 정의 시 타입 파라미터에 변성 변경자를 추가할 수 있다.


```kotlin
// 아웃-프로젝션 타입 파라미터를 사용하는 데이터 복사 함수

// out 키워드를 타입을 사용하는 위치 앞에 붙이면 
// T 타입을 in 위치에 사용하는 메소드를 호출하지 않는다는 뜻
fun <T> copyData(s: MutableList<out T>, d: MutableList<T>) {
    for( item in s ){
        d.add(item)
    }
}

val list: MutableList<out Number> = ...
list.add(42) // error!!
// 컴파일러는 타입 파라미터 T를 함수 인자 타입으로 사용하지 못하게 함.
```

**타입 프로젝션**  
s를 MutableList의 프로젝션을 한(제약을 가한) 타입으로 만든다.

`copyData`는 `MutableList`의 메소드 중  
반환 타입으로 타입 파라미터 T를 사용하는 메소드만 호출할 수 있다.  

List의 정의는 이미 `class List<out T>`이므로 `List<out T>`는 그냥 `List<T>`와 같다. -> 불필요한 프로젝션

```kotlin
// in 프로젝션 타입 파라미터를 사용하는 데이터 복사 함수

// in을 붙여 그 파라미터를 더 상위 타입으로 대치할 수 있다.
fun <T> copyData(s: MutableList<T>, d: MutableList<in T>) {
    for( item in s ){
        d.add(item)
    }
}
```

---
코틀린의 사용 지점 변성 선언은 자바의 한정 와일드카드 (bounded wildcard)와 똑같다.

- 코틀린 `MutableList<out T>` == 자바 `MutableList<? extends T>`

- 코틀린 `MutableList<in T>` == 자바 `MutableList<? super T>`

---

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


