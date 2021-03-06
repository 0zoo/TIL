# 2.1 기본 요소: 함수와 변수
코틀린이 왜 불변 데이터 사용을 장려하는지 배워보자.

## 2.1.1 Hello World
```kotlin
fun main(args: Array<String>){
    println("Hello, world!")
}
```
- 함수의 선언은 _fun_ 키워드를 사용한다.
- 파라미터 이름 : 파라미터 타입
- 함수를 최상위 수준에 정의할 수 있다. (자바처럼) 꼭 클래스 안에 함수를 넣어야 할 필요 없음.
- 자바와 달리 배열 처리를 위한 문법이 따로 존재하지 않는다.
- 표준 자바 라이브러리를 간결하게 사용할 수 있게 감싼 wrapper를 제공해준다. System.out.println -> println
- 끝에 `;`을 붙이지 않아도 된다.

## 2.1.2 함수
반환값이 존재하는 함수는 반환값의 타입을 어디에 지정해야 할까?
```kotlin
fun max(a: Int, b: Int): Int{
    return if(a>b) a else b
    //(a>b) ? a : b
}
```
괄호와 반환 타입 사이에 `:`으로 구분해야 한다.

### statement(문)과 expression(식)의 구분
코틀린에서 `if`는 expression이다.  
식은 값을 만들어 내며 다른 식의 하위 요소로 계산에 참여할 수 있다.
문은 자신을 둘러싸고 있는 가장 안쪽 블록의 최상위 요소로 존재하며 아무런 값을 만들어내지 않는다.
자바에서는 모든 제어 구조가 문인 반면, 코틀린에서는 루프를 제외한 대부분의 제어 구조가 식이다. 
반면, 대입문은 자바에서는 식이었으나, 코틀린에서는 문이 됐다. 그로 인해 자바와 달리 대입식과 비교식을 잘못 바꿔 써서 버그가 생기는 경우가 없다.

### 식이 본문인 함수

- 블록이 본문인 함수 : {}로 둘러싸인 함수
- 식이 본문인 함수 : 등호와 식으로 이루어진 함수

* _Inteli J Idea_ 팁 :
convert to expression body , Convert to block body

앞의 예시를 더 간결하게 만들어 보자.
```kotlin
fun max(a: Int, b: Int): Int = if (a>b) a else b
```
반환타입을 생략하면 더 간략하게 만들 수 있다.
```kotlin
fun max(a: Int, b: Int) = if (a>b) a else b
```
이유 : 식이 본문인 함수의 경우 사용자가 굳이 반환 타입을 명시하지 않아도 컴파일러가 **타입 추론**을 해준다.

주의! 식이 본문인 함수일 경우에만 생략 가능. 블록이 본문인 경우에는 반환 타입과 return을 통해 반환값을 명시해야 한다.

## 2.1.3 변수
코틀린에서는 키워드로 변수 선언을 시작하는 대신, 변수 이름 뒤에 타입을 명시하거나 생략하게 허용한다.
```kotlin
val question = "삶, 우주, 그리고 모든 것에 대한 궁극적인 질문"
val answer : Int = 42
//val answer = 42
val yearsToCompute = 7.5e6
//부동 소수점 상수를 사용한다면 변수 타입은 Double이 된다.
```
- 초기화 식을 사용하지 않고 변수를 선언하려면 변수 타입을 반드시 명시해야 한다. 초기화 값이 없다면 컴파일러가 타입 추론을 할 수 없기 때문. 
```kotlin
val answer : Int
answer = 42
```

### 변경 가능한 변수와 변경 불가능한 변수
1. **val** : 변경 불가능한 (immutable) 참조를 저장하는 변수.
val로 선언된 변수는 일단 초기화하고 나면 다시 대입이 불가능하다.
자바의 final 변수에 해당.
2. **var** : 변경 가능한 (mutable) 참조 변수. 자바의 일반 변수에 해당.

기본적으로 모든 변수를 **val** (불변 변수)로 선언하고, 나중에 꼭 필요할 경우에만 **var**로 변경하라.

val 변수는 블록을 실행할 때 정확히 한 번만 초기화돼야 한다.
```kotlin
val message: String
if(canPerformOperation()){
    message = "Success"
}else{
    message = "Failed"    
}
```
단, 초기화 문장이 한 번만 실행됨이 보장될 경우에는 위와 같은 경우는 허용된다.

val 참조 자체는 불변일지라도 그 참조가 가리키는 객체의 내부 값은 변경될 수 있다.

```kotlin
val languages = arrayListOf("Java") // 불변 참조를 선언한다.
languages.add("kotlin") //참조가 가리키는 객체 내부를 변경한다.
```

var의 경우 변수 값을 변경할 수 있지만, 타입을 변경하는 것은 불가능하다.
컴파일러는 변수 선언 시점의 초기화식으로부터 변수의 타입을 추론하며, 변수 선언 이후 변수 재대입이 이뤄질 떄는 이미 추론한 변수의 타입을 염두에 두고 대입문의 타입을 검사한다.

## 2.1.4 더 쉽게 문자열 형식 지정: 문자열 템플릿
```kotlin
fun main(args: Array<String>){
    val name = if(args.size>0) args[0] else "Kotlin"
    println("Hello, $name !")
    //println("Hello, ${args[0]} !")
}
```
위 예제는 **문자열 템플릿**이라는 기능을 보여준다.
스크립트 언어와 비슷하게 코틀린에서도 변수를 문자열 안에 사용할 수 있다.
변수 앞에 **$** 키워드를 붙여주어야 한다.

```kotlin
fun main(args: Array<String>){
    println("Hello, ${if (args.size > 0) args[0]
    else "someone"}!")
    //중괄호 안에 큰 따옴표 사용 가능하다.
}
```

### 한글을 문자열 템플릿에서 사용할 경우 주의할 점
코틀린에서는 변수 이름에 한글이 들어갈 수 있다.    
// println("안녕하세요 $name님 반갑습니다.")  
// 영문자와 한글을 한꺼번에 식별자로 인식해서   
// unresolved reference 오류 발생    
// 해결 방법: 중괄호로 한 번 감싸주는 것.  
// $(name)  
  