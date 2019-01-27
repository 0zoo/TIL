# Chapter5. 연산자

## 5.1 연산자의 종류

### 5.1.1 할당 연산자

- 할당(대입) 연산자: A `=` B 

### 5.1.2 산술 연산자

- A `+` B 
- A `-` B 
- A `*` B 
- A `/` B 
- A `%` B 

스위프트는 부동소수점 타입의 나머지 연산도 지원합니다.
```swift
let result: Double = 12.truncatingRemainder(dividingBy: 2.5) // 2.0
```

스위프트는 데이터 타입에 엄격해 서로 다른 자료형끼리의 연산을 제한합니다.   
(Int와 UInt 끼리의 연산도 제한)

### 5.1.3 비교 연산자

- A `==` B, A `!=` B
- A `>=` B, A `<=` B, A `>` B, A `<` B

- A `===` B , A `!==` B
    - 참조 값 비교
- A `~=` B
    - 패턴 매치

### 5.1.4 삼항 조건 연산자

- Question `?` A `:` B
    - 참이면 A, 거짓이면 B

### 5.1.5 범위 연산자

- 폐쇄 범위 연산자
    - A`...`B : A이상 B이하
- 반폐쇄 범위 연산자
    - A`..<`B : A이상 B 미만
- 단방향 범위 연산자
    - A`...` : A 이상
    - `...`A : A 이하 
    - `..<`A : A 미만

### 5.1.6 부울 연산자

- NOT : `!`B
- AND : A `&&` B
- OR : A `||` B

### 5.1.7 비트 연산자

- NOT : `~`A
- AND : A `&` B
- OR : A `|` B
- XOR : A `^` B
- SHIFT(비트 이동) : A `>>` B

### 5.1.8 복합 할당 연산자

- A `+=` B, A `-=` B, A `*=` B, A `/=` B, A `%=` B
- A `<<=` N, A `>>=` N
- A `&=` B, A `|=` B, A `^=` B

### 5.1.9 오버플로 연산자

오버플로 연산자를 사용하면 오버플로를 자동으로 처리합니다.

- `&+`
- `&-`
- `&*`

예) `UInt8` 타입에서 0 아래로 내려가는 계산을 하면 런타임 오류 발생. 오버플로 연산을 사용하면 오류 없이 처리해줌.  
0 &- 1 => 255  
255 &+ 1 => 0


### 5.1.10 기타 연산자

- **nil 병합 연산자**: A가 nil이 아니면 A반환, nil이면 B반환
    -  A `??` B 
    - 옵셔널 사용시 유용하다.  
    (`A != nil ? A! : 0` 과 `A ?? 0` 의 결과 같음)
- **부호변경 연산자**: 숫자 A의 부호 변경
    - `-`A
- **옵셔널 강제 추출 연산자**
    - O`!`
- **옵셔널 연산자** 
    - V`?`

## 5.2 연산자 우선순위와 결합방향

프로그래머가 임의로 정의하는 사용자 정의 연산자 또한 우선순위 규칙에 따라 실행 순서가 결정됩니다.

결합 방향
- 1 + 2 + 3 (덧셈은 결합방향 왼쪽)
- 왼쪽: ( (1 + 2) + 3 )
- 오른쪽: ( 1 + (2 + 3) )

스위프트 연산자 우선순위는 상대적인 수치. (절대치 X)

스위프트 표준 라이브러리에는 다양한 **연산자 우선순위 그룹**이 있다.


 
C 계열 언어와 스위프트 연산자 우선순위나 결합방향이 완전히 같지는 않기 때문에 주의해야 합니다. 


## 5.3 사용자정의 연산자


키워드

- 전위 연산자: `prefix`
- 중위 연산자: `infix`
- 후위 연산자: `postfix`

- 연산자: `operator`
- 결합 방향: `associativity`
- 우선순위: `precedence`

사용자정의 연산자 조건
- 이미 사용되고 있는 것들은 재정의 X 사용 X
- 아스키 문자 /, =, +, -, !, *, %, <, >, &, |, ^, ?, ~ 를 결합해 사용
- 마침표(.) 사용 가능. 단, `.+.`처럼 맨 처음이 마침표일 경우만. `+.+`의 경우 `+`와 `.+` 두 개로 인식.


### 5.3.1 전위 연산자 정의와 구현

Int 타입의 제곱을 구하는 연산자 `**`을 전위 연산자로 사용하고 싶다면?

```swift
// 전위 연산자 정의
prefix operator **
```

```swift
// 전위 연산자 구현과 사용
prefix operator **

prefix func **(value: Int) -> Int {
    return value * value
}

let num: Int = -5
**num // 25
```

기존에 존재하던 연산자에 기능 추가하고 싶다면 오버로딩 (오버라이딩은 허용 X)

```swift
prefix func !(value: String) -> Bool {
    return value.isEmpty
}

// !"0zoo" // false
// !"" // true
```

### 5.3.2 후위 연산자 정의와 구현


```swift
// 전위 연산자와 후위 연산자 동시 사용
// => 후위 연산 먼저 수행함.

prefix operator **
postfix operator **

prefix func **(value: Int) -> Int {
    return value * value
}
postfix func **(value: Int) -> Int {
    return value + 10
}

// **5** : (10+5) * (10+5)
```


### 중위 연산자 정의와 구현

중위 연산자는 우선순위 그룹을 명시해줄 수 있다.

```
precedencegroup [우선순위 그룹 이름] {
    higherThan: [더 낮은 우선순위 그룹 이름]
    lowerThan: [더 높은 우선순위 그룹 이름]
    associativity: [결합방향(left/right/none)]
    assignment: [할당방향 사용(true/false)]
}
```

결합방향이 없는 연산자는 여러 번 연달아 사용 X  
예) `1 < 2 < 3` (X)


- `assignment`: 
    - `true`: 오른쪽부터 옵셔널 체이닝.
    - `false`(디폴트): 왼쪽부터 옵셔널 체이닝. 


우선순위 그룹 명시 X => `DefaultPrecedence` 그룹 (우선순위 가장 높음)

```swift
infix operator **: MultiplicationPrecedence

func **(lhs: String, rhs: String) -> Bool {
    return lhs.contains(rhs)
}

// "Hello 0zoo" ** "0zoo" // true
```

클래스, 구조체 등에서 유용하게 사용할 수 있는 연산자도 새로 정의하거나 중복 정의할 수 있다

```swift
class Car {
    var modelYear: Int? 
    var modelName: String?
}

struct SmartPhone {
    var company: String? 
    var model: String?
}

// Car 인스턴스 == 사용자 연산자 
func ==(lhs: Car, rhs: Car) -> Bool {
    return lhs.modelName == rhs.modelName
}

// SmartPhone 구조체 == 
func ==(lhs: SmartPhone, rhs: SmartPhone) -> Bool {
    return lhs.model == rhs.model
}
```

```swift
// 타입 메서드로 구현된 사용자정의 비교 연산자
class Car {
    var modelYear: Int?
    var modelName: String?

    static func ==(lhs: Car, rhs: Car) -> Bool {
        return lhs.modelName == rhs.modelName
    }
}
```

타입 메서드 사용자정의 연산자는 extension으로 구현해도 된다.

