# Constraint Layout

연결 가능한 타깃
- 같은 레이아웃 안에 들어 있는 다른 뷰
- 부모 레이아웃
- 가이드라인

가능한 연결점 위치
- Top, Bottom, Left(Start), Right(End)
- CenterX(가로축), CennterY(세로축)
- Baseline(텍스트뷰)

뷰를 화면의 정가운데에 위치하고 싶다면?
- 연결점을 각각 화면의 네 벽면에 연결한다.  
(이 경우 마진은 무시됨)

정가운데가 아니라 어느 한 쪽으로 치우치게 만들고 싶다면?
- **Bias**를 조절해보자.
    - Bias : 화면을 비율로 나눈 후 어느 곳에 위치시킬 것인지를 결정하는 값.  
    (기본값 0.5)

----

1. 부모 여유 공간 채우기:  
사각형 안쪽의 선이 구불구불한 선으로 표시   
layout_width, layout_height 는 0dp  
-> layout_width, layout_height로 크기 설정하는 것이 아님. 

2. 뷰의 내용물 채우기:  
사각형 안쪽의 선이 중앙을 향하는 화살표로 표시  
layout_width, layout_height 는 wrap_content

3. 고정 크기:  
사각형 안쪽의 선이 직선으로 표시  
layout_width, layout_height 는 지정한 값

----

- **Guideline**  
: 여러 개의 뷰를 일정한 기준 선에 정렬할 때 사용.

----

ConstraintLayout 에서 뷰와 뷰를 연결할 때 사용하는 xml 속성:
```
layout_constraint[소스뷰 연결점]_[타깃뷰 연결점]="[타깃뷰id]"
app:layout_constraintTop_toBottomOf="@+id/imageView"
```











