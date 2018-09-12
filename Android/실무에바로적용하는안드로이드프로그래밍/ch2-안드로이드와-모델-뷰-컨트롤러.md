# Chapter.2 안드로이드와 모델-뷰-컨트롤러

## 새로운 클래스 만들기

```kotlin
data class Question(var mTextResId : Int, var mAnswerTrue: Boolean)
```

> 모델 <- 컨트롤러 -> 뷰

- 모델 : Question 클래스
- 컨트롤러 : QuizActivity
- 뷰(레이아웃) : TextView, Button ...

## 모델-뷰-컨트롤러와 안드로이드

애플리케이션의 어떤 객체든 **모델 객체** or **뷰 객체** or **컨트롤러 객체**가 되어야 한다는 것이 MVC의 주요 관점이다.


- **모델 객체** :  
애플리케이션의 데이터와 **비즈니스 로직**을 갖는다.  
모델 객체는 사용자 인터페이스를 모른다.  
**데이터를 보존하고 관리**하는 것이 유일한 목적.  
모든 모델 객체들은 **모델 계층(layer)** 을 구성한다.

- **뷰 객체** :  
**화면**에서 볼 수 있는 것.  
뷰 객체들은 **뷰 계층**을 구성한다.

- **컨트롤러 객체** :  
뷰와 모델 객체를 **결속**시키며, **애플리케이션 로직**을 포함한다.  
뷰 객체에 의해 촉발되는 **이벤트들에 응답**하고, **데이터의 흐름을 관리**하기 위해 설계된다.  
안드로이드에서 컨트롤러는 일반적으로 Activity나 Fragment, Service의 서브 클래스다.


> 모델과 뷰 객체는 서로 직접 통신하지 않는다. 
>> 뷰 <-> 컨트롤러 <-> 모델

```
사용자 입력 
    |
        ----메세지 전송--->           --모델 객체 변경-->    
    뷰                      컨트롤러                   모델
      <-변경된 모델을 뷰에 반영-        <-모델 데이터 가져옴-
```

### MVC의 장점

개별적인 클래스 대신 계층의 관점으로 생각할 수 있다는 장점.

뷰를 추가해도 모델의 구현 사항을 신경쓰지 않아도 된다.

클래스를 더 쉽게 재사용할 수 있게 해준다.

## 뷰 계층 수정하기

문자열 안에 아포스트로피( ' )를 넣어야 할 때는  
`\'`와 같이 이스케이프 시퀀스 문자를 추가해야 한다.  

## 컨트롤러 계층 수정하기

이제는 보여줄 질문이 여러 개이므로 모델과 뷰 계층을 결합하기 위해 QuizActivity가 더 힘든 일을 해야 한다.

```kotlin
class QuizActivity : AppCompatActivity() {

    private val mQuestionBank = listOf(
            Question(R.string.question_oceans, true),
            Question(R.string.question_mideast, false),
            Question(R.string.question_africa, false),
            Question(R.string.question_americas, true),
            Question(R.string.question_asia, true))

    private var mCurrentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.size
            updateQuestion()
        }

        updateQuestion()

    }

    private fun updateQuestion() {
        val question: Int = mQuestionBank[mCurrentIndex].textResId
        questionTextView.setText(question)
    }

    private fun checkAnswer(userPressedTrue: Boolean) {

        val messageResId: Int =
                if (userPressedTrue == mQuestionBank[mCurrentIndex].answerTrue)
                    R.string.correct_toast
                else
                    R.string.incorrect_toast


        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

    }
}

```

## 장치에서 실행하기
실제 하드웨어 장치에서 실행될 수 있도록 시스템, 장치, 애플리케이션 설정.

### 장치 연결하기

### 개발용 장치 구성

개발자 옵션에서 USB 디버깅 체크

## 아이콘 추가하기

- 장치의 화면 픽셀 밀도를 나타내는 수식자(_qualifier_)

    - mdpi : 중밀도 화면 (~160dpi)

    - hdpi : 고밀도 화면 (~240dpi)
    - xhdpi : 초고밀도 화면 (~320dpi)
    - xxhdpi : 극초고밀도 화면 (~480dpi)


- drawable
- drawable-v24
??


### 프로젝트에 리소스 추가하기

drawable-mdpi , drawable-hdpi, drawable-xhdpi, drawable-xxhdpi 디렉토리가 없다면 생성하자.

이미지 파일에는 리소스 ID가 자동으로 부여되고, 
파일 이름은 소문자여야 하며 중간에 공백이 없어야 한다.


앱이 실행될 때 어떤 drawable 디렉터리의 이미지를 사용할 것인가는 안드로이드 운영체제가 결정한다.


### XML에서 리소스 참조하기

`@drawable/`

## 챌린지

### 챌린지: 리스너를 TextView에 추가하기

TextView를 눌렀을 때도 다음 질문을 사용자가 볼 수 있도록 만들어 보자.

```kotlin
questionTextView.setOnClickListener {
    updateQuestion()
}
```

### 챌린지: 이전 버튼 추가하기

```xml
<LinearLayout
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="horizontal">

    <Button
        android:id="@+id/backButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/back_button"
        android:drawableStart="@drawable/arrow_left"
        android:drawableLeft="@drawable/arrow_left"
        android:drawablePadding="4dp"/>

    <Button
        android:id="@+id/nextButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/next_button"
        android:drawableEnd="@drawable/arrow_right"
        android:drawableRight="@drawable/arrow_right"
        android:drawablePadding="4dp"/>
        
</LinearLayout>
```



### 챌린지: Button을 ImageButton으로 변경하기

ImageButton은 ImageView에서 상속받는 위젯.

Button은 TextView에서 상속받음.

ImageButton 으로 변경시 Description이 빠졌다는 경고는  
시력이 안 좋은 사용자들을 지원하기 위한 것.



```xml
<ImageButton
    android:id="@+id/previousButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@drawable/arrow_left"
    android:contentDescription="@string/previous_button" />

<ImageButton
    android:id="@+id/nextButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@drawable/arrow_right"
    android:contentDescription="@string/next_button" />

```
