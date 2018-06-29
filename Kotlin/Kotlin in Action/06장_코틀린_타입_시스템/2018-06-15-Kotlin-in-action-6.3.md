# 6.3 컬렉션과 배열
## 6.3.1 널 가능성과 컬렉션
타입 인자로 쓰일 타입에도 ?를 사용할 수 있다.
```kotlin
// 널이 될 수 있는 값으로 이뤄진 컬렉션 만들기
fun readNumbers(reader: BufferedReader): List<Int?>{
    val result = ArrayList<Int?>()
    // null이 될 수 있는 Int 리스트
    for(line in reader.lineSequence()){
        try{
            val number = line.toInt()
            result.add(number)
        }catch(e: NumberFormatException){
            result.add(null)
            // 현재 줄을 파싱할 수 없으므로 리스트에 null을 추가한다.
        }
    }
    return result
}

// 코틀린 1.1부터는 
// 파싱에 실패하면 null을 반환하는 
// String.toIntOrNull을 사용해
// 이 예제를 더 간결하게 할 수 있다.
```

- List< Int? >  
    : 리스트 자체는 notNull. 리스트 안의 각 값은 nullable
- List< Int >?  
    : 리스트 자체는 nullable. 리스트 안의 각 값은 notNull
- List< Int? >?  
    : 리스트 자체는 nullable. 리스트 안의 각 값은 nullable

```kotlin
// 널이 될 수 있는 값으로 이뤄진 컬렉션으로 널 값을 걸러내는 경우
// 코틀린 표준 라이브러리 함수 filterNotNull() 사용

fun addValidNumbers(numbers: List<Int?>){
    val validNumbers = numbers.filterNotNull()
    // validNumbers는 List<Int> 타입을 보장받는다.
}
```

## 6.3.2 읽기 전용과 변경 가능한 컬렉션

코틀린에서는 컬렉션안의 데이터에 접근하는 인터페이스와 변경하는 인터페이스를 분리했다.

*kotlin.collections.Collection* 인터페이스 안에는  
데이터를 읽는 여러 연산들은 제공하지만  
원소를 추가하거나 제거하는 메소드는 없다.

컬렉션의 데이터를 수정하려면 Collection 인터페이스를 확장하는  
*kotlin.collections.MutableCollection* 인터페이스를 사용하라.  
원소 추가, 삭제, 모든 원소 삭제 등 메소드 추가 제공함.

- Collection
    1. size()
    2. iterator()
    3. contains()
- MutableCollection
    1. size()
    2. iterator()
    3. contains()
    4. add()
    5. remove()
    6. clear()

가능하면 항상 읽기 전용 인터페이스를 사용하자.  
변경할 필요가 있을 때만 MutableCollection 쓰자.

```kotlin
fun <T> copyElements(source: Collection<T>, target: MutableCollection<T>){
    for(item in source){ //source의 모든 원소 루프
        target.add(item)
    }
}

// target에 읽기 전용 컬렉션을 넘기면 컴파일 오류가 발생함
```

**읽기 전용 컬렉션이라고 해서 꼭 변경 불가능한 컬렉션일 필요는 없다.**  
읽기 전용 인터페이스 타입인 변수를 사용할 때  
그 인터페이스는 실제로 어떤 인스턴스의 수많은 참조들 중 하나일 수 있다.  
한 인스턴스를 읽기 전용 컬렉션과 변경 가능 컬렉션이 동시에 참조가 가능하다는 것.  
이는 동시에 컬렉션의 내용을 변경할 수 있어 *ConcurrentModificationException*이나 다른 오류를 발생시킬 수 있다.  
-> **읽기 전용 컬렉션이 항상 thread safe 하지는 않음.**


## 6.3.3 코틀린 컬렉션과 자바

코틀린 컬렉션은 자바 컬렉션과 호환됨.

변경 가능한 각 인터페이스는 자신과 대응하는 읽기 전용 인터페이스를 확장한다.

- List
    - 읽기 전용 타입: 
        - listOf
    - 변경 가능 타입:
        - mutableListOf
        - arrayListOf
- Set
    - 읽기 전용 타입:
        - setOf 
    - 변경 가능 타입:
        - mutableSetOf
        - hashSetOf
        - linkedSetOf
        - sortedSetOf
- Map
    - 읽기 전용 타입: 
        - mapOf
    - 변경 가능 타입:
        - mutableMapOf
        - hashMapOf
        - linkedMapOf
        - sortedMapOf

*setOf()* 와 *mapOf()*는 자바 표준 라이브러리에 속한 클래스의 인스턴스를 반환하고, 내부에서는 변경 가능한 클래스다.

자바 메소드로 코틀린의 컬렉션을 인자로 바로 넘길 수 있다.  
여기서 문제가 생김.  
자바는 읽기 전용과 변경 가능 컬렉션을 구분하지 않기 때문에  
코틀린에서 읽기 전용 컬렉션이라도 자바 코드에서는 내용을 변경할 수 있다.  
따라서 자바 쪽에서 컬렉션을 변경할 여지가 있다면  
아예 코틀린 쪽에서도 변경 가능한 컬렉션 타입을 사용해서  
자바 코드 수행 후 컬렉션 내용이 변할 수 있음을 코드에 남겨야 한다.

이런 함정은 널이 아닌 원소로 이루어진 컬렉션 타입에서도 발생한다.  
따라서 컬렉션을 자바 코드에게 넘길때는 특별히 주의해야 한다.


## 6.3.4 컬렉션을 플랫폼 타입으로 다루기

자바에서 선언한 컬렉션 타입의 변수를 코틀린에서는 플랫폼 타입으로 본다.

자바의 메소드를 오버라이드하려는 경우, 읽기 전용 컬렉션과 변경 가능 컬렉션 중 어떤 타입으로 표현할지 결정해야 한다.

- 컬렉션이 널이 될 수 있는가?
- 컬렉션의 원소가 널이 될 수 있는가?
- 오버라이드하는 메소드가 컬렉션을 변경할 수 있는가?


```java
// 컬렉션 파라미터가 있는 다른 자바 인터페이스
interface DataParser<T>{
    void parseData(String input, List<T> output, List<String> errors);
}
```
이 인터페이스를 코틀린으로 구현하려면..
- 항상 오류 메세지를 받아야 하므로 List<String>은 널이 될 수 없게
- 오류가 발생하지 않으면 errors의 원소는 널이 될 수 있다.
- 구현 코드에서 원소를 추가할 수 있어야 하므로 List<String>는 변경 가능하게

```kotlin
// 코틀린으로 인터페이스 구현
class PersonParser: DataParser<Person>{
    override fun parseData(input: String, output: MutableList<Person>, errors: MutableList<String?>){
        //...
    }
}
```

## 6.3.5 객체의 배열과 원시 타입의 배열

```kotlin
fun main(args: Array<String>){
    for(i in args.indicies){ 
    // 배열의 인덱스 값의 범위에 대해 이터레이션하기 위해
    // array.indices 확장 함수를 사용한다.
        println("${args[i]}")
        // array[index]로 인덱스로 배열 원소에 접근
    }
}
```

- arrayOf 함수에 원소를 넘기면 배열을 만들 수 있다.
- arrayOfNulls 함수에 배열 크기를 인자로 넘기면 모든 원소가 null인 배열이 만들 수 있다. 
- array 생성자는 배열 크기와 람다를 인자로 받아서 람다를 호출해 각 배열 원소를 초기화해준다.  
arrayOf를 쓰지 않고 각 원소가 null이 아닌 배열을 만들어야 하는 경우 이 생성자를 사용한다.


```kotlin
val letters = Array<String>(26){
    i -> ('a'+i).toString()
}
```
람다는 배열 원소의 인덱스를 인자로 받아서 배열의 해당 위치에 들어갈 원소를 반환한다.

```kotlin
// 컬렉션을 vararg 메소드에게 넘기기
val strings = listOf("a", "b", "c")

// vararg 인자를 넘기기 위해 스프레드 연산자(*)를 써야 한다.
// toTypedArray()는 컬렉션을 배열로 바꿔준다.
println("$s/$s/$s".format(*strings.toTypedArray()))
// a/b/c
```

배열 타입의 타입 인자도 항상 객체 타입이 된다.  
박싱하지 않은 원시 타입의 배열이 필요하다면 특별한 배열 클래스를 사용해야 한다.

코틀린은 원시 타입의 배열을 표현하는 클래스를 원시 타입마다 하나씩 제공한다.  
IntArray, ByteArray, CharArray, BooleanArray ...
(-> int[], byte[], char[] ...)

원시 타입의 배열을 만드는 방법은 다음과 같다.
1. 생성자는 size 인자를 받아서 디폴트 값으로 초기화된 size 크기의 배열을 반환한다.
2. 팩토리 함수는 여러 값을 가변 인자로 받아서 그런 값이 들어간 배열을 반환한다. (IntArray를 생성하는 intArrayOf 등..)
3. 크기와 람다를 인자로 받는 생성자를 사용한다.  

```kotlin
//1
val fiveZeros1 = IntArray(5)
//2
val fiveZeros2 = intArrayOf(0, 0, 0, 0, 0)
//3
val squares = IntArray(5){ i -> (i+1)*(i+1) }
```

forEachIndexed 함수는 배열의 모든 원소를 갖고 인자로 받은 람다를 호출해준다. 이 때 배열의 원소와 그 원소의 인덱스를 람다에게 인자로 전달한다.

```kotlin
fun main(args: Array<String>){
    args.forEachIndexed{
        index, element ->
        println("$element}")
    }
}
```
