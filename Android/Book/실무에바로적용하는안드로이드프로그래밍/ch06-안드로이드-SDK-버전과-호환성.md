# Chapter6. 안드로이드 SDK 버전과 호환성

## 안드로이드 SDK 버전
2018년 9월 기준
![](https://user-images.githubusercontent.com/38287485/45821855-28c15a80-bd25-11e8-847a-bff92e52d3fa.png)

https://developer.android.com/about/dashboards/

위의 표에서도 볼 수 있듯이   
새로운 버전의 안드로이드가 나오더라도 지난 버전을 사용하는 장치들이 곧바로 업그레이드 되거나 교체되지 않는다.

## 호환성과 안드로이드 프로그래밍

서로 다른 크기의 장치들은 안드로이드의 레이아웃 시스템에 의해서 비교적 쉽게 관리할 수 있다. (태블릿 - configuration qualifier)


**증분적인 릴리즈(예 4.x의 x)** 는 과거 버전과의 호환성 유지에 문제가 없다.

그러나 **메이저 버전(예 4.x의 4)** 의 경우에는 많은 변화가 생기기 때문에 호환성 유지에 더 많은 노력이 필요하다. 
(예: 4.x와 함께 2.x 버전을 지원해주려면 더 많은 노력이 필요함)  
-> 구글에서 지원 라이브러리 제공


- build.gradle (Module: app)
    1. compileSdkVersion
    2. minSdkVersion
    3. targetSdkVersion

### 최소 SDK 버전

앱을 설치하는 기준으로 삼는 최소한의 안드로이드 버전.

API 레벨 19은 약 95%

API 레벨 16(젤리빈)은 99.4%의 안드로이드 장치에서 앱 실행 가능.

### 목표 SDK 버전

targetSdkVersion 값은 개발된 앱이 실행되는 API 레벨을 안드로이드에 알려준다.

대부분의 경우 targetSdkVersion은 가장 최신의 안드로이드 버전이 될 것.

targetSdkVersion를 더 하위 버전으로 지정하는 경우?  
상위 버전에서 잘 작동한 앱이 하위 버전에서도 잘 작동하는지 확인할 때 사용.  

### 컴파일 SDK 버전

우리와 컴파일러만 아는 정보.

compileSdkVersion은 코드를 빌드할 때 사용할 버전을 나타낸다.

안드로이드 스튜디오가 import할 때 어떤 버전의 SDK에서 찾을 것인가를 결정하는 것.

compileSdkVersion의 가장 좋은 선택은 가장 최신의 API 레벨.

### 상위 버전의 API 코드를 안전하게 추가하기

GeoQuiz 앱의 경우  
- minSdkVersion: 16
- compileSdkVersion: 27
- targetSdkVersion: 27

minSdkVersion 과 compileSdkVersion이 차이가 꽤 있기 때문에 호환성을 고려해야 한다.

minSdkVersion보다 더 상위 버전의 코드를 호출한다면?  
minSdkVersion에 해당하는 안드로이드 장치에서는 앱이 중단될 것.  
-> 안드로이드 Lint가 빌드 에러 알려줌.

GeoQuiz 앱에 API 레벨 21의 클래스와 메서드를 사용해 보자.  
![](https://user-images.githubusercontent.com/38287485/45878478-253cda80-bddc-11e8-91eb-d0a7920397b6.png)

이런 에러를 어떻게 없앨 수 있을까??  
1. minSdkVersion 을 21로 올리자.  
-> 호환성 이슈를 회피하는 것은 진정한 대처 방법이 아니다.
2. 장치의 안드로이드 버전을 확인하는 조건문으로 상위 버전의 API코드를 둘러싸는 것.

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    val cx = showAnswerButton.width / 2
    val cy = showAnswerButton.height / 2
    val radius = showAnswerButton.width.toFloat()

    val anim = ViewAnimationUtils
        .createCircularReveal(showAnswerButton, cx, cy, radius, 0f)

    anim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            // 애니메이션이 끝나면 정답을 보여주고 정답 보기 버튼을 감춘다.
            showAnswerButton.visibility = View.INVISIBLE
        }
    })

    anim.start()
} else {
    showAnswerButton.visibility = View.INVISIBLE
}
```

`Build.VERSION.SDK_INT`: 현재 장치의 안드로이드 버전


## 안드로이드 개발자 문서 사용하기

안드로이드 개발자 문서 : https://developer.android.com  
- Design (디자인)
- Distribute (배포)
- Develop (개발)
    1. 안드로이드 교육
    2. API 가이드
    3. 참조 문서
    4. 도구
    5. 구글 서비스
    6. 샘플


## 챌린지: 빌드 버전 보여주기

장치의 API 레벨을 알려주는 텍스트뷰를 추가하자.

```kotlin
val apiLevel = "API 레벨 ${Build.VERSION.SDK_INT}"
apiLevelTextView.text = apiLevel
```

