App이 처음 구동될 때 생성되는 인스턴스 분리

`Application()`을 상속받는 어플리케이션 클래스를 생성하고,  
`AndroidManifest.xml`에 등록한다.

```
<application
        android:name=".ChatApplication"
```
```kotlin
class ChatApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this);
    }
}
```

-------

Firebase의 서비스 계정 파일은 절대 공개된 Github에 올리면 안됩니다.  

=> `app/.gitignore`에 `google-services.json`을 추가.


-----


MVC - Activity(Fragment) == Controller

------

데이터의 변경을 확인하는 2가지 방법

1) pull  
: 데이터가 필요한 사람이 필요한 시점에 끌어온다.

2) push - Observer Pattern  
: 데이터가 변경된 시점에 필요한 사람에게 알려준다.

-----

`lateinit` : Non-null 프로퍼티에 대한 수동적 초기화 지연

 lazy initialization과는 달리  
 생성자 단계에서 값이 저장되지 않아도 정상적으로 컴파일이 되도록.


`by lazy`는 Kotlin에서 초기화 지연을 실행하는 읽기 전용의 프로퍼티를 구현할 때 매우 유용하게 사용할 수 있습니다.

`by lazy { ... }`가 포함하는 코드는 정의된 프로퍼티가 사용되는 최초의 지점에서 초기화 과정을 실행합니다.

```kotlin
private val messageView : TextView by lazy {
    findViewById(R.id.message_view) as TextView
}
```

`프로퍼티 위임(Delegated property)`은 프로퍼티에 대한 `getter/setter`를 위임하여  
위임받은 객체로 하여금 값을 읽고 쓸 때 어떠한 중간 동작을 수행하는 기능

```kotlin
val/var <property name>: <Type> by <delegate>
```


1. `lazy()`는 람다를 전달받아 저장한 `Lazy<T>` 인스턴스를 반환합니다.

2. 최초 getter 실행은 `lazy()`에 넘겨진 람다를 실행하고, 결과를 기록합니다.

3. 이후 getter 실행은 기록된 값을 반환합니다.


즉, lazy는 프로퍼티의 값에 접근하는 최초 시점에 초기화를 수행하고  
이 결과를 저장한 뒤 기록된 값을 재반환하는 인스턴스를 생성하는 함수

------

## Observable

`Delegates.observable()`은 초기값과 수정 핸들러를 받는다. 핸들러는 프로퍼티에 값이 할당될 때마다 (할당이 일어난 후에) 호출된다. 핸들러는 할당된 프로퍼티, 기존 값, 새로운 값을 파라미터로 받는다.

```kotlin
import kotlin.properties.Delegates

class User {
    var name: String by Delegates.observable("<no name>") {
        prop, old, new ->
        println("$old -> $new")
    }
}

fun main(args: Array<String>) {
    val user = User()
    user.name = "first"
    user.name = "second"
}

```

```
<no name> -> first
first -> second
```

https://reniowood.github.io/kotlin/2018/04/12/%EC%BD%94%ED%8B%80%EB%A6%B0-%EC%9C%84%EC%9E%84%EB%90%9C-%ED%94%84%EB%A1%9C%ED%8D%BC%ED%8B%B0.html


```kotlin
var items: List<ChatItem> by Delegates.observable(emptyList()) { prop, old, new ->
        notifyDataSetChanged()
    }
```

-------


