# Chapter3. Git 브랜치

독립적으로 개발 가능.

Git의 브랜치는 커밋 사이를 가볍게 이동할 수 있는 어떤 **포인터** 같은 것이다. 

1. Git의 브랜치는 매우 가볍다.

2. 다른 버전 관리 시스템과는 달리 Git은 브랜치를 만들어 작업하고 나중에 Merge 하는 방법을 권장한다.

## 브랜치란 무엇인가

git은 데이터를 Change Set이나 변경사항(diff)로 기록하지 않고 일련의 **스냅샷**으로 기록한다. --Chapter1

이전 커밋 포인터가 있어 현재 커밋이 무엇을 기준으로 바뀌었는지를 알 수 있다.

브랜치를 합친 Merge의 경우에는 이전 커밋 포인터가 여러 개 있음.

git 저장소에 파일을 저장 -- Blob


git commit하면 먼저 루트와 하위 트리 개체들을 체크섬과 함께 저장소에 저장한다.

그 다음 커밋 개체를 만들고 메타데이터와 루트 디렉토리 트리 개체를 가리키는 포인터 정보를 넣어 저장한다.  

그래서 필요하면 언제든지 스냅샷을 다시 만들 수 있음.

![](https://user-images.githubusercontent.com/38287485/51459909-5d115a00-1d9d-11e9-9312-5e67a2928e23.png)

1. 메타데이터와 루트 트리를 가리키는 포인터가 담긴 커밋 개체 1개

2. 파일과 디렉토리 구조가 들어 있는 트리 개체 1개

3. 각 파일에 대한 Blob 3개

commit 후 총 5개의 데이터 개체가 생성됨.


![](https://user-images.githubusercontent.com/38287485/51460123-ef196280-1d9d-11e9-9ad2-c7fbccc79409.png)

![](https://user-images.githubusercontent.com/38287485/51460617-4c61e380-1d9f-11e9-876f-bc488f274267.png)

이전 커밋 정보 저장.

기본적으로 git은 **master** 브랜치를 만든다.  

커밋을 만들 때마다 브랜치가 자동으로 가장 마지막 커밋을 가리키게 한다.

### 새 브랜치 생성하기

```
$ git branch [브랜치명]
```

새로 만든 브랜치도 작업중이던 마지막 커밋을 가리킨다.

- **HEAD** 포인터: 지금 작업하는 로컬 브랜치를 가리킨다. 

`git branch`는 브랜치를 만들기만 하고 브랜치를 옮기지는 않는다.


```
브랜치가 어떤 커밋을 가리키는지 확인

$ git log --oneline --decorate
```


### 브랜치 이동하기

```
$ git checkout [브랜치명]
```
이렇게 하면 **HEAD**가 해당 브랜치를 가리킨다.
 

$ `git checkout master` : 
1. master가 가리키는 커밋을 HEAD가 가리키도록
2. 워킹 디렉토리의 파일도 그 시점으로 되돌려놓음.  
만약, 파일 변경시 문제가 있다면 브랜치 이동 명령 수행하지 않음.


프로젝트 히스토리는 분리되어 진행한다. (독립적)  

```
현재 브랜치가 가리키고 있는 히스토리가 무엇이고 어떻게 갈라져 나왔는지 보여주는 명령

$ git log --oneline --decorate --graph --all
```

```
* c2b9e (HEAD, master) ...
| * 87ab2 (testing) ...
|/
* f30ab ...
* 34ac2 ...
```

브랜치를 하나 새로 만드는 것은 약 41바이트 파일을 하나 만드는 것과 같기 때문에   
프로젝트를 통째로 복사해야 하는 다른 버전관리 도구에 비해 부담이 훨씬 적다.  

게다가 이전 커밋 정보도 저장하기 때문에 Merge시 어디서부터 합쳐야 하는지 안다.


## 브랜치와 Merge의 기초

예)
브랜치 1에서 새로운 작업을 진행하던 중,  
새로운 이슈 생김.  
이전의 브랜치로 이동 후 새로운 브랜치 2를 만들고 이슈를 처리한다.  
작업을 마치고 운영 브랜치로 merge한다.   


### 브랜치의 기초

```
브랜치를 만들면서 checkout까지 

$ git branch iss53
$ git checkout iss53

위의 두 명령과 아래의 명령은 같음.

$ git checkout -b iss53
```

새로운 커밋을 실행하면, `iss53` 브랜치가 앞으로 진행한다.

아직 커밋하지 않은 파일이 checkout할 브랜치와 충돌 나면 브랜치 변경 X. (워킹 디렉토리를 정리하는 것이 좋다.) 

지금은 작업하던 것을 모두 커밋하고 master 브랜치로 옮긴다. 

`hotfix` 브랜치 만들고 커밋.

![](https://user-images.githubusercontent.com/38287485/51471669-b6d44d00-1dba-11e9-8605-60dd89e29ba9.png")


문제를 해결했다면, `master` 브랜치에 합쳐야 한다.

```
$ git checkout master
$ git merge hotfix

Updating f42c576..3a0874c
Fast-forward
 index.html | 2 ++
 1 file changed, 2 insertions(+)
```

A브랜치에서 다른 B브랜치를 merge할 때 B가 A 이후의 커밋을 가리키고 있으면  
그저 A가 B의 커밋을 가리키게 하는 merge 방식을 **Fast forward**라 한다.  

![](https://user-images.githubusercontent.com/38287485/51472392-aae98a80-1dbc-11e9-950d-5dacc82d815c.png)


```
브랜치 삭제

$ git branch -d hotfix
```

이제 다시 작업하던 `iss53`브랜치로 이동해 하던 작업을 마무리한다.  

![](https://user-images.githubusercontent.com/38287485/51472523-0c115e00-1dbd-11e9-8bbc-02a8cb8700c3.png)


### Merge의 기초

`master`에 `iss53` 브랜치 merge

```
$ git checkout master
$ git merge iss53

Merge made by the 'recursive' strategy.
README | 1 +
1 file changed, 1 insertion(+)
```

`hotfix`를 merge했을 때와 메시지가 달라졌다.  

이 경우, 각 브랜치가 가리키는 커밋 두 개와 공통 조상 하나를 사용해 **3-way Merge** 를 한다.  
그 결과를 별도의 커밋으로 만들고 해당 브랜치가 그 커밋을 가리키도록 이동.

![](https://user-images.githubusercontent.com/38287485/51472761-c3a67000-1dbd-11e9-8f90-9d859cf22620.png)

![](https://user-images.githubusercontent.com/38287485/51474163-c99e5000-1dc1-11e9-8316-2b625acc2578.png)


Git은 공통 조상을 자동으로 찾는다. (CVS, Subversion은 직접 찾아야 함.)


### 충돌의 기초

같은 파일을 동시에 수정하고 merge하는 경우 **conflict** 메시지를 출력하고 merge 실패.


Merge 충돌이 일어났을 때 Git이 어떤 파일이 원인인지 보려면 `git status` 를 이용한다.

```
$ git status


On branch master
You have unmerged paths.
 (fix conflicts and run "git commit")

Unmerged paths:
 (use "git add <file>..." to mark resolution)

 both modified: index.html

no changes added to commit (use "git add" and/or "git commit -a")
```

```
<<<<<<< HEAD:index.html
<div id="footer">contact : email.support@github.com</div>
=======
<div id="footer">
 please contact us at support@github.com
</div>
>>>>>>> iss53:index.html
```

`=======`의 위쪽은 HEAD 버전, 아래쪽은 iss53 브랜치


```
Merge tool 보기

$ git mergetool
```

Merge 도구를 종료하고 잘 마쳤다고 입력하면  
자동으로 `git add`가 수행되고 해당 파일이 Staging Area에 저장된다.

마지막으로 merge 결과물을 commit한다.

## 브랜치 관리

p.91

## 브랜치 Workflow

### Long-Running 브랜치

### 토픽 브랜치

## 리모트 브랜치

### Push하기

### 브랜치 주석

### Pull 하기

### 리모트 브랜치 삭제

## Rebase 하기

### Rebase의 기초

### Rebase 활용

### Rebase의 위험성

### Rebase한 것을 다시 Rebase하기

### Rebase vs. Merge

