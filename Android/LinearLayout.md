# LinearLayout

## anko 라이브러리를 사용해 kotlin으로 LinearLayout 구성하기
```kotlin
val layout = LinearLayout(this)
layout.orientation = LinearLayout.HORIZONTAL
val name = EditText(this)
layout.addView(name)
```

## 뷰 정렬하기

----
#### 정렬 속성
1. `layout_gravity` : 부모에 뷰가 모두 채워지지 않아 여유 공간이 생겼을 때 여유 공간 안에서 뷰를 정렬.

2. `gravity` : 뷰 안에 표시하는 내용물을 정렬할 때 

----