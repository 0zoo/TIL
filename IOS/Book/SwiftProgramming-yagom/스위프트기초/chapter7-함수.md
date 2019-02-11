# Chapter7. 함수

> 하나의 프로그램은 하나의 큰 함수다.

## 7.1 함수와 메서드

정의하는 위치와 호출되는 범위 등에 따라 **호칭이 달라질 뿐, 함수라는 것 자체는 변함 X**

- 함수: 모듈에서 전역적으로 사용할 수 있는 함수
- 메서드: 구조체, 클래스, 열거형 등 특정 타입에 연관되어 사용하는 함수

## 7.2 함수의 정의와 호출

함수에서는 `()` 생략 불가

오버라이드, 오버라이딩 모두 지원

### 7.2.1 기본적인 함수의 정의와 호출

```swift
func `함수 이름` (`매개변수...`) -> `반환 타입` {
    `실행 구문`
    return `반환 값`
}
```

```swift
func hello(name: String) -> String {
    return "Hello \(name)"
}

hello(name:"0zoo")

// 매개변수: name
// 전달인자: 0zoo
```

### 7.2.2 매개변수

#### 매개변수가 없는 함수와 매개변수가 여러 개인 함수

매개변수 없으면 `()`

매개변수 여러개 `(매개변수 이름: 전달인자, 매개변수 이름: 전달인자)`

#### 매개변수 이름과 전달인자 레이블

전달인자 레이블을 별도로 지정하면 함수 외부에서 매개변수의 역할을 좀 더 명확히 할 수 있다.

- 키워드 대부분은 매개변수 이름으로 사용 불가.   
(단, ``으로 감싸주면 사용 가능)

```swift
func `함수 이름` (`전달인자 레이블` `매개변수 이름`: `매개변수 타입`) -> `반환 타입` {
    `실행 구문`
    return `반환 값`
}
```

- 함수 내부에서는 전달인자 레이블 사용 불가
- 함수 호출시 매개변수 이름 사용 불가

```swift
func sayHello(from myName: String, to name: String) -> String {
    return "Hello \(name), I'm \(myName)"
}
sayHello(from: "0zoo", to: "Jenny")
// Hello Jenny, I'm 0zoo
```

전달인자 레이블을 사용하고 싶지 않다면? -> 와일드카드 식별자 `_` 사용

```swift
func sayHello(_ myName: String, _ name: String) -> String {
    return "Hello \(name), I'm \(myName)"
}
sayHello("0zoo", "Jenny")
```

전달인자 레이블을 변경하면 함수의 이름 자체가 변경됨.  
-> 전달인자 레이블만 변경해도 **오버로딩**

```
func sayHello(to name: String, _ times: Int) -> String {
    //...
}

func sayHello(to name: String, repeatCount times: Int) -> String {
    //...
}

// sayHello(to: "AAAA", 2)
// sayHello(to: "AAAA", repeatCount: 2)
```

#### 매개변수 기본값

```swift
func sayHello(_ name: String, times: Int = 3) -> String {
    //...
}
sayHello("0zoo") // times 기본값
sayHello("0zoo", times: 2) //times 2
```

- 기본값이 없는 매개변수가 앞에 와야 한다.

#### 가변 매개변수와 입출력 매개변수

- 가변 매개변수는 0개 이상의 값을 받아올 수 있다.  
- 가변 매개변수로 들어온 인자 값은 배열처럼 사용 가능하다.  
- 함수마다 가변 매개변수는 하나만 가질 수 있다.

```swift
func sayHelloToFriends(me: String, friends names: String...) -> String {
    var result: String = "I'm \(me)!"

    for friend in names {
        result += friend + " "
    }

    return result 
}
sayHelloToFriends(me: "0zoo", friends: "A", "B", "C")
// I'm 0zoo! A B C 
sayHelloToFriends(me: "0zoo")
// I'm 0zoo!
```

함수의 전달인자로 **값을 전달**할 때는 보통 값을 **복사**해서 전달한다.

**참조를 전달**하려면 **입출력 매개변수**를 사용합니다.

참조를 전달인자로 보낼시 함수 내부에서 참조하여 **원래 값을 변경함.** C의 포인터와 비슷.  
이 방법은 함수 외부의 값에 어떤 영향을 줄지 몰라 함수형 프로그래밍에서는 사용 X. (객체지향에서는 O)

애플은 객체지향 패러다임을 사용해서 괜찮을 수 있지만, 다른 함수형 패러다임에서는 입출력 매개변수 사용 X


[입출력 매개변수의 전달 순서]
1. 함수를 호출할 때, 전달인자의 값을 **복사**합니다.
2. 해당 전달인자의 값을 변경하면 1에서 복사한 것을 함수 **내부에서 변경**합니다.
3. 함수를 반환하는 시점에 2에서 변경된 값을 **원래의 매개변수에 할당**합니다.

(연산 프로퍼티 또는 감시자가 있는 프로퍼티가 입출력 매개변수로 전달된다면,  
함수 호출 시점에 그 프로퍼티의 접근자가 호출되고 함수의 반환 시점에 프로퍼티의 설정자가 호출됩니다.) ???

- 참조는 `inout` 매개변수로 전달될 변수 또는 상수 앞에 `&`를 붙여서 표현합니다. 

```swift
var numbers: [Int] = [1, 2, 3]

func nonReferenceParameter(_ arr: [Int]) {
    var copiedArr: [Int] = arr
    copiedArr[1] = 1
}

func referenceParameter(_ arr: [Int]) {
    arr[1] = 1
}

nonReferenceParameter(numbers)
print(numbers[1]) // 2

referenceParameter(&numbers) // 참조 표현 &
print(numbers[1]) // 1
```

```swift
class Person {
    var height: Float = 0.0
    var weight: Float = 0.0
}

var me: Person = Person()

// 참조 타입의 inout 매개변수 사용 주의 요망! (C의 이중 포인터와 유사)
func reference(_ person: inout Person) {
    person.height = 130 // 이렇게 사용하면 기존 참조 매개변수처럼 동작하지만,
    print(me.height) // 130
    person = Person() // 다른 인스턴스 할당하면 참조 자체가 변경됨.
}

reference(&me)
me.height // 0 
```

- 입출력 매개변수는 기본값 가질 수 없음.
- 입출력 매개변수는 가변 매개변수로 사용될 수 없음.
- 입출력 매개변수의 전달인자로 상수 불가


### 7.2.3 반환 타입

`Void`로 반환 값이 없다고 명시해주거나, 반환 타입 표현 생략 가능.

### 7.2.4 데이터 타입으로서의 함수

스위프트의 함수는 일급 객체. => 하나의 데이터 타입으로 사용 가능.

```
(매개변수 타입의 나열) -> 반환 타입
```

```
(String, Int) -> String
(String, String...) -> String
```

```
매개변수 X 반환 값 X

(Void) -> Void
() -> Void
() -> ()
```

함수를 데이터 타입으로 사용할 수 있다는 것 => 함수를 전달, 반환 가능

```swift
typealias CalTwoInts = (Int, Int) -> Int

func addTwoInts(_ a: Int, _ b: Int) -> Int {
    return a + b
}

func multiplyTwoInts(_ a: Int, _ b: Int) -> Int {
    return a * b
}

var mathFunction: CalTwoInts = addTwoInts
// var mathFunction: (Int, Int) -> Int = addTwoInts 와 동일한 표현.
mathFunction(2, 5) // 7

mathFunction = multiplyTwoInts
mathFunction(2, 5) // 10
```

```swift
// 함수를 전달받는 함수
func printResult(_ mathFunction: CalTwoInts, _ a: Int, _ b: Int) {
    print(mathFunction(a, b))
}
printResult(addTwoInts, 3, 5) // 8
```

```swift 
// 함수를 반환하는 함수
func chooseMathFunction(_ toAdd: Bool) -> CalTwoInts {
    return toAdd ? addTwoInts : multiplyTwoInts
}
printResult(chooseMathFunction(true), 3, 5) // 8
```

- 전달인자 레이블은 함수 타입의 구성요소가 아니기 때문에 함수 타입 작성할 때 사용 X  
```
let someFunc: (lhs: Int) -> Int // X
let someFunc: (_ lhs: Int) -> Int // OK
let someFunc: (Int) -> Int // OK
```

C언어는 함수가 일급 객체가 아니었기 때문에 함수의 포인터를 사용해야 했다.

일급객체의 장단점을 비교해서 어떤 경우에 유용하게 사용 가능한가 공부!!


## 7.3 중첩 함수

스위프트는 데이터 타입의 중첩이 자유롭다.

중첩 함수는 상위 함수의 몸통 블록 내부에서만 함수 사용 가능함.  

아예 외부에서 사용 불가능한 것은 아님.  
(중첩 함수를 반환하여 담은 함수로 사용 가능함.)

```swift
typealias MoveFunc = (Int) -> Int

func functionForMove(_ shouldGoLeft: Bool) -> MoveFunc {
    func goRight(_ curPosition: Int) -> Int {
        return curPosition + 1
    }

    func goLeft(_ curPosition: Int) -> Int {
        return curPosition - 1
    }

    return shouldGoLeft ? goLeft : goRight
}

var position: Int = -2 // 현 위치

let moveToZero: MoveFunc = functionForMove(position > 0)

while position != 0 {
    print(position)
    position = moveToZero(position)
}
print("0")

// -2
// -1
// 0
```

전역함수가 많은 큰 프로젝트에서는 전역으로 사용이 불필요한 함수들의 사용 범위를 더 명확하게 표현 가능.

## 7.4 종료되지 않는 함수

=> 정상적으로 끝나지 않는 함수

비반환 함수(Nonreturning Function) / 비반환 메서드

주로 익셉션 처리 후 프로세스 종료.

- 반환타입을 **Never** 로 명시하면 됨.

- 비반환 함수는 어디서든 호출 가능.
- `guard` 구문의 `else` 블록에서도 호출 가능.
- 재정의는 할 수 있지만 비반환 타입은 변경 불가

```swift
func crashAndBurn() -> Never {
    fatalError("~~")
}

func someFunction(isAllIsWell: Bool) {
    guard isAllIsWell else {
        print("마을에 도둑이 들었습니다")
        crashAndBurn()
    }
    print("All is well")
}

someFunction(isAllIsWell: false) 
// 마을에 도둑이 들었습니다
// 프로세스 종료 후 오류 보고
```

## 7.5 반환 값을 무시할 수 있는 함수

`@discardableResult` 

```swift
@discardableResult func discResultSay(_ something: String) -> String {
    return something
}

discResultSay("hello") // 반환값을 사용하지 않았음에도 컴파일러 경고 X
```

