# 5.4 자바 함수형 인터페이스 활용

코틀린 람다를 자바 API에 사용하는 방법에 대해 알아보자.

`button.setOnClickListener{...}` // 람다를 인자로 넘김

```java
// java
public class Button{
    public void setOnClickListener(OnClickListener l){/*...*/}
}

public interface OnClickListener{
    void onClick(View v);
}
// 자바 8 이전에는 setOnClickListener 인자로 넘기기 위해
// 무명 클래스의 인스턴스를 만들어야만 했다.

button.setOnClickListener(new setOnClickListener(){
    @Override
    public void onClick(View v){
        //...
    }
})

```

```kotlin
// 코틀린에서는 람다를 넘길 수 있다.
button.setOnClickListener{ view -> ... }
// 람다의 파라미터는 메소드의 파라미터와 대응한다.
// onClick(View v) 와 { view -> ... }
// OnClickListener에는 추상 메소드가 
// onClick 단 하나만 존재하기 때문에 람다를 넘기는 것이 가능하다. 
```

이런 인터페이스를 **함수형 인터페이스** 또는 **SAM 인터페이스** 라고 한다.  
**SAM**은 **Single Abstract Method (단일 추상 메소드)**

- 코틀린은 함수형 인터페이스를 인자로 취하는 자바 메소드를 호출할 때 람다를 넘길 수 있게 해준다.

-----
#### 노트
자바와 달리 코틀린에서는 제대로 된 함수 타입이 존재한다.   
따라서 코틀린에서 함수를 인자로 받을 필요가 있는 함수는 인자 타입을 함수 타입으로 사용하자. (함수형 인터페이스 사용 x)   
코틀린 컴파일러는 코틀린 람다를 함수형 인터페이스로 변환해 주지 않는다.  

------

## 5.4.1 자바 메소드에 람다를 인자로 전달

함수형 인터페이스를 인자로 원하는 자바 메소드에 코틀린 람다를 전달할 수 있다. 
```java
// java
void postponeComputation(int delay, Runnable computation);
```
코틀린에서 람다를 이 함수에 넘길 수 있다.   
컴파일러는 자동으로 람다를 _Runnable_ 인터페이스를 구현한 무명 클래스의 인스턴스를 생성하고 전달한다.   
람다 본문은 무명 클래스의 유일한 추상 메소드의 구현부가 된다.

```kotlin
postponeComputation(1000){ println(42) }
// Runnable 인터페이스는 1개만 생성된다.
```

_Runnable_ 을 구현하는 무명 객체를 명시적으로 만들어 사용할 수도 있다. 

```kotlin
postponeComputation(1000, object : Runnable {
    override fun run(){
        println(42)
    }
})
```

람다와 무명 객체 사이에는 차이가 존재한다.  
람다는 객체를 한 번만 생성하고 반복 사용하지만 무명 객체는 호출할 때마다 객체를 새로 생성한다. 

```kotlin
// 명시적인 object 선언을 사용하면서 람다와 동일한 코드는 다음과 같다.
val runnable = Runnable { println(42) }
// Runnable은 SAM 생성자
// 전역 변수로 컴파일되므로 단 하나의 인스턴스만 존재한다. 
fun handleComputation(){
    postponeComputation(1000,runnable)
    // 하나의 runnable 객체 재사용
}
```

만약, 람다가 주변 영역의 변수를 포획한다면 매 호출마다 같은 인스턴스를 사용할 수 없다.  
그런 경우 컴파일러는 매번 주변 영역의 변수를 포획한 새로운 인스턴스를 생성해준다. 

```kotlin
fun handleComputation(id: String){
// 람다 안에서 id 변수를 포획한다. 
    postponeComputation(1000){ println(id) }
    // handleComputation을 호출할 때마다
    // 새로 Runnable 인스턴스를 생성한다.
}
```

------
### 람다의 자세한 구현 
코틀린 1.1 부터는 자바 8 바이트코드를 생성할 수 있지만, 여전히 코틀린 1.0처럼 람다마다 별도의 클래스를 만들어낸다.  

_HandleComputation$1_ 처럼 람다가 선언된 함수 이름을 접두사로 하는 이름이 람다를 컴파일한 클래스에 붙는다. 

```kotlin
// 포획이 있는 람다 식의 바이트코드를 디컴파일 한 코드
class HandleComputation$1(val id: String): Runnable{
    override fun run(){
        println(id)
    }
}

fun handleComputation(id: String){
    postponeComputation(1000,HandleComputation$1(id))
}
```
컴파일러는 포획한 변수마다 그 값을 저장하기 위한 필드를 만든다.

------

컬렉션을 확장한 메소드( _inline_ 이 표시된 함수 )에 람다를 넘기는 경우 코틀린은 무명 클래스를 만들지 않기 때문에 위와 같이 동작하지 않는다. 

## 5.4.2 SAM 생성자: 람다를 함수형 인터페이스로 명시적으로 변경

**SAM 생성자**는 람다를 함수형 인터페이스의 인스턴스로 변환할 수 있게 컴파일러가 자동으로 생성한 함수이다.

SAM 생성자를 사용하는 경우 :
- 함수형 인터페이스의 인스턴스를 반환하고 싶을 때 

```kotlin
// SAM 생성자를 사용해 값 반환하기

fun createAllDoneRunnable(): Runnable{
    return Runnable{ println("..") }
}

createAllDoneRunnable().run()
// ..
```

SAM 생성자의 이름은 사용하려는 함수형 인터페이스의 이름과 같다.  
SAM 생성자는 람다만을 인자로 받아 함수형 인터페이스를 구현하는 클래스의 인스턴스를 반환한다.
 
```kotlin
// SAM 생성자를 사용해 리스너 재사용하기
val listener = OnClickListener{ view ->
    val text = when(view.id){
        R.id.button1 -> "."
        R.id.button2 -> ".."
        else -> "..."
    }
    toast(text)
}

// 여러 버튼에 같은 리스너를 적용
button1.setOnClickListener(listener)
button2.setOnClickListener(listener)

```

------
### 람다와 리스너 등록/해제 하기
무명 객체와 달리 **람다에는 this가 없다**는 사실에 유의하라.  
람다 안에서 this는 람다를 둘러싼 클래스의 인스턴스를 가리킨다.   
컴파일러 입장에서 람다는 객체가 아니므로 람다를 변환한 무명 클래스의 인스턴스를 참조할 방법이 없다.  

리스너가 이벤트를 처리하다가 자신의 리스너 등록을 해제해야 하는 경우에는 람다를 사용할 수 없기 때문에 무명 객체를 사용하는 것이 좋다. 

------

함수형 인터페이스를 요구하는 메소드를 호출할 때  
대부분의 SAM 변환을 컴파일러가 자동으로 해주지만,  
가끔 오버로드한 메소드들 중 선택할 때 모호한 경우가 있다.  
그런 경우 명시적으로 SAM 생성자를 적용하면 컴파일 오류를 피할 수 있다. 

