# RecyclerView


## 주요 클래스
- `Adapter` – 기존의 ListView에서 사용하는 Adapter와 같은 개념으로 데이터와 아이템에 대한 View생성
- `ViewHolder` – 재활용 View에 대한 모든 서브 뷰를 보유
- `LayoutManager` – 아이템의 항목을 배치
- `ItemDecoration` – 아이템 항목에서 서브뷰에 대한 처리
- `ItemAnimation` – 아이템 항목이 추가, 제거되거나 정렬될때 애니메이션 처리

### Adapter
리스트뷰는 데이터가 어디서 왔냐에 따라 BaseAdapter를 상속하는 여러 어댑터 클래스를 구분하여 사용함.  

하지만, 리사이클러뷰는 유연한 어댑터 사용이 가능함.    
다음의 3가지 메소드를 구현해야 한다.

1. `onCreateViewHolder(ViewGroup parent, int viewType)`   
: 뷰 홀더를 생성하고 뷰를 붙여줌.  
리스트 뷰의 getView()는 매번 호출되면서 null 처리를 해줘야했다면, onCreateViewHolder는 새롭게 생성될 때만 불림.
 
2. `onBindViewHolder(ListItemViewHolder holder, int position)`  
: 재활용 되는 뷰가 호출하여 실행되는 메소드.  
뷰 홀더를 전달하고 어댑터는 position의 데이터를 결합.

3. `getItemCount()`  
: 데이터의 개수 반환.  



### ListView보다 RecyclerView를 권장하는 이유

- 리사이클러 뷰는 ViewHolder 패턴의 사용을 강제한다.

- 리스트 뷰는 수직 스크롤만 가능.  
리사이클러 뷰는 수평 스크롤도 지원할 뿐만 아니라 더 다양한 형태의 레이아웃을 제공해줌.

`RecyclerView.LayoutManager`

 1. LinearLayoutManager : 수평, 수직 스크롤을 제공하는 리스트

 2. StaggeredGridLayouManager : 뷰마다 크기가 다른 레이아웃

 3. GridLayoutManager : 사진첩 같은 격자형 리스트



## Glide를 코틀린에서 이용하기

참고
https://medium.com/@vlonjatgashi/using-glide-with-kotlin-5e345b557547


--------

## 코틀린에서 RecyclerView 사용해보기

### 1. View Holder 클래스 생성
```kotlin
class RepoViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_repo, parent, false)
)
```

### 2. RecyclerView Adapter

```kotlin
class SearchListAdapter : RecyclerView.Adapter<RepoViewHolder>() {
    var items: List<GithubRepo> = emptyList()

    // 재사용 가능한 View가 없을 경우 호출되는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        return RepoViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    // View의 내용을 변경할 때 사용하는 함수
    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val item = items[position]

        with(holder.itemView) {
            repoNameText.text = item.fullName
            repoOwnerText.text = item.owner.login

            // Glide 사용. 
            GlideApp.with(this)
                    .load(item.owner.avatarUrl)
                    .placeholder(R.drawable.ic_github_logo)
                    .into(ownerAvatarImage)
        }
    }
}
```

### 3. 어댑터 등록

```kotlin
searchListView.adapter = SearchListAdapter()
```

### 4. 사용할 레이아웃매니저 등록

```kotlin
searchListView.layoutManager = LinearLayoutManager(this)
```


### 5. 뷰에 데이터 세팅

```kotlin
listAdapter.items = it.items
listAdapter.notifyDataSetChanged()
```