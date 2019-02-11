# Chapter6. 흐름 제어

스위프트는 `()`는 생략 가능, `{}`는 생략 불가

## 6.1 조건문

if, switch, guard

### 6.1.1 if 구문

조건 꼭 **Bool** 타입이어야 한다.

### 6.1.2 switch 구문

`break`가 없어도 switch 구문 종료됨.  
break를 사용하지 않고 연속 실행하던 트릭 더 이상 사용 불가.  
=> `fallthrough` 키워드를 사용하면 `case` 연속 실행

스위프트의 switch 구문의 조건에는 다양한 값이 들어갈 수 있다.  
- 입력값과 비교값이 같은 타입이어야 한다.
- 명확히 한정적인 값이 아닌 경우에는 `default`를 필수 작성해야 한다.
- 각 `case`에 범위 연산자, `where`절로 조건 확장 가능하다.

```swift
switch `입력값` {

    case `비교값1`: 
        `실행 구문`

    case `비교값2`:
        `실행 구문`
        // 이번 case를 마치고 switch 탈출 X. 아래 case로 넘어감.
        fallthrough

    case `비교값3`, `비교값4`, `비교값5`: // 여러 값 한 번에 비교 가능
        `실행 구문`
        break // break를 통한 종료는 선택 사항

    default: // 열거형처럼 한정된 범위가 명확지 않다면 필수
        `실행 구문`
}
```

```swift
let integerValue: Int = 5

switch integerValue {
    case 0:
        print("Value == 0")
    case 1...10:
        print("Value == 1~10")
        fallthrough
    case Int.min..<0, 101..<Int.max:
        print("Value < 0 or Value > 100")
        break
    default: 
        print("10 < Value <= 100")
}

// result:
// Value == 1~10
// Value < 0 or Value > 100 // fallthrough 키워드를 사용해 다음 case도 실행되도록 했기 때문.
```

부동소수 타입의 범위 연산 가능.  
`case 1.5...10.5:`

문자, 문자열, 열거형, 튜플, 범위, 패턴이 적용된 타입 등 다양한 타입의 값도 사용 가능.

```swift
// 잘못된 case 사용
let stringValue: String = "peach"

switch stringValue {
    case "banana":
        // banana에 해당하는 실행 코드가 들어와야 한다. 비어있으면 오류 발생
    case "peach":
        // 비어있으면 오류 발생
    case "melon":
        print("\(stringValue) is a fruit.")
    default:
        print("unknown")
}
```
```swift
// fallthrough 사용
let stringValue: String = "peach"

switch stringValue {
    case "banana":
        fallthrough
    case "peach":
        fallthrough
    case "melon":
        print("\(stringValue) is a fruit.")
    default:
        print("unknown")
}
// result:
// peach is a fruit.
```


튜플은 **와일드카드 식별자(`_`)** 와 함께 사용하면 더 유용합니다.
```swift
typealias NameAge = (name: String, age: Int)

let tupleValue: NameAge = ("0zoo", 26)

switch tupleValue {
    case ("0zoo", 26):
        print("맞았습니다.")
    case ("0zoo", _):
        print("이름만 맞았습니다. 나이: \(tupleValue.age)")
    case (_, 26):
        print("나이만 맞았습니다. 이름: \(tupleValue.name)")
    default: 
        print("아닙니다.")
}
```

와일드 카드 식별자를 사용하면 무시된 값을 직접 가져와야 하는 불편함이 생긴다.  
-> **값 바인딩**을 사용하자.

```swift
// 값 바인딩을 사용한 튜플 switch case
    ...
    case ("0zoo", let age):
        print("이름만 맞았습니다. 나이: \(age)")
    case (let name, 26):
        print("나이만 맞았습니다. 이름: \(name)")
    ...
}
```

```swift
// where절을 사용하여 switch case 확장
let 직급: String = "사원"
let 연차: Int = 1
let 인턴인가: Bool = false

switch 직급 {
    case "사원" where 인턴인가 == true:
        print("인턴입니다.")
    case "사원" where 연차 < 2 && 인턴인가 == false:
        print("신입사원입니다.")
    case "사원" where 연차 > 5:
        print("연식 좀 된 사원입니다.")
    default:
        print("??")
}
// 신입사원입니다.
```

열거형과 같이 한정된 범위의 값을 입력 값으로 받게 될 때  
값에 대응하는 각 case를 구현한다면 default는 구현하지 않아도 된다.

```swift
// 열거형을 입력 값으로 받는 switch 구문
enum School {
    case elementary, middle, high
}

let 학력: School = School.high

switch 학력 {
    case .elementary:
        print("초등학생입니다.")
    case .middle:
        print("중학생입니다.")
    case .high:
        print("고등학생입니다.")
}

// 고등학생입니다.
```

## 6.2 반복문

스위프트의 반복문
- 전통적인 C 스타일의 for 구문이 사라졌다.  
- 조건에 `()` 생략 가능하다.
- do-while 구문은 `repeat-while`구문으로 구현

### 6.2.1 for-in 구문

반복적인 데이터나 시퀀스를 다룰 때 많이 사용합니다.  
(타 언어의 for-each문과 비슷)

```swift
for `임시 상수` in `시퀀스 아이템` {
    `실행 코드`
}
```

```swift
for i in 0...2 {
    print(i)
}
// 0, 1, 2

for i in 0...5 {
    if i % 2 == 0 {
        print(i)
        continue // 바로 다음 시퀀스로 건너뜀
    }
    print("\(i) == 홀수")
}
// 0
// 1 == 홀수
// 2
// ...

let stringValue: String = "good afternoon"
for char in stringValue.characters {
    print(char)
}

var result: Int = 1
for _ in 1...3 { // 사용하지 않으면 _
    result *= 10
}
// 1000
```

딕셔너리는 넘겨받는 값의 타입이 튜플로 지정되어 넘어옵니다.

```swift
// Dictionary
let friends: [String: Int] = ["A": 10, "B": 20, "C": 40]
for (이름, 나이) in friends {
    print("\(이름): \(나이)살")
}
// A: 10살
// B: 20살
// C: 40살

// Set
let 지역번호: Set<String> = ["02", "031", "032"]
for 번호 in 지역번호 {
    print(번호)
}
```

함수형 프로그래밍을 더 이해하면 for-in 구문보다  
`map`, `filter`, `flatMap` 등을 더 많이 사용하게 될 것.

### 6.2.2 while 구문

`continue`, `break` 등의 제어 키워드 사용 가능.

```swift
var names: [String] = ["A", "B", "C"]

while names.isEmpty == false {
    print("Goodbye \(names.removeFirst())")
}
// Goodbye A
// Goodbye B
// Goodbye C
```

### 6.2.3 repeat-while 구문

do-while과 비슷

`repeat` 블록을 최초 1회 실행 후 `while` 조건이 성립하면 블록 내부의 코드 반복 실행함.

```swift
var names: [String] = ["A", "B", "C"]

repeat {
    print("Goodbye \(names.removeFirst())")
} while names.isEmpty == false
```

## 6.3 구문 이름표

반복문 앞에 `[이름]:` 을 붙여 구문 이름표 사용 가능.

```swift
// 중첩된 반복문의 구문 이름표 사용
var numbers: [Int] = [3, 2342, 6, 3252]

numbersLoop: for num in numbers {
    if num > 5 || num < 1 {
        continue numbersLoop
    }

    var count: Int = 0

    printLoop: while true {
        print(num)
        count += 1

        if count == num {
            break printLoop
        }
    }

    removeLoop: while true {
        if numbers.first != num {
            break numbersLoop
        }
        numbers.removeFirst()
    }
}

// 3 
// 3 
// 3 
// numbers[2342, 6, 3252]
```
