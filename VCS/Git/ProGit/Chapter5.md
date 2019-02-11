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

공개 팀 - 모든 개발자가 직접적으로 쓰기 권한을 가지지는 않는다.  
프로젝트 관리자는 보통 Fork 하는 것으로 프로젝트를 운영한다.

Git 호스팅 사이트 대부분은 **Fork** 기능을 지원한다.

1. 메인 저장소를 clone
2. 토픽 브랜치(`featureA`)를 만들고 일정 부분 기여한다.

3. 프로젝트의 웹사이트에서 **Fork** 버튼을 누르면  
원래 프로젝트 저장소에서 갈라져 나온, 쓰기 권한이 있는 저장소가 만들어진다. (push 가능)
4. 그 저장소를 로컬 저장소의 리모트 저장소로 등록한다.  
```
$ git remote add myfork (url)
```
5. 리모트 저장소에 push.  
master 브랜치에 merge 후 push하는 것 보다  
리모트 브랜치에 바로 push하는 방법이 더 간단하다.   
```
$ git push -u myfork featureA
```
6. 프로젝트 관리자에게 알리기 -- **Pull Request**  
```
1. 토픽 브랜치의 base 브랜치
2. 토픽 브랜치가 위치한 저장소 URL (위에서 저장한 리모트 저장소 이름)
$ git request-pull origin/master myfork
```

각 토픽은 서로 독립적으로 수정하고 rebase 가능.


```
$ git merge --squash featureB

--squash 옵션: 현재 브랜치에 Merge 할 때 해당 브랜치의 커밋을 모두 하나의 커밋으로 합쳐서 Merge 한다. (Merge 커밋은 만들지 않는다.) 
```


"히스토리 단장하기"  
$ `rebase -i` 명령을 사용하면 커밋들을 하나의 커밋으로 합치거나 커밋 정리. 

### 대규모 공개 프로젝트와 이메일을 통한 관리

대규모 프로젝트들은 보통 각각 자신만의 규칙을 갖고 있다.  


(오래된 대규모 프로젝트)  
커밋 내용을 메일로 만들어 개발자 메일링리스트에 제출한다.

1. mbox 형식의 파일을 생성
```
$ git format-patch -M origin/master

각 커밋은 하나씩 메일 메시지로 생성
커밋 메시지의 첫 번째 라인이 제목
Merge 메시지 내용과 Patch 자체가 메일 메시지의 본문
```

2. 메일을 보내려면 먼저 `~/.gitconfig` 파일에서 이메일 부분 설정한다. 

```
[imap]
 folder = "[Gmail]/Drafts"
 host = imaps://imap.gmail.com
 user = user@gmail.com
 pass = p4ssw0rd
 port = 993
 sslverify = false
```

3. Patch 파일을 IMAP 서버의 Dra 폴더에 이메일로 보낸다.
```
$ cat *.patch |git imap-send
```

4. Gmail의 Draft 폴더로 가서 To 부분을 메일링리스트의 주소로 변경하고 CC 부분에 관리자의 메일 주소를 적고 실제로 전송


## 프로젝트 관리하기

효율적으로 운영하는 방법

1. format-patch 명령으로 생성한 Patch를 이메일로 받아서 프로젝트에 Patch를 적용하는 것

2. 프로젝트의 다른 리모트 저장소로부터 변경 내용을 Merge 하는 것

### 토픽 브랜치에서 일하기

메인 브랜치에 통합하기 전에 임시 토픽 브랜치에 통합해 보고 나서 다시 메인 브랜치에 통합하는 것이 좋다. 

토픽 브랜치의 이름을 잘 지어야 한다.
`sc` 라는 사람이 작업한 Patch라면 `sc/ruby_client` 처럼 앞에 닉네임을 붙여서 브랜치를 만들 수 있다. 

```
$ git checkout -b sc/ruby_client master
```
이렇게 토픽 브랜치를 만들고 Patch를 적용해보고 적용한 내용을 다시 Long-Running 브랜치로 Merge 한다.

### 이메일로 받은 Patch를 적용하기

Patch를 적용하는 방법 2가지
1. $ `git apply`
2. $ `git am`


#### APPLY 명령을 사용하는 방법

`diff`로 만든 Patch 파일을 적용할 때는 `git apply`

- 기존의 Patch파일에만 사용한다.

```
--check 옵션으로 미리 테스트. 

$ git apply --check 0001-seeing-if-this-helps-the-gem.patch

아무 내용도 뜨지 않았다면 성공
```

```
Patch 파일 내용에 따라 현재 디렉토리의 파일들을 변경한다. 

$ git apply /tmp/patch-ruby-client.patch
```

- patch 명령: 
    - 중간에 실패하면 그 자리에서 중단. 깔끔 X
- apply 명령:
    - 더 꼼꼼하게 비교 (추가, 삭제, 변경 모두 적용)
    - "모두 적용, 아니면 취소"
    - 자동으로 커밋 X, 변경된 파일을 직접 Staging Area에 추가하고 커밋해야 한다.

#### AM 명령을 사용하는 방법

`format-patch` 명령으로 만든 Patch 파일: 기여자의 정보와 커밋 정보가 포함되어 있음.

```
$ git am 0001-limit-log-function.patch
```

-> patch가 성공하면 자동으로 새로운 커밋이 하나 만들어진다.


patch에 실패하는 경우? => `git am` 명령 중단하고 어떻게 처리할 것인가 사용자에게 물음.
- Patch가 생성된 시점보다 해당 브랜치가 너무 업데이트 됐을 때
- 아직 적용되지 않은 다른 Patch가 필요한 경우


```
(fix the file)
$ git add ticgit.gemspec
$ git am --resolved
```

```
3-way Patch를 적용
$ git am -3 0001-seeing-if-this-helps-the-gem.patch
```

```
patch를 여러 개 적용할 때마다 묻는 옵션
$ git am -3 -i mbox
```


### 리모트 브랜치로부터 통합하기

리모트 저장소로 등록하면 커밋의 히스토리도 알 수 있다


리모트 저장소로 등록하지 않고 URL을 직접 사용햐여 Merge 할 수 있다. (계속 함께 일할 개발자가 아닐 경우)
```
$ git pull https://github.com/onetimeguy/project
```

### 무슨 내용인지 확인하기

메인 브랜치에 Merge 할 때 필요한 명령어  
(주로 토픽 브랜치를 검토하는데 필요한 명령)

```
현재 작업하는 브랜치(contrib)에서 master 브랜치에 속하지 않는 커밋만 (master..contrib)

$ git log contrib --not master
```

- `git log -p` : 각 커밋에서 변경된 내용도 확인 가능.


master 브랜치와 토픽 브랜치의 공통 조상인 커밋을 찾아서  
토픽 브랜치가 현재 가리키는 커밋과 비교해야 한다.

```
공통 조상인 커밋을 찾고 
$ git merge-base contrib master
36c7dba2c95e6bbb78dfa822519ecfec6e1ca649

이 조상 커밋에서 변경된 내용을 살펴본다.
$ git diff 36c7db
```

Git은 **Triple-Dot**으로 간단하게 위와 같이 비교하는 방법을 지원한다. 

```
두 브랜치의 공통 조상과 브랜치의 마지막 커밋을 비교
$ git diff master...contrib
```

### 기여물 통합하기

#### MERGE 하는 WORKFLOW

master 브랜치에 Merge 하는 것이 가장 간단하다.

![](https://user-images.githubusercontent.com/38287485/52556879-29bc6b00-2e31-11e9-944a-9e5c50302fde.png)

![](https://user-images.githubusercontent.com/38287485/52556881-2c1ec500-2e31-11e9-9feb-8f40e7239e36.png)


규모가 큰 프로젝트라면, 최소 두 단계로 Merge 추천.

1. develop 브랜치에 토픽 브랜치를 Merge한다.
2. 릴리즈해도 될만한 수준이 되면 master 브랜치를 develop 브랜치까지 Fast-forward시킨다.


#### 대규모 MERGE WORKFLOW

Git을 개발하는 프로젝트는 Long-Running의 브랜치를 4개 운영한다. (`master`, `next`, `pu` (Proposed Updates), `maint`--마지막으로 릴리즈한 버전을 지원하는 브랜치)


관리자는 새로운 기능 자신의 저장소에 토픽 브랜치로 관리. 계속 안정화 테스트.   
안정화 되었다면 `next`로 merge하고 저장소에 push. 

토픽 브랜치가 개선될 필요가 있다면 `pu`에 merge한다.  
그 후에 검증이 완료되면 `next`로 옮기고 이것을 기반으로 `pu`를 다시 만든다.  


- `next` 브랜치는 가끔 Rebase 하고  
`pu`는 자주 Rebase 하지만  
`master`는 항상 Fast-forward 한다.


토픽 브랜치가 master에 merge되면 삭제한다.  

그리고 이전 릴리즈 버전에 patch가 필요하면 `maint` 브랜치를 이용해 대응.


#### REBASE와 CHERRY-PICK WORKFLOW

- **Cherry-pick**: 한 브랜치에서 다른 브랜치로 작업한 내용을 옮기는 또 다른 방식.  
커밋 하나로 Patch 내용을 만들어 현재 브랜치에 적용을 하는 것. (커밋 하나만 Rebase)  

```
e43a6 커밋 하나만 현재 브랜치에 적용
$ git cherry-pick e43a6fd3e94888d76779ad79fb568ed180e5fcdf

변경을 적용한 시점이 다르므로 새 커밋의 SHA-1 해시값은 달라진다. 
```

![](https://user-images.githubusercontent.com/38287485/52558304-5a9e9f00-2e35-11e9-89bb-f37ce5ea0d1d.png)

![](https://user-images.githubusercontent.com/38287485/52558308-5c686280-2e35-11e9-8a8d-11b42ed40764.png)



#### RERERE

- Rerere: 충돌 해결방법 재사용(reuse recorded reolution)으로 수작업으로 충돌 해결하던 것을 쉽게 해결해준다.  


Merge가 성공할 때 마다 그 이전과 이후 상태를 저장.  
나중에 충돌이 발생하면 비슷한 상황에서 Merge가 성공한 적이 있었는지 찾아보고 해결이 가능하다면 자동으로 해결.  


```
$ git config --global rerere.enabled true
```


### 릴리즈 버전에 태그 달기

되돌릴 수 있도록 태그를 다는 것이 좋다.

```
서명된 태그
$ git tag -s v1.5 -m 'my signed 1.5 tag'
```

태그에 서명하면 서명에 사용한 PGP 공개키도 배포해야 한다.

```
어떤 PGP 공개키를 포함할지 확인
$ gpg --list-keys
```

```
Git 저장소 안에 바로 공개키 저장하고, SHA-1 값을 알려준다.
$ gpg -a --export F721C45A | git hash-object -w --stdin
```

```
PGP 공개키를 가리키는 태그를 만든다.
$ git tag -a maintainer-pgp-pub 659ef797d181633c87ec71ac3f9ba29fe5775b92
```

```
다른 사람이 태그의 서명을 확인하기
PGP 공개키를 꺼내서 GPG키 데이터베이스에 저장해야 한다.
$ git show maintainer-pgp-pub | gpg --import
```

### 빌드넘버 만들기

```
비교적 더 쉬운 이름을 얻는 명령.
$ git describe master
```

git describe 명령은 -a나 -s 옵션을 주고 만든 Annotated 태그가 필요

릴리즈 태그는 git describe 명령으로 만드니까 꼭 이름이 적당한지 사전에 확인해야 한다. 

### 릴리즈 준비하기

소스코드 스냡샷 압축.  

```
ZIP 형식

$ git archive master --prefix='project/' --format=zip > `git describe master`.zip
```

### Shortlog 보기

지난 릴리즈 이후의 변경 사항 목록. 커밋 요약해서 보여줌

```
v1.0.1 버전 이후의 커밋을 요약

$ git shortlog --no-merges master --not v1.0.1
```






