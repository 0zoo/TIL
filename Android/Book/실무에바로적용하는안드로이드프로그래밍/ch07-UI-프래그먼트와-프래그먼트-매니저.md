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

프래그먼트와 액티비티의 생명주기 차이점 :  
프래그먼트 생명주기 메서드들은 안드로이드 운영체제가 아닌 **자신을 호스팅하는 액티비티에서 호출**된다는 것.

프래그먼트는 액티비티의 내부에서 처리되기 때문에  
안드로이드 운영체제는 액티비티가 사용 중인 프래그먼트를 알지 못한다.

![](https://www.safaribooksonline.com/library/view/android-programming-the/9780134706061/ciUIFragments/fragment_lifecycle_typical.png)


### 호스팅의 두 가지 방법

1. 프래그먼트를 액티비티의 **레이아웃**에 정적으로 추가한다.   
    - 레이아웃 프래그먼트: 간단하지만 유연하지 못함. 프래그먼트가 액티비티에 고정되기 때문에 액티비티의 생애 동안 프래그먼트를 교체할 수 없다.

2. 프래그먼트를 액티비티의 **코드**에 동적으로 추가한다.  
    - 런타임 시에 프래그먼트를 제어할 수 있는 유일한 방법.

### 컨테이너 뷰 정의하기

- container view: 액티비티의 뷰 계층에 프래그먼트의 뷰가 위치할 곳 (예 FrameLayout). 컨테이너 뷰는 여러 프래그먼트가 공동으로 사용.

## UI 프래그먼트 생성하기

### CrimeFragment의 레이아웃 정의하기

### CrimeFragment 클래스 생성하기

1. `Fragment(android.app)`: 각 안드로이드 버전의 표준 라이브러리에 있는 프래그먼트 클래스.

2. `Fragment(android.support.v4.app)`: 과거 버전 호환성 지원해줌. 우리가 사용할 프래그먼트 클래스. 

#### 프래그먼트 생명주기 메서드 구현하기

`CrimeFragment`는 모델 및 뷰 객체와 상호 동작하는 컨트롤러.

```kotlin
class CrimeFragment : Fragment() {
    private lateinit var mCrime: Crime
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCrime = Crime()
    }
}
```

1. `Activity.onCreate(...)`은 **protected** 메서드.  
`Fragment.onCreate(...)`은 **public** 메서드.
    - Fragment의 생명주기 메서드들은 프래그먼트를 호스팅하는 액티비티에서도 호출되기 때문에 **`public`** 이어야 한다.

2. 프래그먼트는 자신의 상태를 저장하거나 읽는 번들 객체를 갖는다. (`onSaveInstanceState(Bundle)`) 

3. `onCreateView(...)`에서 프래그먼트의 뷰를 생성 및 구성함. (`onCreate(..)`에서 뷰 인플레이트 X)  
`onCreate(..)`에서는 프래그먼트 인스턴스를 구성함. 
    ```java
    // 프래그먼트의 뷰를 인플레이트한 후
    // 인플레이트된 View를 호스팅 액티비티에 반환하는 메서드
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    // LayoutInflater와 ViewGroup은 레이아웃을 인플레이트하기 위해 필요함.
    // Bundle은 저장된 상태로부터 뷰를 재생성하기 위한 데이터를 가짐.
    ```

```kotlin
override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val v: View = inflater.inflate(R.layout.fragment_crime, container, false)
    return v 
}
```
```java
public View inflate(int resource, ViewGroup root, boolean attachToRoot)
// 1. 레이아웃 리소스 아이디. 프래그먼트의 뷰를 명시적으로 인플레이트.
// 2. 위젯들을 올바르게 구성하기 위해 필요한 뷰의 부모.  
// 3. 인플레이트된 뷰를 뷰의 부모에게 추가할 것인지를 LayoutInflater에 알려줌.  
// false를 전달하는 이유: 호스팅 액티비티의 코드에서 인플레이트된 뷰를 추가할 것이기 때문.
```

#### 위젯들을 프래그먼트에 연결하기

```kotlin
override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    ...
    v.crimeTitle.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            // 이 메서드의 실행 코드는 여기서는 필요 없음.
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 이 메서드의 실행 코드는 여기서는 필요 없음.
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            mCrime.title = s.toString()
        }
    })
    ...
}
```

- `Activity.findViewById()` 대신 프래그먼트에서는 **`View.findViewById()`** 를 호출한다.  
    - `Activity.findViewById()`는 내부적으로 `View.findViewById()`를 호출하는 편의 메서드.  
    프래그먼트 클래스에는 그것과 부합되는 편의 메서드가 없기 때문에 직접 `View.findViewById()`를 호출하는 것.


## UI 프래그먼트를 FragmentManager에 추가하기

프래그먼트 클래스가 허니콤 버전에 추가되면서  
FragmentManager를 호출하는 코드를 포함하도록 Activity 클래스가 변경되었다.

- **FragmentManager**: 
    - 프래그먼트를 관리해줌.  
    - 액티비티 뷰 계층에 프래그먼트 뷰를 추가해줌.

![](https://cdn-images-1.medium.com/max/1600/1*TULxEYpKqkxUNx2W4w-9xQ.png)

- FragmentManager는  
프래그먼트 리스트와 FragmentTransaction의 Back Stack을 처리한다.


1. FragmentManager 인스턴스 얻기
    ```kotlin
    class CrimeActivity : FragmentActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            ...
            val fm: FragmentManager = supportFragmentManager
        }
    }
    ```
    - 주의 : `import android.support.v4.app.FragmentManager`
    - **`supportFragmentManager`** 를 사용하는 이유:  
    지원 라이브러리와 FragmentActivity 클래스를 사용하기 때문.  
    허니콤 이전 장치를 지원하지 않는다면 FragmentActivity 대신 Activity의 서브 클래스로 지정하고 `getFragmentManager()`를 사용하면 됨.

### 프래그먼트 트랜잭션

```java
FragmentManager fm = getSupportFragmentManager();
Fragment fragment = fm.findFragmentById(R.id.fragment_container);

if(fragment == null){
    fragment = new CrimeFragment();
    fm.beginTransaction()
        .add(R.id.fragment_container, fragment)
        .commit();
}
```

- **프래그먼트 트랜잭션**: 
    - 프래그먼트를 사용해서 런타임 시에 화면을 구성 또는 재구성하는 방법.
    - 프래그먼트 리스트에 add, remove, attach, detach, replace하는 데 사용됨.
    - FragmentManager는 프래그먼트 트랜잭션의 back stack을 유지 관리함.

- `FragmentManager.beginTransaction()`은 FragmentTransaction의 인스턴스를 생성하여 반환한다.  
FragmentTransaction 클래스는 **fluent interface**를 사용한다.  
    - fluent interface: 코드를 이해하기 쉽게 해주는 객체지향 기법.  
    일반적으로 메서드의 연쇄 호출 형태로 구현됨.
즉, FragmentTransaction을 구성하는 메서드들이 void 대신 FragmentTransaction 객체를 반환하기 때문에 그 메서드들을 연속적으로 연결해서 호출할 수 있다.  
> 새로운 프래그먼트 트랜잭션 인스턴스를 생성하고 그 인스턴스에 프래그먼트 객체를 추가한 후 커밋한다.

- 컨테이너 뷰 ID의 목적: 
    - 액티비티 뷰의 어디에 프래그먼트 뷰가 나타나야 하는지를 FragmentManager에 알려준다.
    - FragmentManager의 리스트에서 프래그먼트를 고유하게 식별하는 데 사용된다.

프래그먼트 매니저로부터 CrimeFragment를 가져오기 :  
`Fragment fragment = fm.findFragmentById(R.id.fragment_container);`

컨테이너 뷰의 리소스 아이디로 UI 프래그먼트를 식별하는 것이 프래그먼트 매니저가 동작하는 방법이다.  
만일 하나의 액티비티에 여러 개의 프래그먼트를 추가한다면 각 프래그먼트에 대해 별도의 리소스 아이디를 갖는 컨테이너 뷰를 생성하게 될 것.

`R.id.fragment_container`의 컨테이너 뷰 아이디와 연관된 프래그먼트를 프래그먼트 매니저에 요청한다.  
만일 그 프래그먼트가 리스트에 이미 있으면 프래그먼트 매니저가 그것을 반환할 것이다.  
요청한 프래그먼트가 리스트에 이미 있는 이유? 액티비티가 소멸되었다가 다시 생성될 때를 대비해서 리스트에 보존하기 때문.  
즉, 장치가 회전되거나 안드로이드 운영체제의 메모리 회수로 인해 액티비티가 소멸되었다가 다시 생성되면 새로운 프래그먼트 매니저 인스턴스가 그 리스트를 읽어서 리스트에 있는 프래그먼트들을 다시 생성하여 이전 상태로 복원한다.  
지정된 컨테이너 뷰 아이디의 프래그먼트가 리스트에 없다면 프래그먼트는 null이 될 것이다.  
이런 경우, 우리는 새로운 크라임 프래그먼트와 새로운 프래그먼트 트랜잭션(프래그먼트를 리스트에 추가하는)을 생성한다.


### FragmentManager와 프래그먼트 생명주기

## 프래그먼트 사용 시의 애플리케이션 아키텍처

프래그먼트는 주요 컴포넌트를 재사용 가능한 방법으로 캡슐화하기 위한 목적으로 만들어졌다.  
(주요 컴포넌트 - 주로 앱의 전체 화면 처리를 담당함.)  

화면을 구성하는 작은 컴포넌트들은 커스텀 뷰를 만들어 사용하는 것이 좋다.

가급적 한 화면에는 최대 2~3개 정도의 프래그먼트를 사용하는 것이 좋다.



### 액티비티에서 프래그먼트를 사용하는 이유

`YAGNI` Extreme Programming 방법론 : "You Aren't Gonna Need It"  
나중에 필요할 수 있다고 생각하는 코드는 지금 작성하지 말자.

프래그먼트를 나중에 필요할 때 추가하자는 생각은 비추천.  

> 프래그먼트냐 액티비티냐??  
>> `AUF`: "Always Use Fragments"

## 지원 라이브러리의 프래그먼트가 좋은 이유

지원 라이브러리 프래그먼트가 더 우수함. (예_ 프래그먼트 중첩 기능)

## 각 안드로이드 버전의 표준 라이브러리 사용

표준 라이브러리의 프래그먼트를 사용하려면 
1. FragmentActivity 클래스 대신 표준 라이브러리의 `android.app.Activity`의 서브 클래스로 액티비티 클래스를 생성한다.  
API 레벨 11 이상의 버전에서는 액티비티에서 자체적으로 프래그먼트를 지원하기 때문
2. `android.app.Fragment`의 서브 클래스로 프래그먼트 클래스를 생성한다.
3. 프래그먼트 매니저 객체를 얻을 때 `getFragmentManager()`를 사용한다.
