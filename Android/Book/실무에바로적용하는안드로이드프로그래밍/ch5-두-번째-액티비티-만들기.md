# Chapter5. 두 번째 액티비티 만들기

## 두 번째 액티비티 준비하기

문자열 추가하기

```xml
<string name="warning_text">정말로 답을 보겠습니까?</string>
<string name="show_answer_button">정답 보기</string>
<string name="cheat_button">커닝하기!</string>
<string name="judgment_toast">커닝은 나쁜짓이죠.</string>
```

### 새로운 액티비티 생성하기

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              xmlns:tools="http://schemas.android.com/tools"
              android:layout_width="match_parent"
              android:layout_height="match_parent"
              android:gravity="center"
              android:orientation="vertical"
              tools:context="com.bignerdranch.android.geoquiz.CheatActivity">
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:padding="24dp"
        android:text="@string/warning_text"/>
    <TextView
        android:id="@+id/answer_text_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:padding="24dp"
        tools:text="정답"/>
    <Button
        android:id="@+id/show_answer_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/show_answer_button" />
</LinearLayout>
```

### 자동 생성된 우리의 액티비티 클래스

### 매니페스트에 액티비티 선언하기

- `app/manifests/AndroidManifest.xml` 
- 안드로이드 운영체제에 애플리케이션을 설명하는 메타데이터 포함
- 모든 액티비티는 안드로이드 운영체제가 알 수 있도록 반드시 매니페스트에 선언되어야 한다.

```xml
<activity android: name=".CheatActivity"></activity>
```


### QuizActivity에 커닝하기! 버튼 추가하기

1. 커닝 버튼을 디폴트 레이아웃, 가로 레이아웃에 각각 추가하기
2. 클릭 리스너 설정하기

## 액티비티 시작시키기

```java
public void startActivity(Intent intent)
```

`startActivity()`는 직접 액티비티 실행 X, 실행 요청 O

1. 안드로이드 운영체제의 컴포넌트 ActivityManager로 `startActivity(intent)` 전달
2. ActivityManager가 실행할 액티비티의 인스턴스를 생성하고 `onCreate()` 호출

### 인텐트로 통신하기

**인텐트**는 **컴포넌트**가 운영체제와 통신하기 위해 사용할 수 있는 객체.  
다목적 통신 도구.

- component의 종류 : service, broadcast receiver, content provider ...

```java
// 시작시킬 액티비티를 알려주기 위해 사용하는 인텐트 생성자
public Intent(Context packageContext, Class<?> cls)
```

Context 인자는 액티비티 클래스를 찾을 수 있는 애플리케이션 패키지를 액티비티 매니저에게 알려준다.


```java
startActivity(new Intent(QuizActivity.this, CheatActivity.class));
```

액티비티를 시작시키기 전에 ActivityManager는  
시작시킬 액티비티 클래스가 해당 패키지의 매니페스트에 선언되어 있는지 확인한다.

#### 명시적 인텐트와 암시적 인텐트

- **명시적(explicit) 인텐트** : 같은 애플리케이션 내부에 있는 액티비티를 시작시키기 위해 사용됨.  
Context와 Class 객체를 갖는 Intent 객체를 생성하는 것.  

- **암시적(implicit) 인텐트** : 다른 애플리케이션의 액티비티를 시작시키기 위해 사용됨.

## 액티비티 간의 데이터 전달

### 인텐트 엑스트라 사용하기

CheatActivity에 전달할 값 : `mQuestionBank[mCurrentIndex].answerTrue`

엑스트라 키는 엑스트라의 데이터를 읽어서 사용하고자 하는 액티비티에 정의해야 한다.  
엑스트라의 수식자로 패키지 이름을 사용하면 다른 앱의 엑스트라와 이름 충돌을 방지할 수 있다.  

```kotlin
// CheatActivity.kt
private const val EXTRA_ANSWER_IS_TRUE = "xyz.e0zoo.geoquiz.answer_is_true"
```

QuizActivity에서는 CheatActivity가 인텐트로 무엇을 받는지 알 필요가 없다.  
-> CheatActivity를 실행을 위해 인텐트를 요청하는 코드를 별도의 메서드로 **캡슐화** 하는 것이 좋겠다.

```kotlin
// CheatActivity의 새로운 newIntent()
companion object {
    ...
    fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent{
        val i = Intent(packageContext, CheatActivity::class.java)
        i.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
        return i
    }
}
```

```kotlin
// QuizActivity.kt
cheatButton.setOnClickListener {
    val answerIsTrue = mQuestionBank[mCurrentIndex].answerTrue
    startActivity(CheatActivity.newIntent(this,answerIsTrue))
}
```

```kotlin
// CheatActivity.kt
val mAnswerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false)
        
showAnswerButton.setOnClickListener { 
    if (mAnswerIsTrue) 
        answerTextView.setText(R.string.true_button)
    else 
        answerTextView.setText(R.string.false_button)
}
```

### 자식 액티비티로부터 결과 돌려받기

```java
public void startActivityForResult(Intent intent, int requestCode)
```

- 요청 코드 (`requestCode`) : 사용자가 정의한 정수. 어떤 자식 액티비티가 결과를 돌려주는지 알고자 할 때 사용.


```kotlin
// QuizActivity.kt
cheatButton.setOnClickListener {
    ...
    val i = CheatActivity.newIntent(this,answerIsTrue)
    startActivityForResult(i, REQUEST_CODE_CHEAT)    
}
```

#### 결과 데이터 설정하기

```java
// 부모 액티비티에 데이터를 돌려주기 위해 자식 액티비티에서 호출할 수 있는 메서드
pulic final void setResult(int resultCode)
pulic final void setResult(int resultCode, Intent data)
```

일반적으로 **resultCode**는 `Activity.RESULT_OK`(-1), `Activity.RESULT_CANCELED`(0) 두 개중 하나이다.

자식 액티비티가 startActivityForResult()로 시작되었다면, 결과 코드는 항상 부모에게 반환된다.  
이때 자식 액티비티에서 setResult()가 호출되지 않고 사용자가 Back 버튼을 누르면, 부모는 `Activity.RESULT_CANCELED`을 결과로 받게 된다.

#### 인텐트 돌려주고 결과 데이터 처리하기

`Activity.setResult(int, Intent)`를 호출해 부모 액티비티에서 데이터를 받도록 해보자.

```kotlin
// CheatActivity.kt
override fun onCreate(savedInstanceState: Bundle?) {
    showAnswerButton.setOnClickListener {
        ...
        setAnswerShownResult(true)
    }
}
private fun setAnswerShownResult(isAnswerShown: Boolean){
    val data = Intent()
    data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
    setResult(Activity.RESULT_OK, data)
}
```

1. QuizActivity에서 커닝하기 버튼을 누름.
2. `startActivityForResult()`를 호출해 CheatActivity로 감.
3. 정답 보기 버튼을 누름.
4. CheatActivity는 `setResult()`를 호출.
5. 사용자가 Back 버튼을 눌러 QuizActivity로 돌아감.
6. ActivityManager는 `onActivityResult()`를 호출함.
    ```java
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    // requestCode : QuizActivity에서 보낸 requestCode
    // resultCode와 data : CheatActivity에서 setResult()한 것
    ```
7. resultCode와 requestCode를 확인하고 mIsCheater 변수에 사용자가 커닝했는지 여부를 저장한다.
    ```kotlin
    // CheatActivity.kt
    // 결과로 돌려주는 엑스트라 데이터의 타입을 변환하기 위한 메서드
    companion object {
        ...
        fun wasAnswerShown(result: Intent): Boolean 
            = result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false)
    }
    ```
    ```kotlin
    // QuizActivity.kt
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == REQUEST_CODE_CHEAT) {
            data?.let {
                mIsCheater = CheatActivity.wasAnswerShown(it)
            }
        }
    }
    ```
8. 커닝 여부에 따라 적절한 응답을 준다.

## 안드로이드가 액티비티를 어떻게 알까?

처음 앱을 실행하면 안드로이드 운영체제는 **launcher 액티비티**를 시작시킨다.
```xml
<!-- AndroidManifest.xml -->
...
<activity android:name=".QuizActivity">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
...
```

액티비티들은 **back stack**에 쌓이고  
Back 버튼을 누르면 스택의 top 있던 액티비티 인스턴스는 제거된다.

`Activity.finish()`를 호출해도 스택에서 제거됨.

- Back 버튼을 눌러 이전에 실행하던 앱의 화면으로 돌아갈 수 있다.
- ActivityManager는 안드로이드 운영체제에 존재한다.

- back stack은 하나의 앱에서만 사용되는 것이 아니라 운영체제와 장치 전체에서 사용된다.

## 챌린지

문제점  
1. CheatActivity에서 정답을 커닝하고 장치를 회전하면 커닝 결과가 지워진다.
    - 솔루션:  
    액티비티가 중지하기 전에 onSaveInstanceState()에서 커닝 여부를 저장한다.  
    액티비티가 다시 생성되면 savedInstanceState가 존재하면 커닝 여부값을 확인하고  
    커닝을 했다면 정답 텍스트뷰에 정답을 넣어주고 setResult도 해준다.  

2. 커닝을 하고 돌아와 장치를 회전하면 mIsCheater 값을 지울 수 있다.
    - 솔루션 :  
    onSaveInstanceState()에서 mIsCheater값도 저장해준다.
    
3. 사용자는 자신이 커닝했던 질문이 다시 나타날 때까지 다음 버튼을 누를 수 있다.
    - 솔루션 :  
    마지막 문제에서는 다음 버튼이 안보이게 한다.

