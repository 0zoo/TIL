# Layout의 xml 기본 속성

## xml 살펴보기

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".SignInActivity">
   
   ...

</android.support.constraint.ConstraintLayout>
```

XML 파일에 일반적으로 추가하는 정보이며,  
이 파일이 XML의 형식으로 된 것임을 알려준다.
```xml
<?xml version="1.0" encoding="utf-8"?>
```

안드로이드의 기본 API에 포함되어 있다면 이름만 있어도 되지만,
ConstraintLayout은 나중에 추가되어 외부 라이브러리로 분류되어 있어, 그 앞에 패키지 이름까지 같이 적어놔야 함. 
```
android.support.constraint.ConstraintLayout 
```
---

`xmlns:`로 시작되는 속성

- `xmlns:android` : 안드로이드 기본 SDK에 포함되어 있는 속성 사용.  
나머지 속성의 prefix로 사용됨. ex) `android:layout_width`

- `xmlns:app` : 프로젝트에서 사용하는 외부 라이브러리에 포함되어 있는 속성 사용.

- `xmlns:tools` : 안드로이드 스튜디오의 디자이너 도구 등에서 화면에 보여줄 때 사용.  
이 속성은 앱이 실행될 때는 적용되지 않고 안드로이드 스튜디어에서만 적용됨.

---


`tools:` 접두어로 시작되는 속성  
: 앱이 실행될 때는 적용되지 않는 속성으로 디자이너 도구에서 화면에 표시하기 위해 사용.  
직접 속성을 수정할 필요가 없으며 디자이너 도구에서 자동으로 설정한다.
예) `tools:layout_editor_absoluteX="35dp"`


---
#### 사용 가능한 단위들

단위 | 단위 표현 | 설명
--------- | --------- | ---------
px | pixel | 화면 픽셀의 수
dp 또는 dip | density independent pixel | 160dpi 화면을 기준으로 한 픽셀.
sp 또는 sip | scale independent pixel | 텍스트 크기를 지정할 때 사용하는 단위. dp와 유사하나 글꼴의 설정에 따라 1sp당 픽셀수가 달라짐.
in | inch | 1 인치로 된 물리적 길이
mm | millimeter | 1 밀리미터로 된 물리적 길이
em | text size | 글꼴과 상관없이 동일한 텍스트로 크기 표시

---

## 대표적인 레이아웃 5가지

이름 | 설명 
--------- | --------- 
ConstraintLayout | Constraint based model. 제약 조건을 사용해 화면을 구성함.
LinearLayout | Box model. 한 쪽 방향으로 차례대로 뷰를 추가하여 화면을 구성함. 뷰가 차지할 수 있는 사각형 영역을 할당.
RelativeLayout | Rule based model. 부모 컨테이너나 다른 뷰와의 상대적 위치로 화면을 구성함.
FrameLayout | Single model. 가장 상위에 있는 하나의 뷰 또는 뷰그룹만 보여주는 방법. 여러 개의 뷰를 중첩한 후 각 뷰를 전환하여 보여주는 방식으로 자주 사용함.
TableLayout | Grid model. 격자 모양의 배열을 사용하여 화면을 구성함.

---


## 뷰의 영역

![view05](https://user-images.githubusercontent.com/38287485/42566278-08c5b116-8541-11e8-8e8a-b98cbc01e428.png)

---







