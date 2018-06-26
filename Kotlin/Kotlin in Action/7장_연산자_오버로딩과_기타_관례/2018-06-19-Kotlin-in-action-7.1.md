# 7.1 산술 연산자 오버로딩

## 7.1.1 이항 산술 연산자 오버로딩
```kotlin
// plus 연산자 함수 구현
data class Point(val x: Int, val y: Int){
    operator fun plus(other: Point): Point{
        return Point(x + other.x, y + other.y)
    }
}

println(p1+p2)
// +로 계산하면 plus 함수가 호출된다.
```

- 연산자를 오버로딩하는 함수 앞에는 **operator** 키워드를 붙여야 한다.

` + 연산자는 plus 함수 호출로 컴파일된다. `
> a + b  -->  a.plus(b)

- 연산자를 멤버 함수로 만드는 대신 확장 함수로 정의할 수도 있다.
```kotlin
operator fun Point.plus(other: Point): Point{
    return Point(x + other.x, y + other.y)
}
```

외부 함수의 클래스에 대한 연산자를 정의할 때는 **관례를 따르는 이름의 확장 함수로 구현**하는 게 일반적인 패턴이다. 

- 코틀린에서 미리 정해둔 연산자를 정해진 이름으로만 오버로딩 가능하다.

----
#### 오버로딩 가능한 이항 산술 연산자
식 | 함수 이름
----- | -----
a * b | times
a / b | div
a % b | mod (1.1부터 rem)
a + b | plus
a - b | minus

연산자 우선 순위:  
{( * ) = ( / ) = ( % )} > {(+) = (-)}

----

### 연산자 함수와 자바

모든 오버로딩 연산자는 함수로 정의되며, 
긴 이름(FQN)을 사용하면 일반 함수로 호출할 수 있다.  

자바를 코틀린에서 호출하는 경우,  
함수 이름이 코틀린의 관례에 맞아 떨어지면 연산자 식을 사용해 호출 가능. 

자바 클래스에 이름만 다른 연산자 기능을 제공하는 메소드가 이미 있다면  
관례에 맞는 이름을 가진 확장 함수를 작성하고 연산을 기존 자바 메소드에 위임하면 된다. 

----

- 두 피연산자가 같은 타입일 필요는 없다.
```kotlin
operator fun times(scale: Double): Point{
    return Point((x*scale).toInt(), (y*scale).toInt())
}
```

- 코틀린 연산자가 자동으로 교환 법칙을 지원하지 않는다.  
( point * 1.5 와 1.5 * point를 자동으로 지원 x. 따로 또 함수 정의해주어야 함. )

- 연산자 함수의 반환 타입이 꼭 두 피연산자 중 하나와 일치해야만 하는 것도 아니다.
```kotlin
operator fun Char.times(count: Int): String{
    return toString().repeat(count)
}

print('a' * 3)
// aaa
```

- 이름은 같지만 파라미터 타입이 서로 다른 연산자 함수를 여럿 만들 수 있다.

----
### 비트 연산자에 대해 특별한 연산자 함수를 사용하지 않는다.
다음은 코틀린에서 비트 연산을 수행하는 함수의 목록이다.

* shl - 왼쪽 시프트 (<<)
* shr - 오른쪽 시프트 (부호 비트 유지. >>)
* ushr - 오른쪽 시프트 (부호 비트 0으로. >>>)
* and - 비트 곱 (&)
* or - 비트 합 (|)
* xor - 비트 배타 합 (^)
* inv - 비트 반전 (~)

`0x0F and 0xF0`  
`0x1 shl 4`  

----

## 7.1.2 복합 대입 연산자 오버로딩

- 코틀린은 **복합 대입 연산자** (+=, -= 등..)을 지원한다.

```kotlin
var point = Point(1,2)
point += Point(3,4)
println(point)
// Point(x=4, y=6)
```

복합 대입 연산자는  
연산의 결과가 되는 새로운 객체로 참조를 변경하게 되는데,  
참조 대상을 변경하지 않고 원래 객체의 내부 상태를 변경하게 만들고 싶다면?

- 반환 타입이 Unit인 plusAssign 함수를 정의하면 코틀린은 += 연산자에 그 함수를 사용한다. ( timesAssign, minusAssign 등..)

```kotlin
// 변경 가능한 컬렉션에 대해 plusAssign 정의
operator fun <T> MutableCollection<T>.plusAssign(element: T){
    this.add(element)
}
```

- plus와 plusAssign 함수를 동시에 정의하지 말라.  
둘 다 정의하면 양쪽을 컴파일하는 오류 발생.
    - 일반 연산자를 사용해 오류 해결
    - var를 val로 바꿔 plusAssign 적용이 불가능하게 하는 방법 
    - 하지만, 위의 두 방법보다는 동시에 정의하지 않는 것 추천.

- 클래스가 변경 불가능하다면 plus와 같이 새로운 값을 반환하는 연산만 추가하도록.
- 빌더와 같이 변경 가능한 클래스를 설계한다면 plusAssign 같은 연산만 제공하라.


코틀린 표준 라이브러리는 컬렉션에 대해 두 가지 접근 방법을 함께 제공한다. 
* +와 - : 새로운 컬렉션 반환 
* +=과 -= : 항상 변경 가능한 컬렉션에 적용해 메모리에 있는 객체 상태를 변화시킨다.  
읽기 전용 컬렉션에서는 변경을 적용한 복사본을 반환한다.  


```kotlin
val list = arrayListOf(1,2)

list += 3 
// +=는 "list"를 변경한다.

val newList = list + listOf(4,5) 
// +는 두 리스트의 모든 원소를 포함하는 새로운 리스트를 반환

println(list)
// [1,2,3]

println(newList)
// [1,2,3,4,5]
```

## 7.1.3 단항 연산자 오버로딩

단항 연산자를 오버로딩하는 절차도 이항 연산자와 같다.
```kotlin
operator fun Point.unaryMinus(): Point{
    return Point(-x, -y)
}

val p = Point(10,20)
println(-p)
// Point(x=-10, y=-20)
```

- 단항 연산자 오버로딩 함수는 인자를 취하지 않음.

----
#### 오버로딩 가능한 단항 산술 연산자
식 | 함수 이름
----- | -----
+a | unaryPlus
-a | unaryMinus
!a | not
++a, a++ | inc
--a, a-- | dec

----

```kotlin
operator funn BigDemical.inc() = this+ BigDemical.ONE

var bd = BigDemical.ZERO
println(bd++)
// 0 
println(++bd)
// 2
```

