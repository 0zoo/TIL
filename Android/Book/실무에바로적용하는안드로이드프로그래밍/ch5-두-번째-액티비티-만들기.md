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

Context와 Class 객체를 갖는 Intent 객체를 생성하는 것은 **명시적(explicit) 인텐트** 를 생성하는 것.

명시적 인텐트는 애플리케이션 내부에 있는 액티비티를 시작시키기 위해 사용된다.

## 액티비티 간의 데이터 전달

### 인텐트 엑스트라 사용하기

### 자식 액티비티로부터 결과 돌려받기

#### 결과 데이터 설정하기

#### 인텐트 돌려주기

#### 결과 데이터 처리하기

## 안드로이드가 액티비티를 어떻게 알까?

## 챌린지


