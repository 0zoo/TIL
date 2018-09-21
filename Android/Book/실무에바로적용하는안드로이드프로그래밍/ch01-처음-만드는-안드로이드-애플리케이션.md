# Intro

솔루션 파일 다운받기 : 
https://github.com/Jpub/AndroidBNR2

# Chapter1. 처음 만드는 안드로이드 애플리케이션

- **GeoQuiz** 앱은 사용자가 지리를 알고 있는지 테스트하는데, 사용자가 정답을 선택하면 앱 화면에서 즉시 결과를 알려준다.

## 앱 기본 사항

- **Activity**: 화면을 통해서 사용자가 작업할 수 있게 해준다.

- **Layout**: 사용자 인터페이스 객체들과 그것들의 화면 위치를 정의한다. 

## 안드로이드 프로젝트 생성하기

- package name : 안드로이드 생태계 전체에서 우리 앱을 고유하게 식별하는데 사용. company domain url을 거꾸로 하고 그 뒤에 앱 이름이 붙어서 자동 생성됨.

## 사용자 인터페이스 레이아웃 만들기

요소와 속성이 어떻게 동작하는지 알려면 계층적인 관점으로 레이아웃을 살펴봐야 한다.

### 뷰 계층구조

위젯들은 뷰 계층구조(view hierarchy)에 존재한다. 

계층구조의 최상위에 있는 레이아웃은 안드로이드 리소스 XML 네임스페이스를     `xmlns:android="http://schemas.android.com/apk/res/android"`로 지정해야 한다.


- View Group : View 서브 클래스. 다른 위젯들을 포함하고 배열하는 위젯.  
(LinearLayout, FrameLayout, RelativeLayout ...)


### 위젯 속성

#### android:layout_width 와android:layout_height

거의 모든 타입의 위젯에서 필요함.

- match_parent : 부모만큼의 크기
- wrap_content : 자신에게 필요한 크기만큼

최상위 레이아웃의 높이와 넓이 속성의 값 또한 match_parent인 이유는 ?   
-> 루트 요소이지만 안드로이드에서 제공한 부모 뷰를 갖기 때문에.

#### android:orientation

LinearLayout의 속성.

- 수직 : vertical
- 수평 : horizontal


#### android:text

- 문자열 리소스 : 별도의 문자열 XML 파일에 정의된 문자열.

`android:text = "True"` 처럼 직접 지정할 수도 있지만, 좋은 방법이 아님.  

문자열 리소스를 참조하는 방법을 사용하면, 여러 나라의 언어를 지원하기 위한 지역화를 쉽게 할 수 있기 때문.


### 문자열 리소스 생성하기

모든 프로젝트에는 `app/res/values/strings.xml`이라는 디폴트 문자열 파일이 포함된다.

`strings.xml` 이외에 여러개의 문자열 파일을 가질 수 있고, 파일명 또한 변경이 가능하다.  
단, 그 파일들은 `res/values` 디렉토리 아래에 위치해야 하고 `resources`라는 루트 요소를 갖는다.

### 레이아웃 미리 보기


## 레이아웃 XML에서 뷰 객체로

- AppCompatActivity : 안드로이드 Activity 클래스의 서브 클래스. 과거 안드로이드 버전과의 호환성을 지원하기 위해 제공됨. 

- **onCreate(Bundle)** 메서드는 액티비티 서브 클래스의 인스턴스가 생성될 때 자동으로 호출된다.
    - > public void setContentView( int layoutResID )
    - 위 메서드는 레이아웃을 inflate 하여 화면에 나타낸다.
        - inflate : 레이아웃을 뷰 객체로 생성하는 것.
    - 위 메서드를 호출할 때는 인플레이트할 레이아웃의 리소스 아이디를 인자로 전달한다.


### 리소스와 리소스 ID

- 레이아웃은 **리소스**이다.

- 코드에서 리소스를 사용하려면 리소스 ID를 지정해야 한다.

- `app/build/generated/source/r/debug`에 있는 `R.java`파일은 빌드시 자동 생성및 변경되며 수정하면 안된다.

- `R.java`
    - `R.layout.activity_quiz`가 있는 곳.  
    R 클래스의 내부 클래스인 layout 안에 정의되어 있음.

- 모든 위젯이 리소스 ID가 필요하지는 않음.



android:id 에서는 ID를 생성하고,   
android:text에서는 문자열을 참조만 하기 때문에  
`android:id="@+id/button"`처럼 + 부호가 붙는다.


## 위젯을 코드와 연결하기

```java
private Button mTrueButton;
// 변수 이름 앞에 붙은 m은 
// 안드로이드의 작명법을 따른 것.
```

### 위젯의 참조 얻기 

```java
public View findViewById(int id)
// 위젯의 리소스 ID를 인자로 받아 그 위젯의 View를 반환
```
반환된 View 객체 참조의 타입을 Button으로 casting 해서 변수로 지정해야 함을 유의하자.

```java
mTrueButton = (Button) findViewById(R.id.true_button);
```

### 리스너 설정하기

안드로이드 애플리케이션은 시작된 후 이벤트 발생을 기다리는 이벤트 기반(event-driven)으로 구동된다.


- 애플리케이션에서 특정 이벤트를 기다리는 것을 가리켜 그 이벤트를 `**리스닝 한다**`라고 한다.  
- 이벤트에 응답하기 위해 생성하는 객체를 `**리스너(listener)**` 라고 한다.   
- `리스너`는 해당 이벤트의 `리스너 인터페이스를 구현`한다.

안드로이드 SDK에는 다양한 이벤트의 리스너 인터페이스들이 준비되어 있으므로 추가로 만들 필요는 없다.

```java
// True 버튼에 리스너 설정하기
mTrueButton.setOnClickListener(new View.OnClickListener(){
    @Overide
    public void onClick(View v){
        // ...
    }
});
```
`setOnClickListener(OnClickListener)`는 OnClickListener 인터페이스를 구현하는 객체를 인자로 받는다.

```kotlin
// kotlin
trueButton.setOnClickListener { 
    //...
}
```

#### 익명의 내부 클래스 사용하기

`new View.OnClickListener(){...}`는  
View의 서브 클래스를 익명으로 정의하고, 그것의 인스턴스를 생성한다는 뜻.

- 별도의 이름을 갖는 클래스보다 익명의 내부 클래스를 사용하는 경우 :  
리스너를 구현하는 코드가 재사용할 필요가 없고, 이벤트 처리가 필요한 곳에서 처리 코드를 바로 볼 수 있기 때문. 

## 토스트 만들기

- 토스트(toast) : 사용자에게 뭔가를 알려주지만, 어떤 입력이나 액션도 요구하지 않는 짤막한 메세지

```java
// 토스트를 생성하기 위해 호출하는 메서드
public static Toast makeText(Context context, int resId, int duration)
```

- **Context**는 일반적으로 Activity의 인스턴스.  
(Activity는 Context의 서브 클래스)

- duration은 문자열을 얼마나 오래 보여줄지

### 코드 완성 기능 사용하기

```java
mTrueButton.setOnClickListener(new View.OnClickListener(){
    @Overide
    public void onClick(View v){
        Toast.makeText(QuizActivity.this,"",Toast.LENGTH_SHORT).show();

        // context에 단순히 this만 전달하면 안 됨.
        // this는 내부 클래스인 View.OnClickListener를 참조한다.
    }
});
```

## 에뮬레이터에서 실행하기

애플리케이션을 실행하려면  
1. 실제 장치
2. AVD(Android Virtual Device)  

가 필요하다.

에뮬레이터를 생성할 때는 에뮬레이션할 안드로이드 장치와 시스템 이미지를 선택한다.  
시스템 이미지에는 리눅스 커널과 안드로이드 프레임워크가 포함된다.

### AVD 에뮬레이터 버전 2


### 인텔 HAXM을 사용하여 에뮬레이터 성능 향상시키기

Intel x86 계열의 CPU를 갖는 컴퓨터(인텔 코어 i3/i5/i7 등)에서는 HAXM을 사용할 수 있다.

HAXM을 사용하여 에뮬레이터를 실행하면 속도가 상당히 빨라지기 때문에 사용을 추천한다.

## 안드로이드 앱 빌드 절차

프로젝트의 내용을 수정하면 따로 명령을 주지 않아도 안드로이드 스튜디오가 자동으로 빌드해 주고 있는데

빌드를 하는 동안 안드로이드 도구들이 우리의 리소스와 코드, AndroidManifest.xml 파일을 가지고 하나의 **`.apk`** 파일로 만든다.  
그리고 이 파일은 실제 장치나 에뮬레이터에서 실행될 수 있게 디버그 키가 부여된다. 

`.apk`를 구글 플레이 스토어에 배포하려면 구글에서 릴리즈 키를 받아 우리의 앱에 포함시켜야 한다.  
 참고: https://developer.android.com/studio/publish/preparing?hl=ko

- Build Process

    ![Build Process](https://developer.android.com/images/tools/studio/build-process_2x.png)

    ![Build Process](https://stuff.mit.edu/afs/sipb/project/android/docs/images/build.png)

    

- 레이아웃 xml 파일을 View 객체로 어떻게 변환할까?  
`aapt`가 레이아웃의 리소스들을 컴파일하고, 컴파일된 리소스들은 `.apk` 파일로 통합된다. `setContentView()`가 호출되면 `LayoutInflater`를 사용해서 레이아웃 파일에 정의된 각 `View`의 인스턴스를 생성한다.
 
https://developer.android.com/studio/build/




 ### 안드로이드 빌드 도구들

 모든 빌드 작업이 안드로이드 스튜디오 IDE에 통합되어 있기 때문에  
 `aapt`와 같은 안드로이드 표준 빌드 도구들이 호출되어 빌드 작업이 자동으로 수행된다.

 - 모든 빌드 절차는 안드로이드 스튜디오가 그래들 빌드 시스템을 사용해서 관리한다.

 - 터미널에서 빌드 방법   
 (단, 실행은 안 됨.)
 > $ ./gradlew installDebug  
 


