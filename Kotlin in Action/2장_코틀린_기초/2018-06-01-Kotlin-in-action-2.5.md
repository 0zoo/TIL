# 2.5 코틀린의 예외 처리

코틀린의 기본 예외 처리 구문은 자바와 비슷하다.

```kotlin
if(percentage !in 0..100){
    throws IllegalArgumentException("메세지")
}
```

자바와 달리 코틀린의 **throw는 식**이므로 다른 식에 포함될 수 있다.
```kotlin
val percentage = 
    if(number in 0..100)
        number
    else
        throws IllegalArgumentException("메세지")

```

## 2.5.1 try, catch, finally

_BufferedReader.close_ 는 _IOException_ 을 던질 수 있는데, 예외 처리를 반드시 해주어야 한다. 하지만 실제 스트림을 닫다가 실패하는 경우에 클라이언트 프로그램이 특별히 할 수 있는 동작이 없으므로 이 _IOException_ 을 잡아내는 코드는 불필요하다.  

```kotlin
fun readNumber(reader: BufferedReader):Int? {
// 함수가 던질 수 있는 예외를 명시할 필요가 있다.
    try{
        val line = reader.readLine()
        return Integer.parseInt(line)
    }catch(e: NumberFormatException){
        return null
    }finally{
        reader.close()
    }
}
```

코틀린은 **체크 예외**(checked exception)와 **언체크 예외**(unchecked exception)를 구별하지 않는다.  
실제 자바 프로그래머들이 체크 예외를 사용하는 방식을 고려해 설계했음.  
코틀린에서는 함수가 던지는 예외를 지정하지 않고, 발생한 예외 처리 해도 되고 안해도 됨.


_try-with-resource_ 는 어떨까? 코틀린이 특별히 문법을 제공하지는 않지만, 8.2.5절에서 라이브러리 함수로 같은 기능을 구현하는 방법을 살펴본다.

## 2.5.2 try를 식으로 사용

코틀린으 **try** 키워드는 if, when과 마찬가지로 **식**이다.  
if와 달리 반드시 try의 본문은 중괄호{}로 감싸줘야 한다.

```kotlin
fun readNumber(reader: BufferedReader){
    val number = try{
        Integer.parseInt(reader.readLine()) // 결과값이 try의 값
    }catch(e: NumberFormatException){
        return
    }
    println(number)
}
```
이 예제는 *catch* 안에서 *return*을 사용한다. 따라서 예외가 발생한 경우 catch블럭 다음은 실행되지 않는다. 계속 하고 싶다면 아래의 방법 사용. 

```kotlin
fun readNumber(reader: BufferedReader){
    val number = try{
        Integer.parseInt(reader.readLine()) // 예외가 발생하지 않으면 이 값을 사용.
    }catch(e: NumberFormatException){
        null // 예외가 발생하면 null값을 사용
    }
    println(number)
}
```

