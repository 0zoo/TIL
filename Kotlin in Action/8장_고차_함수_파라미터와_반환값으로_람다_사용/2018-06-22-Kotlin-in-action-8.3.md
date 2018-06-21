# 8.3 고차 함수 안에서 흐름 제어

## 8.3.1 람다 안의 return문: 람다를 둘러싼 함수로부터 반환

컬렉션에 대한 이터레이션 두 가지를 살펴보자.

```kotlin
// 일반 루프 안에서 return 사용하기
fun lookingForA(people: List<Person>){
    for(person in people){
        if(person.name == "A"){
            println("found!")
            return
        }
    }
    println("A is not found")
}

```

```kotlin
// forEach에 전달된 람다에서 return 사용하기
fun lookingForA(people: List<Person>){
    people.forEach{
        if(it.name == "A"){
            println("found!")
            return            
        }
    }
    println("A is not found")
}
```

람다 안에서 return 을 사용하면 그 람다를 호출하는 함수가 실행을 끝내고 반환된다.

- 자신을 둘러싸고 있는 블록보다 더 바깥에 있는 다른 블록을 반환하게 만드는 return 문을 **넌로컬(non-local) return** 이라 부른다.

- return이 바깥쪽 함수를 반환시킬 수 있는 경우는  
**람다를 인자로 받는 함수가 인라인 함수인 경우**만 가능함.

## 8.3.2 람다로부터 반환: 레이블을 사용한 return

람다 안에서 local return은 for루프의 break와 비슷한 역할을 한다.
람다의 실행을 끝내고 람다를 호출했던 코드의 실행을 계속 이어간다.

local return 과 non-local return 을 구분하기 위해
**레이블(label)** 사용.

실행을 끝내고 싶은 람다 식 앞에 레이블을 붙이고, return 키워드 뒤에 그 레이블을 추가.

```kotlin
// 레이블을 통해 로컬 리턴 사용하기
fun lookForA(people: List<Person>){
    people.forEach label@{//람다 식 앞에 레이블 붙임
        if(it.name == "A")
            return@label
            // 앞에서 정의한 레이블을 참조
    }
    println("항상 이 줄이 출력된다.")
}
```

 > people.forEach `label@`{  
     if(it.name=="A") return`@label`  
 }  
 >> `label@` : 람다 레이블  
 >> `@label` : return식 레이블


람다를 인자로 받는 인라인 함수의 이름을 return 뒤에 레이블로 사용 가능.

```kotlin
//함수 이름을 return 레이블로 사용하기
fun lookForA(people: List<Person>){
    people.forEach {
        if(it.name == "A")
            return@forEach //람다 식으로부터 반환
    }
    println("항상 이 줄이 출력된다.")
}
```

람다 식의 레이블을 명시하면 함수 이름을 레이블로 사용할 수 없다는 점에 유의!  
람다 식에는 레이블이 2개 이상 붙을 수 없다.

---
### 레이블이 붙은 this 식

수신 객체 지정 람다의 본문에서는  
this 참조를 사용해 묵시적인 컨텍스트 객체  
(람다를 만들 때 지정한 수신 객체)를 가리킬 수 있다.

```kotlin
println(StringBuilder().apply sb@{
// this@sb를 통해 이 람다의 묵시적 수신 객체에 접근 가능 

    listOf(1,2,3).apply{
        this@append(this.toString())
        // 모든 묵시적 수신 객체를 사용할 수 있다.
        // 다만, 바깥쪽 묵시적 수신 객체에 접근할 때는
        // 레이블을 명시해야 한다.
    }
})
// [1,2,3]
```

---


## 8.3.3 무명 함수: 기본적으로 로컬 return

```kotlin
// 무명 함수 안에서 return 사용하기
fun lookForA(people: List<Person>){
    people.forEach(fun (person) {
    // 람다 식 대신 무명 함수를 사용한다.

        if(person.name == "A") return
        // return은 가장 가까운 함수를 가리키는데
        // 이 위치에서 가장 가까운 함수는 무명 함수

        println("${person.name} is not A")
    })
}

lookForA(people)
// B is not A
```

무명 함수와 일반 함수의 차이는  
함수 이름이나 파라미터 타입을 생략할 수 있다는 점 뿐.

```kotlin
// filter에 무명 함수 넘기기
people.filter(fun (person): Boolean{
    return person.age<30
})
```
식을 본문으로 하는 무명 함수의 반환 타입은 생략 가능하다.

```kotlin
// 식이 본문인 무명 함수 사용하기
people.filter(fun (person) = person.age<30 )
```

> return 식은 fun 키워드로 정의된 함수를 반환시킨다.

람다식은 fun을 사용해 정의되지 않으므로 람다 본문의 return은 람다 밖의 함수를 반환시킨다.

무명 함수 안에서 레이블이 붙지 않은 return 식은 무명 함수 자체만 반환시킨다.  

무명함수는 일반 함수와 비슷해 보이지만 실제로는 람다 식에 대한 문법적 편의일 뿐이다.
