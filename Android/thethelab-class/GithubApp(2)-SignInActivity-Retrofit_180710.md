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
(`enqueue`를 따로 util로 분리해주면 좋음)

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

## Retrofit 사용하기

Retrofit은 백그라운드 유아이 스레드 처리도 해줌.

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


