# Chapter2. Git의 기초

## Git 저장소 만들기

1. 기존 프로젝트
2. 다른 서버의 저장소 Clone

### 기존 디렉토리를 Git 저장소로 만들기

```
$ git init
```

이 명령은 `.git`이라는 하위 디렉토리를 만든다.

```
파일 추가 및 커밋
$ git add *.c
$ git add LICENSE
$ git commit -m 'initial project version'
```

### 기존 저장소를 Clone 하기

다른 프로젝트에 contribute 하거나 복사하고 싶을 때 사용.

`git clone`을 실행하면 프로젝트 히스토리를 전부 받아온다.  

실제로 서버의 디스크가 망가져도 클라이언트 저장소 중에서 아무거나 가져다가 복구하면 된다.  

```
$ git clone [url]
```

```
$ git clone https://github.com/libgit2/libgit2
libgit2 폴더 안에 .git 디렉토리를 만들고 데이터를 가장 최신 버전을 checkout해 놓는다.
```

```
$ git clone https://github.com/libgit2/libgit2 mylibgit
디렉토리 이름을 mylibgit으로 
```

다양한 프로토콜 지원.
- `https://`
- `git://`
- `user@server:path/to/repo.git`

## 수정하고 저장소에 저장하기

파일을 수정하다가 저장하고 싶으면 스냅샷을 커밋한다.

- Working Directory
    - Tracked(관리 대상): 이미 스냅샷에 포함돼 있던 파일 
        - Unmodified (수정하지 않음)
        - Modified (수정함)
        - Staged (커밋으로 저장소에 기록할 예정)
    - Untracked(관리 대상 X): 아직 스냅샷(커밋)에 넣어지지 않은 파일

라이프사이클   
Modified -> Staged -> commit -> Unmodified -수정-> Modified -> ...

### 파일의 상태 확인하기

```
$ git status
```

### 파일을 새로 추적하기

```
$ git add README
```

Untracked 상태였던 `README` 파일이 Tracked 상태이면서 커밋에 추가될 Staged 상태가 됨.

**"Changes to be committed"** - Staged 상태

### Modified 상태의 파일을 Stage하기

이미 Tracked 상태인 파일을 수정

**"Changes not staged for commit"** - 수정한 파일이 Tracked이며 Staged 상태는 아닌

**git add** 는 언제?
1. 파일을 새로 추적
2. 수정한 파일을 Staged 상태로 만들 때
3. Merge할 때 충돌난 파일을 Resolve 상태로 만들 때

`$ git add`를 사용해 staged 상태가 된 후  
커밋하지 않고 다시 파일을 수정하고 `$ git status`로 상태를 확인해보면 해당 파일이 Staged 상태이면서 동시에 Unstaged 상태로 나온다.  
이 상태로 커밋하면, 마지막으로 add한 버전이 커밋된다.  

그러므로, 수정한 경우 다시 add 명령을 실행해 최신 버전을 Staged한 상태로 만들어야 한다.

### 파일 상태를 짤막하게 확인하기

```
$ git status -s
$ git status --short

>
M README
MM Rakefile
A lib/git.rb
M lib/simplegit.rb
?? LICENSE.txt
```

?? - Untracked 
A - Staged 상태 중 새로 생성한 파일
M - 수정한 파일
MM - Staged 이면서 Unstaged 상태인 파일

### 파일 무시하기

```
$ cat .gitignore

*.[oa] // ".o"나 ".a"인 파일. (오브젝트와 아카이브 파일) 
*~ // ~로 끝나는 파일. (텍스트 편집기가 임시로 만들어내는 파일)
```

.gitignore 파일은 보통 처음에 만들어 두는 것이 편리하다.  


`.gitignore`의 패턴
- `#` 주석
- 표준 Glob 패턴 사용
- `/`로 시작하면 하위 디렉토리에 적용되지 않는다.
- 디렉토리는 `/`를 끝에 사용하는 것으로 표현한다.
- `!`로 시작하는 패턴의 파일은 무시하지 않는다.

----- page 46 ------


### Staged와 Unstaged 상태의 변경 내용을 보기

### 변경사항 커밋하기

### Staging Area 생략하기

### 파일 삭제하기

### 파일 이름 변경하기

## 커밋 히스토리 조회하기

### 조회 제한조건

## 되돌리기

### 파일 상태를 Unstage로 변경하기

### Modified 파일 되돌리기

## 리모트 저장소

### 리모트 저장소 확인하기

### 리모트 저장소 추가하기

### 리모트 저장소를 Pull하거나 Fetch하기

### 리모트 저장소에 Push하기

### 리모트 저장소 살펴보기

### 리모트 저장소 이름을 바꾸거나 리모트 저장소를 삭제하기