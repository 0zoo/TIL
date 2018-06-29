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

- synchronized 함수의 본문뿐 아니라  
synchronized에 전달된 람다의 본문도 함께  
인라이닝 된다는 점 유의!

- 인라인 함수를 호출하면서 함수 타입의 변수를 넘길 수도 있다.

```kotlin
class LockOwner(val l: Lock){
    fun runUnderLock(body: () -> Unit){
        synchronized(l, body)
        // 람다 대신 함수 타입 변수 넘김
    }
}

// runUnderLock을 컴파일한 바이트 코드와 비슷한 코드
class LockOwner(val l: Lock){
    fun __runUnderLock__(body: () -> Unit){
        l.lock()
        try{
            body()
            // synchronized를 호출하는 부분에서 
            // 람다를 알 수 없으므로
            // 본문( body() )은 인라이닝되지 않음.
        }finally{
            l.unlock()
        }
    }
}
```

- 인라인 함수 호출시 함수 타입의 변수를 넘기는 경우,  
람다 본문은 인라이닝되지 않고,  
synchronized 함수의 본문만 인라이닝됨.

- 한 인라인 함수를 두 곳에서 각각 다른 람다를 사용해 호출한다면  
그 두 호출은 각각 인라이닝된다.


## 8.2.2 인라인 함수의 한계

람다를 사용하는 모든 함수를 인라이닝할 수는 없다.

파라미터로 받은 람다를 다른 변수에 저장하고 나중에 그 변수를 사용한다면  
람다를 표현하는 객체가 어딘가는 존재해야 하기 때문에 람다를 인라이닝할 수 없다.

 
```kotlin
// 람다를 받아서 모든 시퀀스 원소에 그 람다를 적용한 새 시퀀스를 반환하는 함수

fun <T, R> Sequence<T>.map(transform: (T) -> R): Sequence<R>{
    return TransformingSequence(this, transform)
}
// transform 파라미터로 전달받은 함수를 호출하지 않는 대신,
// TransformingSequence의 생성자에게 그 함수 값을 넘긴다.
// TransformingSequence 생성자는 전달 받은 람다를 프로퍼티로 저장한다.  

// 이런 기능을 지원하려면
// map에 전달되는 transform 인자를 인라이닝하지 않게 만들어야 함.

// -> 즉, 여기서는 transform을 
//  함수 인터페이스를 구현하는 익명 클래스 인스턴스로 만들어야 함.
```

- noinline 변경자를 사용하면 인라이닝을 금지할 수 있다.

```kotlin
inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit){
    //...
}
```

## 8.2.3 컬렉션 연산 인라이닝

```kotlin
// 람다를 사용해 컬렉션 거르기
val people = listOf(Person("A",29), Person("B", 43))

println(people.filter{it.age < 30})
// [Person(name =A, age=29)]
```

```kotlin
// 컬렉션을 직접 거르기
val people = listOf(Person("A",29), Person("B", 43))

val result = mutableListOf<Person>()
for(person in people){
    if(person.age<30) result.add(person)
}

println(result)
// [Person(name =A, age=29)]
```

- 코틀린의 filter함수는 인라인 함수다.

따라서, 위의 두 예제의 바이트코드는 거의 같다. 

```kotlin
// filter 와 map을 연쇄해서 사용
people.filter{it.age < 30}.map(Person::name)
// filter 와 map은 인라인 함수.
// 추가 객체 생성은 없지만,
// 리스트를 걸러낸 결과를 저장하는 중간 리스트를 만든다.
```
 
시퀀스 연산에서는 람다가 인라이닝되지 않기 때문에  
크기가 작은 컬렉션은 오히려 일반 컬렉션 연산이 더 성능이 좋을 수 있다.

- 시퀀스를 통해 성능을 향상시킬 수 있는 경우는  
컬렉션의 크기가 큰 경우뿐.


## 8.2.4 함수를 인라인으로 선언해야 하는 경우

inline 키워드를 사용해도 람다를 인자로 받는 함수만 성능이 좋아질 가능성이 높다.  

JVM은 가장 효율적인 방향으로 호출을 인라이닝한다.  
JVM의 최적화를 활용한다면  
바이트코드에서는 각 함수 구현이 한 번만 있으면 되고,   
그 함수를 호출하는 부분에서 따로 함수 코드를 중복할 필요가 없다.

반면, 코틀린 인라인 함수는 바이트 코드에서 각 함수 호출 시점을  
함수 본문으로 대치하기 때문에 코드 중복이 생긴다.   
게다가 함수를 직접 호출하면 스택 트레이스가 더 깔끔해진다.

- 람다를 인자로 받는 함수를 인라이닝할 경우의 장점
    1. 함수 호출 비용알 줄일 수 있고, 람다 인스턴스 만들 필요 없음.
    2. 현재의 JVM은 함수 호출과 람다를 인라이닝해 줄 정도로 똑똑하지 못함.
    3. 일반 람다에서 사용할 수 없는 몇가지 기능 사용 가능함. (ex. non-local 반환)


인라이닝하는 함수가 큰 경우  
람다 인자와 무관한 코드를 별도의 비인라인 함수로 빼내자.

- 크기가 작은 함수만 인라이닝하자!


## 8.2.5 자원 관리를 위해 인라인된 람다 사용

람다로 중복을 없앨 수 있는 일반적인 패턴  
: 어떤 작업을 하기 전에 자원을 획득하고 작업을 마친 후에 자원을 해제하는 자원 관리

- 자원(resource): file, lock, database transaction, ...

코틀린 라이브러리에는 Lock 인터페이스의 확장 함수인 **withLock** 이라는 함수를 제공한다.

```kotlin
// withLock 함수 정의
fun <T> Lock.withLock(action: () -> T): T{
// 락을 획득한 후 작업하는 과정을 별도의 함수로 분리한다.
    lock()
    try{
        return action()
    }finally{
        unlock()
    }
}
```

```kotlin
val l: Lock = //...

l.withLock{ // 락을 잠근 다음에 주어진 동작을 수행한다.
    // 락에 의해 보호되는 자원을 사용
}
```

코틀린 표준 라이브러리의 **use** 함수는 자바7의 try-with-resource와 같은 기능을 제공한다.

```kotlin
// use 함수를 자원 관리에 활용하기
fun read(path: String): String{
    BufferedReader(FileReader(path)).use{
    // BufferedReader 객체를 만들고
    // use 함수를 호출하면서 
    // 파일에 대한 연산을 실행할 람다를 넘긴다.    
        br -> 
        return br.readLine()
        // 넌 로컬 return
        // 자원에서 맨 처음 가져온 한 줄을
        // (람다가 아닌) read에서 반환한다.
    }
}
```

```java
// java 의 try-with-resource
static String read(String path) throws IOException{
    try(BufferedReader br 
        = new BufferedReader(new FileReader(path))
    ){
        return br.readLine();
    }
}
```

- use 함수는 closeable 자원에 대한 확장 함수며, 람다를 인자로 받는다.

- use는 람다를 호출한 다음에 자원을 무조건 닫아준다.

- use 함수도 인라인 함수다.

- 람다의 본문 안에서의 return br.readLine()은 **넌로컬 return**이다.  
이 반환문은 람다가 아니라 read 함수를 끝내면서 값을 반환한다.

