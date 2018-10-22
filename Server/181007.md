nodemon 설치하기
```
$ npm install -g nodemon
```

koa-logger 지워주기

```
$ sudo npm remove koa-logger
```

버니언을 통해 리스폰스와 리퀘스트를 핸들러하는 코드를 만들것.

## 동기, 비동기

Node.js  - 비동기 기반의 프레임워크

동기(Synchronous) vs 비동기(Asynchronous)

- 동기(Synchronous)    
`A = foo()`
    - 장점: 간단하고 직관적이다.
    - 단점: 이벤트가 완료될 때까지 무한정 대기해야 한다. CPU가 놀고 있다. (비효율적)  

- 비동기(Asynchronous)  
`afoo(onComplete(a) {...})`  
    - 장점: 자원을 효율적으로 사용할 수 있다. 기다리지 않고 다음 작업을 수행.
    - 단점: 사건의 완료 시점에 반환값을 바로 받는 것이 아니고 별도의 작업을 해줘야 한다.  
    (통보받는 매커니즘_callback)



```javascript
// 동기
// 코드의 시퀀스가 분명하게 보인다.
let data = fs.readFileSync("index.js");
console.log("data: " + data);

data = fs.readFileSync("logger.js");
console.log("data: " + data);
```

```javascript
// 비동기
fs.readFile("index.js", function (err, data) {
  if (err) {
    console.err("readFile failed");
    return;
  }

  fs.readFile("logger.js", function (err, data) {
    if (err) {
      console.err("readFile failed");
      return;
    }

    console.log("read data: " + data);
  });

  console.log("read data: " + data);
});


// callback hell
setTimeout(function () {
  console.log("1초가 지났습니다.");
  setTimeout(function () {
    console.log("2초가 지났습니다.");
    setTimeout(function () {
      console.log("3초가 지났습니다.");
    }, 3000);
  }, 2000);
}, 1000);
```

비동기 처리시 문제점
1. 에러 처리 로직은 동일한데 반복해서 처리해줘야 하는 **보일러 플레이트** 발생한다.
2. flow가 한눈에 보이지 않는다.

node.js는 이런 부분들을 성능이 더 좋은 비동기 방식으로 처리하는 것을 원함.  
 -> **Promise** 패턴을 사용하자.

> **Promises** are a more abstract pattern of working with async code in JavaScript.

https://nooheat.github.io/Node.js-Callback-Hell%EC%9D%80-NEVER!-Promise%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%98%EC%9E%90/

```javascript
// Promise (Future) 
function readFile(path) {
  return new Promise((resolve, reject) => {
    fs.readFile(path, (err, data) => {
      if (err) {
        reject(err);
      }
      resolve(data);
    });
  });
}

readFile("index.js")
    .then(function (data) {
        console.log(data);
    })
   .catch(function (err) {
       console.error(err);
    });
```

```javascript
// 배열로 묶어서 한 번에 처리
const jobs = [
  readFile("index.js"),
  readFile("logger.js"),
];

Promise.all(jobs)
  .then(function (data1, data2) {
    console.log("" + data1);
    console.log("" + data2);
  })
  .catch(function (err) {
    console.error(err);
  });
```

async/await으로 더 효과적으로 처리하기

```javascript
// async/await
async function foo() {
  const data1 = await readFile("index.js");
  const data2 = await readFile("logger.js");

  console.log(data1);
  console.log(data2);
}
```
await은 무조건 사용할 수 있는 것이 아니다.  
async 함수만 사용 가능.

- arrow function (람다)  
- 클로저(closure): 자신을 둘러싼 context 내의 변수에 접근 가능. 외부 범위의 변수를 함수 내부로 바인딩


------

핸들러를 async로 만들어보자.

```javascript
// logHandler.js

const reqSerializer = (ctx = {}) => {
    return {
        method: ctx.method,
        path: ctx.path,
        url: ctx.url,
        headers: ctx.headers,
        protocol: ctx.protocol,
        ip: ctx.ip,
        query: ctx.query,
        body: ctx.request.body,
    };
}  
const resSerializer = function (ctx = {}) {
    return {
        statusCode: ctx.status,
        responseTime: ctx.responseTime,
        type: ctx.type,
        headers: (ctx.response || {}).headers,
        body: ctx.body,
    };
}

const log = function({
    logger = null 
} = {}){ 
    if(_.isNil(logger)){        
        throw Error("Logger is required");
    }
    return async(ctx, next) => {
        ctx.log = logger;
        ctx.log.addSerializers({
            req: reqSerializer,
            res: resSerializer,
            err: bunyan.stdSerializers.err,
        });

        ctx.log.info({
            req: ctx,
            event: "request",
        });

        await next();

        ctx.log.info({
            res: ctx,
            event: "response",
        });
    };
}

module.exports = log;
```

작동은 잘 되지만, 에러 처리하는 방법이 아쉽다.  
request가 들어오는 시점에 로깅을 하고  
response가 나가는 시점에 로깅을 하는 것으로 변경해주자.  
-> 내부적으로 에러가 발생해도 request 정보가 로깅에 나온다.

```javascript
return async(ctx, next) => {
        ctx.log = logger;
        ctx.log.addSerializers({
            req: reqSerializer,
            res: resSerializer,
            err: bunyan.stdSerializers.err,
        });

        ctx.log.info({
            req: ctx,
            event: "request",
        });

        try {
            await next();
            ctx.log.info({
              res: ctx,
              event: "response",
            });
        } catch (err) {
            ctx.log.error({
              err,
              event: "error",
            });
            throw err;
        }
    };
```

```
{"name":"todolist-server","hostname":"yeongjujjangjjang-ui-MacBook-Pro.local","pid":26714,"level":30,"req":{"method":"GET","path":"/","url":"/","headers":{"host":"127.0.0.1:3000","connection":"keep-alive","cache-control":"max-age=0","upgrade-insecure-requests":"1","user-agent":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36","accept":"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8","accept-encoding":"gzip, deflate, br","accept-language":"ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7"},"protocol":"http","ip":"::ffff:127.0.0.1","query":{}},"event":"request","msg":"","time":"2018-10-19T11:25:46.619Z","v":0}
```

이런 로그를 더 깔끔하게 보여주려면 

- 파이프(`|`) : 앞에 있는 프로세스의 표준 출력을 뒤에 있는 표준 입력의 프로세스로 보내줌.
```
$ npm start | npx bunyan
```

실제 개발할 때는 $npm start를 사용할 것이기 때문에
package.json에 추가해주자.
```json
"scripts": {
    "start": "nodemon server.js | bunyan",
    "test": "echo \"Error: no test specified\" && exit 1"
  }
```

```
[2018-10-19T11:28:40.970Z]  INFO: todolist-server/26732 on yeongjujjangjjang-ui-MacBook-Pro.local:  (event=request, req.path=/, req.protocol=http, req.ip=::ffff:127.0.0.1, req.query={})
    GET / HTTP/1.1
    host: 127.0.0.1:3000
    connection: keep-alive
    cache-control: max-age=0
    upgrade-insecure-requests: 1
    user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36
    accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
    accept-encoding: gzip, deflate, br
    accept-language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
[2018-10-19T11:28:40.971Z]  INFO: todolist-server/26732 on yeongjujjangjjang-ui-MacBook-Pro.local:  (event=response, res.type=text/plain)
    HTTP/1.1 200 OK
    content-type: text/plain; charset=utf-8
    content-length: 19
    
    Hello, TODO Service
```

response time은 내가 직접 측정해야 한다.

```
$ npm i humanize-number
```

```javascript
const humanize = require('humanize-number');

function time(start) {
    const delta = Date.now() - start
    return humanize(delta < 10000 ?
        delta + 'ms' :
        Math.round(delta / 1000) + 's')
}
...

    try {
        const startTime = new Date();
        await next();
        ctx.responseTime = time(startTime);
        ...
    }
...
```

--------

## mongoose

데이터베이스에 접속해서 데이터를 저장하고 로드하는 연산을 수행하는 클라이언트

```
$ npm i mongoose
```

```javascript
// javascript의 함수를 정의하는 3가지 방법
//  => 호이스팅

// 1.
function setupDatabase(){}
// 이 방법은 별로 권장하지 않음.

// 2.
const setupDatabase = function(){}

// 3.
const setupDatabase = () => {}
```

자바스크립트의 호이스팅?


















