# Chapter23. HTTP & 백그라운드 태스크

## PhotoGallery 생성하기

## 네트워킹 기본

```kotlin
class FlickrFetchr {
    @Throws(IOException::class)
    private fun getUrlBytes(urlSpec: String): ByteArray {
        val url = URL(urlSpec)
        val connection = url.openConnection() as HttpURLConnection
        try {
            val out = ByteArrayOutputStream()
            val cin = connection.inputStream

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("${connection.responseMessage}: with $urlSpec")
            }

            cin.copyTo(out, 1024)
            out.close()
            return out.toByteArray()
        } finally {
            connection.disconnect()
        }
    }
    @Throws(IOException::class)
    fun getUrlString(urlSpec: String): String = String(getUrlBytes(urlSpec))
}
```

HttpURLConnection은 `getInputStream()` 또는 `getOutputStream()`을 호출할 때 까지 실제로 연결 X

연결의 데이터가 없을 때까지 `read()` 반복적으로 호출하고  
InputStream에서 읽은 데이터는 ByteArrayOutputStream의 바이트 배열로 저장됨.

- 코틀린의 `copyTo()`
```kotlin
/**
 * Copies this stream to the given output stream, returning the number of bytes copied
 *
 * **Note** It is the caller's responsibility to close both of these resources.
 */
public fun InputStream.copyTo(out: OutputStream, bufferSize: Int = DEFAULT_BUFFER_SIZE): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        bytes = read(buffer)
    }
    return bytesCopied
}
```

### 네트워크의 퍼미션 요청하기

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

안드로이드 6.0 마시멜로  
처음 필요할 때 퍼미션 요청  
단, dangerous permission은 **런타임 퍼미션** - 매니페스트 등록 + 앱 실행시 사용자의 승인을 받아야 함.

## AsyncTask를 사용해서 백그라운드 스레드로 실행하기

`FlickrFetchr.getUrlString()`을 프래그먼트에서 바로 호출 X  
-> 백그라운드 스레드에서 실행해야 한다.


**AsyncTask**
- 백그라운드 스레드를 생성하고 `doInBackground()` 실행.
- 스레드 자원을 신경쓸 필요가 없어 쉽고 안전하게 스레드 사용 가능함.


```kotlin
class PhotoGalleryFragment : Fragment() {
    ...
    override fun onCreate(savedInstanceState: Bundle?) {
        ...
        FetchItemsTask().execute()
    }

    private class FetchItemsTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val result = FlickrFetchr().getUrlString("https://m.naver.com")
                Log.i(TAG, "Fetched contents of URL $result")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to fetch URL ", e)
            }
            return null
        }
    }
}
```

1. `AsyncTask.execute()`
2. AsyncTask 시작
3. 백그라운드 스레드 시작
4. `doInBackground()`

## Main Thread

main thread에서 네트워킹 작업 X -> **NetworkOnMainThreadException**

1. 일반 스레드
    - 단일 실행 시퀀스
2. 메인 스레드
    - 무한 루프를 돌며 이벤트를 기다리고 이벤트를 처리
    - **UI** 스레드

- 모든 안드로이드 앱은 **메인 스레드**로 시작된다.  

### background thread

Application Not Responding (**ANR**)

10초동안 사용자 인터페이스의 응답이 없으면 안드로이드 시스템에서 ANR 발생시킴.

백그라운드 스레드로 동작하는 AsyncTask를 사용해 네트워크를 액세스해보자.

## 플리커에서 JSON 가져오기

https://www.flickr.com/services/api/

```
REST Endpoint URL: https://api.flickr.com/services/rest/
```

우리가 필요한 기능은 https://www.flickr.com/services/api/flickr.photos.getRecent.html

> 플리커에 업로드된 가장 최근의 공개 사진들 내역을 반환한다.

`flickr.photos.getRecent`
- 인증: 필요 없음
- 인자: 
    - **api_key**(필수): Your API application key
    - extras, per_page, page, ... (선택)  
    url_s: 작은 크기의 사진이 있을 경우 url


```
GET/
https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=XXX&format=json&nojsoncallback=1
```

### JSON 텍스트 파싱하기


json.org.API 의 **JSONObject**와 **JSONArray**를 사용해보자.

```java
JSONObject jsonBody = new JSONObject(jsonString);
```

## AsyncTask로부터 메인 스레드로 돌아오기

```kotlin
class PhotoGalleryFragment : Fragment() {
    ...
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        ...
        setupAdapter()
        ...
    }
    fun setupAdapter() {
        if (isAdded) {
            mPhotoRecyclerView.adapter = PhotoAdapter(mItems)
        }
    }
    ...
}
```

`setupAdapter()`는 모델이 변경될 때마다 호출되어야 한다.

`isAdded()`가 `true`인지 확인하는 이유?  
프래그먼트가 액티비티에 연결되었는지 확인하기 위해  
(`getActivity`가 null이 아닐 것이다.)

**프래그먼트는 어떤 액티비티에도 연결되지 않은 상태로 존재할 수 있다.**

지금까지의 예제에서는 안드로이드 프레임워크의 콜백들에 의해 프래그먼트 메서드가 호출되었다.  
프래그먼트가 콜백 호출을 받았다는 것은 어떤 액티비티에 연결되었다는 것.  
호스팅하는 액티비티가 없다면 콜백 호출 또한 받을 수 없다.


하지만,  
**AsyncTask**를 사용하고 있으므로 백그라운드 스레드의 일부 콜백 메서드가 자동 호출된다.  
-> 호스팅 액티비티가 있다고 단정할 수 없음.  
--> `isAdded()`로 확인!

주의!  
**백그라운드 스레드에서 UI 변경은 허용되지 않음.**  
(안전 x, 바람직 x)

-> `AsyncTask.onPostExecute()`

- `doInBackground()`가 완전히 실행을 끝난 후에 `onPostExecute()`가 실행된다.
- `onPostExecute()`는 메인 스레드에서 실행된다.  
-> UI 변경을 여기서 하는 것을 추천함.


```kotlin
private class FetchItemsTask internal constructor(context: PhotoGalleryFragment) :
    AsyncTask<Void, Void, List<GalleryItem>>() {

    private val reference: WeakReference<PhotoGalleryFragment> = WeakReference(context)

    override fun onPostExecute(result: List<GalleryItem>?) {
        reference.get()?.let { 
            if(it.isRemoving) return
            it.mItems = result ?: return
            it.setupAdapter()
        }
    }
    ...
}
```

[memory leaks 참고](../../memory-leaks.md)

## AsyncTask 클린업하기

정상적으로 스레드를 종료하기 위해 AsyncTask 스레드의 상태를 파악할 필요가 있다.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    retainInstance = true
    FetchItemsTask(this).execute()
}
```
`retainInstance = true`  
: 장치 회전 등 으로 인해 AsyncTask 인스턴스가 새로 생성된다면 JSON 데이터를 다시 가져오는 일이 발생한다.  

더 복잡하게 사용하는 경우??  
-> AsyncTask 스레드를 인스턴스 변수에 지정.  
--> `AsyncTask.cancel(Boolean)` 으로 실행중인 스레드를 중단시킬 수 있다.  
    1. `cancel(false)`  
        - `doInBackground()`에서 `isCanceled()`가 true인지 확인하고, 백그라운드 스레드 정상 종료.

    2. `cancel(true)`  
        - `doInBackground()`에서 실행중인 스레드가 무조건 중단됨.  
        - 이 방법은 사용하지 않는 것이 좋다.


언제 어디서 AsyncTask를 중단시키는 것이 좋을까?

1. 프래그먼트나 액티비티 소멸, 화면에서 보이지 않을 때 작업 취소 
    - `onStop()`- 뷰 X
    - `onDestroy()` - 프래그먼트, 액티비티 소멸

2. 프래그먼트, 액티비티, 뷰 살아있는 것을 원한다면
    - AsyncTask를 중단시키지 말고 작업이 끝날 때까지 놔두면 된다.  
    -> 메모리 누출 문제 발생할 수 있다.  
    -> UI 부적합 상태일 때 문제 생길 수 있음.

3. 사용자가 무엇을 하든 관계 없이 완료해야 하는 중요한 작업이라면?  
    - **Service**를 론칭하자.

## AsyncTask에 관해 추가로 알아보기

```java
AsyncTask<Void, Void, Void>()
```

1. 첫 번째 타입 매개변수  
    - `execute()`의 인자로 전달하는 입력 매개변수 타입 지정.
    - 이 매개변수는 ``
    - `AsyncTask<String, Void, Void>()`
    

프로그레스 바를 사용해서 백그라운드 스레드의 작업 진척도를 UI에 보여주어보자.

`publishProgress()`, `onProgressUpdate()`


UI 변경은 `doInBackground()`와 `publishProgress()`를 사용해서 제어한다.

## AsyncTask의 대안

장치 구성 변경시 생명주기를 관리하고 데이터를 어딘가에 보존해야 할 때  
Fragment의 `retainInstance = true`를 사용하는 것이 좋다.

AsyncTask를 사용중일 때 Back을 누른다거나 
AsyncTask를 실행했던 프래그먼트가 장치의 메모리 부족으로 인해 안드로이드 운영체제에 의해 소멸되는 경우는   
내가 직접 처리해주어야 한다.

이럴 때 **Loader**를 사용하면 어느 정도 처리할 수 있다.  
loader - 데이터 소스(db, ContentProvider, network, process,..)로부터 데이터를 로드하기 위해 설계되었다.

**AsyncTaskLoader**


AsyncTask 대신 로더를 사용하는 이유?  
LoaderManager가 우리 컴포넌트와 연관된 Loader들의 생명주기를 시작, 중단, 유지 관리하는 책임을 갖는다.

따라서 이미 데이터 로딩을 끝낸 로더를 장치 회전 후 우리기 초기화하면  
로더매니저가 소스로부터 데이터를 다시 가져오지 않고 그 데이터를 즉시 전달할 수 있음. (retain과 관계 없음.)  
-> 유보 관련 문제를 신경 쓸 필요 없다.


## 챌린지: Gson

## 챌린지: 페이징

`RecyclerView.OnScrollListener`를 구현해보자.

## 챌린지: 열의 개수를 동적으로 조정하기


