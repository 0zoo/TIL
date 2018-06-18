# 1장. 코틀린이란 무엇이며, 왜 필요한가?
### 코틀린 언어의 특징
1) __간결함__
	: _보일러 플레이트(boilerplate)_를 제거해야 한다.
2) __안전함__
	: _Null Pointer_를 안전하게 다루는 방법을 제공한다. 
	-> Nullable (Optional)
3) __자바와의 상호운용__
	: Kotlin에서 Java로 작성된 코드를 사용할 수 있고, Kotlin으로 작성된 코드를 Java에서 사용할 수 있다. 

# 1.1 코틀린 맛보기
http://try.kotl.in

```java

data class Person(val name: String, val age: Int? = null) // Int? 는 null이 될 수 있는 타입.  

fun main(args: Array<String>){
	val persons = listOf( Person("영희"), Person("철수",age=25) )
	val oldest = persons.maxBy { it.age ?: 0 } // 람다식과 엘비스 연산자.
// 엘비스 연산자(?:)란 age가 null일 경우에는 0을 반환하고, 그 이외의 경우에는 age값을 반환한다.

	println("나이가 가장 많은 사람: $oldest") //문자열 템플릿
}

//실행 결과
// 나이가 가장 많은 사람: Person(name=철수, age=25)
// toString 자동 생성

```

