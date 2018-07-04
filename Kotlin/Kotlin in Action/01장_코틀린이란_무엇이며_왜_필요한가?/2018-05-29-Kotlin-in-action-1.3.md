# 1.3 코틀린 응용
## 1.3.1 코틀린 서버 프로그래밍

다음과 같은 분야가 서버 프로그래밍에 포함된다.  
- 브라우저에 HTML 페이지를 돌려주는 웹 애플리케이션
- 모바일 애플리케이션에게 HTTP를 통해 JSON API를 제공하는 백엔드
- RPC 프로토콜을 통해 서로 통신하는 작은 서비스들로 이루어진 마이크로 서비스

새로운 기술이나 프레임워크는 항상 기존의 것을 확장, 개선, 대치한다.  
 -코틀린은 자바와 **상호 운용**이 가능하기에 큰 장점.  


1. Builder Pattern

```kotlin
// 간단한 HTML 생성 라이브러리
fun renderPersonList(persons: Collection<Person>) = createHTML().table{
	for(person in persons){
		tr{
			td{+person.name}
		}
	}
}	
```

2. Persistence(영속성) Framework  
예) Exposed 프레임워크    
	: 읽기 쉬운 DSL을 제공하고, 코틀린 코드만을 사용해 완전한 타입 검사를 지원하면서 데이터베이스 질의 실행 가능.
 
—> 나중에 7.5절과 11장에서 더 자세히 살펴볼 예정



## 1.3.2 코틀린 안드로이드 프로그래밍

_Anko_ 라이브러리 (https://github.com/kotlin/anko) 를 사용하면 안드로이드 API에 대한 코틀린 어댑터를 제공받을 수 있다.

``` kotlin
//Anko를 사용한 간단한 예제
verticalLayout{
	val name = editText()

	button("say hello"){ 
	// 클릭시 텍스트 필드의 값을 표시

		onClick{ toast("Hello, ${name.text}!") } 
		// 버튼에 리스너를 추가하고 토스트를 표시하는 간결한 API
	}
}
```

[코틀린의 이점] 
1. 애플리케이션의 신뢰성이 높아짐  
: 안드로이드 개발시 `process has stopped` 오류를 많이 경험해보았을텐데, 이는 애플리케이션에서 처리되지 않는 예외.   
주로 Null Pointer Exception이 발생한 경우 표시.   
(자바에서 Null Pointer Exception이 발생하는 코드는 대부분 코틀린에서는 컴파일도 되지 않음)
2. Java 6와 완전히 호환 
3. 성능 측면에서 손해가 없다.   
: 코틀린 컴파일러가 생성한 바이트 코드 또한 효율적으로 실행됨.  
대부분 인자로 받은 람다 함수를 inlining한다. 따라서 람다를 사용해도 새로운 객체가 만들어지지 않으므로 객체 증가로 인한 부담 없음.



