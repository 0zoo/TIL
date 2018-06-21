# 7.5 프로퍼티 접근자 로직 재활용: 위임 프로퍼티

**위임 프로퍼티(delegated property)**

위임 프로퍼티를 사용하면  
값을 뒷받침하는 필드에 단순히 저장하는 것보다  
더 복잡한 방식으로 작동하는 프로퍼티를 쉽게 구현할 수 있다.  
또한, 그 과정에서 접근자 로직을 매번 재구현할 필요도 없다.

예를 들어, 프로퍼티는 위임을 사용해  
자신의 값을 필드가 아닌  
데이터베이스 테이블이나 브라우저 세션, 맵 등에 저장할 수 있다. 

이런 특성의 기반에는 **위임**이 있다.

**위임**은 객체가 직접 작업을 수행하지 않고 다른 도우미 객체가 그 작업을 처리하게 맡기는 디자인 패턴.  
도우미 객체를 **위임 객체(delegate)** 라고 부른다.  


## 7.5.1 위임 프로퍼티 소개

```kotlin
//위임 프로퍼티의 일반적인 문법

class Foo{
    var p: Type by Delegate()
}

// p 프로퍼티는 접근자 로직을 다른 객체에게 위임한다.
// Delegate 클래스의 인스턴스를 위임 객체로 사용
// by 뒤에 있는 식을 계산해서 위임에 쓰일 객체를 얻는다. 
```

- 프로퍼티 위임 객체가 따라야 하는 관례를 따르는 모든 객체를 위임에 사용할 수 있다.


```kotlin
class Foo{
    private val delegate = Delegate()
    // 컴파일러가 생성한 도우미 프로퍼티

    var p: Type

    // p 프로퍼티를 위해 컴파일러가 생성한 접근자는
    // delegate의 getValue 와 getValue 메소드를 호출한다.

    set(value: Type) = delegate.setValue(..., value)

    get() = delegate.getValue(...)
}
```


```kotlin
class Delegate{
    operator fun getValue(...) {...} // 게터 구현 로직

    operator fun setValue(..., value: Type) {...} // 세터 구현 로직

}

class Foo{
    var p: Type by Delegate()
    // by 키워드는 프로퍼티와 위임 객체를 연결한다. 
}

val foo = Foo()

val oldValue = foo.p
// foo.p 라는 호출은 내부에서
// delegate.getValue(...)를 호출한다.

foo.p = newValue
```


## 7.5.2 위임 프로퍼티 사용: by lazy()를 사용한 프로퍼티 초기화 지연


## 7.5.3 위임 프로퍼티 구현


## 7.5.4 위임 프로퍼티 컴파일 규칙


## 7.5.5 프로퍼티 값을 맵에 저장


## 7.5.6 프레임워크에서 위임 프로퍼티 활용



