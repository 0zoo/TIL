# 2장. 코틀린 기초
## 2.1 기본 요소: 함수와 변수
코틀린이 왜 불변 데이터 사용을 장려하는지 배워보자.

### 2.1.1 Hello, World!
```java
fun main(args: Array<String>){
    println("Hello, world!")
}
```
- 함수의 선언은 _fun_ 키워드를 사용한다.
- 파라미터 이름 : 파라미터 타입
- 함수를 최상위 수준에 정의할 수 있다. (자바처럼) 꼭 클래스 안에 함수를 넣어야 할 필요 없음.
- 자바와 달리 배열 처리를 위한 문법이 따로 존재하지 않는다.
- 표준 자바 라이브러리를 간결하게 사용할 수 있게 감싼 wrapper를 제공해준다. System.out.println -> println
- 끝에 (;)을 붙이지 않아도 된다.

### 2.1.2 함수
반환값이 존재하는 함수는 반환값의 타입을 어디에 지정해야 할까?
```java
fun max(a: Int, b: Int): Int{
    return if(a>b) a else b
    //(a>b) ? a : b
}
```
괄호와 반환 타입 사이에 (:)으로 구분해야 한다.

#### statement(문)과 expression(식)의 구분
코틀린에서 if는 expression이다.
식은 값을 만들어 내며 다른 식의 하위 요소로 계산에 참여할 수 있다.
문은 자신을 둘러싸고 있는 가장 안쪽 블록의 최상위 요소로 존재하며 아무런 값을 만들어내지 않는다.
자바에서는 모든 제어 구조가 문인 반면, 코틀린에서는 루프를 제외한 대부분의 제어 구조가 식이다. 
반면, 대입문은 자바에서는 식이었으나, 코틀린에서는 문이 됐다. 그로 인해 자바와 달리 대입식과 비교식을 잘못 바꿔 써서 버그가 생기는 경우가 없다.

#### 식이 본문인 함수

- 블록이 본문인 함수 : {}로 둘러싸인 함수
- 식이 본문인 함수 : 등호와 식으로 이루어진 함수

* _Inteli J Idea_ 팁 :
convert to expression body , Convert to block body

앞의 예시를 더 간결하게 만들어 보자.
```java
fun max(a: Int, b: Int): Int = if (a>b) a else b
```
반환타입을 생략하면 더 간략하게 만들 수 있다.
```java
fun max(a: Int, b: Int) = if (a>b) a else b
```
이유 : 식이 본문인 함수의 경우 사용자가 굳이 반환 타입을 명시하지 않아도 컴파일러가 **타입 추론**을 해준다.

주의! 식이 본문인 함수일 경우에만 생략 가능. 블록이 본문인 경우에는 반환 타입과 return을 통해 반환값을 명시해야 한다.

### 2.1.3 변수
코틀린에서는 키워드로 변수 선언을 시작하는 대신, 변수 이름 뒤에 타입을 명시하거나 생략하게 허용한다.
```java
val question = "삶, 우주, 그리고 모든 것에 대한 궁극적인 질문"
val answer : Int = 42
//val answer = 42
val yearsToCompute = 7.5e6
//부동 소수점 상수를 사용한다면 변수 타입은 Double이 된다.
```
- 초기화 식을 사용하지 않고 변수를 선언하려면 변수 타입을 반드시 명시해야 한다. 초기화 값이 없다면 컴파일러가 타입 추론을 할 수 없기 때문. 
```java
val answer : Int
answer = 42
```

#### 변경 가능한 변수와 변경 불가능한 변수
1. **val** : 변경 불가능한 (immutable) 참조를 저장하는 변수.
val로 선언된 변수는 일단 초기화하고 나면 다시 대입이 불가능하다.
자바의 final 변수에 해당.
2. **var** : 변경 가능한 (mutable) 참조 변수. 자바의 일반 변수에 해당.

기본적으로 모든 변수를 **val** (불변 변수)로 선언하고, 나중에 꼭 필요할 경우에만 **var**로 변경하라.

val 변수는 블록을 실행할 때 정확히 한 번만 초기화돼야 한다.
```java
val message: String
if(canPerformOperation()){
    message = "Success"
}else{
    message = "Failed"    
}
```
단, 초기화 문장이 한 번만 실행됨이 보장될 경우에는 위와 같은 경우는 허용된다.

val 참조 자체는 불변일지라도 그 참조가 가리키는 객체의 내부 값은 변경될 수 있다.

```java
val languages = arrayListOf("Java") // 불변 참조를 선언한다.
languages.add("kotlin") //참조가 가리키는 객체 내부를 변경한다.
```

var의 경우 변수 값을 변경할 수 있지만, 타입을 변경하는 것은 불가능하다.
컴파일러는 변수 선언 시점의 초기화식으로부터 변수의 타입을 추론하며, 변수 선언 이후 변수 재대입이 이뤄질 떄는 이미 추론한 변수의 타입을 염두에 두고 대입문의 타입을 검사한다.

### 2.1.4 더 쉽게 문자열 형식 지정: 문자열 템플릿
```java
fun main(args: Array<String>){
    val name = if(args.size>0) args[0] else "Kotlin"
    println("Hello, $name !")
    //println("Hello, ${args[0]} !")
}
```
위 예제는 **문자열 템플릿**이라는 기능을 보여준다.
스크립트 언어와 비슷하게 코틀린에서도 변수를 문자열 안에 사용할 수 있다.
변수 앞에 **$**키워드를 붙여주어야 한다.

```java
fun main(args: Array<String>){
    println("Hello, ${if (args.size > 0) args[0]
    else "someone"}!")
    //중괄호 안에 큰 따옴표 사용 가능하다.
}
```

#### 한글을 문자열 템플릿에서 사용할 경우 주의할 점
코틀린에서는 변수 이름에 한글이 들어갈 수 있다.
유니코드 변수 이름으로 인해 문자열 템플릿을 볼 때 오해가 생길 수 있다.
$변수명 뒤에 한글을 붙여서 사용하면 코틀린 컴파일러는 영문자와 한글을 한꺼번에 식별자로 인식해서 unresolved reference 오류를 발생시킨다.
//println("안녕하세요 $name님 반갑습니다.")
해결 방법은 중괄호로 한 번 감싸주는 것.
$(name)처럼 중괄호로 감싸주는 습관을 기르도록 하자.


## 2.2 클래스와 프로퍼티
클래스를 선언하는 기본 문법에 대해 알아보자.
```java
//java의 Person 클래스
public class Person{
    private final String name;
    
    public Person(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
```

```java
//Kotlin의 Person 클래스
class Person(val name: String)
```
이런 코드가 없이 데이터만 저장하는 클래스를 *value object*라 부른다.

자바 -> 코틀린 :  public 가시성 변경자가 사라졌다.
코틀린의 기본 가시성은 public이므로 이런 경우 변경자 생략 가능함.

### 2.2.1 프로퍼티
자바에서는 필드와 접근자를 한데 묶어 property라고 부른다.
코틀린의 프로퍼티는 자바의 필드와 접근자 메소드를 완전히 대신한다.
- val로 선언한 프로퍼티 : 읽기 전용
- var로 선언한 프로퍼티 : 변경 가능

```java
class Person(
    val name: String, //읽기 전용. (비공개)필드와 (공개)게터
    var isMarried : Boolean //쓸 수 있음. (비공개)필드, (공개)게터, (공개)세터
)
```
코틀린은 비공개 필드, 세터, 게터로 이루어진 디폴트 접근자 구현을 제공한다.
```java
val person = Person("Bob",true) //new키워드를 사용하지 않고 생성자를 호출한다.
println(person.name)
println(person.isMarried)
//프로퍼티의 이름을 직접 사용해도 자동으로 게터를 호출해줌.
```
자바의 게터나 세터 메소드를 호출하는 대신, 코틀린에서는 프로퍼티를 직접 사용한다.
// person.isMarried = false 
- tip : 자바에서 선언한 클래스에 대해 코틀린 문법을 사용해도 된다. 코틀린에서는 자바 클래스의 게터를 val 프로퍼티처럼 사용할 수 있고, 게터/세터 쌍이 있는 경우에는 var 프로퍼티처럼 사용할 수 있다.

- backing field (뒷받침하는 필드) : 프로퍼티의 값을 저장하기 위한 필드.

### 2.2.2 커스텀 접근자

프로퍼티의 접근자를 직접 작성하는 방법을 알아보자.

```java
class Rectangle(val height: Int, val width: Int){
    val isSquare:Boolean
    get() { //프로퍼티 게터 선언
        return height == width
    }
    //get() = height == width
}

```
//isSquare 프로퍼티에는 값을 저장하는 필드가 필요 없다. 게터만 존재.
//프로퍼티에 접근할 때마다 게터가 값을 매번 계산한다.

### 2.2.3 코틀린 소스코드 구조: 디렉터리와 패키지
코틀린에도 자바와 비슷한 개념의 패키지가 있다.
모든 코틀린 파일의 맨 앞에 _package_문을 넣을 수 있다.
같은 패키지에 속해 있다면 다른 파일에서 정의한 선언일지라도 직접 사용할 수 있다.
반면 다른 패키지에 정의한 선언을 사용하려면 _import_를 통해 선언을 불러와야 함.

코틀린에서는 클래스 임포트와 함수 임포트에 차이가 없으며, 모든 선언을 import키워드로 가져올 수 있다. 


- geometry
    - example
        - Main.java
    - shapes
        - Rectangle.java

// 자바에서는 디렉터리 구조가 패키지 구조를 그대로 따라야 한다.


- geometry
    - example.kt
    - shapes.kt

// 코틀린은 패키지 구조와 디렉터리 구조가 맞아 떨어질 필요는 없다.
geometry.shapes라는 패키지가 있다면, 하위 패키지에 해당하는 별도의 디렉터리를 만들지 않고 geometry라는 폴더 안에 shapes.kt를 넣어도 된다.


하지만 자바처럼 패키지별로 디렉터리를 구성하는 것이 좋다. 자바의 방식을 따르지 않으면 자바 클래스를 코틀린 클래스로 마이그레이션할 때 문제가 생길 수 있다. 

## 2.3 선택 표현과 처리: enum과 when
### 2.3.1 enum 클래스 정의
```java
enum class Color{
    RED,ORANGE,YELLOW,GREEN,BLUE
}
```
enum은 자바 선언보다 코틀린 선언이 더 긴 흔치 않은 경우이다.
코틀린에서 enum은 _soft keyword_라고 불린다.

**enum**은 class앞에 있을 때는 특별한 의미를 지니지만, 다른 곳에서는 이름에 사용할 수 있다.

**class**는 키워드이기 때문에 이름을 사용할 수 없다. (예: clazz, aClass)

```java
//프로퍼티와 메소드가 있는 enum 클래스 선언하기
enum class Color(
    val r: Int. val g: Int, val b: Int
){
    RED(255,0,0), ORANGE(255,165,0), YELLOW(255,255,0), GREEN(0,255,0);
    // 여기에는 반드시 세미콜론(;)을 붙여줘야 한다.

    fun rgb() = (r*256+g)*256+b
    // enum클래스 안에서 메소드 정의
} 
```
enum 클래스 안에 메소드를 정의하는 경우, 반드시 enum 상수 목록과 메소드 정의 사이에 세미콜론(;)을 넣어야 한다.

### 2.3.2 when으로 enum 클래스 다루기
자바의 switch에 해당하는 코틀린의 구성요소는 **when**이다.
식이 본문인 함수에 when을 바로 사용할 수 있다.
```java
fun getMnemonic(color: Color) = when(color){
    Color.RED -> "Richard"
    Color.ORANGE -> "Of"
    Color.YELLOW -> "York"
    Color.GREEN -> "Gave"
    Color.BLUE -> "Battle"
}

println(getMnemonic(Color.BLUE)) //Battle
```

- 자바와는 달리 각 분기의 끝에 break를 삽입하지 않아도 된다.

- 한 분기 안에서 여러 값을 매치 패턴으로 사용하려면 값 사이를 콤마(,)로 분리한다.
```java
fun getWarmth(color: Color) = when(color){
    Color.RED, Color.ORANGE, Color.YELLOW -> "warm"
    Color.GREEN -> "neutral"
}
```

- 상수값을 import하면 Color.RED -> RED로 코드를 더 간결하게 만들 수 있다.
```java
import ch02.colors.Color // 다른 패키지에서 정의한 Color클래스 import
import ch02.colors.Color.* // 짧은 이름으로 사용하기 위해 enum상수를 모두 임포트한다.

fun getWarmth(color: Color) = when(color){
    RED, ORANGE, YELLOW -> "warm"
    GREEN -> "neutral"
}
```

### 2.3.3 when과 임의의 객체를 함께 사용
코틀린의 when의 분기 조건은 임의의 객체를 허용한다. (자바는 상수만 허용)
```java
fun mix(c1: Color, c2: Color) = 
    when(setOf(c1,c2)){ //when은 인자로 받은 객체가 각 분기 조건에 있는 객체와 같은지 테스트한다. equality(동등성)을 사용하여 각 객체를 비교함.
        setOf(RED,YELLOW) -> ORANGE
        // setOf함수는 각 원소의 순서를 고려하지 않는 집합(Set)객체로 만들어준다. 
        setOf(YELLOW,BLUE) -> GREEN
        else -> throw Exception("Dirty Color")
    }

println(mix(BLUE,YELLOW)) // GREEN

```

### 2.3.4 인자 없는 when 사용










