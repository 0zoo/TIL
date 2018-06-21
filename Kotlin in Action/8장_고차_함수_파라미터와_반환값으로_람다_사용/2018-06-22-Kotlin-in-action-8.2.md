# 8.2 인라인 함수: 람다의 부가 비용 없애기

5장에서 코틀린이 보통 람다를 무명 클래스로 컴파일하지만,  
람다 식을 사용할 때마다 새로운 클래스를 만들지 않는다는 사실을 공부했음.  
람다가 변수를 포획하면 람다가 생성되는 시점마다 새로운 무명 클래스 객체가 생긴다는 사실도 공부했음.

-> 이런 경우 실행 시점에 무명 클래스 생성어 따른 부가 비용이 들어감.

> 람다를 사용하는 구현은 일반 함수보다 비효율적이다.

>> inline 변경자를 함수에 붙이면 컴파일러는 그 함수를 호출하는 모든 문장을 함수 본문에 해당하는 바이트 코드로 바꿔준다.

## 8.2.1 인라이닝이 작동하는 방식

어떤 함수를 inlined으로 선언하면  
함수를 호출하는 코드를 함수 본문을 번역한 바이트 코드로 컴파일한다.

```kotlin
// 인라인 함수 정의하기

inline fun <T> synchronized(lock: Lock, action: () -> T): T {
    lock.lock()
    try{
        return action()
    }finally{
        lock.unlock()
    }
}

val l = Lock()
synchronized(l){
    /*..*/
}
// synchronized 함수를 inline으로 선언했으므로 
// synchronized를 호출하는 코드는 자바의 synchronized문과 같다.

```

코틀린 표준 라이브러리는 아무 타입의 객체나 인자로 받을 수 있는 synchronized 함수를 제공한다.

하지만, 명시적인 락을 사용하는 것이 더 좋음.


```kotlin
fun foo(l: Lock){
    println("Before sync")

    synchronized(l){
        println("Action")
    }

    println("After sync")
}
```

```kotlin
// foo 함수를 컴파일한 버전

fun __foo__(l: Lock){
    println("Before sync")

    l.lock()
    try{
        println("Action")
        //return action()
        // 람다 본문이 인라이닝된 코드
    }finally{
        l.unlock()
    }// synchronized 함수가 인라이닝 된 코드

    println("After sync")
}

```



## 8.2.2 인라인 함수의 한계


## 8.2.3 컬렉션 연산 인라이닝
## 8.2.4 함수를 인라인으로 선언해야 하는 경우
## 8.2.5 자원 관리를 위해 인라인된 람다 사용
