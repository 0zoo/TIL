# Chapter4. 데이터 타입 고급

## 4.1 데이터 타입 안심

### 4.1.1 데이터 타입 안심이란?

스위프트는 type-safe 언어


스위프트가 컴파일 시 타입을 확인하는 것을 **타입 확인**이라고 한다.

### 4.1.2 타입 추론

```swift
var name = "0zoo" // String 타입 추론

name = 100 // 오류 발생. - 타입 안심
```

## 4.2 타입 별칭

데이터 타입에 **별칭** 부여 가능.

```swift
// 기존에 사용하던 데이터 타입의 이름과 별칭 모두 사용 가능

typealias MyInt = Int
typealias YourInt = Int

let age: MyInt = 100
var year: YourInt = 2000

year = age // 둘 다 Int이기 때문에 같은 타입으로 취급
```

## 4.3 튜플

- **Tuple(튜플)**: (타입의 이름이 따로 지정 X) 프로그래머 마음대로 만드는 타입. **지정된 데이터의 묶음**  

파이썬의 튜플과 비슷함. 

타입의 이름이 없으므로 일정 타입의 나열만으로 생성 가능.

```swift
// String, Int, Double 타입을 갖는 튜플
var person: (String, Int, Double) = ("0zoo", 100, 182.3)

// 인덱스 사용 가능
print("이름: \(person.0), 나이: \(person.1), 키: \(person.2)") 
```

```swift
// 튜플 요소 이름 지정

var person: (name: String, age: Int, height: Double) = ("0zoo", 100, 182.3)

print("이름: \(person.name), 나이: \(person.age), 키: \(person.2)") 
```

```swift
// 튜플 별칭 지정 

typealias PersonTuple = (name: String, age: Int, height: Double)

let eric: PersonTuple = ("eric", 20, 175.3)
```

## 4.4 컬렉션 타입

### 4.4.1 배열

배열: 같은 타입의 데이터를 일렬로 나열한 후 순서대로 저장하는 형태의 컬렉션 타입 (중복 허용)

스위프트의 Array는 C언어의 배열처럼 **buffer** 입니다.  
단, C언어와 달리 버퍼의 크기 자동 조절해주므로 요소의 삽입 및 삭제가 자유롭다.  

```swift
// [String]은 Array<String>의 축약 표현
var names: Array<String> = ["eric", "Tom", "Amy", "Bob"]
var names: [String] = ["eric", "Tom", "Amy", "Bob"]

// Any를 데이터 요소로 갖는 빈 배열 생성
var emptyArray: [Any] = [Any]()
var emptyArray: [Any] = Array<Any>()

// 배열의 타입을 명시했다면 []만으로 빈 배열 생성 가능함.
var emptyArray: [Any] = []
```

```swift
// names의 마지막에 B와 C가 추가된다.
names.append(contentsOf: ["B", "C"])

// names의 3의 위치에 차례대로 D와 E가 추가됨.
names.insert(contentsOf: ["D", "E"], at: 3)

names[1 ... 3] // 인덱스 1, 2, 3 해당 값
```

### 4.4.2 딕셔너리

딕셔너리: 순서 X. 키와 값의 쌍

```swift
typealias StringIntDictionary = [String: Int]

// 모두 같은 동작
var numberForName: Dictionary<String, Int> = Dictionary<String, Int>()
var numberForName: [String: Int] = [String: Int]()
var numberForName: StringIntDictionary = StringIntDictionary()
var numberForName: [String: Int] = [:]

var numberForName: [String: Int] = ["A": 100, "B": 20, "C": 50]
```

### 4.4.3 세트

**세트**: 같은 타입의 데이터. **순서 X**. 하나의 묶음으로 저장하는 형태의 컬렉션 타입 (**중복 허용 X**)  


```swift
// 빈 세트 생성
var names: Set<String> = Set<String>() 
var names: Set<String> = []

var names: Set<String> = ["A", "B", "C"]

var numbers = [100, 200, 300] // 타입 추론시 Array 타입으로 추론함.
```

- Array와 마찬가지로 `[ ]`사용함.    

- 축약형 없음 (예: `Array<Int>` - `[Int]`)

- 세트의 요소로는 **해시 가능한 값** (`Hashable` 프로토콜을 따른다는 것. 스위프트의 기본 데이터 타입은 모두 해시 가능함.) 이 들어와야 한다.

- 집합관계 표현시 유용
    - 교집합: `intersection()`
    - 여집합의 합: `symmetricDifference()`
    - 합집합: `union()`
    - 차집합: `subtracting()`

    - 서로 배타적인가?: `isDisjoint()`
    - 부분집합인가?: `isSubset()`
    - 전체집합인가?: `isSuperset()`


## 4.5 열거형

연관된 항목들을 묶어서 표현할 수 있는 타입

배열이나 딕셔너리와 다르게 프로그래머가 정의해준 항목 값 외에는 추가/수정 불가.

다음과 같은 경우에 요긴하게 사용
- 제한된 선택지
- 정해진 값 외에는 입력받고 싶지 않을 때
- 예상된 입력 값이 한정되어 있을 때

### 4.5.1 기본 열거형

### 4.5.2 원시 값

### 4.5.3 연관 값

### 4.5.4 순환 열거형

