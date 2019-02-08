# Chapter5. 분산 환경에서의 Git

## 분산 환경에서의 Workflow

> Git은 **분산형**이다.


중앙집중형: 각 개발자는 중앙 저장소를 중심으로 하는 하나의 노드

**분산형(Git)**: 각 개발자의 저장소가 하나의 노드이면서 중앙저장소 같은 역할.
-> (협업) 코드를 운영할 때 **다양한 Workflow**를 만들 수 있도록 해준다.

### 중앙집중식 Workflow 

- **중앙 저장소는 딱 하나** 존재.    
변경 사항은 모두 이 중앙 저장소에 집중되고 이곳을 중심으로 개발자 작업함.


머지 후 푸쉬

### Integration-Manager Workflow

- (프로젝트를 대표하는) **하나의 공식 저장소**

1. 기여자는 공식 저장소를 clone하고, 수정후 자신의 저장소에 push
2. 기여자는 Integration-Manager에게 변경사항을 적용해 줄 것을 e-mail같은 것으로 요청한다.
3. Integration-Manager는 기여자의 저장소를 리모트 저장소로 등록하고 수정사항을 merge하여 테스트한다.
4. 결과물을 메인 저장소에 push한다.


- 프로젝트를 Fork하고 수정사항을 반영하여 다시 공개하기 좋은 구조.
- Github, GitLab
- 장점: 기여자와 Integration-Manager 각자 사정에 맞춰 프로젝트를 유지할 수 있다.


### Dictator and Lieutenants Workflow 

- 저장소를 여러개 운영하는 방식을 변형한 구조 (대형 프로젝트 - 예: 리눅스 커널 프로젝트)

- **Lieutenants**: 자신이 맡은 부분만을 담당. 최종 관리자 아래에 있다.  

- **Dictator**: 최종 관리자. 최종 관리자가 관리하는 저장소를 공식 저장소로 한다.  
모든 코드를 통합하기 전에 코드를 부분부분 통합하도록 여러 명의 Lieutenant에게 위임한다.

- 모든 프로젝트 참여자는 공식 저장소를 기준으로 작업한다.


1. 개발자는 master브랜치(dictator의 브랜치)를 기준으로 rebase.
2. Lieutenants들은 개발자들의 수정사항을 자신이 관리하는 master 브랜치에 merge한다.
3. Dictator는 Lieutenants의 master 브랜치를 자신의 master 브랜치로 merge한다.
4. Dictator가 merge한 것을 push.  
다른 모든 개발자는 Dictator의 브랜치를 기준으로 rebase.


### Workflow 요약

1. **중앙집중식** Workflow
    - 중앙 저장소는 딱 하나 존재
2. **Integration-Manager** Workflow
    - 프로젝트를 대표하는 하나의 공식 저장소
3. **Dictator and Lieutenants** Workflow
    - 최종 관리자 Dictator와 부분적으로 관리하는 일을 위임받은 여러 개의 Lieutenant들.

## 프로젝트 기여하기

기여하는 방식에 영향을 끼치는 변수 여러 개 있음. 

변수 1. 활발히 활동하는 개발자의 수

변수 2. 프로젝트에서 선택한 Workflow(저장소 운영 방식)

변수 3. 접근 권한


### 커밋 가이드라인

Git 프로젝트의 **Documentation/SubmittingPatches** 문서를 참고하자.

1. 공백문자를 깨끗하게 정리하고 커밋해야 한다.  
```
공백 문자 검사
$ git diff --check
```
2. 각 커밋은 논리적으로 구분되는 Changeset이다.  
(하나의 커밋에는 하나의 이슈만. 여러가지 이슈 한꺼번에 X --이런 경우에는 Staging Area 사용)  
```
같은 파일의 다른 부분을 수정하는 경우 
$ git add -patch
한 부분씩 나누어 Staging Area에 저장한다. 
```

3. 일반적인 커밋 메시지 작성 규칙 
    - 메시지의 첫 라인에 간략한 요약 (50자 이내)  
    다음 한 줄 띄고, 커밋 자세히 설명
    - 현재형 표현을 사용하는 것이 좋다.
    - 목록 표시 가능. (`-`나 `*`)


### 비공개 소규모 팀

"비공개" - 소스코드가 공개되지 않은 것. (외부 접근 불가 의미가 아님)

모든 개발자는 공유하는 저장소에 쓰기 권한이 있어야 한다.


Subversion 같은 중앙집중형 버전 관리 시스템에서
사용하던 방식을 사용한다.  

가장 큰 차이점: 클라이언트 쪽에서 Merge한다.  


같은 저장소를 A와 B가 각각 clone하고, 로컬에서 수정 작업을 하고 커밋한 상태이다. A가 먼저 서버에 push했다면, B의 push는 서버에서 거절된다.    

같은 파일을 수정한 것도 아닌데 왜 거절되는 걸까?  
Subversion에서는 이런 merge작업은 자동으로 서버가 처리한다.  
Git은 로컬에서 먼저 Merge해야 한다.   
=> B는 A의 커밋을 Fetch하고 Merge해야 한다.
```
$ git fetch origin 
$ git merge origin/master
```


```
어떤 내용이 merge 되는지 확인
$ git log --no-merges issue54..origin/master
```

`issue54..origin/master` 문법은 히스토리를 검색할 때 뒤의 브랜치 (origin/master)에 속한 커밋 중 앞의 브랜치(issue54)에 속하지 않은 커밋을 검색하는 문법이다.

![](https://user-images.githubusercontent.com/38287485/52400241-ee086500-2b01-11e9-9164-65d2b7345f78.png)

1. $ `git checkout master`
2. $ `git merge issue54`
3. $ `git merge origin/master`
4. $ `git push origin master`

1,2,3
![](https://user-images.githubusercontent.com/38287485/52400372-4c354800-2b02-11e9-8d02-f3c8a323c932.png)


4
![](https://user-images.githubusercontent.com/38287485/52400694-32483500-2b03-11e9-9f11-befbe6b3753c.png)


![](https://user-images.githubusercontent.com/38287485/52402395-7f2e0a80-2b07-11e9-8be9-c881c9d2180c.png)


### 비공개 대규모 팀

보통 팀을 여러 개로 나눈다.

Integration-manager Workflow을 추천함.

팀마다 브랜치를 하나씩 만들고 Integration-manager는 그 브랜치를 pull해서 merge한다.


`featureA` 브런치를 만들고 수정후 커밋.
```
$ git push -u origin featureA
(Integration-Manager만 master 브랜치를 업데이트할 수 있음)
```

다른 작업 `featureB`를 진행
```
$ git fetch origin
$ git checkout -b featureB origin/master
```
몇가지 작업 후 `featureB` 브랜치에 커밋

![](https://user-images.githubusercontent.com/38287485/52403050-26f80800-2b09-11e9-995b-b242eb6fcd5b.png)

작업을 마치고 push하려는데, 다른 팀원이 해당 브랜치에 일부 작업을 하고 서버에 `featureBee` 브랜치로 push했다는 소식..  
-> 그 작업을 먼저 merge해야만 푸시 가능.

```
$ git fetch origin
$ git merge origin/featureBee
```

그런데, 문제가 있음. 
나는 `featureB` 브랜치에서 작업했는데, 서버에는 브랜치가 `featureBee`라는 이름으로 되어있다.

```
"refspec" - [로컬 브랜치]:[서버 브랜치]

$ git push -u origin featureB:featureBee
```

이때, 또 다른 팀원이 `featureA`에 작업을 하고 push했으니 확인해달라는 연락.  

```
$ git fetch origin
```

$ ` git log featureA..origin/featureA`   
: 어떤 것이 업데이트되었는지 확인하는 명령

```
변경사항 확인 후 로컬 featureA 브랜치로 merge한다.
$ git checkout featureA
$ git merge origin/featureA

$ git commit -am 'small tweak'
$ git push
```

![](https://user-images.githubusercontent.com/38287485/52403714-b651eb00-2b0a-11e9-80a9-a1361d55f208.png)


이제 `featureA`와 `featureBee` 브랜치가 프로젝트의 메인 브랜치로 Merge할 준비가 되었다고 Integration-Manager에게 알려준다.



Integration-Manager가 두 브랜치를 모두 Merge 하고 난 후에 메인 브랜치를 Fetch 하면?

![](https://user-images.githubusercontent.com/38287485/52403786-e9947a00-2b0a-11e9-95b8-6b0cd4b66d4f.png)


![](https://user-images.githubusercontent.com/38287485/52403870-16489180-2b0b-11e9-962b-5e052793ec8b.png)

### 공개 프로젝트 Fork

### 대규모 공개 프로젝트와 이메일을 통한 관리





## 프로젝트 관리하기

효율적으로 운영하는 방법

1. format-patch 명령으로 생성한 Patch를 이메일로 받아서 프로젝트에 Patch를 적용하는 것

2. 프로젝트의 다른 리모트 저장소로부터 변경 내용을 Merge 하는 것

### 토픽 브랜치에서 일하기

### 이메일로 받은 Patch를 적용하기

### 리모트 브랜치로부터 통합하기

### 무슨 내용인지 확인하기

### 기여물 통합하기

### 릴리즈 버전에 태그 달기

### 빌드넘버 만들기

### 릴리즈 준비하기

### Shortlog 보기






