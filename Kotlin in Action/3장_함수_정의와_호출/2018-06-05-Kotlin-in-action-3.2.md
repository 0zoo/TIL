# 3.2 함수를 호출하기 쉽게 만들기

자바 컬렉션에는 디폴트 _toString_ 제공.  
만약 기본 제공 출력 형식을 변경하고 싶다면? 서드파티 프로젝트를 추가하거나 직접 관련 로직을 구현해야 한다.  
코틀린에는 이런 요구 사항을 처리할 수 있는 함수가 표준 라이브러리에 존재한다.

```kotlin
// joinToString 함수 초기 구현
// 컬렉션의 원소를 StringBuilder에 붙인다. 
// 원소 사이에는 구분자 추가
// 맨 앞과 뒤에는 prefix와 postfix 추가
fun <T> joinToString(
    collection: Collection<T>,
    separator: String,
    prefix: String,
    postfix: String
): String {
    val result = StringBuilder(prefix)
    for((index,element) in collection.withIndex()){
        if((index>0)) 
            result.append(separator)
        result.append(element)
    }
    result.append(postfix)
    return result.toString()
}
```

```kotlin
val list = listOf(1,2,3)
println(joinToString(list,";","[","]"))
// [1;2;3]
```
잘 작동하지만, 매번 호출할 때 마다 인자 4개를 모두 전달하지 않을 수 있는 방법을 생각해보자.

## 3.2.1 이름 붙인 인자

해결하고 싶은 문제 : 함수 호출시 가독성  

```kotlin
joinToString(collection, separator=" ", prefix= " ", postfix = ".")
```
호출 시 인자 중 어느 하나라도 이름을 명시하고 나면 혼동을 막기 위해 그 뒤에 오는 모든 인자는 이름을 꼭 명시해야 한다.

* 경고 : 자바로 작성한 코드를 호출할 경우에는 이름 붙인 인자를 사용할 수 없다. (안드로이드 제공 함수도 마찬가지) 

## 3.2.2 디폴트 파라미터 값

자바의 일부 클래스에서는 오버로딩 메소드가 너무 많다는 문제점. 보일러 플레이트 발생.

코틀린에서는 함수 선언에서 파라미터의 디폴트 값을 지정할 수 있어 이런 문제점 상당수 해결. 

```kotlin
// 디폴트 파라미터 값을 사용한 joinToString 정의
fun <T> joinToString(
    collection: Collection<T>,
    separator: String= ",",
    prefix: String= "",
    postfix: String= ""
): String {
    //함수 구현 부분 위와 동일함
}
```

```kotlin
// prefix와 postfix 생략
println(joinToString(list,";"))
// 1;2;3
```

일반 호출 문법을 사용: 함수 선언 순서와 같아야 한다. 만약 일부를 생략했다면, 뒷부분의 인자들은 생략된다.

```kotlin
println(joinToString(list,postfix=";",prefix="#"))
// #1,2,3;
```
이름 붙은 인자 사용: 순서 상관 없음. 

### 디폴트 값과 자바
자바에는 디폴트 파라미터 값이라는 개념이 없다. 자바에서 코틀린 함수를 호출하는 경우에는 코틀린에서 디폴트 값을 설정했더라도 모든 인자를 명시해야 한다.  
*@JvmOverloads* 애노테이션을 추가하면, 코틀린 컴파일러가 자동으로 맨 마지막 파라미터로부터 파라미터를 하나씩 생략한 오버로딩한 자바 메소드를 추가해준다.  

## 3.2.3 정적인 유틸리티 클래스 없애기: 최상위 함수와 프로퍼티

자바에서는 함수를 클래스 안에 선언해야 한다. 무의미한 클래스 생성 문제점. (예: JDK의 *Collections* 클래스)

```java
// java
package strings;
public class JoinKt{
    public static String joinToString(...){...}
}
```

코틀린에서는 함수를 클래스 밖에 위치 시킬 수 있다. 이런 함수들은 여전히 그 파일의 패키지의 멤버 함수이기 때문에 다른 패키지에서 사용하려면 해당 패키지를 import해야 한다. 

```kotlin
//kotlin
package strings
fun joinToString(...): String{...}
```
위 함수가 어떻게 실행이 가능할까?  
    : 컴파일시 새로운 클래스 정의해줌.  
    코틀린 컴파일러가 생성하는 클래스의 이름은 최상위 함수가 들어있던 코틀린 소스 파일의 이름과 대응한다. (join.kt -> JoinKt.Class)

```java
// java에서 joinToString 호출
import strings.JoinKt;

public class Main{
    public static void main(String[] args){
        JoinKt.joinToString(list,",","","");
    }
}
```
### 파일에 대응하는 클래스의 이름 변경하기
최상위 함수가 포함되는 클래스의 이름을 바꾸고 싶다면,   
파일의 맨 앞 (패키지 이름 선언 이전에) *@JvmName* 애노테이션을 추가하라.  

```kotlin
@file: JvmName("StringFunctions")

package strings

fun joinToString(...): String{...}
```
```java
// java
import strings.StringFunctions;

public class Main{
    public static void main(String[] args){
        StringFunctions.joinToString(list,",","","");
    }
}
```

### 최상위 프로퍼티
함수와 마찬가지로 프로퍼티도 파일의 최상위 수준에 놓을 수 있다.  
흔히 사용하지는 않지만 가끔 유용하다. 
예를 들어, 어떤 연산을 수행한 횟수를 저장하는 var 프로퍼티를 예로 들 수 있다.
```kotlin
var opCount= 0 // 최상위 프로퍼티 선언
fun performOperation(){
    opCount++
}
fun reportOperationCount(){
    println("$opCount")
}
```
이런 최상위 프로퍼티의 값은 정적 필드에 저장된다.  
`val LINE_SEPARATOR = "\n" `  
과 같이 상수로 사용 가능하다.

최상위 프로퍼티도 접근자 메소드를 통해 자바 코드에 노출된다. (val은 게터, var은 게터와 세터)  
그런데, 상수의 경우에는 게터로 접근하는 것이 부자연스럽다.  
**const** 키워드를 사용하면 *public static final* 필드로 컴파일된다.  

> const val LINE_SEPARATOR = "\n"   

