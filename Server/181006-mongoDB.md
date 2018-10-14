

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

```
$ npm init
```

미들웨어로 사용할 koa 설치하기

```
$ npm i koa
```

```javascript
// require: Java의 import와 동일한 개념 
const Koa = require("koa");
// 대문자와 소문자 구별하기.
// 대문자는 객체 또는 클래스
// 소문자는 이미 생성된 인스턴스
```

- 컴파일 언어:  
    - C, C++, Java, C#  
    - Source -> Binary  
    - 성능 저하 없음. 진입 장벽이 있음.
- 스크립트 언어:  
    - 컴파일 단계가 존재하지 않음.  
    - Runtime이라고 부르는 스크립트 해석기  
    => 인터프리터  
    인터프리터가 코드를 실시간으로 해석해서 수행한다.  
    - line by line으로 인해 성능 저하가 있음.  
    - 최적화 방법: hotspot (성능적으로 cpu를 많이 잡아먹는 부분은 미리 컴파일하는 방법)

Javascript - Web Client

**Node.js**
  : 비동기 기반의 프레임워크  
  Javascript를 브라우저가 아닌 네이티브(Windows, Linux, Mac)에서 수행할 수 있도록 해주는 플랫폼.

- WAS Middleware: express, koa, restify, Hapi, ...

레트로핏의 장점? 보일러플레이트를 관리해준다.

```javascript
// server.js
const Koa = require('koa');
const Router = require('koa-router');

const app = new Koa();
const router = new Router();

router.get('/', function(ctx){
    ctx.body = "hello world"
});

app.use(router.routes())
    .use(router.allowedMethods());

app.listen(3000);
```

```
$ npm i koa-logger
```

```javascript
const logger = require('koa-logger');

app.use(logger());
// 미들웨어들끼리 충돌할 수 있어
// 순서가 중요하다.
// app.use(router.routes()) 얘보다 위에 있어야 함.
```

logger.js 파일 추가하고,
middlewares 디렉토리를 만들자.

```
$ npm i bunyan
```

```javascript
// logger.js

"use strict"
// 자바스크립트가 가지고 있는 표준 제약을 사용하겠다.
// 일관성을 가지도록

const bunyan = require("bunyan");
const name = "todolist-server";

const config = { 
// const config = require("./config/logger");
// require은 해당 파일에서 모듈의 export를 통해 객체를 가져오는 것.
// 파일을 따로 안만들고 config를 그대로 받아오는 형태로 바로 사용하겠다는 의미.
    name, // name: name (더 직관적으로 보이도록 생략 가능.)
    streams: [{
        type: "stream",
        stream: process.stdout,
        level: "debug"
    }]
};

const options = {
    ...config, // flatmap 처럼 작용. 
    serializers: bunyan.stdSerializers, 
    // bunyan.stdSerializers: 
    // bunyan이 제공하는 request, response를 내부적으로 어떻게 처리할지를 결정해주는 기능.
};

const logger = bunyan.createLogger(options);
module.exports = logger;
// 모듈의 exports에 로거를 등록하면 
// 외부에서 require를 통해 등록된 로거를 가져올 수 있다.
```

```javascript
// server.js

const Koa = require('koa');
const Router = require('koa-router');

// const logger = require('koa-logger')
const logger = require('./logger'); // ./logger.js

const app = new Koa();
const router = new Router();

const logHandler = require("./middlewares/logHandler");

app.use(logHandler({
  logger,
}));

router.get('/', function(ctx){
    ctx.body = "Hello, TODO Service"
});

// app.use(logger())
// 는 이제 사용 불가.
// 미들웨어가 제공하는 기본 형식을 따라야한다.
// 인터페이스를 암묵적으로 약속한다.
// => 별도의 logHandler를 만들어 사용한다.

app.use(router.routes())
    .use(router.allowedMethods());

app.listen(3000);
```

```javascript
// logHandler.js
"use strict"

const bunyan = require('bunyan');
const _ = require('lodash');

const log = function({
    logger = null  // logger의 디폴트값 null
} = {}){ 
    // ==(객체 동등성), ===(참조 동등성)
    // 암묵적 형변환으로 인한 자바스크립트의 삼위일체 문제 
    // undefined의 위험성을 방지하기 위해서 lodash를 사용하자.
    if(_.isNil(logger)){
    // _.isNil(value): Checks if value is null or undefined.
        throw Error("Logger is required");
    }
    return function(ctx, next){ 
        // ctx: request, response 정보.
        // next: 다음 미들웨어로 pass 또는 block할 수 있도록 하는 것.
        // 미들웨어는 요청이 들어올때마다 자동적으로 호출되기 때문에 보일러플레이트를 줄일 수 있음.
        logger.info("hello");
        next();
    };
    // 함수를 반환하는 함수: 고차함수
}

module.exports = log;
```

- **AOP**(Aspect Oriented Programming)
    - 관점(관심) 지향 프로그래밍
    - 공통모듈(트랜잭션/로그/보안/인증 처리 등)을 만든 후에 코드 밖에서 이 모듈을 비지니스 로직에 삽입하는 것









