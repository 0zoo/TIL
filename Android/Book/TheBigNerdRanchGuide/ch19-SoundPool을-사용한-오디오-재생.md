# Chapter19. SoundPool을 사용한 오디오 재생

**SoundPool**클래스: 안드로이드 오디오 API를 쉽게 사용할 수 있도록 해주는 도구
- 리소스나 파일로부터 음원을 메모리로 로드하고 재생해줌.
- 최대 동시 음원 재생 횟수 제어 가능.
- MediaPlayer 서비스를 사용해 16비트 PCM 모노나 스테레오 스트림으로 음원을 디코딩해줌.  
-> 압축 음원을 앱에 포함시켜 배포 가능. 

## SoundPool 생성하기

롤리팝부터 `SoundPool.Builder` 사용 가능.  

(우리 앱은 minSDK가 16. 호환성 유지하기 위해 일반 생성자 사용.)

```java
mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
// 인자 1) 해당 시점에 재생할 음원의 최대 개수 
//        지정 개수 초과시 가장 오래된 음원부터 재생 중지
// 인자 2) 오디오 스트림의 종류
//        안드로이드는 각각 독립적인 볼륨 설정을 갖고 있음
//        .STREAM_MUSIC: 장치의 음악과 게임이 동일한 볼륨 설정을 가짐
// 인자 3) 샘플 레이트 컨버터의 퀄리티
//        아무런 영향을 주지 않아 0으로 지정
```

## 음원 로드하기

SoundPool의 장점은 응답이 빠르다는 것.

로드할 각 음원은 정수 ID를 갖는다.

```kotlin
data class Sound(val assetPath: String) {
    
    var soundId: Int? = null
    ...
}
```

```kotlin
class BeatBox(context: Context) {
    ...
    private val mSoundPool by lazy { 
        SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0)
    }
    
    @Throws(IOException::class)
    private fun load(sound: Sound){
        val afd = mAssets.openFd(sound.assetPath)
        sound.soundId = mSoundPool.load(afd, 1)
    }

    private fun loadSounds() {

        val soundNames: Array<String> = try {
            mAssets.list(SOUNDS_FOLDER)
        } catch (ioe: IOException) {
            Log.e(TAG, "Could not list assets", ioe)
            return
        }
        
        Log.i(TAG, "Found ${soundNames.size} sounds")

        for (filename in soundNames) {

            try {
                val assetPath = "$SOUNDS_FOLDER/$filename"
                val sound = Sound(assetPath)
                load(sound)
                mSounds.add(sound)

            } catch (ioe: IOException) {
                Log.e(TAG, "Could not load sound $filename", ioe)
            }
        }
    }
    ...
}
```

## 음원 재생하기

```kotlin
    fun play(sound: Sound) {
        val soundId = sound.soundId
        soundId?.let {
            mSoundPool.play(it, 1.0f, 1.0f, 1, 0, 1.0f)
            // 인자: 음원ID, 왼쪽 볼륨, 오른쪽 볼륨, 스트림 우선순위, 반복 재생 여부, 재생률
            // 스트림 우선순위 - 0이면 최저 우선순위
            // 반복 재생 여부 - 0이면 반복 안함. -1이면 무한 반복. 그 외의 숫자는 반복 횟수
        }
    }
```

```kotlin
    inner class SoundHolder(inflater: LayoutInflater, container: ViewGroup?)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_sound, container, false)), 
            View.OnClickListener {
        ...
        init {
            mButton.setOnClickListener(this)
        }
        
        override fun onClick(v: View?) {
            mBeatBox.play(mSound)
        }
    }
```

release() 하기 전에는 음원 재생 안됬음..ㅜ

## 음원 클린업하기

```kotlin
// SoundPool 클린업(리소스 해지)
SoundPool.release()
```


## 장치의 방향 회전과 객체의 지속성

장치가 회전하면 BeatBoxActivity는 소멸되기 때문에 재생중이던 음원이 중단되는 문제가 있음.

장치 회전 -> BeatBoxActivity 소멸 -> BeatBoxFragment 소멸 
-> `BeatBoxFragment.onPause()` -> `BeatBoxFragment.onStop()` 
-> `BeatBoxFragment.onDestroy()`-> `BeatBoxFragment.release()`

그동안은 `onSavedInstanceState()`를 사용해서 이런 문제들을 해결했지만, 이제는 적용 X.  
이유: Bundle 객체 내부의 **Parcelable** 데이터를 사용해서 데이터를 보존 및 복원하기 때문.

- **Parcelable**: 객체를 바이트 스트림으로 보존하는 API.  
보존되는 객체는 Parcelable 인터페이스를 구현해야 함.

- 객체가 보존 가능(**stashable**) 조건 
    1. Bundle
    2. Serializable
    3. Parcelable

- 보존 가능: Sound에 포함된 모든 것.
- 보존 불가능: SoundPool. (예_TV 시청 세션) 
    - 이전과 같은 상태를 만들 수는 있지만 중단은 불가피하다.
    - 보존 불가능은 **전염성**이 강하다. (종속 관계)  
    SoundPool 보존 불가능 -> BeatBox 보존 불가능 


### 프래그먼트 유보하기

```kotlin
class BeatBoxFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true // 프래그먼트 유보
    }
    ...
}
```

**retainInstance**의 디폴트값은 false.  
- false: 장치 회전시 retain되지 않고 소멸되었다가 다시 생성됨.
- true: 소멸되지 않고 보존되었다가 새로운 액티비티 인스턴스에 전달됨.  
    (모든 인스턴스 변수값 유지됨)


### 장치 방향 회전과 유보 프래그먼트

유보 프래그먼트의 **장점**: **자신은 소멸되지 않으면서** 자신의 **뷰는 소멸되고 재생성** 될 수 있다.

1. 구성이 변경됨. 
2. FragmentManager는 리스트의 프래그먼트 뷰들을 소멸.
3. FragmentManager는 retainInstance 값 확인.
    - false: 프래그먼트 인스턴스 소멸   
        4. 프래그먼트, 뷰 재생성.
    - true: 프래그먼트 뷰 소멸. 인스턴스 소멸 X.  
        4. 뷰만 재생성.


![](https://1.bp.blogspot.com/-x_x91Y2hyu4/UYasqUK3epI/AAAAAAAAH5U/mmQf6ZNt9EI/s1600/fragment_lifecycle_04.png)

유보 프래그먼트는 소멸되는 액티비티로부터 **분리(detached)** 된다.  
프래그먼트는 여전히 존재하지만 어떤 액티비티에서도 호스팅하지 않는다.



유보 상태 조건
1. `setRetainInstance(true)` 호출될 때
2. 구성 변경으로 호스팅 액티비티가 소멸될 때


## 유보를 할 것인가 말 것인가

꼭 필요한 경우에만 사용하는 것을 권장한다.

왜?
1. 유보 프래그먼트는 더 **복잡**하다.
2. 유보 프래그먼트는 **구성 변경**에 따른 액티비티 소멸 상황 **만** 처리한다.   
-> 운영체제의 메모리 회수시 모든 유보 프래그먼트도 소멸된다는 뜻.   
데이터 유실 가능성 존재.



## 장치 회전 처리 시의 추가 고려 사항

`onSavedInstanceState()`와 `retainInstance`의 중요한 차이:  
보존된 데이터가 얼마나 **오래 존속** 하는가


만일 사용자가 앱을 잠시 떠난 후에 메모리가 회수된다면 모든 유보 프래그먼트도 소멸된다.


앞서 만들었던 GeoQuiz 앱에서 장치 회전시 질문 인덱스가 0으로 변경되는 이슈 (프래그먼트라 가정):  
- 질문의 개수가 적을 경우: 유보 프래그먼트를 사용하는 것이 쉽다.  `retainInstance = true`
- 질문이 100개 있을 경우: 잠시 앱을 떠난 동안 프로세스 셧다운이 된다면 첫번째 문제로 돌아가기 때문에 사용자는 짜증날 것.   
-> 인덱스 데이터를 액티비티 레코드의 생애동안 존속시킬 필요가 있다. `onSavedInstanceState()`



