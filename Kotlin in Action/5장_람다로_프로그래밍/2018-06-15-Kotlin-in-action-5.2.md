# 5.2 컬렉션 함수형 API

함수형 프로그래밍에서는 람다나 함수를 인자로 받거나 함수를 반환하는 함수를 **고차함수**(_HOF, High Order Function_) 이라고 부른다.  
고차 함수는 기본 함수를 조합해서 새로운 연산을 정의하거나, 다른 고차함수를 통해 조합된 함수를 또 조합해서 더 복잡한 연산을 쉽게 정의할 수 있다는 장점이 있다.   
이런 식으로 고차 함수와 단순한 함수를 조합해서 코드를 작성하는 기법을 **combinator pattern**이라고 부른다.   
컴비네이터 패턴에서 복잡한 연산을 만들기 위해 값이나 함수를 조합할 때 사용하는 고차 함수를 **combinator**라고 부른다.

## 5.2.1 필수적인 함수: filter와 map

**filter** 함수는 컬렉션을 이터레이션하면서 주어진 람다에 각 원소를 넘겨서 람다가 true를 반환하는 원소만 모은다.
```kotlin
val list = listOf(1,2,3,4)
println(list.filter{ it % 2 == 0 })
// [2,4]

val people = listOf(Person("A",12),Person("B",25))
println(people.filter{ it.age > 20 })
// [Person(name=B,age=25)]
```
**filter** 함수는 원소를 변환할 수는 없다.  
원소를 변환하려면 **map** 함수를 사용해야 한다.

**map** 함수는 주어진 람다를 컬렉션의 각 원소에 적용한 결과를 모아서 새 컬렉션을 만든다.
```kotlin
val list = listOf(1,2,3,4)
println(list.map{ it*it })
// [1,4,9,16]

val people = listOf(Person("A",12),Person("B",25))
//println( people.map{ it.name } )
println( people.map(Person::name) )
// [A, B]

// 연쇄 호출 가능
println( people.filter{it.age>20}.map(Person::name) )
//[B]

// 가장 나이 많은 사람 구하기
people.filter{ it.age == people.maxBy(Person::age)!!.age }
// people.maxBy(Person::age)가 null일수도 있기 때문에 !!을 붙여준다. 
// 하지만, 위 식은 매번 최댓값 연산을 반복하기 때문에 비효율적이다.

val maxAge = people.maxBy(Person::age)!!.age
people.filter{ it.age == maxAge }

// 맵에 적용.
val numbers = mapOf(0 of "zero", 1 to "one")
println( numbers.mapValues{it.value.toUpperCase()} )
// [0=ZERO, 1=ONE]
// 그 외에도 filterKeys, filterValues, mapKeys가 있다.
```

## 5.2.2 all, any, count, find: 컬렉션에 술어 적용

*술어(predicate)*: 참 또는 거짓을 반환하는 함수

- **all** : 컬렉션의 모든 원소가 술어를 만족하는지 판단. 
- **any** : 컬렉션의 모든 원소들 중에 술어를 만족하는 원소가 하나라도 있는지 판단.  
- **count** : 조건을 만족하는 원소 개수 반환.
- **find** : 조건을 만족하는 첫번째 원소 반환. 

```kotlin
val canBeIn27 = { p:Person -> p.age<=27 }
val people = listOf(Person("A",27),Person("B",31))

println( people.all(canBeIn27) ) // false

println( people.any(canBeIn27) ) // true

val list = listOf(1,2,3)
println( !list.all{ it==3 } ) // true
println( list.any{ it!=3 } ) // true
// 드모르간의 법칙에 의해 두 결과는 같다. 

println( people.count(canBeIn27) ) // 1
// count와 size를 적절하게 사용하자
println( people.filter(canBeIn27).size ) // 1
// 둘 다 조건에 만족하는 원소의 개수를 반환하지만
// filter로 조건을 검사하고 크기를 세는 경우에는
// 조건을 만족하는 원소들이 있는 중간 컬렉션이 생성되기 때문에
// count가 훨씬 효율적이다. 

println( people.find(canBeIn27) ) 
// Person(name="A",age=27)
// find는 첫번째 원소를 반환.
// 조건을 만족하는 원소가 없으면 null 반환.
// firstOrNull과 같다.

```


## 5.2.3 groupBy: 리스트를 여러 그룹으로 이뤄진 맵으로 변경

```kotlin
val people = listOf(Person("A",31),Person("B",29),Person("C",31))

println( people.groupBy{it.age} )
// { 29=[Person(name=B,age=29)], 31=[Person(name=A,age=31),Person(name=C,age=31)] }

// - 결과 타입: Map<Int, List<Person>>
```
**groupBy**의 결과 Map
- key : 컬렉션의 원소를 구분하는 특성  
위 예제에서는 _it.age_
- value : key값에 따른 각 그룹  
위 예제에서는 _Person_ 객체의 모임

```kotlin
val list = listOf("a", "ab", "b")
println( list.groupBy(String::first) )
// { a=[a,ab], b=[b] }
// first는 String의 확장함수. 멤버 참조 사용 가능
```

## 5.2.4 flatMap과 flatten: 중첩된 컬렉션 안의 원소 처리

- **flatMap** 함수 : 
    1. 인자로 주어진 람다를 컬렉션의 모든 객체에 매핑한다.
    2. 람다를 적용한 결과 얻어지는 여러 리스트를 한 리스트로 펼친다.

```kotlin
val strings = listOf("abc","def")
println( strings.flatMap{ it.toList() } )
// [a, b, c, d, e, f]
```
1.  "abc" ---mapping---> [a,b,c]  
    "def" ---mapping---> [d,e,f]  

2. [a,b,c], [d,e,f] ---flatten---> [a, b, c, d, e, f]

```kotlin
class Book(val title: String, val authors: List<String>)
val books = listOf(Book("1",listOf("A")),Book("2",listOf("B")),Book("3",listOf("A","C")))

// 저자들 리스트
println( books.flatMap{ it.authors } )
// [A, B, A, C]

// 저자들 리스트의 집합
println( books.flatMap{ it.authors }.toSet() )
// [A, B, C]

```

특별히 반환해야 하는 내용이 없는 경우에는 리스트의 리스트를 평평하게 펼치기만 해도 된다. 이럴 경우엔 **flatten()** 함수를 사용하자.

```kotlin
println(books.map{ it.authors })
// [[A], [B], [A, C]]
println(books.map{ it.authors }.flatten())
// [A, B, A, C]
```
