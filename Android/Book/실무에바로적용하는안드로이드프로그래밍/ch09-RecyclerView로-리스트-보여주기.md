# Chapter9. RecyclerView로 리스트 보여주기

## CriminalIntent의 모델 계층 변경하기

### 싱글톤과 집중 데이터 스토리지

- **singleton** : 딱 하나의 인스턴스만 생성할 수 있는 클래스.  

싱글톤은 애플리케이션이 메모리에 있는 한 계속 존재한다.  
-> 리스트를 싱글톤에 저장하면 액티비티와 프래그먼트의 생명주기가 변경되는 동안에도 항상 범죄 데이터를 사용할 수 있다.  

- 싱글톤 사용시 **주의점** : 메모리에서 제거되면 소멸될 수 있음.  
-> `CrimeLab 싱글톤`은 장기간의 데이터 보존을 위한 솔루션은 아니다.  
--> 그러나 `CrimeLab`만이 범죄 데이터를 소유함으로써  
컨트롤러와의 데이터 전달을 쉽게 할 수 있다는 장점이 있음.



1. `private 생성자`와 `get()` 메서드를 각각 하나씩 갖는다.
2. 자신의 인스턴스가 이미 있다면 `get()`에서 기존 인스턴스를 반환한다.
3. 인스턴스가 없다면 `get()`에서 생성자를 호출하여 인스턴스를 생성한 후 반환한다.


```java
public class CrimeLab {
    // 안드로이드의 작명 규칙 - static 변수의 접두사로 s를 사용한다.
    private static CrimeLab sCrimeLab;

    private List<Crime> mCrimes;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    // 생성자가 private 이기 떄문에 
    // 외부에서 인스턴스를 생성하려면 반드시 get()을 호출하야 함.
    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("범죄 #" + i);
            crime.setSolved(i % 2 == 0); // 짝수 번째 요소에는 true 임의 설정.
            mCrimes.add(crime);
        }
    }

    public List<Crime> getmCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }
        return null;
    }
}
```


## 프래그먼트의 호스팅을 위한 추상 액티비티

### 보편화된 프래그먼트 호스팅 레이아웃

### 추상 액티비티 클래스

## RecyclerView, Adapter, ViewHolder

### ViewHolder와 Adapter

### RecyclerView 사용하기

### 어댑터와 ViewHolder 구현하기

## 리스트 항목의 커스터마이징

### 리스트 항목의 레이아웃 생성하기

### 커스텀 항목 뷰 사용하기

## 리스트 항목 선택에 응답하기

## ListView와 GridView

## 싱글톤