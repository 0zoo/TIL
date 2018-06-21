# 8.3 고차 함수 안에서 흐름 제어

## 8.3.1 람다 안의 return문: 람다를 둘러싼 함수로부터 반환

컬렉션에 대한 이터레이션 두 가지를 살펴보자.

```kotlin
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

## 8.3.2 람다로부터 반환: 레이블을 사용한 return


## 8.3.3 무명 함수: 기본적으로 로컬 return

