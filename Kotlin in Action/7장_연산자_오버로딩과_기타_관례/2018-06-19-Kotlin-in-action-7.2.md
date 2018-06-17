# 7.2 비교 연산자 오버로딩
코틀린에서는 모든 객체에 대해 비교 연산을 수행할 수 있다.

## 7.2.1 동등성 연산자: equals

== 와 !=는 내부에서 인자가 널인지 검사하므로 널이 될 수 있는 값에도 적용 가능함.

> 동등성 검사 ==는 equals 호출과 널 검사로 컴파일된다.  
>>` a == b ` ---> `a?.equals(b) ?: (b == null) `

```kotlin
// equals 메소드 직접 구현하기
class Point(val x: Int, val y: Int){
    override fun equals(obj: Any?): Boolean{
        if(obj === this) return true

        if(obj !is Point) return false

        return obj.x == x && obj.y == y
    }
}
```

- 식별자 비교 연산자 (===)를 사용해 같은 객체인지 검사한다.  
(서로 같은 객체를 참조하는지. 원시 타입이라면 두 값이 같은지.)

- === 를 오버로딩할 수는 없다.

- equals 앞에는 override를 붙인다. Any에 정의된 연산자 메소드를 오버라이드하기 때문. 

- Any에서 상속받은 equals가 확장 함수보다 우선순위가 높기 때문에 equals를 확장 함수로 정의할 수 없다.


## 7.2.2 순서 연산자: compareTo

자바에서는 e1.compareTo(e2)를 명시적으로 사용해야 한다.

- 코틀린은 compareTo 메소드를 호출하는 관례를 제공한다.  

- 비교 연산자(<, >, <=, >=)는 compareTo 호출로 컴파일된다. 

> 두 객체를 비교하는 식은 compareTo의 결과를 0과 비교하는 코드로 컴파일된다. 
>>` a >= b ` ---> `a.compareTo(b) >= 0  `


```kotlin
class Person(val firstName: String, val lastName: String): Comparable<Person>{
    override fun compareTo(other: Person): Int{
        return compareValuesBy(this, other, Person::lastName, Person::firstName)
        // 인자로 받은 함수를 차례로 호출하면서 값을 비교한다. 
        // 성을 먼저 비교하고, 성이 같으면 이름을 비교한다.
    }
}

val p1 = Person("Alice", "Smith")
val p2 = Person("Bob", "Johnson")
println(p1 < p2)
// false

```

- 틀린 표준 라이브러리의 compareValuesBy는 0이 아닌 값이 처음 나올때까지 인자로 받은 함수를 차례로 호출해 두 값을 비교하며, 모든 함수가 0을 반환하면 0을 반환한다. 

- 각 비교 함수는 람다나 프로퍼티/메소드 참조일 수 있다. 

- 필드를 직접 비교하면 비교 속도가 훨씬 더 빨라진다. 


```kotlin
// Comparable 인터페이스를 구현하는 모든 자바 클래스를 
// 코틀린에서는 간결한 연산자 구문으로 비교할 수 있다. 
println("abc" < "bac")
//true
``` 

- 비교 연산자를 자바 클래스에 대해 사용하기 위해 특별히 확장 메소드를 만들거나 할 필요는 없다. 