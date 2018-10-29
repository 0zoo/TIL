동기와 비동기
동기: 결과가 바로 반환됨.
비동기: 1. callback 2. 반환값 Promise<T>

```javascript
ctx.body = {
          result : "ok",
          data: data
      }
```

이런 방식은 굉장히 불편하다..
status code로 성공 여부를 알 수 있기 때문.

ctx.body = data;

정규표현식 Regex

비밀번호 
6글자 이상
반드시 영문자 + 숫자
특수문자



HTTP 는 비암호화 통신
-> HTTPS를 사용해야 한다.


자바스크립트의 spread. 문법
…부분은 그대로 들어가고
내가 원하는 부분만 덮어씌여서 들어갈 수 있다.

```javascript
async handler(ctx) {
        /*

        // 1. 해당하는 사용자가 이미 존재하는지 확인
        const {
            email,
            password,
        } = ctx.request.body

        const exist = await User.findOne({
            email
        }).exec();

        if(!_.isNil(exist)){
            throw new ClientError("Already exist email")
        }
        // 2. 비밀번호를 암호화해서 저장
        const passwordHash = await bcrypt.hash(password, 10);
        const user = new User({
            ...ctx.request.body,
            password : passwordHash
        })

        const data = (await user.save()).toObject();
        delete data.password;

        ctx.body = data;

        //const user = new User(ctx.request.body)
        // 기호에 맞게 원하는 방식으로 사용
        // 1.
        // const data = await user.save()
        // ctx.body = data;
        // 2.
        // await user.save();
        // ctx.body = user

        */

        // 위의 로직은 모델이 처리해주게 해줘야 한다.
        ctx.body = await User.signUp(ctx.request.body);
    }
```

———

update할 때 
1. 필요한 부분만 업데이트
2. save가 아닌 다른 방식으로 저장



HTTP의 특징: 상태가 없다.  
HTTP - connection - less  
=> stateless

요청온 사람이 누군지 나는 모름 그래서 토큰이 필요한 것.
헤더의 토큰을 통해서 서버가 누구의 요청인가를 구분한다.

로그인 순서
1. 유저 로그인 요청
2. 비밀번호 검증
3. 토큰 발급
4. 데이터베이스 저장 (CREATE)

-> 그래서 로그인은 POST로 만든다.
(로그아웃은 GET - 데이터베이스에서 토큰 삭제)


메모리 속도가 빠른 순서 
캐시 - 메모리 - SSD(HDD) - 네트워크 스토리지(데이터베이스)
노드js는 파일 저장 디폴트가 SSD. 서버에서 스케일 아웃할 경우 문제가 생긴다. 
-> 세션 저장은 Memory DB를 사용하는 것이 좋다. (Redis, )

JSON Web Token 방식을 사용하면 레디스같은 메모리 디비를 구축할 필요가 없다.
JWT : JSON -> Token -> JSON


```
$ npm i jsonwebtoken
```