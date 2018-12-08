# Chapter24. Looper, Handler, HandlerThread

## 이미지를 보여주기 위해 리사이클러뷰 준비하기

`scaleType="centerCrop"` : 이미지를 중앙에 위치시킨 후 크기 조정

## 다운로드 관련 고려 사항들

AsyncTask는 백그라운드 스레드를 실행하는 가장 쉬운 방법이지만, 반복적이면서 실행 시간이 긴 작업을 하기에는 적합한 스레드 모델이 아님.

-> 필요한 시점에 다운로드하는 스레드 모델을 구현하자.

## 메인 스레드와 소통하기

**message queue**를 사용하여 동작하는 스레드를 **message loop**라 하며,  
그 스레드는 자신의 큐에서 새로운 메시지를 찾기 위해 반복해서 루프를 실행한다.

- **message loop**
    - 스레드
    - Looper
        - 메시지 큐 관리

Main thread는 message loop이다.

## 백그라운드 스레드 만들기

HandlerThread는 Looper를 준비해준다.

```java
public class ThumbnailDownloader<T> extends HandlerThread{
// 제네릭 인자를 사용하면 특정 타입에만 클래스를 사용하도록 제한하지 않아 더 유연한 설계 가능.

    private static final String TAG = "ThumbnailDownloader";
    public ThumbnailDownloader(){
        super(TAG);
    }
    public void queueThumbnail(T target, String url){
    // T target: 다운로드의 식별자로 사용 
        Log.i(TAG, "Got a URL: "+ url);
    }
}
```
```kotlin
class ThumbnailDownloader<T>(private val TAG: String = "ThumbnailDownloader") : HandlerThread(TAG) {
    fun queueThumbnail(target: T, url: String) {
        Log.i(TAG, "Got a URL: $url")
    }
}
```

```kotlin
class PhotoGalleryFragment : Fragment() {

    private lateinit var mThumbnailDownloader: ThumbnailDownloader<PhotoHolder>
    // ThumbnailDownloader의 제네릭 인자: 
    // 다운로드의 식별자로 사용될 객체 타입
    // 어떤 타입도 지정 가능함.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        FetchItemsTask(this).execute()

        mThumbnailDownloader = ThumbnailDownloader()
        mThumbnailDownloader.start()
        mThumbnailDownloader.looper
        // start() 호출 -> getLooper() 호출
        // 스레드가 준비되었는지 확인하는 방법
        // (onLooperPrepared() 호출 보장 X)
        Log.i(TAG, "Background Thread started.")
    }

    override fun onDestroy() {
        super.onDestroy()
        mThumbnailDownloader.quit()
        // quit()하지 않으면 좀비처럼 계속 살아있음.
        Log.i(TAG, "Background Thread destroyed.")
    }
    ...

    inner class PhotoAdapter(val items: List<GalleryItem>) : RecyclerView.Adapter<PhotoHolder>() {
        ...
        override fun onBindViewHolder(photoHolder: PhotoHolder, position: Int) {
            ...
            mThumbnailDownloader.queueThumbnail(photoHolder, item.url)
        }
    }
    ...
}
```

## 메시지와 메시지 핸들러

### 메시지 구조

**Message**
- **what**: 메시지를 나타내는 사용자 정의 정수 값
- **obj**: 메시지와 함께 전달되는 사용자 지정 객체
- **target**: 메시지를 처리할 Handler

Message를 생성하면 자동으로 Handler에 연결된다.  

### 핸들러 구조

메시지를 사용해서 실제 작업을 하려면 먼저 Handler의 인스턴스가 필요하다.

**Handler**
- Message를 처리
- Message를 생성
- Message를 게시

Looper가 메시지 수신함을 소유하기 때문에 Message는 반드시 Looper로부터 게시되고 사용되어야 한다.
따라서 **Handler는 항상 Looper의 참조를 갖는다.**

![](https://cdn-images-1.medium.com/max/1200/1*-Hdn8U5v3rtZ-93aqnxmgg.png)

- Handler는 하나의 Looper에 연결된다.
- Message는 하나의 목표 Handler에 연결된다.  
- Looper는 MassageQueue를 갖는다.
- 다수의 Message들이 동일한 대상 Handler 참조 가능.


- 다수의 Handler들이 하나의 Looper에 연결 가능.  
(즉, Handler의 Message들이 다른 Handler의 Message들과 나란히 존재 가능.)

### 핸들러 사용하기

메시지는 `Handler.obtainMessage()`를 호출하여 생성하는 것이 좋다. 메시지 필드들을 인자로 전달하면 이 메서드가 호출된 Handler 객체를 대상 핸들러로 설정해준다.  

`Handler.obtainMessage()`는 매번 새로운 Message 객체를 생성하지 않고 재활용한다.

Message 객체를 얻으면 `sendToTarget()`호출해 Looper의 메시지 큐 제일 끝에 메시지를 넣는다.

- Message
    - what: MESSAGE_DOWNLOAD
    - obj: T 타입 객체
    - target: 

Looper느 Message를 큐에서 꺼낸 후 그것을 그 메시지의 목표 핸들러에 전달해 처리하게 한다. (`Handler.handleMessage()`)



```java
private Handler mRequestHandler;
// Handler 참조 보존
// 다운로드 요청 큐로 관리하는 일
// 큐에서 다운로드 요청 메시지 꺼내질 때 처리하는 일 
```

```java
private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();
// ConcurrentMap : 스레드에 안전한 HashMap
// key: 다운로드 식별 객채(PhotoHolder)
// value: URL
```

```java
public void queueThumbnail(T target, String url){
    if( url == null ){
        mRequestMap.remove(target);
    }else{
        mRequestMap.put(target, url);

        mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        // mRequestHandler로부터 Message 객체를 얻고
        // 그 메시지의 대상 필드를 mRequestHandler로 자동 설정.
        // 메시지의 
        // what -> MESSAGE_DOWNLOAD, 
        // obj -> target (PhotoHolder)
    }
}
```

```java
@Override
protected void onLooperPrepared(){
// Looper가 최초로 큐를 확인하기 전에 호출됨.
// -> Handler를 구현하기에 적합

    mRequestHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == MESSAGE_DOWNLOAD){
                T target = (T) msg.obj;
                
                try{
                    
                    final String url = mRequestMap.get(target);

                    if(target == null){
                        return;
                    }

                    // url로 이미지 다운로드하여 바이트 배열로 비트맵 생성
                }catch(IOException ioe){             
                }
            }
        }
    }
}
```


### 핸들러 전달하기

Main Thread: 여러개의 핸들러와 하나의 Looper를 갖는 Message Loop

메인 스레드에서 핸들러를 생성하면 Looper와 연결되고, 그 다음에 다른 스레드로 핸들러를 전달.  
전달된 핸들러는 자신이 생성된 스레드의 Looper와 계속 연결되어 있기 때문에 해당 스레드의 큐에서 메시지가 처리된다. 

Response(다운로드 이미지) - Request(메인 스레드) 간의 소통에 사용될 리스너 인터페이스를 추가해보자.

```java
public interface ThumbnailDownloaderListener<T>{
    void onThumbnailDownloaded(T target, Bitmap thumbnail);
    // 이미지가 완전히 다운로드되어 UI에 추가될 준비가 될 때 호출됨.
}
// 다운로드된 이미지를 다른 클래스에 위임
```

```java
// PhotoGalleryFragment.onCreate()
mThumbnailDownloader = new ThumbnailDownloader<>(new Handler());

mThumbnailDownloader.setThumbnailDownloaderListener(
    new ThumbnailDownloader.ThumbnailDownloaderListener<PhotoHolder>(){
        @Override
        public void onThumbnailDownloaded(PhotoHolder phothoHolder, Bitmap bitmap){
            ..
            phothoHolder.bindDrawable(drawable);
        }
    }
);
```

- 기본적으로 Handler는 **현재 스레드의 Looper**에 자신을 연결한다.

-> 여기서는 `onCreate()`에서 생성되었기 때문에 main 스레드의 Looper에 연결


```java
Runnable myRunnable = new Runnable(){
    public void run(){
    }
};
Message m = mHandler.obtainMessage();
m.callback = myRunnable;
// Message가 자신의 콜백 필드를 설정하면,
// 메시지 큐에서 꺼내어질 때 자신의 목표 핸들러에 전달되지 않는다.
// 대신, 콜백에 저장된 Runnable의 run()이 직접 실행됨.
```


```java
// 이미지 다운로드 완료하고 비트맵 생성 완료한 후
mResponseHandler.post(new Runnable(){
    public void run(){
        if( mRequestMap.get(target) != url ){
        // 비트맵 다운로드를 끝마칠 동안 
        // 그 사이에 리사이클러뷰는 다른 URL을 요청할 수 있기 때문에
        // requestMap 다시 확인.
            return;
        }
        mRequestMap.remove(target);
        mThumbnailDownloaderListener.onThumbnailDownloaded(target, bitmap);
    }
}); 
```

만일 장치 회전시 엉뚱한 이미지가 보일 위험 있음.  
-> 프래그먼트가 `onDestroyView()`를 호출할 때  
ThumbnailDownloader의 큐의 모든 요청 메시지들을 지우는 메서드를 호출하자.  
`mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);`

## AsyncTask vs. Threads

- **AsyncTask**
    - 짧은 시간에 처리. 
    - 많이 반복되지 않는 작업에 적합.
    - AsyncTask 객체를 많이 생성하거나 긴 시간 동안의 작업에는 적합하지 않다.
    - 안드로이드 3.2부터 AsyncTask 인스턴스 별개의 스레드로 생성되지 않음.  
    **Executor**를 사용해 하나의 백그라운드 스레드에서 모든 백그라운드 작업을 실행함.(각 AsyncTask가 번갈아 실행되는 것)  
    -> 오래 실행되는 AsyncTask는 다른 AsyncTask가 실행되는 것을 방해할 수 있다는 뜻.

## 챌린지: 프리로딩과 캐싱

1. **caching layer** 추가
2. 이미지 **preloading**

**LRU (last recently used)**  
-> 캐시의 공간이 부족할 경우 가장 오래 전에 사용했던 항목을 제거함.

안드로이드 지원 라이브러리의 **LurCache**를 사용해 캐시를 생성해보고,  
비트맵 이미지를 다운로드할 때 캐시에 넣는다.  
새로운 이미지를 다운로드할 때 캐시에 이미 있는지 확인한다.

캐시를 생성하면 이미지를 프리로딩할 수 있다.  
**프리로딩**: 이미지들이 실제로 필요하기 전에 미리 캐시에 로딩하는 것.

챌린지: 모든 갤러리 아이템에 대해 이전과 이후 각각 10개의 이미지를 프리로딩 해보자.

## 이미지 다운로딩 관련 문제 해결하기

Picasso 라이브러리를 사용하면 썸네일다운로더리스너의 콜백 작업과 썸네일다운로더의 모든 일을 대신 해준다.

