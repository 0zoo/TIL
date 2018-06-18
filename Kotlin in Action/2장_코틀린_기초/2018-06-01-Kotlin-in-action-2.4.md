# 2.4 대상을 이터레이션: while과 for루프

## 2.4.1 while 루프

```kotlin
while(조건){
    /* ... */
}


do{
    /* ... */
}while(조건)
```

## 2.4.2 수에 대한 이터레이션: 범위와 수열

```java
//java
for (int i = 1; i <= 5; i++) {
  System.out.print(i);
}
```
```kotlin
//kotlin
for( i in 1..5) {
    print(i)
}
```
> 1 2 3 4 5

```kotlin
// 100부터 거꾸로. 2씩 건너뜀
for( i in 100 downTo 1 step 2) {
    print(i)
}
```
> 100 98 96 94 92 ....

**..** 연산자는 항상 범위의 끝 값을 포함한다.  
닫힌 범위를 만들고 싶다면 **until** 함수를 사용하자.  
`for(x in 0 until 100)` 은 `for(x in 0 .. 99)`와 같다.

## 2.4.3 맵에 대한 이터레이션

```kotlin
val binaryReps = TreeMap<Char, String>()
// 키에 대해 정렬하기 위해 Treemap 사용

for(c in 'A'..'F'){ // A~F
    val binary = Integer.toBinaryString(c.toInt())
    binaryReps[c] = binary
    // c를 key로, 바이너리를 value
}

for( (letter, binary) in binaryReps ){
// 맵에 대해 이터레이션.
// letter : 맵의 key
// binary : 맵의 value 
    println("$letter = $binary")
}
```

자바 : `binaryReps.put(c,binary)`  
코틀린 : `binaryReps[c] = binary`

```kotlin
val list = arrayListOf("10","11","1001")
for( (index, element) in list.withIndex() ){
//인덱스와 함께 컬렉션 이터레이션
    println("$index: $element")
}
```
> 0: 10  
1: 11  
2: 1001  

## 2.4.4 in으로 컬렉션이나 범위의 원소 검사

**in** 연산자는 어떤 값이 범위에 속하는지 검사할 수 있다.  
**!in** 연산자는 어떤 값이 범위에 속하지 않는지 검사할 수 있다.  

```kotlin
fun isLetter(c: Char) = c in 'a'..'z' || c in 'A'..'Z'

fun isNotDigit(c: Char) = c !in '0'..'9'
```

```kotlin
// when에서 in 사용하기
fun recognize(c: Char) = when(c) {
    in '0'..'9' -> "It's a digit!"
    in 'a'..'z', in 'A'..'Z' -> "It's a letter!"
    else -> "I don't know"
}
```

비교가 가능한 클래스(_java.lang.Comparable_ 인터페이스를 구현한 클래스)라면, 그 클래스의 인스턴스 객체를 사용해 범위를 만들 수 있다.

// String에 있는 Comparable 구현이 두 문자열을 알파벳 순서로 비교함.  
`println("Kotlin" in "Java".."Scala")`
> true  


`println("Kotlin" in setOf("Java","Scala"))`
> false

