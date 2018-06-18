# 2.2 클래스와 프로퍼티
클래스를 선언하는 기본 문법에 대해 알아보자.
```java
//java의 Person 클래스
public class Person{
    private final String name;
    
    public Person(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
```

```kotlin
//Kotlin의 Person 클래스
class Person(val name: String)
```
이런 코드가 없이 데이터만 저장하는 클래스를 *value object*라 부른다.

자바 -> 코틀린 :  public 가시성 변경자가 사라졌다.
코틀린의 기본 가시성은 public이므로 이런 경우 변경자 생략 가능함.

## 2.2.1 프로퍼티
자바에서는 필드와 접근자를 한데 묶어 property라고 부른다.
코틀린의 프로퍼티는 자바의 필드와 접근자 메소드를 완전히 대신한다.
- val로 선언한 프로퍼티 : 읽기 전용
- var로 선언한 프로퍼티 : 변경 가능

```kotlin
class Person(
    val name: String, //읽기 전용. (비공개)필드와 (공개)게터
    var isMarried : Boolean //쓸 수 있음. (비공개)필드, (공개)게터, (공개)세터
)
```
코틀린은 비공개 필드, 세터, 게터로 이루어진 디폴트 접근자 구현을 제공한다.
```kotlin
val person = Person("Bob",true) //new키워드를 사용하지 않고 생성자를 호출한다.
println(person.name)
println(person.isMarried)
//프로퍼티의 이름을 직접 사용해도 자동으로 게터를 호출해줌.
```
자바의 게터나 세터 메소드를 호출하는 대신, 코틀린에서는 프로퍼티를 직접 사용한다.
// person.isMarried = false 
- tip : 자바에서 선언한 클래스에 대해 코틀린 문법을 사용해도 된다. 코틀린에서는 자바 클래스의 게터를 val 프로퍼티처럼 사용할 수 있고, 게터/세터 쌍이 있는 경우에는 var 프로퍼티처럼 사용할 수 있다.

- backing field (뒷받침하는 필드) : 프로퍼티의 값을 저장하기 위한 필드.

## 2.2.2 커스텀 접근자

프로퍼티의 접근자를 직접 작성하는 방법을 알아보자.

```kotlin
class Rectangle(val height: Int, val width: Int){
    val isSquare:Boolean
    get() { //프로퍼티 게터 선언
        return height == width
    }
    //get() = height == width
}

```
//isSquare 프로퍼티에는 값을 저장하는 필드가 필요 없다. 게터만 존재.
//프로퍼티에 접근할 때마다 게터가 값을 매번 계산한다.

## 2.2.3 코틀린 소스코드 구조: 디렉터리와 패키지
코틀린에도 자바와 비슷한 개념의 패키지가 있다.
모든 코틀린 파일의 맨 앞에 _package_문을 넣을 수 있다.
같은 패키지에 속해 있다면 다른 파일에서 정의한 선언일지라도 직접 사용할 수 있다.
반면 다른 패키지에 정의한 선언을 사용하려면 _import_를 통해 선언을 불러와야 함.

코틀린에서는 클래스 임포트와 함수 임포트에 차이가 없으며, 모든 선언을 import키워드로 가져올 수 있다. 


- geometry
    - example
        - Main.java
    - shapes
        - Rectangle.java

// 자바에서는 디렉터리 구조가 패키지 구조를 그대로 따라야 한다.


- geometry
    - example.kt
    - shapes.kt

// 코틀린은 패키지 구조와 디렉터리 구조가 맞아 떨어질 필요는 없다.
geometry.shapes라는 패키지가 있다면, 하위 패키지에 해당하는 별도의 디렉터리를 만들지 않고 geometry라는 폴더 안에 shapes.kt를 넣어도 된다.


하지만 자바처럼 패키지별로 디렉터리를 구성하는 것이 좋다. 자바의 방식을 따르지 않으면 자바 클래스를 코틀린 클래스로 마이그레이션할 때 문제가 생길 수 있다. 

