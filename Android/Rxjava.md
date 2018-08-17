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

Observable은
1. `onNext` - 새로운 데이터를 전달한다.
2. `onCompleted` - 스트림의 종료.
3. `onError` - 에러 신호를 전달한다

Observable을 더 이상 구독하고 있을 필요가 없다면, `unsubscribe`나 `dispose`를 해주어야 한다.  
- unsubscribe : 구독을 중지
- dispose : 아예 더 이상 구독을 하지 않도록 처분하는 것 

옵저버 패턴과 비슷하지만,  
Observable은 보통 누군가 명시적으로 아이템을 구독(subscribe)하기 전까지는 아이템을 발행하지 않는 차이점이 있다.

### 요소

1. `Observable`  
: 이벤트를 만들어내는 주제로, 이벤트 스트림을 통해 만든 이벤트를 내보냅니다. 한개부터 여러개의 이벤트를 만들 수 있으며, 만들지 않는 경우도 있습니다.

2. `Observer`  
: Observable 에서 만들어진 이벤트가 반응(React)하며, 이벤트를 받았을 때 수행할 작업을 정의합니다.  
    * Observer가 Observable를 **구독(subscribe)** 한다고 합니다.  
    `myObservable.subscribe(myObserver)`

3. `Operator`  
: 연산자는 이벤트 스트림을 통해 전달되는 이벤트를 변환합니다.  
이벤트가 가지고 있는 값을 다른 형태로 변환하는 것도 가능하고, -> map, flatMap  
특정 조건을 만족하는 이벤트 스트림을 흘려보내거나, 개수를 변경하는 작업등을 수행할 수 있습니다. -> filter, first, last

4. `Scheduler`  
: 해당 작업을 수행할 스레드를 지정합니다.  
UI - main thread  
IO / Worker / New Thread  
observerOn 메소드를 사용해서 지정하며, 이 메소드를 호출한 직후에 오는 연산자나 옵저버에서 수행되는 작업의 스레드가 변경됩니다.

5. `Disposable`  
: Observer가 Observable을 구독할 때 생성되는 객체로서, Observable에서 만드는 이벤트 스트림과 이에 필요한 리소스를 관리합니다.  
Observable로 부터 이벤트를 받지 않기 위해서는 이 객체를 통해 구독해지가 가능합니다.  
`CompositeDisposable`을 사용하면 여러개의 Disposable 객체를 하나의 객체에서 관리할 수 있습니다.


----


- 데이터 주입 : `subscribeOn`  
데이터를 주입하는 시점에 대한 쓰레드 선언이며 모든 stream 내에서 최종적으로 선언한 쓰레드가 할당됩니다.
- 데이터 처리 : `observeOn`  
쓰레드를 선언한 다음부터 새로운 쓰레드가 선언되기 전까지 데이터 처리에 동작할 쓰레드를 할당합니다.

-----
### 코틀린으로 바인딩하기

```
implementation 'com.jakewharton.rxbinding2:rxbinding-kotlin:2.1.1'
```

-----

## Observer pattern

한 객체의 상태가 바뀌면 그 객체에 의존하는 다른 객체들한테 연락이 가고 자동으로 내용이 갱신되는 방식으로, 1:N 의존성을 정의한다.

상호의존성을 최소화하여 변경에 유연하다는 장점

Subject Interface : add(), delete(), notifyObservers()
Observer Interface : update()

### 상태 전달하기
- PUSH : Subject가 Observer에게 상태를 보내는 방식
- PULL : Observer가 Subject로부터 상태를 요청하는 방식