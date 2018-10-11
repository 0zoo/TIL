# Chapter11. ViewPager 사용하기

![image](https://user-images.githubusercontent.com/38287485/46788962-53c42a80-cd76-11e8-97a7-a94cf523c68b.png)

- ViewPager: 사용자가 데이터 페이지들을 좌우로 넘길 수 있게 해주는 안드로이드 레이아웃 매니저 클래스.

## CrimePagerActivity 생성하기

CrimePagerActivity는 ViewPager를 생성하고 관리할 것이다.s

`android.support.v4.view.ViewPager`

### ViewPager와 PagerAdapter

ViewPager는 PagerAdapter가 필요하다.

- **FragmentStatePagerAdapter** :   
프래그먼트를 사용하는 PagerAdapter를 구현한 것.   
프래그먼트의 상태도 저장하고 복원할 수 있다.  
`getCount()`와 `getItem()`  
`getItem(범죄 리스트의 항목 인덱스)`이 호출되면, 범죄 내역을 보여주기 위해 구성된 CrimeFragment를 반환할 것이다.
(범죄 리스트는 Crime 객체들이 저장된 ArrayList를 말하며 CrimeLab 싱글톤 인스턴스가 갖고 있다.)


```
// PagerAdapter 설정하기
```



### CrimePagerActivity 통합하기

## FragmentStatePagerAdapter vs FragmentPagerAdapter

## ViewPager가 실제로 동작하는 방법

## 코드에서 뷰의 레이아웃 처리하기

