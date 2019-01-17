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

- ?? - Untracked 
- A - Staged 상태 중 새로 생성한 파일
- M - 수정한 파일
- MM - Staged 이면서 Unstaged 상태인 파일

### 파일 무시하기

```
$ cat .gitignore

*.[oa] // ".o"나 ".a"인 파일. (오브젝트와 아카이브 파일) 
*~ // ~로 끝나는 파일. (텍스트 편집기가 임시로 만들어내는 파일)
```

.gitignore 파일은 보통 처음에 만들어 두는 것이 편리하다.  


`.gitignore`의 패턴
- `#` 주석
- 표준 Glob 패턴 사용 (단순한 정규 표현식)
- `/`로 시작하면 하위 디렉토리에 적용되지 않는다.
- 디렉토리는 `/`를 끝에 사용하는 것으로 표현한다.
- `!`로 시작하는 패턴의 파일은 무시하지 않는다.

```
# 확장자가 .a인 파일 무시
*.a

# lib.a는 무시하지 않음
!lib.a

# 현재 디렉토리에 있는 TODO 파일은 무시하고 subdir/TODO처럼 하위 디렉토리에 있는 파일은 무시하지 않음
/TODO

# build/ 디렉토리에 있는 모든 파일은 무시
build/

# doc/notes.txt 파일 무시
doc/*.txt

# doc 디렉토리 아래의 모든 .txt 파일을 무시
doc/**/*.txt
```

### Staged와 Unstaged 상태의 변경 내용을 보기


**git diff** 명령
- 어떤 내용이 변경되었는지 살펴보기
- Unstaged 상태인 변경 부분 확인 
- `git diff --cached`옵션으로 Staged 상태인 파일 확인
- 외부 도구 사용하기 - git difftool

### 변경사항 커밋하기

Unstaged 상태의 파일은 커밋되지 않음을 기억하자. (수정 후 git add 하지 않은 파일.. 여전히 Modified 상태)

Git은 **Staging Area에 속한 스냅샷을 커밋** 한다는 것을 기억하자.

그 후에  $ `git commit`을 실행해 커밋한다.
(자동으로 커밋 메세지 생성.- git status 명령의 결과.)  
($ `git commit -v`옵션은 diff 메시지도 추가됨.)


인라인으로 커밋 메시지: $ `git commit -m "..."`


### Staging Area 생략하기

```
$ git commit -a -m 'added new benchmarks'
```

`-a`옵션을 추가하면 Git은 Tracked 상태의 파일을 자동으로 Staging Area에 넣는다.  
(-> git add 생략 가능)


### 파일 삭제하기

```
디렉토리에서 파일 삭제 - Unstaged 상태

$ rm grit.gemspec
```

```
이제 삭제한 파일은 Staged 상태가 됨.

$ git rm grit.gemspec
```

```
이미 파일을 수정했거나 Staging Area에 추가한 경우 -f 옵션 필수

$ git rm -f grit.gemspec 
```

```
파일은 그대로 두고 git 추적 안하도록

$ git rm --cached README
```

```
한꺼번에 삭제 - file glob 패턴 사용

log 디렉토리 안에 있는 모든 .log 파일 삭제
$ git rm log/\*.log

~로 끝나는 파일을 모두 삭제
$ git rm \*~
```


### 파일 이름 변경하기

Git은 파일 이름이 변경 정보를 별도로 저장하지 않는다.  
그렇다면 파일 이름 변경을 어떻게 추적할까?  

```
파일 이름 변경

$ git mv file_from file_to
```

위의 명령은 사실 아래의 명령을 실행한 것과 같다.

```
$ mv README.md README

이름을 변경한 후에는 rm/add명령을 해주어야 한다.

$ git rm README.md
$ git add README
```

`git mv`는 단지 편리하게 명령을 세 번 실행해주는 것.  

## 커밋 히스토리 조회하기

```
커밋 히스토리 시간순으로
$ git log
```

다양한 옵션들을 제공한다.

- `-p`: 각 커밋의 diff 결과를 보여준다.
- `-2`(`-<n>`): 최근 두 개의 결과만 보여준다.
    ```
    $ git log -p -2
    ```
- `--stat`: 각 커밋의 통계 정보를 조회할 수 있다.

- `--pretty`: 히스토리 내용을 보여줄 때 기본 형식 이외에 여러가지 중 하나 선택 가능.
    - `oneline` 옵션: 각 커밋을 한 라인으로 보여줌.
        ```
        $ git log --pretty=oneline
        ```
    - `short`, `full`, `fuller` 옵션: 정보를 조금씩 가감해서 보여줌.
    - `format`: 내가 원하는 포맷으로 결과 출력  
    특히, 결과를 다른 프로그램으로 파싱하고자 할 때 유용.
        ```
        $ git log --pretty=format:"%h - %an, %ar : %s"
        > a11bef0 - Scott Chacon, 6 years ago : first commit
        ```
        - `%h`: 짧은 길이 커밋 해시
        - `%an`: 저자 이름
        - `%ad`: 저자 시각 
        - `%ar`: 저자 상대적 시각
        - `%cn`: 커미터 이름
        - `%s`: 요약


저자(`%an`)와 커미터(`%cn`)의 차이?  
**저자**는 원래 작업을 수행한 원작자.  
**커미터**는 마지막으로 이 작업을 저장소에 포함시킨 사람.


```
브랜치와 머지 히스토리를 보여주는 아스키 그래프 출력

$ git log --pretty=format:"%h %s" --graph
```

이 이외에도 다른 유용한 옵션들이 있음.

### 조회 제한조건

- `-(n)`: 최근 n개의 커밋만
- `--since`, `--after`: 명시한 날짜 이후의 커밋만
- `--until`, `--before`: 명시한 날짜 이전의 커밋만
- `--author`: 입력한 저자의 커밋만 
- `--committer`: 입력한 커미터의 커밋만
- `--grep`: 커밋 메시지 안의 텍스트를 검색
- `-S`: 커밋 변경(추가/삭제) 내용 안의 텍스트 검색


```
지난 2주 동안의 커밋들만 조회하는 명령

$ git log --since=2.weeks
```

(`--author`, `--grep` 같이 사용하려면 `--all-match`옵션 함께 사용 필수)

```
$ git log --Sfunction_name
```


## 되돌리기

한 번 되돌리면 복구할 수 없기에 주의해야 한다.  

완료한 커밋을 수정해야 할 때 다시 커밋하고 싶으면 `--amend`옵션을 사용한다.

```
커밋하자마자 바로 이 명령을 실행하는 경우는 커밋 메시지만 수정한다.  

$ git commit --amend
```


```
커밋을 했는데 Stage하는 것을 빠트렸다면,

$ git commit -m 'initial commit'
$ git add forgotten_file
$ git commit --amend

위의 명령어 3개는 모두 하나의 커밋으로 기록됨.
두 번째 커밋은 첫 번째 커밋을 덮어쓴다.
```

### 파일 상태를 Unstage로 변경하기

예)  
파일 두 개를 따로따로 커밋하려고 했지만, 실수로 `git add *`을 실행한 경우?  
두 파일 모두 Staging Area에 들어 있는 상태. 둘 중 하나를 어떻게 꺼낼까?

```
Staging Area에 있는 benchmarks.rb 파일을 Unstated 상태로 변경

$ git reset HEAD benchmarks.rb
```

`git reset` 명령을 `--hard`옵션과 함께 사용하면 워킹 디렉토리의 파일이 수정되기 때문에 조심해야 한다.  


### Modified 파일 되돌리기

```
$ git checkout -- benchmarks.rb
```

`git checkout -- [file]` 명령은 원래 파일로 덮어씌우기 때문에 수정한 내용이 전부 사라지기 때문에 조심해야 한다.

변경한 내용을 쉽게 버릴수는 없지만 되돌려야만 하는 상황이라면 `Stash`와 `Branch`를 사용하자


커밋한 모든 것은 언제나 복구할 수 있다. (`--amend` 옵션으로)

## 리모트 저장소

리모트 저장소는 인터넷이나 네트워크 어딘가에 있는 저장소를 말한다.  

다른 사람들과 함께 일한다는 것은 리모트 저장소를 관리하면서 데이터를 거기에 Push하고 Pull하는 것이다. 

리모트 저장소를 관리한다는 것?  
저장소를 추가/삭제하는 것뿐만 아니라 브랜치를 관리하고 추적할지 말지 등을 관리하는 것을 말한다. 

### 리모트 저장소 확인하기

```
리모트 저장소의 [단축 이름]을 보여줌.
$ git remote
```

```
[단축 이름] + [url]
$ git remote -v
```

### 리모트 저장소 추가하기

```
$ git remote add [단축이름] [url]
```

### 리모트 저장소를 Pull하거나 Fetch하기

리모트 저장소에서 데이터를 가져오기 (로컬에는 없는)
```
$ git fetch [remote-name]
```

clone한 이후로 변경된 것 가져오기
```
$ git fetch origin
```

`fetch`명령은 Merge 자동 X. 수동으로 해줘야 함.

위 방법 이외에 쉽게 pull하는 방법:  
`git clone` 명령은 자동으로 로컬의 master가 리모트 저장소의 master를 추적하도록 한다.  
`git pull` 명령은 Clone한 서버에서 데이터를 가져오고 그 데이터를 자동으로 현재 작업하는 코드와 Merge 시킨다.

### 리모트 저장소에 Push하기

```
$ git push [리모트 저장소 이름] [브랜치 이름]

$ git push origin master
```

push하기 전에 다른 사람이 먼저 push했다면 merge를 먼저 해야한다.

### 리모트 저장소 살펴보기

```
$ git remote show [리모트 저장소 이름]
```

### 리모트 저장소 이름을 바꾸거나 리모트 저장소를 삭제하기

리모트 저장소 이름을 pb에서 paul로 변경

```
$ git remote rename pb paul
```

리모트 저장소 paul 삭제 
```
$ git remote rm paul
```
## 태그

보통 릴리즈할 때 사용.

### 태그 조회하기

```
$ git tag
```

```
1.8.5 버전의 태그들만 검색

$ git tag -l 'v1.8.5*'
```

### 태그 붙이기


1. Lightweight: 브랜치와 비슷하지만, 단순히 특정 커밋에 대한 포인터.

2. Annotated: 여러 정보들을 저장하는 태그. (만든 사람 이름, 이메일, 날짜, 메시지)


### Annotated 태그

Annotated 태그 생성하기: `-a` 옵션

```
$ git tag -a v1.4 -m 'my version 1.4'
```

### Lightweight Tags

Lightweight 태그는 파일에 커밋 체크섬을 저장하는 것 뿐. (다른 정보 저장 X)

Lightweight 태그는 옵션 사용 X

```
$ git tag [태그 이름]
```

### 나중에 태그하기

예전 커밋에 대해서도 태그할 수 있다. 

```
9fceb02d0ae59... update rakefile
```
위와 같은 `9fceb02d0ae59...XX` 커밋 체크섬을 가진 `update rakefile` 커밋에 태그를 붙이려면


```
$ git tag -a v1.2 9fceb02

(긴 체크섬 전부 사용 안해도 됨.)
```

### 태그 공유하기

`git push`는 자동으로 태그 전송 X. 별도로 push 해줘야 한다. 

```
$ git push origin [태그 이름]

한 번에 태그 여러개 Push하기
$ git push origin --tags
```

### 태그를 Checkout하기

태그는 브랜치와 달리 가리키는 커밋을 바꿀 수 없는 이름.  
Checkout 해서 사용 X

태그가 가리키는 특정 커밋 기반의 브랜치를 만들어 작업하려면 아래와 같이 새로 브랜치를 생성한다.

```
$ git checkout -b version2 v2.0.0
```

물론 이렇게 브랜치를 만든 후에 version2 브랜치에 커밋하면 브랜치는 업데이트된다.  
하지만 v2.0.0 태그는 가리키는 커밋이 변하지 않았으므로 두 내용이 가리키는 커밋이 다르다는 것을 알 수 있다.

### Git Alias

명령을 전부 입력하기 귀찮다면 `git config`를 사용해 **각 명령의 Alias** 를 쉽게 만들 수 있다. 

```
$ git config --global alias.co checkout
$ git config --global alias.br branch
$ git config --global alias.ci commit
$ git config --global alias.st status
```
```
$ git config --global alias.unstage 'reset HEAD --'
아래의 두 명령은 동일하게 된다.

$ git unstage fileA
$ git reset HEAD fileA
```

최근 커밋 더 쉽게 확인
```
$ git config --global alias.las 'log -1 HEAD'
```


새로운 명령 만들기

```
gitk 실행하는 명령

$ git config --global alias.visual '!gitk'
```

**`!`** 를 제일 앞에 추가하면 외부 명령을 실행한다.   
(커스텀 스크립트 사용시 유용)
