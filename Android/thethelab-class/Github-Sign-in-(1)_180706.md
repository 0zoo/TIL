OAuth2
1. App -> Github
2. Github -> App (Redirect)  
내가 설정한 Redirect URL => Activity에 대해서 Intent Filter를 등록

-----

Android Framework  

- Main Thread : UI를 업데이트 하는 역활  
=> UI 스레드안에서 네트워크 요청이 수행될 수 없다.  
: NetworkOnMainThreadException  

-----

Intent? : 의도  

- tel://010-1234-5678                 
    - 전화  
- sms://010-1234-5678?message=안녕하세요 
    - 문자  
- https://github.com/login/oauth/authorize?client_id=XXX 
    - 브라우져  


-----

callback url : githubapp://authorize
- scheme="githubapp"
- host="authorize"

이렇게 선언된 경우 githubapp://authorize 이라는 url을 호출하는 경우  
해당 Activity가 실행됨.

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data
        android:host="authorize"
        android:scheme="githubapp" />
</intent-filter>

```


-----

`@field:SerializedName` 어노테이션을 사용하면  
원하는 이름으로 사용 가능함.

```kotlin
data class Auth(
    @field:SerializedName("access_token")
    val accessToken: String,
    @field:SerializedName("token_type")
    val tokenType: String) {}
```

-----

1. CLIENT_ID, CLIENT_SECRET 은 `companion object` 안에 `const`로 정의해 static 상수처럼 사용하자.

2. onCreate  
signInButton 버튼 클릭 -> 인터넷 브라우저를 실행하는 Intent 전달 (https://github.com/login/oauth/authorize?client_id=XXX) 
    ```kotlin
    // https://github.com/login/oauth/authorize?client_id=XXX

    signInButton.setOnClickListener {
        val authUri = Uri.Builder().scheme("https")
                    .authority("github.com")
                    .appendPath("login")
                    .appendPath("oauth")
                    .appendPath("authorize")
                    .appendQueryParameter("client_id", CLIENT_ID)
                    .build()

        // 인터넷 브라우저를 실행하는 Intent
        // => Custom Tabs: android-support-library
        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(this,authUri)
    }
    ```

3. `onNewIntent`를 그냥 사용할 경우,    
결과를 받아와야 하는데 계속 새로운 화면이 뜨는 문제 발생함.

```xml
<!-- AndroidManifest에 launchMode - singleTask 추가 -->
<activity
    android:name=".SignInActivity"
    android:launchMode="singleTask">
```

launchMode가 singleTask 혹은 singleTop인 액티비티에 intent를 보낼 경우  
존재한다면 onNewIntent, 존재하지 않는다면 액티비티 새로 시작.

```kotlin
override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)

    check(intent != null)
    check(intent?.data != null)

    val uri = intent?.data
    val code = uri?.getQueryParameter("code") ?: throw IllegalStateException("no code!!")

    getAccessToken(code)
}
```

4. intent를 통해 받아온 code와 CLIENT_ID, CLIENT_SECRET를 POST한다. 

```json
// HTTP Request - POST  
// https://github.com/login/oauth/access_token 
{  
    "client_id": "<client_id>",  
    "client_secret": "<client_secret>",  
    "code": "<code>"  
}
```

```kotlin
val requestBody = RequestBody
                .create(MediaType.parse("application/json"), json)

val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .post(requestBody).build()
```

5. 받은 response를 비동기로 처리해야 한다.

이유? 결과를 받기 위해 UI thread가 작업을 멈추고 기다리는 일이 발생하면 안되기 때문에.

```kotlin
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
    // 로그 레벨 지정.
}

val httpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
```

```kotlin
val call = httpClient.newCall(request)

// val response = call.execute()
// Log.i("Request", response.body().toString())
// 이와 같이 비동기 처리하지 않으면 익셉션 발생!

// 비동기로 처리해야 한다.
call.enqueue(object : Callback {
    override fun onFailure(call: Call, e: IOException) {
        runOnUiThread {
        //ui thread에 접근하려면 runOnUiThread 사용
            toast("Failed")
        }
    }

    override fun onResponse(call: Call, response: Response) {
        runOnUiThread {
            val statusCode = response.code()
            
            when (statusCode) {
                in 200.until(300) -> {
                    //toast("Status OK")

                    // 방법 1.
                    Log.i(TAG,response.body()!!.string())
                    // null인 경우 의미가 없으면 assertion으로 죽여도 됨.

                    // 방법 2. let으로 처리
                    response.body()?.let {
                        val result = it.string()

                        val auth = gson.fromJson(result, Auth::class.java)
                        Log.i(TAG, auth.toString())
                    }
                }
                in 400.until(500) -> toast("Client Error")
                in 500.until(600) -> toast("Server Error")
            }
        }
    }
})

```


----










