# Chapter7. UI 프래그먼트와 프래그먼트 매니저

## UI 유연성의 필요

1. 리스트-디테일의 이상적 인터페이스
![](https://camo.githubusercontent.com/00beb34fcc566c5303c2faf3064e10370fbe9440/687474703a2f2f646576656c6f7065722e616e64726f69642e636f6d2f696d616765732f747261696e696e672f6261736963732f667261676d656e74732d73637265656e2d6d6f636b2e706e67)

2. 리스트로 돌아가지 않고 다음 디테일을 보고 싶은 경우

액티비티는 이런 유연성 제공 X  
액티비티는 사용자에 의해 사용되는 특정 화면과 강하게 결속된다.

## 프래그먼트 개요

하나의 액티비티는 하나의 뷰와 결속된다. (안드로이드 기본 원칙)

프래그먼트는 액티비티의 사용자 인터페이스 관리를 대신할 수 있는 컨트롤러 객체다.

- UI Fragment: 사용자 인터페이스를 관리하는 프래그먼트.  
자신의 뷰를 하나 가지며, 그 뷰는 레이아웃 파일로부터 inflate된다.  

## CriminalIntent 앱 개발 시작하기

CrimeFragment의 인스턴스는 CrimeActivity에 의해 **호스팅(포함되어 실행)**

프래그먼트는 액티비티의 뷰 계층에 포함될 때만 화면에 나타날 수 있기 때문에  
화면에 나타나는 뷰를 자체적으로 가질 수 없다.

MVC
- 모델: Crime
- 컨트롤러: CrimeFragment, CrimeActivity
- 뷰: LinearLayout, EditText, FrameLayout

### 새로운 프로젝트 생성하기

### 프래그먼트와 지원 라이브러리

프래그먼트는 API Level 11에서 추가됨.

더 하위 버전을 사용해도 프래그먼트를 사용할 수 있다.  
-> **Android Support Library**

Support Library는 API 레벨 4까지 동작 가능한 프래그먼트를 포함한다.  
앱이 어떤 버전의 안드로이드에서 실행되든 안전하게 사용 가능.

우리가 사용할 주요 클래스:  
1. android.support.v4.app.Fragment
2. android.support.v4.app.FragmentActivity

### 안드로이드 스튜디오에 라이브러리 추가하기

```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    ...    
}
```
`implementation fileTree(dir: 'libs', include: ['*.jar'])`은  
libs 디렉토리에 있는 모든 jar 파일에 대해 컴파일 의존성을 갖는다는 것을 지정한 것.

그래들에서는 프로젝트에 포함되지 않은 모듈들에 대한 의존성도 지정할 수 있다.

안드로이드 스튜디오는 공통으로 사용하는 라이브러리들을 유지 관리해준다.

> app 모듈의 라이브러리 의존성 내역 보기
>> File > Project Structure... > app > Dependencies

![](https://user-images.githubusercontent.com/38287485/45881514-57ebd080-bde6-11e8-9f44-78295249ed70.png) 


하단의 + 버튼을 클릭해 원하는 라이브러리 의존성을 추가할 수도 있다.

> `implementation 'com.android.support:support-v4:27.1.1'`
>> **`groupId`**:**`artifactId`**:**`version`**

- groupId: 메이븐 리포지터리에 있는 라이브러리들의 고유 식별자.
- artifactId: 패키지에 있는 특정 라이브러리 이름. (-vX는 지원하는 최소 API Level)
- version: 라이브러리의 개정 번호. 지속적으로 변경됨.

### Crime 클래스 생성하기

```kotlin
/*
    id: 읽기 전용
    title: 읽고 쓰기 가능
    Crime 객체가 생성할 때 고유 식별 가능한 아이디 값 할당.
*/
data class Crime(val id: UUID = UUID.randomUUID(), var title: String = "")
```

## UI 프래그먼트의 호스팅

호스팅 조건: 
1. 액티비티 자신의 레이아웃에 프래그먼트 뷰를 넣을 위치를 정의해야 한다.
2. 프래그먼트 인스턴스의 생명주기를 관리해야 한다.

### 프래그먼트 생명주기

### 호스팅의 두 가지 방법

### 컨테이너 뷰 정의하기

## UI 프래그먼트 생성하기

### CrimeFragment의 레이아웃 정의하기

### CrimeFragment 클래스 생성하기

## UI 프래그먼트를 FragmentManager에 추가하기

### 프래그먼트 트랜잭션

### FragmentManager와 프래그먼트 생명주기

## 프래그먼트 사용 시의 애플리케이션 아키텍처

### 액티비티에서 프래그먼트를 사용하는 이유

## 지원 라이브러리의 프래그먼트가 좋은 이유

## 각 안드로이드 버전의 표준 라이브러리 사용