# Chapter9. RecyclerView로 리스트 보여주기

## CriminalIntent의 모델 계층 변경하기

### 싱글톤과 집중 데이터 스토리지

- **singleton** : 딱 하나의 인스턴스만 생성할 수 있는 클래스.  

싱글톤은 애플리케이션이 메모리에 있는 한 계속 존재한다.  
-> 리스트를 싱글톤에 저장하면 액티비티와 프래그먼트의 생명주기가 변경되는 동안에도 항상 범죄 데이터를 사용할 수 있다.  

- 싱글톤 사용시 **주의점** : 메모리에서 제거되면 소멸될 수 있음.  
-> `CrimeLab 싱글톤`은 장기간의 데이터 보존을 위한 솔루션은 아니다.  
--> 그러나 `CrimeLab`만이 범죄 데이터를 소유함으로써  
컨트롤러와의 데이터 전달을 쉽게 할 수 있다는 장점이 있음.



1. `private 생성자`와 `get()` 메서드를 각각 하나씩 갖는다.
2. 자신의 인스턴스가 이미 있다면 `get()`에서 기존 인스턴스를 반환한다.
3. 인스턴스가 없다면 `get()`에서 생성자를 호출하여 인스턴스를 생성한 후 반환한다.


```java
public class CrimeLab {
    // 안드로이드의 작명 규칙 - static 변수의 접두사로 s를 사용한다.
    private static CrimeLab sCrimeLab;

    private List<Crime> mCrimes;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    // 생성자가 private 이기 떄문에 
    // 외부에서 인스턴스를 생성하려면 반드시 get()을 호출하야 함.
    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("범죄 #" + i);
            crime.setSolved(i % 2 == 0); // 짝수 번째 요소에는 true 임의 설정.
            mCrimes.add(crime);
        }
    }

    public List<Crime> getmCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }
        return null;
    }
}
```


## 프래그먼트의 호스팅을 위한 추상 액티비티

### 보편화된 프래그먼트 호스팅 레이아웃

### 추상 액티비티 클래스

CrimeActivity의 코드는 보편적이다.

재사용이 가능한 코드는 추상 클래스로 뽑아내자.

```java
// SingleFragmentActivity.java
public abstract class SingleFragmentActivity extends FragmentActivity {
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        
        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
}
```
달라진 부분:  
```java
protected abstract Fragment createFragment();
``` 
```java
fragment = createFragment();
```
----

1. activity_fragment.xml 로부터 인플레이트되는 액티비티의 뷰를 설정한다.
2. 그 컨테이너의 FragmentManager에서 프래그먼트를 찾는다.
3. 만일 프래그먼트가 없으면 하나를 생성하고 FragmentManager의 리스트에 추가한다.


```java
protected abstract Fragment createFragment();
// 이 추상 메서드는 프래그먼트 인스턴스 생성에 사용된다.
// SingleFragmentActivity의 서브 클래스애서는 
// 이 메서드를 구현하여 
// 액티비티가 호스팅하는 프래그먼트 인스턴스를 반환해야 한다.
```

#### 추상 클래스 사용하기

```kotlin
// CrimeActivity.kt
class CrimeActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment = CrimeFragment()
}
```

#### 새로운 컨트롤러 클래스 생성하기

```
class CrimeListActivity: SingleFragmentActivity(){
    override fun createFragment(): Fragment = CrimeListFragment()
}
```

```
class CrimeListFragment: Fragment()
```

#### CrimeActivity 선언하기

CrimeListActivity가 실행될 수 있도록 매니페스트에 선언해야 한다.    
또한 사용자가 보는 첫 화면으로 범죄 리스트를 보여주기 위해  
CrimeListActivity를 론처 액티비티로 선언하자.

```xml
<activity android:name=".CrimeListActivity">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />

        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

## RecyclerView, Adapter, ViewHolder

RecyclerView는 ViewGroup의 서브 클래스로, 자식 뷰 객체들의 리스트를 보여준다.  


리스트에 있는 모든 항목에 대해 텍스트뷰를 하나씩 만들면 많은 부담이 될 것.

- 필요할 때만 View 객체를 생성하는 것이 바람직하다.

- RecyclerView는 필요한 뷰 객체만 생성하고, 끊임없이 뷰를 재활용한다.


### ViewHolder와 Adapter

RecyclerView는 뷰를 재활용하고 화면에 보여주는 책임만 갖는다.


1. ViewHolder  
: 하나의 View를 보존하는 일.  
    ![image116](https://user-images.githubusercontent.com/38287485/46277366-ece88980-c59d-11e8-989d-c955bca3d47b.jpg)

```java
// 일반적인 ViewHolder의 서브 클래스
public class ListRow extends RecyclerView.ViewHolder {
    public ImageView mThumbnail;
    
    public ListRow(View itemView) {
        super(itemView);
        mThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
    }
}
```

```java
// 일반적인 ViewHolder의 사용 예
ListRow row = new ListRow(inflater.inflate(R.layout.list_row, parent, false));
// ListRow 인스턴스를 생성
View view = row.itemView;
// itemView는 슈퍼 클래스인 RecyclerView.ViewHolder가 지정해준 필드
// super(itemView)의 인자로 전달한 View 객체 참조를 보존한다.
// (itemView는 리스트 항목의 범죄 제목을 갖는 TextView의 참조)
ImageView thumbnailView = row.mThumbnail;
```

RecyclerView는 자신이 뷰 객체를 생성하지 않고 Adapter를 통해서 ViewHolder 객체를 생성하고 사용한다.  
ViewHolder는 자신의 itemView로 뷰 객체를 가져온다.

![image117](https://user-images.githubusercontent.com/38287485/46277410-05f13a80-c59e-11e8-8385-5127eb6d4d45.jpg)

#### 어댑터

- RecyclerView는 자신이 ViewHolder를 생성하지 않는다.  
-> 대신 그 일을 **adapter**에게 요청한다.

- **어댑터(adapter)**: 컨트롤러 객체. RecyclerView와 RecyclerView가 보여줄 데이터 사이에 위치한다.
    - adapter의 책임:
        1. 필요한 ViewHolder 객체를 생성한다.
        2. 모델 계층의 데이터를 ViewHolder와 결합한다.


어댑터를 생성하려면 RecyclerView.Adapter의 서브 클래스를 정의해야 한다.  
이 서브 클래스는 CrimeLab에서 Crime 리스트를 가져온다.

![image118](https://user-images.githubusercontent.com/38287485/46277425-130e2980-c59e-11e8-96df-57ff7c75988a.jpg)

1. `RecyclerView`: `getItemCount()`- 리스트에 보여줄 객체 개수 요청.
2. `RecyclerView`: `onCreateViewHolder(ViewGroup, int)`-  ViewHolder 객체를 받음.
3. `RecyclerView`: `onBindViewHolder(ViewHolder, int)`- 리스트 항목의 위치와 함께 ViewHolder 객체를 인자로 전달. 
4. `Adapter`: 그 위치의 모델 데이터를 찾은 후 그것을 ViewHolder의 View에 결합한다.
5. `RecyclerView`: 하나의 리스트 항목을 화면에 위치시킴.

- **주의!**  
`onCreateViewHolder()`가 `onBindViewHolder()`보다 적게 호출될 수 있다.  
이유: 충분한 개수의 ViewHolder가 생성되면 `onCreateViewHolder()`의 호출을 중단하기 때문.   
-> 기존에 생성된 ViewHolder를 재사용하여 시간과 메모리를 절감한다.

### RecyclerView 사용하기

```
implementation 'com.android.support:recyclerview-v7:27.1.1'
```

```xml
<!-- fragment_crime_list.xml -->
<?xml version="1.0" encoding="utf-8"?>
<android.support.v7.widget.RecyclerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/crimeRecyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

</android.support.v7.widget.RecyclerView>
```

```kotlin
// CrimeListFragment.kt
class CrimeListFragment : Fragment() {
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
       
        view.crimeRecyclerView.layoutManager = LinearLayoutManager(activity)
        // RecyclerView가 생성된 후에는 LayoutManager를 설정해주어야 한다.
        // 설정하지 않으면 동작 x
        
        return view
    }
}
```

- RecyclerView는 텍스트뷰를 재활용하고 화면에 위치시키는 책임이 있다.
- RecyclerView는 텍스트뷰들을 화면에 위치시키는 일을 LayoutManager에게 위임한다.  
- LayoutManager는 텍스트뷰들의 화면 위치를 처리하고 스크롤 동작도 정의한다.


LayoutManager는 여러 종류가 있다.( LinearLayoutManager, GridLayoutManager, ... )  
여기서 선택한 LinearLayoutManager는 리스트의 항목을 수직 방향으로 위치시킨다.  

### 어댑터와 ViewHolder 구현하기

```kotlin
class CrimeListFragment : Fragment() {
    ...

    private class CrimeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            val mTitleTextView: TextView = itemView as TextView
        }
    }
}
```

```java
class CrimeListFragment extends Fragment {
    ...

    private class CrimeHolder extends RecyclerView.ViewHolder{
        pulbic TextView mTitleTextView;

        public CrimeHolder(View itemView){
            super(itemView);
            mTitleTextView = (TextView) itemView;
        }
    }
}
```

## 리스트 항목의 커스터마이징

### 리스트 항목의 레이아웃 생성하기

### 커스텀 항목 뷰 사용하기

## 리스트 항목 선택에 응답하기

## ListView와 GridView

## 싱글톤

