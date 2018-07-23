# ListView 사용하기

## 사용준비

SignInActivity에서 로그인에 성공했다면  
검색 결과를 list로 보여주는 SearchActivity 액티비티로 전환해보자.

Activity 만드는 방법
1. res/layout  
activity_search.xml
2. main/java  
SearchActivity.kt  
3. AndroidManifest.xml - SearchActivity 등록
4. Intent를 통해서 전환

```kotlin
// anko 사용 x
val intent = Intent(this, SearchActivity::class.java)
startActivity(intent)
finish()
```
Anko를 사용하면 intent를 간단하게 전환할 수 있다.

```kotlin
// anko 사용
startActivity<SearchActivity>()
```

------

## Adapter

데이터를 리스트로 표현하기 위한 구성 요소 3가지
- ListView
- 어댑터
- 원본 데이터

어댑터는 원본 데이터를 ListView와 연결시켜줌과 동시에, 리스트에 원본 데이터를 어떻게 표시할 지 정의해줍니다.

데이터와 리스트뷰 연결하기
1. 어댑터 정의
2. 어댑터 생성
3. 해당 어댑터를 ListView에 set

```kotlin
class SearchActivity : AppCompatActivity() {

    ...

    // 1. 어댑터 정의
    class SearchListAdapter(val context: Context): BaseAdapter(){
        var items: List<GithubRepo> = emptyList()

        override fun getItem(position: Int): Any {
        }

        override fun getItemId(position: Int): Long {
        }

        override fun getCount(): Int {
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        }
    }

    ...

    lateinit var listAdapter: SearchListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        ...

        // 2. 어댑터 생성
        listAdapter = SearchListAdapter(this)
        
        // 3. 해당 어댑터를 ListView에 set
        searchListView.adapter = listAdapter

        ...
    }

}

```

**주의!** 

데이터에 생긴 변화는 어댑터가 자동으로 감지하지 못하므로 어댑터에게 이 사실을 알려야 합니다.   

`notifyDataSetChanged()` 메소드를 호출하게 되면  
어댑터가 다시 리스트로부터 최신의 데이터를 받아오고,  
업데이트된 내용이 다시 ListView에 표시될 수 있게 해줍니다.

> `it.items` : `List<GithubRepo>` <- `Adapter` -> `ListView`

```kotlin
call.enqueue({ response ->
    if (response.isSuccessful) {
        response.body()?.let {
            listAdapter.items = it.items
            // 데이터 변경 사실 알려줘야 함.
            listAdapter.notifyDataSetChanged()
        }
    } else {
        ...
    }
}, {...}
)
```


http://androidhuman.tistory.com/entry/11-List-%EC%A7%91%EC%A4%91%EA%B3%B5%EB%9E%B5-1-%EA%B8%B0%EB%B3%B8-%EB%8B%A4%EC%A7%80%EA%B8%B0


------

ListView의 문제점
* **View Holder Pattern**을 사용하지 않아도 동작한다.
* Item Layout의 형태를 변경할 수 없다.
* 타입 안정성이 없다.

## ViewHolder Pattern

스크롤되어 화면에서 사라진 view를 메모리에 남겨두고 매번 새로 생성한다면 메모리에 상당한 부담이 된다.

데이터의 개수만큼 view를 생성하는 것이 아니라, 화면에 보이는 만큼만 view를 갖고 있자.  

ViewHolder Pattern : 뷰가 화면에 사라질 때마다 **재사용**하는 패턴.

-> `getView()` 안에 있는 `convertView`를 사용하자.

**convertView** : 재사용 가능한 view. 만약 null이면 생성해서 사용해야 한다.


----

```kotlin
class SearchListAdapter(val context: Context): BaseAdapter(){
    var items: List<GithubRepo> = emptyList()

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.count()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = items[position]
        
        // LayoutInflater: xml로 되어 있는 layout 을 Kotlin의 View로 변환한다.

        return if (convertView == null) {
            // Item View 생성
            
            val view = LayoutInflater.from(context).inflate(R.layout.item_repo, null)
            view.repoNameText.text = item.fullName

            view
        } else {
            // Item View 재사용
            
            convertView.repoNameText.text = item.fullName

            convertView
        }
    }
}
```

----



---

1. SearchListAdapter 클래스를 SearchActivity 클래스의 안에 정의했음.  
Inner Class? Nested Class?
???
이유는??

2. `val view = LayoutInflater.from(context).inflate(R.layout.item_repo, null)`
왜 null 로 ??

----








