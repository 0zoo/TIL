## Imperative vs Declarative Programming

> 명령형 프로그래밍은 **어떻게** 할 것인가 이고,  
선언형 프로그래밍은 **무엇을** 할 것인가 이다.

예시)  
"지금 월마트에 있어. 여기서 너네 집까지 어떻게 가니?"

- 명령형 접근 :  
주차장의 북쪽 입구로 나와서 좌회전 해라. 뱅갈 고속도로 표지판이 보일때까지 1-15길로 가라. 우회전해서 이케아가 보일때까지 직직해라. 첫번째 신호에서 우회전해라. 다음 신호까지 직진 한 다음 좌회전 해라. 내 집은 #298이다.

- 선언형 접근 :  
내 집 주소는 298 West Immutable Alley, Draper Utah 84020 이다.


### 명령형 프로그래밍 (Imperative programming)

선언형과 반대되는 개념. 

알고리즘을 명시하고 목표를 명시하지 않는다.

명령형 방법을 사용하는 경우 개발자는 컴퓨터에서 목표를 이루기 위해 수행해야 하는 단계를 매우 자세히 설명하는 코드를 작성하며,
이러한 프로그래밍을 알고리즘 프로그래밍이라고도 한다.

### 선언형 프로그래밍 (Declarative Programming)

목표를 명시하고 알고리즘을 명시하지 않는다.

궁극적인 목표가 무엇인지에 집중하기 때문에 컨텍스트에서 자유로워 같은 코드를 다른 프로그램에도 사용할 수 있다.

------

논리적으로 해결하는 것이 좋을 수도 있지만, 선언적인 방법으로 해결할 수 있다면 선언적으로 해결하는 방법이 더 좋을수도 있다.

-> Java : stream, Kotlin : sequence

filter, map, forEach ...

------



# Reactive Programming

> 반응형 프로그래밍은 데이터의 흐름과 변화에 대한 전달을 기반으로 하는 프로그래밍 패러다임이다.

끊임없이 요청/변경되는 데이터에 반응하기 위해 나온 **Reactive Programming** 은 데이터를 처리함에 있어서 **비동기적**으로 데이터를 처리할 때 효율적으로 할 수 있도록 하기 위한 방법.

일반적인 비동기 데이터 처리가 처리가 끝날때까지 쓰레드를 대기시키거나 콜백을 받아서 처리하기 때문에 불필요한 컴퓨팅 리소스 사용이 발생하게 됩니다.  
반면 Messaging 기반의 Reactive Programming 에서는 **필요한 경우에만 쓰레드를 생성후 메세지 형태로 전달**하기 때문에 더 효율적으로 컴퓨팅 리소스를 사용할 수 있습니다.

## RxJava2

데이터를 가공/변형/처리를 하는 라이브러리

- Observable : 아이템을 발행 
- Subscriber : 아이템을 소비 

Observable은 0..N 개의 Item을 발행하며 `onNext(item)`,  
최종적으로 `onCompleted()` 또는 `onError()`를 전파하여 흐름이 종료되었음을 알린다.

Observable을 더 이상 구독하고 있을 필요가 없다면, `unsubscribe`나 `dispose`를 해주어야 한다.  
- unsubscribe : 구독을 중지
- dispose : 아예 더 이상 구독을 하지 않도록 처분하는 것 



옵저버 패턴과 비슷하지만,  
Observable은 보통 누군가 명시적으로 아이템을 구독(subscribe)하기 전까지는 아이템을 발행하지 않는 차이점이 있다.

### 1. Observable 생성하기
```java
Observable.create(
    subscriber -> {
        subscriber.onNext("Hello");
        subscriber.onNext("Hi");
        subscriber.onCompleted();
    }
)

// Observable.from(Arrays.asList("Hello", "Hi"));
// 단, Observable.just() 나 Observable.from()을 사용하려면
// 발행되는 Item 들이 observable 생성시점에 이미 정해져있어야 한다. 
```


----

### Data Binding


데이터 바인딩 모델? 

Rx 를 사용하면

데이터를 바인딩해준다. 일관성을 가짐


view 안에 있는 textView가 있고,

model이 있을 경우에

데이터 바인딩 뷰와 모델이 일관성을 가지도록 ...



-----

## Observer pattern

한 객체의 상태가 바뀌면 그 객체에 의존하는 다른 객체들한테 연락이 가고 자동으로 내용이 갱신되는 방식으로, 1:N 의존성을 정의한다.

상호의존성을 최소화하여 변경에 유연하다는 장점

Subject Interface : add(), delete(), notifyObservers()
Observer Interface : update()

### 상태 전달하기
- PUSH : Subject가 Observer에게 상태를 보내는 방식
- PULL : Observer가 Subject로부터 상태를 요청하는 방식