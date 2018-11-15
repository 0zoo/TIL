# Chapter21. XML drawables

XML drawable은 화면 밀도와 무관하다.

## 균일한 버튼 만들기

버튼 사이에 간격을 주고, 간격은 화면의 크기에 따라 유동적으로 변하도록 FrameLayout으로 Button을 감싸주도록 레이아웃을 수정하자.

## 형태 drawable

**shape drawable** 을 사용해서 버튼을 둥글게 만들어보자.

```xml
<!-- res/drawable/button_beat_box_normal.xml -->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">

    <solid
        android:color="@color/dark_blue"/>
</shape>
```

https://developer.android.com/guide/topics/resources/drawable-resource?hl=ko#Shape

shape drawable을 정의하고 버튼의 배경으로 지정하고 실행하면  

버튼이 동그란 모양으로 나오는 것을 확인할 수 있다.


## 상태 리스트 drawable

```xml
<!-- res/drawable/button_beat_box_pressed.xml -->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">

    <solid
        android:color="@color/red"/>
</shape>
```

- **state list drawable** : 어떤 상태를 기준으로 다른 drawable을 참조하는 drawable


```xml
<!-- res/drawable/button_beat_box.xml -->
<selector xmlns:android="http://schemas.android.com/apk/res/android">

    <item android:drawable="@drawable/button_beat_box_pressed"
        android:state_pressed="true" />

    <item android:drawable="@drawable/button_beat_box_normal"/>
</selector>
```

상태 리스트 drawable은 활성/비활성화, 포커스 등등 여러 상태 지원.


## 레이어 리스트 drawable

- **layer list drawable** 을 사용하면 두 개 이상의 drawable을 결합하여 더 복잡한 드로어블을 만들 수 있다.


레이어 리스트 드로어블을 사용해서 버튼이 pressed 상태일 때 주위에 진한 붉은색의 테두리를 추가해보자.

```xml
<!-- res/drawable/button_beat_box_pressed.xml -->
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape
            android:shape="oval">

            <solid
                android:color="@color/red"/>
        </shape>
    </item>
    <item>
        <shape
            android:shape="oval">

            <stroke
                android:width="4dp"
                android:color="@color/dark_red"/>
        </shape>
    </item>
</layer-list>
```

## XML drawable을 사용해야 하는 이유

이미지 형태로 된 표준 배경을 사용할 때는 일반적으로 화면 밀도별 이미지를 여러개 생성한다.

XML drawable은 화면 밀도와는 상관 없기 때문에 한 번만 정의하면 된다.

## 9-patch 이미지

`res/drawable-xxhdpi`폴더를 만들고 버튼 이미지 2장을 폴더에 넣는다.    
`button_beat_box.xml`에서 이미지 파일로 배경을 변경해준다.

![screenshot_1542201087](https://user-images.githubusercontent.com/38287485/48484752-51b02880-e85a-11e8-8202-175aef53098f.png)

하지만 결과물이 마음에 들지 않는다.

왜냐하면 이미지가 획일적으로 확장되었기 때문.  

내가 원하는 부분만 확장했으면 좋겠다.
-> **9-patch 이미지** (3x3 grid)

원하는 것 
1. 격자의 모서리는 그대로 
2. 옆쪽은 한 방향으로만 확장
3. 중앙은 양방향 확장

![9-patch](https://tekeye.uk/android/examples/ui/images/nine-patch-scaling-diagram.png)

9-patch 이미지와 PNG 이미지와 다른점
- 파일 이름이 **.9.png** 로 끝난다.
- **border pixel** (중앙 사각형 위치를 지정하는데 사용)
    - 중앙: 검은색
    - 테두리: 투명

안드로이드 SDK의 **draw9patch** 도구를 사용하자.

1. 이미지 파일 이름 변경  
`ic_button_beat_box_default.9.png`
2. 아래쪽의 9-patch 탭 클릭



![screenshot_1542202835](https://user-images.githubusercontent.com/38287485/48486251-57a80880-e85e-11e8-959f-6ed3d0eedab6.png)


http://recipes4dev.tistory.com/132

https://developer.android.com/studio/write/draw9patch?hl=ko


## Mipmap 이미지


