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
        //...
    }
},{
    // fail
})
```

유틸리티를 만들 때 따로 전역 변수로 빼서 쓰면 결합에 의한 메모리 누수가 발생할 수 있기 때문에
항상 밖에서 인자로 컨텍스트를 받아야 한다.

익명 클래스보다 람다가 더 좋은점  
-> this 사용 가능.  
익명 클래스는 앞에 `00Activity@this` 이런식으로 명시해줘야함



--------

# Retrofit 사용하기

http://devflow.github.io/retrofit-kr/


```kotlin
// https://github.com
interface AuthApi {

    @FormUrlEncoded
    @POST("login/oauth/access_token")
    @Headers("Accept: application/json")
    fun getAccessToken(@Field("client_id") clientId: String,
                       @Field("client_secret") clientSecret: String,
                       @Field("code") code: String): Call<Auth>

}
```

1. 메소드 정의 방식 2가지

    - Form-encoded  
    `@FormUrlEncoded` 어노테이션을 메소드에 명시하면 form-encoded 데이터로 전송됨.    
    key는 어노테이션 값에, value는 객체를 지시하는 @Field 어노테이션으로 매개변수에 명시하시면 됩니다.

    - Multipart

    ```java
    @Multipart
    @PUT("/user/photo")
    Call<User> updateUser(@Part("photo") RequestBody photo, @Part("description") RequestBody description);
    ```
    Part는 Retrofit의 컨버터나, RequestBody를 통하여 serialization 가능한 객체 사용 가능함.

2. 요청 메소드

    - 기본 제공 요청 메소드 어노테이션:  
    GET, POST, PUT, DELETE, HEAD
    - 정적 쿼리 인자를 URL에 명시 가능  
    `@GET("/users/list?sort=desc")`

3. 헤더 다루기

정적 헤더들은 `@Headers` 어노테이션을 통해 명시.  
참고! 헤더들은 이름에 기준하여 각각의 값을 덮어씌우지 않음.

헤더를 모든 요청마다 추가해야 한다면 **OkHttp interceptor** 를 사용하자.

----

## 비동기 처리

`enqueue()`는 비동기로 Request를 보내고 Response가 돌아 왔을 때 콜백으로 알려줌.  
Retrofit은 Main UI 스레드가 차단되거나 간섭받지 않도록 Background 스레드에서 Request 처리해줌.

`enqueue()`를 사용하려면 2개의 콜백 메소드를 구현해야 함.  
- `onResponse()`
- `onFailure()`

동기 Reuqest를 수행하려면 `execute()` 메소드를 사용하자.  
(Background 스레드에서 실행할 것!) 

-----

## OAuth 2.0의 access token 얻기

`call : Call<Auth>`

call은 사용자가 동기로 할 지 비동기로 할 지 결정할 수 있도록 해주는 개념을 제공.

```kotlin
val call = authApi.getAccessToken(CLIENT_ID, CLIENT_SECRET, code)

call.enqueue({
    it.body()?.let {
        // it.accessToken
        // (accessToken을 json으로) 
    }
}, {
})
```

### 1. Retrofit 사용 준비

```kotlin
val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val httpClient: OkHttpClient = OkHttpClient.Builder().apply {
    addInterceptor(loggingInterceptor)
}.build()


val retrofit: Retrofit = Retrofit.Builder().apply {
    baseUrl("https://github.com/")
    client(httpClient)
    addConverterFactory(GsonConverterFactory.create())
}.build()

val authApi: AuthApi = retrofit.create(AuthApi::class.java)

// 정의한 interface class를 Retrofit에 초기화
// 이제 HTTP 통신을 할 준비 완료!

````

> **tip**  
> `Builder()`가 보이면 수신 객체 지정 람다인 `apply`를 사용하자.  
>> `apply` :  
객체 자신을 return한다.  
인스턴스를 만들면서 특정 프로퍼티를 초기화하는 경우에 사용.
builder pattern을 편리하게 구현 가능.

#### GsonConverterFactory 추가하기  
Retrofit은 addConverterFactory를 추가해 응답을 json으로 편리하게 받을 수 있다.
( gradle에 추가해야 사용 가능함.
`implementation 'com.squareup.retrofit2:converter-gson:2.4.0'`
)

이렇게 하면 다음과 같은 interface 정의를 사용할 수 있다.

```kotlin
// baseUrl -> https://github.com

interface AuthApi{
    @FormUrlEncoded
    @POST("login/oauth/access_token")
    @Headers("Accept: application/json")
    fun getAccessToken(@Field("client_id") clientId: String,
                       @Field("client_secret") clientSecret: String,
                       @Field("code") code: String): Call<Auth>
}
```

이제 레트로핏을 적용하기 전에 만들었던 post 메소드가 필요 없게 된다.



### 2. accessToken 얻기

`getAccessToken()`으로 얻은 `Call<Auth>` 객체에 `enqueue()`를 실행하여 accessToken과 tokenType을 얻을 수 있다.


### 3. 얻은 accessToken을 안드로이드의 SharedPreferences에 저장

```kotlin
call.enqueue({
    it.body()?.let {
        updateToken(this, it.accessToken)
        //val githubApiCall = provideGithubApi(this).searchRepository("hello")
        //githubApiCall.enqueue({},{})
        }
    },{
})
```
왜 App의 설정에 Access Token을 저장할까? -> 매번 로그인 하지 않고 재사용할 수 있도록 하려고 


```kotlin
const val KEY_AUTH_TOKEN = "패키지이름.auth_token"
// 다른 앱과 중복되지 않도록 
// 패키지 이름 같은 고유한 이름을 사용하는 것을 추천

fun updateToken(context: Context, token: String) {
    PreferenceManager
        .getDefaultSharedPreferences(context)
        .edit()
        .putString(KEY_AUTH_TOKEN, token)
        .apply()
}
```

### 4. SharedPreferences에서 accessToken을 가져와 header에 추가한다.

```kotlin
call.enqueue({
    it.body()?.let {
        updateToken(this, it.accessToken)

        val githubApiCall = provideGithubApi(this).searchRepository("hello")

        //githubApiCall.enqueue({},{})
        }
    },{
})
```
`this`(context)를 넘겨주는 이유?  
->
여기서 context를 따로 전역 변수로 빼서 쓰면  
결합에 의한 메모리 누수가 발생할 수 있기 때문에  
항상 밖에서 인자로 컨텍스트를 받아야 한다.

```kotlin
fun provideGithubApi(context: Context) = Retrofit.Builder().apply {
    baseUrl("https://api.github.com/")
    client(authHttpClient(context))
    addConverterFactory(GsonConverterFactory.create())
}.build().create(GithubApi::class.java)!!
```

```kotlin
fun authHttpClient(context: Context) = OkHttpClient.Builder().apply {
    addInterceptor(loggingInterceptor)
    addInterceptor(AuthInterceptor(context))
}.build()!!
```
Interceptor == Middleware

```kotlin
class AuthInterceptor(private val context: Context) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val request = original.newBuilder().apply {

            getToken(context)?.let { token ->
                addHeader("Authorization", "bearer $token")
            }
            
            // getToken() 메소드는 
            // return PreferenceManager.getDefaultSharedPreferences(context)
            // .getString(KEY_AUTH_TOKEN, null) 한다.
            
            // getString(KEY_AUTH_TOKEN, null) 여기서
            // 두번째 인자는 만약에 값이 없을 경우 반환해줄 값을 지정하는 것

        }.build()

        return chain.proceed(request)
    }
}
```


#### okhttp의 Interceptor

네트워크 통신을 하면서 무언가를 공통적으로 실어 보내거나 받아서 써야 할 경우에 사용함.

- Application Interceptor
- Network Interceptor

![263275425694f7bf12](https://user-images.githubusercontent.com/38287485/42747587-083713a4-8918-11e8-826e-f7b71fb40564.png)

okhttp를 앱이 네트워크 통신을 하기 위해 지나가는 다리라고 본다면  
이 다리를 건너가고 건너오는 2번의 intercept(가로채기)를 하는 것.


1. Network Interceptor  
앱의 콘텐츠와 직접적 관계가 없는 로직.    
`loggingInterceptor`


2. Application Interceptor  
response 또는 request 받은 콘텐츠와 관련된 로직.
헤더 추가


- header 추가하기 
    1. `chain.request()` 를 `newBuilder()`하고
    2. request에 `addHeader()`하고 `.build()`를 한다.
    3. 최종적으로 `chain.proceed(request)`를 반환해준다.


http://developer88.tistory.com/67



### 5. Authorization을 헤더에 추가해 hello가 포함된 리포지터리 리스트를 검색한다.

```kotlin
val githubApiCall = provideGithubApi(this).searchRepository("hello")

// provideGithubApi()는
// GithubApi::class.java 를 생성한다.

githubApiCall.enqueue({},{})
```

```kotlin
interface GithubApi {
    @GET("search/repositories")
    fun searchRepository(@Query("q") query: String): Call<RepoSearchResponse>
}
```