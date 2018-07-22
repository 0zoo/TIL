# ListView 사용하기

로그인을 성공했다면  
검색 결과를 list로 보여주는 액티비티로 전환해보자.

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

1. List, ArrayList, HashMap 등의 초기화된 데이터 형을 만들어준다.
2. 초기화된 데이터들은 Adapter를 거치고
3. ListView의 position에 뿌려준다.


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
            return convertView!!
        }

    }
```









