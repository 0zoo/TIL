# 5.5 수신 객체 지정 람다: with와 apply

**수신 객체 지정 람다(lambda with receiver)** : 수신 객체를 명시하지 않고 람다의 본문 안에서 다른 객체의 메소드를 호출할 수 있게 하는 것.

## 5.5.1 with 함수

코틀린은 **with**라는 코틀린의 표준 라이브러리 함수를 통해  
객체의 이름을 반복하지 않고도 다양한 연산을 수행할 수 있는 기능을 제공한다.

```kotlin
// with 함수를 사용하지 않은 알파벳 만들기

fun alphabet(): String{
    val result = StringBuilder()

    for(letter in 'A'..'Z'){
        result.append(letter)
    }
    result.append(".")

    return result.toString()
}

println(alphabet())
// ABCDEFGHIJKLMNOPQRSTUVWXYZ.
```

위 예제에서는 result에 대해 다른 여러 메소드를 호출하면서 매번 result를 반복 사용하고 있다.

```kotlin
// with 함수를 사용한 알파벳 만들기

fun alphabet(): String{
    val builder = StringBuilder()

    return with(builder){ // 수신 객체 지정
        for(letter in 'A'..'Z'){
            this.append(letter)
            // this를 명시해 앞에서 지정한
            // 수신 객체의 메소드를 호출함
        }
        append(".") // this 생략하고 메소드 호출
        
        this.toString() // 람다에서 값 반환
    }
}

// 첫번째 인자: builder
// 두번째 인자: 괄호 밖으로 빼낸 람다 
// with(builder, {/*..*/})와 같다.
```
- with는 파라미터가 2개인 함수다.  
- with 함수는 첫번째 인자로 받은 객체를 두번째 인자로 받은 람다의 수신 객체로 만든다.
- 인자로 받은 람다의 본문에서 this 사용해 수신 객체에 접근 가능.
- this 생략해도 멤버 접근 가능.

-------
### 수신 객체 지정 람다와 확장 함수 비교
확장 함수 안에서 this는 확장하는 타입의 인스턴스를 가리킨다.  
어떤 의미에서는 확장 함수를 수신 객체 지정 함수라 할 수도 있다.

- 일반 함수 - 일반 람다  
람다는 일반 함수와 비슷한 동작을 정의하는 한 방법.  

- 확장 함수 - 수신 객체 지정 람다  
수신 객체 지정 람다는 확장 함수와 비슷한 동작을 정의하는 한 방법.

-------


```kotlin
// 식을 본문으로 하는 함수와 with를 사용한 알파벳 만들기

fun alphabet() = with( StringBuilder() ){ 
        for(letter in 'A'..'Z'){
            append(letter)
        }
        append(".")
        toString()
    }
}
```
불필요한 변수를 삭제하면 함수가 식의 결과를 바로 반환하게 된다.

_StringBuilder_ 의 인스턴스를 만들어 바로 _with_ 에게 인자로 넘기고, 람다 안에서 _this_ 를 사용해 인스턴스를 참조한다.

------
### 메소드 이름 충돌
with에게 인자로 넘긴 객체의 클래스와  
with를 사용하는 코드가 들어있는 클래스 안에  
이름이 같은 메소드가 있다면?

-> this 참조 앞에 레이블을 붙이면 원하는 메소드 호출 가능.

```kotlin
// 수신 객체의 메소드 말고
// 함수의 바깥에 있는 OuterClass의 메소드를 호출하고 싶다면
this@OuterClass.toString()
```
------

## 5.5.2 apply 함수

- with와 apply의 유일한 차이점: **apply**는 항상 자신에게 전달된 객체(수신 객체)를 반환한다는 점.

```kotlin
// apply를 사용한 알파벳 만들기

fun alphabet() = StringBuilder().apply{ 
        for(letter in 'A'..'Z'){
            append(letter)
        }
        append(".")
    }.toString()
}
```



