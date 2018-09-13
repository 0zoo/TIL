# Chapter3. 액티비티 생명주기

![Activity Lifecycle diagram](https://developer.android.com/guide/components/images/activity_lifecycle.png)

![Activity Lifecycle diagram](https://user-images.githubusercontent.com/38287485/45405558-557acf80-b69e-11e8-9610-50da04523309.jpg)

- 액티비티의 3가지 상태
    1. 실행(Running) - 화면에 보이며 foreground에 있음.
    2. 일시 중지(Paused) - 화면에 보임.
    3. 중단(Stopped) - 화면에서 볼 수 없음.


액티비티 생명주기 메서드를 우리가 직접 호출하지 않는다.  
그런 메서드들을 우리의 액티비티에서 오버라이드하면 적절한 시기에 안드로이드 런타임이 호출된다.

## 액티비티 생명주기 로깅하기

### 로그 메세지 만들기

`android.util.Log`클래스는 공유되는 시스템 로그로 로그 메세지를 전달한다.

```java
pulbic static int d(String tag, String msg)
// d는 deug
// 로그 메세지의 레벨
```

```java
// 태그 상수 - java
private static final String TAG = "QuizActivity";
```
```kotlin
// 태그 상수 - kotlin
companion object {
    private val TAG : String = QuizActivity::class.java.simpleName
}
```


```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d(TAG,"OnCreate() called")
}
override fun onStart() {
    super.onStart()
    Log.d(TAG,"onStart() called")
}

override fun onPause() {
    super.onPause()
    Log.d(TAG,"onPause() called")
}

override fun onResume() {
    super.onResume()
    Log.d(TAG,"onResume() called")
}

override fun onStop() {
    super.onStop()
    Log.d(TAG,"onStop() called")
}

override fun onDestroy() {
    super.onDestroy()
    Log.d(TAG,"onDestroy() called")
}
```

onCreate(...)의 경우 `super.onCreate(savedInstanceState)`가 맨 앞에 위치해야 한다. 

`@Override`를 명시적으로 표시해 주는 이유 :  
오버라이드 하는 메서드가 슈퍼 클래스에 있는지를 컴파일러에게 확인하라고 요청하는 것.

만약 `@Override`를 빼먹으면 컴파일러가 확인하지 않으므로 `onCreate()`를 새로 추가하는 오류를 범하게 된다.

### Logcat 사용하기

1. 앱을 실행 했을 때  
    > onCreate(), onStart(), onResume() 

2. Back 버튼을 누르면  
안드로이드 운영체제는 이 액티비티를 메모리에서 소멸시킨다.  
    > onPause(), onStop(), onDestroy()  

3. 앱을 다시 실행시키고, Home 버튼을 누르면  
    > onPause(), onStop()  

4. Recents 버튼을 누르고 GeoQuiz를 실행시키면   
    > onStart(), onResume() 

Home 버튼을 누르면 안드로이드는 액티비티를 일시 중지 및 중단시키지만,  
다시 돌아올 경우를 대비해서 소멸시키지는 않는다.  
(중단된 액티비티가 계속 존속은 보장 x.  
시스템에서 메모리를 회수해야 할 떄는 중단된 액티비티들 소멸시킴.)

팝업 창같이 화면의 일부 또는 전체를 가리는 경우에도 액티비티가 일시 중지되어  
사용자와 상호 동작할 수 없게 된다.  
이런 경우에는 팝업 창이 없어질 때 액티비티가 재실행(resume) 된다.


## 장치 회전과 액티비티 생명주기

현재 GeoQuiz 앱의 문제점 :  
두 번째 질문이 나타난 상태에서 장치를 회전시키면, GeoQuiz에서 다시 첫번째 질문으로 되돌아간다.

> onPause(), onStop(), onDestroy(), OnCreate(), onStart(), onResume()

-> 장치를 회전하면, QuizActivity가 소멸 후 인스턴스가 재생성되는 것을 확인할 수 있다.

-->  mCurrentIndex가 0으로 초기화 되면서 문제가 발생하는 것.

### 장치 구성과 대체 리소스

장치를 회전시키면 **장치 구성(device configuration)** 이 변경된다.

- 장치 구성 : 각 장치의 현재 상태를 나타내는 특성들의 집합  
(화면 방향, 화면 밀도, 화면 크기, 키보드 타입, dock 모드, 언어 ...)

일반적으로 애플리케이션에서는 서로 다른 장치 구성들에 맞추기 위해 대체 리소스를 제공한다.  

화면 밀도는 장치 구성의 변함 없는 요소이므로 런타임 시에 변경 불가.  
화면 방향과 같은 일부 요소들은 런타임 시에 **변경 가능**

#### 가로 방향 레이아웃 생성하기

![](https://user-images.githubusercontent.com/38287485/45480617-5b4ae080-b784-11e8-9aa1-e4c1df65ca13.png
)

-> `res/layout-land/` 폴더가 생성됨.


res 서브 디렉토리의 qualifier는 현재의 장치 구성에 가장 잘 맞는 리소스들을 안드로이드가 식별하는 방법이다.  
(ex -land 접미사 ..)

장치가 가로 방향일 때 안드로이드는 `res/layout-land/` 에서 리소스를 찾아서 사용함.


`land/activity_quiz.xml` 의 레이아웃을 변경해보자.
 
1. LinearLayout을 FrameLayout으로 변경한다.  
    - FrameLayout은 가장 간단한 ViewGroup이며, 어떤 특별한 방법으로도 자식들을 배열하지 않는다.  
    여기서는 자식 뷰들이 자신을의 layout_gravity 속성에 따라 배열된다.


```xml
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/questionTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:padding="24dp"
        android:layout_gravity="center_horizontal" />

   

    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_vertical|center_horizontal"
        android:orientation="horizontal">

        <Button
            android:id="@+id/trueButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/true_button" />

        <Button
            android:id="@+id/falseButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/false_button" />
    </LinearLayout>

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/nextButton"
        android:layout_gravity="bottom|end"
        android:text="@string/next_button"
        android:drawableRight="@drawable/arrow_right"
        android:drawableEnd="@drawable/arrow_right"
        android:drawablePadding="4dp"
        />
</FrameLayout>

```

런타임 시에 언제든 구성 변경(화면 방향, 키보드, 언어 ..)이 생기면 안드로이드는 현재의 액티비티를 소멸시키고  
새로운 인스턴스를 생성한다는 것에 유의하자.  

## 장치 회전 시 데이터 저장하기

```java
protected void onSaveInstanceState(Bundle outstate)
//이 메서드는 onPause(), onStop(), onDestroy()가 호출되기 전에 시스템에서 호출됨.
```

onSaveInstanceState()의 슈퍼 클래스에서 구현한 코드에서는  
액티비티의 모든 뷰들에 Bundle로 상태 정보를 저장할 것을 요구한다.

**Bundle**은 key와 value 한 쌍으로 데이터를 저장하는 구조다.

onCreate()에서 `super.onCreate(savedInstanceState)`를 통해 번들 객체를 슈퍼 클래스로 전달한다.  
슈퍼 클래스 onCreate()에서는 뷰들의 저장된 상태 정보를 알아내어 액티비티의 뷰 계층 구조를 새생성하는 데 사용한다.

### onSaveInstanceState(Bundle) 오버라이드 하기

1. QuizActivity에 번들의 key로 사용 할 상수 추가
    ```kotlin
    private const val KEY_INDEX = "index"
    ```

2. mCurrentIndex 값을 저장하기 위해 onSaveInstanceState() 오버라이드
    ```kotlin
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
        outState?.let { 
            it.putInt(KEY_INDEX,mCurrentIndex)
        }
    }
    ```
3. onCreate()애서 번들 객체 값 확인하기
    ```kotlin
    savedInstanceState?.let {
        mCurrentIndex = it.getInt(KEY_INDEX,0)
    }
    ```

- Bundle 객체에 데이터를 저장하거나 읽는 타입은  
기본형 데이터 타입이거나 Serializable 또는 Parcelable 인터페이스를  
구현하는 객체임을 유의하자.

- onSaveInstanceState()를 오버라이드했을 떄는 테스트를 해보는 것이 좋다.  
장치의 메모리 부족한 경우에는 메모리를 회수하기 위해   
안드로이드가 우리 액티비티를 소멸시키도록 하는 방법은   
이 책의 끝에 나오므로 참고하기.

## 액티비티 생명주기 다시 알아보기

## onSaveInstanceState(Bundle) 테스트하기

## 로깅 레벨과 관련 메서드들

