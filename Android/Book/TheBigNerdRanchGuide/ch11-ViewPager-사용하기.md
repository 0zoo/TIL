# Chapter11. ViewPager 사용하기

![image](https://user-images.githubusercontent.com/38287485/46788962-53c42a80-cd76-11e8-97a7-a94cf523c68b.png)

- ViewPager: 사용자가 데이터 페이지들을 좌우로 넘길 수 있게 해주는 안드로이드 레이아웃 매니저 클래스.

## CrimePagerActivity 생성하기

CrimePagerActivity는 ViewPager를 생성하고 관리할 것이다.

`android.support.v4.view.ViewPager`

### ViewPager와 PagerAdapter

ViewPager는 PagerAdapter가 필요하다.

**FragmentStatePagerAdapter** :   
- 프래그먼트를 사용하는 PagerAdapter를 구현한 것.   
- 프래그먼트의 상태도 저장하고 복원할 수 있다.  
- `getItem(범죄 리스트 인덱스)`: CrimeFragment 반환.  
(범죄 리스트는 CrimeLab 싱글톤 인스턴스가 갖고 있음.)
- `getCount()`: crime 배열의 개수 반환

```kotlin
class CrimePagerActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_pager)
        
        val crimes  = CrimeLab.getCrimes()

        val fm = supportFragmentManager

        crimeViewPager.adapter = object : FragmentStatePagerAdapter(fm) {
            override fun getItem(position: Int): Fragment 
                    = CrimeFragment.newInstance(crimes[position].id)

            override fun getCount(): Int = crimes.size
        }
    }
}
```

FragmentStatePagerAdapter는 ViewPager의 소통을 관리하는 **중개자**.


### CrimePagerActivity 통합하기

이제 CrimeActivity를 CrimePagerActivity로 교체할 수 있다.

```kotlin
// CrimePagerActivity.kt
class CrimePagerActivity : FragmentActivity() {

    companion object {
        private val EXTRA_CRIME_ID = "${CrimePagerActivity::class.java.`package`.name}.crime_id"

        fun newIntent(packageContext: Context, crimeId: UUID): Intent {
            val intent = Intent(packageContext, CrimePagerActivity::class.java)
            intent.putExtra(EXTRA_CRIME_ID, crimeId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ...
        val crimeId = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID
        ...
    }
}
```

이제 CrimeListFragment에서 CrimeActivity 대신 CrimePagerActivity를 시작하면 된다.

```kotlin
// CrimeListFragment.kt
override fun onClick(v: View) {
    val intent = CrimePagerActivity.newIntent(this@CrimeListFragment.requireContext(), crime.id)
    startActivity(intent)
}
```

운영체제가 액티비티를 시작시킬 수 있도록 매니페스트에 등록해주어야 한다.

(CrimeActivity.kt는 필요 없기 때문에 삭제)

ViewPager는 각 방향의 인접 페이지를 미리 로딩해놓는다.

`setOffscreenPageLimit(int)`로 로딩할 페이지 개수를 변경할 수 있다.

여기서 문제점.

ViewPager는 기본적으로 PagerAdapter의 첫번째 항목을 보여준다.  
따라서 우리가 선택한 범죄 상세 데이터를 보여주려면 currentItem으로 변경해줘야 한다.
```kotlin
// CrimePagerActivity.kt
override fun onCreate(savedInstanceState: Bundle?) {
    ...
    for(index in 0..crimes.size){
        if(crimes[index].id == crimeId){
            crimeViewPager.currentItem = index
            break
        }
    }
}
```

## FragmentStatePagerAdapter vs FragmentPagerAdapter

FragmentPagerAdapter와 FragmentStatePagerAdapter의 차이점:  
프래그먼트가 더 이상 필요 없을 때 없애는 방법만 다르다.

![](https://www.oreilly.com/library/view/android-programming-the/9780134171517/ciViewPager/fragment_state_pager.png)

- **FragmentStatePagerAdapter**: 필요 없어진 프래그먼트는 소멸되고   
FragmentMananger에서 완전히 삭제하기 위해 트랜잭션이 커밋된다.    
프래그먼트가 소멸될 때, `onSaveInstanceState()`로 데이터를 보존한다.  
소멸된 프래그먼트로 다시 돌아오면, 보존되었던 상태를 기반으로 새로운 프래그먼트가 복원된다.  
    - 장점: 메모리가 절약된다. (데이터의 크기가 클 경우에 좋음)

- **FragmentPagerAdapter**: 프래그먼트 소멸 X.  
프래그먼트가 필요가 없어지면 트랜잭션에서 `remove()`대신 `detatch()`를 호출한다.  
=> 프래그먼트의 뷰는 소멸되지만, FragmentMananger에서는 소멸 X
    - 장점: 더 안전하다. (예_탭 인터페이스.)

![](https://apprize.info/google/programming/programming.files/image131.jpg)

## ViewPager가 실제로 동작하는 방법

PagerAdapter 인터페이스를 직접 구현하는 경우에는 ViewPager와의 관계가 일반적인 경우와 달라진다.
  
ViewPager에서 프래그먼트가 아닌 View를 호스팅하고자 할 경우에 PagerAdapter 인터페이스를 직접 구현한다.

ViewPager는 PagerAdapter를 사용한다.  

PagerAdapter는 뷰 홀더와 뷰를 반환하는 `onBindViewHolder(...)`대신  
아래의 세 메서드들을 갖는다.  
```java
// 해당 위치의 뷰를 생성하고(즉시 x) 
// ViewGroup에 추가하라고 PagerAdapter에게 알려준다. 
public Object instantiateItem(ViewGroup container, int position)
// instantiateItem() 내부에서 새로운 프래그먼트를 생성하고 Object 타입으로 반환한다.

// 해당 항목을 소멸시키라고 알려줌
public void destroyItem(ViewGroup container, int position, Object object)

// 뷰가 생성된 후 그것이 어떤 뷰인지를 알기 위해 호출하는 메서드
public abstract boolean isViewFromObject(View view, Object object)
```

## 코드에서 뷰의 레이아웃 처리하기

```java
@Override 
protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    ViewPager viewPager = new ViewPager(this);
    setContentView(viewPager);
    ...
}
```

코드에서 뷰를 생성하는것 보다 레이아웃 파일을 생성하는 것이 더 좋다.

레이아웃 파일의 장점:
- 컨트롤러와 뷰를 구분해준다.
- 안드로이드 리소스 선택 시스템 사용 가능함.


