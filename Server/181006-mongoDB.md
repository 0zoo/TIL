

Certification Manager  
Route53  
ELB  
EC2  

------

### MongoDB amazon linux2 설치하기


yum install으로 설치하는 방법은 그렇게 추천하지 않는다. 이전 버전을 지원하지 않을 가능성이 있기 때문. (아마존 리눅스2는 최신 버전으로 지원해줌.)


rpm package를 사용하자!  

RPM 패키지 매니저(RPM Package Manager ← Red Hat Package Manager)  
RPM을 사용하면 각종 소프트웨어의 설치 및 업데이트를 굉장히 편리하게 할 수 있다.


- Application과 Program 차이?  
    - 리소스
    - Program - 실행 가능한 프로그램
    - Java - Jar(zip)  
    - Windows - MSI  
    - Application - DMG
    - Debian - deb
    - Redhat - rpm
    - 의존하고 있는 파일들을 하나의 꾸러미로 묶은..



https://docs.mongodb.com/manual/tutorial/install-mongodb-on-amazon/


```
$ sudo vi /etc/yum.repos.d/mongodb-org-4.0.repo
```

도큐먼트 복붙하기

`/etc/...`는 리눅스의 모든 설정 파일들을 모아놓은 폴더.  
-> 누구나 접근 x. 루트에서만 접근 가능하기 때문에 sudo를 붙여야 한다.


```
$ sudo yum install -y mongodb-org
```


- 데이터베이스 
    - RDB (관계형 데이터베이스)
        - ACID : 데이터베이스 트랜잭션이 안전하게 수행된다는 것을 보장하기 위한 성질을 가리키는 약어
            - 원자성(Atomicity)
            - 일관성(Consistency)
            - 고립성(Isolation)
            - 지속성(Durability)
    - NoSQL (Not only SQL)  
    : 트랜잭션에 대한 기능들이 빈약하다는 단점 있음.

- Transaction: 데이터베이스의 상태를 변화시키기 해서 수행하는 작업의 단위

```
// 현재 운영체제가 가지고 있는 설정값
$ ulimit -a
```
![](https://user-images.githubusercontent.com/38287485/46797592-8841e180-cd8a-11e8-9955-e0b5bd9d8a7e.png)

- open files : 
프로세스가 가질 수 있는 소켓 포함 파일 개수

open files 변경하기 

아마존 리눅스는 별도의 설정 파일로 만들어놨기 때문에 해당 설정 파일에서 수정해야 한다.

```
$ sudo vi /etc/security/limits.conf
```
여기서 #은 주석

```
*                soft    fsize           unlimited
*                hard    fsize           unlimited
*                soft    cpu             unlimited
*                hard    cpu             unlimited
*                soft    as              unlimited
*                hard    as              unlimited
*                soft    memlock         unlimited
*                hard    memlock         unlimited
*                soft    nofile          64000
*                hard    nofile          64000
*                soft    nproc           64000
*                hard    nproc           64000
```

변경하고 무조건 재부팅

```
$ sudo reboot
```

설정값을 다시 확인해보면 open files의 값이 변경된 것을 확인할 수 있다.

![](https://user-images.githubusercontent.com/38287485/46798263-6cd7d600-cd8c-11e8-871e-417654bb4f64.png)




-------

### MongoDB 클라이언트 설치하기

mongoDB compass를 사용할 것.

```
$ brew cask install mongodb-compass
```

mongoDB compass를 실행.

Hostname : 인스턴스의 Public IP  
Port : 27017

이렇게만 설정하고 커넥트하면 안됨. 인스턴스에 접근할 수 없다.  

-> AWS의 보안그룹에 가서 새로운 보안 그룹을 생성해 포트를 열어줘야 한다.

![](https://user-images.githubusercontent.com/38287485/46799016-9691fc80-cd8e-11e8-805c-914ba0814e9a.png)


EC2 인스턴스에도 설정에서 작업 -> 네트워킹 -> 보안그룹 변경으로  
몽고디비 보안그룹을 추가해준다.


ec2로 가서 

```
$ sudo vi /etc/mongod.conf
```

![](https://user-images.githubusercontent.com/38287485/46799611-2b492a00-cd90-11e8-9911-d48f08855811.png)

bindIP가  127.0.0.1이면 자신만 접속할 수 있기 때문에 

![](https://user-images.githubusercontent.com/38287485/46799613-2d12ed80-cd90-11e8-970d-353f0866eb6b.png)

0.0.0.0으로 변경해야 한다.

다음에는 **재구동**해야 함.

```
$ sudo systemctl restart mongod
```

```
$ sudo systemctl start mongod
```

![](https://user-images.githubusercontent.com/38287485/46799755-a01c6400-cd90-11e8-8f5f-3c701cd6869e.png)


---------


이제 할 것:

1. 포트 바꾸기 
2. 아이디 비밀번호 설정하기


### 포트 바꾸기 

27000으로 바꿔보자.

1. 클라이언트 포트 변경

2. `$ sudo vi /etc/mongod.conf` 포트 변경하고 재구동

3. 보안그룹 인바운드 포트 범위 변경
![](https://user-images.githubusercontent.com/38287485/46800165-d0183700-cd91-11e8-8fff-74e334140251.png)


### 아이디, 비밀번호 설정하기



```
$ mongo --port 27000
```

(ctrl + D 는 EOF)


```
> use admin
```
```
> db.createUser({
    user: "admin",
    pwd: "linux123",
    roles: ["root"]
    })
```
> Successfully added user: { "user" : "admin", "roles" : [ "root" ] }


나가서 mongod.conf에서 #sequrity의 앞에 주석 제거해주고,

![](https://user-images.githubusercontent.com/38287485/46801283-32bf0200-cd95-11e8-803d-5c9bc387e085.png)

변경하고 재구동.

- 루트 계정(admin)은 되도록이면 안쓰는 것이 좋다.  
- 접근 권한을 상세하게 분리해서 사용하자.


```
$ mongo --port 27000 -u admin -p linux123 --authenticationDatabase 'admin'
```

```
> use hello
```
```
> db.createUser({ user: "hello", pwd: "linux123", roles: ["dbOwner"] })
```

![](https://user-images.githubusercontent.com/38287485/46801656-3c953500-cd96-11e8-917c-0b3c961d0668.png)

클라이언트 종료 후 재접속해야 됨.

여기까지 데이터베이스 준비 완료!

![](https://user-images.githubusercontent.com/38287485/46801911-e2e13a80-cd96-11e8-8648-e395307ff9f2.png)

-----------

## node

todolist-server 폴더 만들고




