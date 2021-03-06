# 1.2 코틀린의 주요 특성

## 1.2.1 대상 플랫폼: 서버, 안드로이드 등 자바가 실행되는 모든 곳

코틀린의 주 목적: 현재 자바보다 더 간결하고 생산적이며 안전한 대체 언어를 제공하는 것.

## 1.2.2 정적 타입 지정 언어


- 정적 타입 지정 언어(statically typed):  
	- 컴파일시 타입 결정.
	- 변수에 들어갈 값의 형태에 따라 타입을 지정해주어야 한다.
	- 프로그램 안에서 객체의 필드나 메소드를 사용할 때마다 컴파일러가 타입을 검증해줌.
	- 속도가 빠르고 안정성이 확보되어 있음.
	- Java, **Kotlin**, C, C++ ..

- 동적 타입 지정 언어(dynamically typed):
	- 실행시 타입 결정.
	- 타입 없이 변수만으로 값 지정 가능
	- 메소드나 필드 접근에 대한 검증이 실행 시점에 일어남
	- 코드가 더 짧아지고 데이터 구조를 더 유연하게 생성하고 사용할 수 있음
	- 실수를 컴파일시 걸러내지 못하고 실행 시점에 오류가 발생함
	- JavaScript, Python, Groovy, JRuby ..


<정적 타입 지정의 장점>
1. 성능 : 더 빠름
2. 신뢰성 : 실행시 오류 발생 가능성이 더 적다.
3. 유지 보수성 : 처음 보는 코드를 다룰 때 더 쉬움
4. 도구 지원 : 더 안전하게 리팩토링 가능


**Kotlin은 변수의 타입 선언을 생략해도 된다.**   
대부분의 경우 코틀린의 컴파일러가 문맥으로부터 변수의 타입을 유추할 수 있기 때문. -> _타입 추론 (type inference)_

```kotlin
	var x = 1
```
 
코틀린은 타입 추론을 지원하기 때문에 정적 타입 지정 언어에서 직접 타입을 선언해야 하는 불편함이 해소됨.

_Class_, _Interface_, _Generics_ 모두 자바와 비슷하게 작동함.

Java와 다르게 새로 추가된 것:  
1. **nullable type**  
	: 컴파일 시점에 null pointer exception 발생 여부를 검사할 수 있어 프로그램의 신뢰성을 높인다.  
2. **function type**  
	: 함수 타입을 지원.

## 1.2.3 함수형 프로그래밍과 객체지향 프로그래밍

### 함수형 프로그래밍

- **일급 시민(first class)인 함수**
: 함수를 **일반 값**처럼 다룰 수 있다. 함수를 변수에 저장하고 함수를 인자로 넘기거나 반환할 수 있다.   
-> 간결성. 코드의 중복을 줄임.  
	예) findPerson{ it.name == “Bob” }   
	// 람다를 사용해 간결하게 표현 가능.

- **불변성(immutability)**
: 한 번 만들어지면 내부가 바뀌지 않는 **불변 객체**를 사용한다.  
-> 다중 스레드에 안전.

- **부수 효과(side effect) 없음** 
: 입력이 같으면 결과도 같다. 다른 객체를 변경하지 않으며, 외부와 상호작용하지 않는다.
-> 독립적으로 테스트 가능.

[코틀린에서의 지원]  
- 함수 타입 지원.  
- 람다식 지원.   
- 데이터 클래스를 통해 불변 객체(value object)를 간편하게
- 객체와 컬렉션을 함수형 스타일로 다룰 수 있는 API 제공함


## 1.2.4 무료 오픈소스

깃허브 : https://github.com/jetbrains/kotlin
