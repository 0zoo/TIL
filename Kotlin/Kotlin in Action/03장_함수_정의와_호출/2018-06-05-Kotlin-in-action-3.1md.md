# 3.1 코틀린에서 컬렉션 만들기

`val map = hashMapOf(1 to "one", 7 to "seven")` 
// to는 키워드가 아니라 일반 함수!

- 코틀린만의 컬렉션 기능은 없다. 기존 자바의 컬렉션과 같음.  
이유: 자바 코드와의 상호작용

하지만, 코틀린이 자바보다 더 많은 기능 제공한다.  
예)  
`val strings = listOf("first","second","third")`  
`println( strings.last() )`  
// third  
`val numbers = setOf(1,14,2)`  
`println( numbers.max() )`  
// 14

3장에서 _last_ 나 _max_ 와 같은 자바 클래스에 없는 메소드를 코틀린이 어디에 정의하는지 살펴본다.

