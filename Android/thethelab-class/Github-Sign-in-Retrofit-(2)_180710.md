# Retrofit + OkHttp + Gson  
HTTP Request를 사용하는 데 반복되는 코드를 없앨 수 있다.

---
## 로그 보기

```
// 뒤에 버전을 같게 맞춰주자

implementation 'com.squareup.okhttp3:okhttp:3.10.0'
implementation 'com.squareup.okhttp3:logging-interceptor:3.10.0'
```
1. build.gradle - app에 추가해준다.

2. 로그의 레벨을 지정해 `HttpLoggingInterceptor`를 생성  
`ex) HttpLoggingInterceptor.Level.BODY`

3. `OkHttpClient.Builder()`에 `.addInterceptor()`로 추가

---

## 익명 클래스를 람다로 바꾸기

익명 클래스로 사용하는 방법
```kotlin
call.enqueue(object : Callback<Auth> {
    override fun onFailure(call: Call<Auth>, t: Throwable) {
        //...
    }
    override fun onResponse(call: Call<Auth>, response: Response<Auth>) {
        //...
    }
})
```

1. `Call<T>`의 확장 함수 `enqueue`를 정의하기

```kotlin
fun <T> Call<T>.enqueue(success: (response: Response<T>) -> Unit, failure: (t: Throwable) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) = failure(t)
        override fun onResponse(call: Call<T>, response: Response<T>) = success(response)
    })
}
```
2. 람다로 

```kotlin
call.enqueue({
    // success
    it.body()?.let {

        updateToken(this, it.accessToken)

        val githubApiCall = provideGithubApi(this).searchRepository("hello")

        githubApiCall.enqueue({
            // success
            it.body()?.let {
                Log.i(TAG, "total_count: ${it.totalCount}")
                Log.i(TAG, it.items.toString())
            }
        },{
            // fail
        })
    }
},{
    // fail
})
```


```kotlin
call.enqueue({
    it.body()?.let {
        // toast(it.toString())
        // Log.i(TAG, it.toString())
        updateToken(this, it.accessToken)
        toast("로그인에 성공하였습니다.")

        startActivity<SearchActivity>()

        }
    }, {
    toast(it.message.toString())
})
```





익명 클래스보다 람다가 더 좋은점  
-> this 사용 가능.  
익명 클래스는 앞에 `00Activity@this` 이런식으로 명시해줘야함

