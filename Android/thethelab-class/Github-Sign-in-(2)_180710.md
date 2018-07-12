## Retrofit + OkHttp + Gson  
HTTP Request를 사용하는 데 반복되는 코드를 없앨 수 있다.

---
build.gradle - app
```
// 뒤에 버전을 같게 맞춰주자
implementation 'com.squareup.okhttp3:okhttp:3.10.0'
implementation 'com.squareup.okhttp3:logging-interceptor:3.10.0'
```

---

```kotlin
private fun getAccessToken(code: String) {
    val call = authApi.getAccessToken(CLIENT_ID, CLIENT_SECRET, code)

    /*
    call.enqueue(object : Callback<Auth> {
        override fun onFailure(call: Call<Auth>, t: Throwable) {
            //...
        }
        override fun onResponse(call: Call<Auth>, response: Response<Auth>) {
            //...
        }
    })
    */

    call.enqueue({
        it.body()?.let {
            // Log.i(TAG, it.toString())

            updateToken(this, it.accessToken)

            val githubApiCall = provideGithubApi(this).searchRepository("hello")
            githubApiCall.enqueue({
                it.body()?.let {
                    Log.i(TAG, "total_count: ${it.totalCount}")
                    Log.i(TAG, it.items.toString())
                }

            }, {

            })


        }
    }, {
        toast(it.message.toString())
    })

}

// anonymous 대신 lambda를 사용하기 위해 확장
fun <T> Call<T>.enqueue(success: (response: Response<T>) -> Unit, failure: (t: Throwable) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) = failure(t)
        override fun onResponse(call: Call<T>, response: Response<T>) = success(response)
    })
```



익명 클래스보다 람다가 더 좋은점  
-> this 사용 가능.  
익명 클래스는 앞에 `00Activity@this` 이런식으로 명시해줘야함

