
일반적인 방법:
1. xml 레이아웃 코드 작성
2. View 인스턴스화 (findViewById)
3. View 업데이트 (setText, setBackground ...)

## 데이터 바인딩을 이용한 UI 구현

데이터 바인딩 라이브러리를 사용해보자.


1. Data Object - POJO 

POJO (plain old java object)란?   
    - 제약이 없는 일반적인 자바 객체   
    - 상속, 인터페이스 구현, 어노테이션 같은 것들을 라이브러리나 프레임워크로부터 강제받지 않는 객체   
    - 객체 지향 원리에 충실해야 한다.    
    - 예) setter, getter 메서드로 이루어진 단순한 Value Object    



2. <data> 태그 선언

```xml
<layout xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
xmlns:tools="http://schemas.android.com/tools">

    <data>
        <variable name = "변수명" types = "패키지명.클래스이름">
    </data>
</layout>
```

3. 변수값 바인딩

data 태그를 통해 선언된 변수를 `@{}`를 사용해 View의 속성 값으로 넣을 수 있다.

```xml
<TextView
    android: text = "@{변수명.데이터클래스의변수}"
/>
```

4. 데이터 바인딩

바인딩 클래스는 레이아웃 파일의 이름을 기준으로 자동 생성됨.  
예) `R.layout.activity_main` -> `ActivityMainBinding.class`

바인딩 클래스를 정의하고 `<data>` 태그를 통해 선언한 레이아웃 변수를 직접 설정해줘야 한다.

```kotlin
val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
binding.setContent(model);
```

**주의**  
코틀린은 그래들에 kapt 추가해야 함.



이렇게 바인딩 클래스를 설정해 주면, POJO 값들을 View에 사용할 수 있다.  
하지만, 이후에 POJO를 수정해도 업데이트 되지 않는 문제
-> **Observable Object** ,  **Observable Field** ,  **Observable Collection** 

[참고](https://brunch.co.kr/@oemilk/107)
-----

데이터 바인딩을 이용하여 View에서 발생하는 이벤트들을 바인딩할 수 있다.

일반적인 이벤트 처리 방법: 익명 클래스, 인터페이스 구현 등등..


데이터 바인딩의 이벤트 처리 방법 2가지:
1. 메서드 참조  
    - 메서드를 xml에서 직접 바인딩. 
    - 매개변수와 반환 타입 일치.
    - 데이터가 바인딩될 때 리스너 구현 생성됨.
2. 리스너 바인딩  
    - 임의의 바인딩 식을 이용. 
    - 반환 타입 일치.
    - 이벤트가 발생할 때 리스너 구현 생성됨. (비동기 적합)

-----


