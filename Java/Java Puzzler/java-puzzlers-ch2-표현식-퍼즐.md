# Chapter2. 표현식 퍼즐

## 1. 홀수 확인
### Question

```kotlin
fun isOdd(i : Int): Boolean = i%2 == 1
```

매개변수 i에 **음수값**이 들어 온다면 **항상 false**를 반환한다.

> **나머지 연산의 반환 값의 부호** == **왼쪽 피연산자의 부호**
>> 3 % 2 => 1    
>> 3 % -2 => 1  
>> -3 % 2 => -1    
>> -3 % -2 => -1  


### Solution

```kotlin
fun isOdd(i : Int): Boolean = i%2 != 0
```
- 비트 AND 연산자(&)가 속도 측면에서 훨씬 좋다.

```kotlin
fun isOdd(i : Int): Boolean = (i and 2) != 0
```

## 2. 변화를 위한 시간
### Question

```kotlin
println(2.00 - 1.10) // 0.8999999999999999
```

자바는 __이진 부동소수점__ 연산을 사용하기 때문에, 정확한 1.1을 표현할 수 없다.

- 정확한 결과값이 필요하다면 float, double 자료형 사용하지 말 것.

### Solution
__BigDecimal(String)__ 을 사용하자.  
(단, BigDecimal(double) 절대 사용 x)

```kotlin
println(BigDecimal("2.00").subtract(BigDecimal("1.10"))) // 0.90
```

## 3. Long 자료형 나눗셈
### Question

```kotlin
val micros :Long = 24*60*60*1000*1000
// warning 발생. 오버플로우

val millis : Long = 24*60*60*1000
println(micros/millis) // 5
```

__자바는 타깃 타이핑을 지원하지 않는다.__

- **타깃 타이핑 (Target Typing)**:  
	long = int * int 의 연산을 실행한다면  
    결과값의 자료형에 맞춰서 int형을 long 타입으로 변환하여 연산해주는 것.

### Solution

```kotlin
val micros : Long = 24L*60*60*1000*1000
```

- **큰 숫자를 다룰 경우**에는 항상 **오버플로우를 주의**하고, **내부에서 사용하는 변수의 자료형을 확인**하자.

- 파이썬과 루비는 타깃 타이핑을 지원해줌.

## 4. 초등학교 수준의 문제

### Question
```java
System.out.println( 12345 + 5432l);
```

- long형을 쓸 때에는 대문자 L을 사용하자. 

### Solution
```java
System.out.println( 12345 + 5432L);
```

## 5. 16진수의 즐거움

### Question
```java
public class JoyOfHex{
    public static void main(String args[]){  
        System.out.println(
            Long.toHexString(0x100000000L + 0xcafebabe));
    }
}
```

16진수 2개를 더해 16진수로 출력하는 코드.

예상 결과 : 1cafebabe 이지만,  
실행 결과 : cafebabe   

`1_0000_0000`     
`__cafe_babe`

 -> 33번째 비트가 손실.

long 자료형으로 연산한 것이 아니라 int 자료형으로 연산해서 문제 발생힘.

문제점  
1. 16진수와 8진수값은 상위 비트가 정의되면 음수로 인식
    - `0xcafebabe`는 10진수로는 `-889275714`가 된다. 

2. int형이 long형으로 변환될 때, 기본 자료형 확장 변환이 일어남  
int형은 부호가 있는 자료형이므로 확장 변환이 일어날 때 **부호 확장**이 함께 일어난다.  
	- `0xcafebabe`는 long형으로 변환되며  `0xffffffffcafebabe L`이 된다.  
	
```
// 64비트를 넘어가는 범위는 사라진다.
	   1111 1111			
	0x ffff ffff cafe babe L
+	0x 0000 0001 0000 0000 L
------------------------------------
	0x 0000 0000 cafe babe L
```

### Solution

```java
public class JoyOfHex{
    public static void main(String args[]){  
        System.out.println(
            Long.toHexString(0x100000000L + 0xcafebabeL));
        // 오른쪽 피연산자에도 L을 붙여주어 
        // 부호 확장으로 인한 값 변화가 발생하지 않도록 해준다.
    }
}
```

## 6. 다중 자료형 변환

### Question
3번 연속해서 자료형을 바꾸면 어떻게 될까?

```java
public class Multicast{
    public static void main(String args[]){    
        System.out.println((int)(char)(byte)-1);
    }
}

// 결과값 : 65535
```

예상외의 값이 나온 이유는 **부호 확장**이 일어났기 때문이다.  
- 양수값의 부호 확장: 상위 자리를 0으로 채운다.  
- 음수값의 부호 확장: 상위 자리를 1로 채운다.  


> int (32bit) -> byte (32bit)  
byte (8bit) -> char (16bit)  
char (16bit) -> int (32bit)  

음수를 **2의 보수법**으로 표현한다.   
(2의 보수법: 각 자리의 수를 모두 반전하고 1을 더한 값.)  

1. `int형 -1의 2의 보수` :  
`11111111 11111111 11111111 11111111`   
여기서 첫번째 변환인 int -> byte 과정에서  
하위 8bit를 제외한 나머지를 버리는  
**작은 자료형으로 변환**이 일어남.  
`byte형` :  
	`11111111`

2. byte형은 부호가 있지만, char형은 부호가 없다.  
-> **기본 자료형 확장 후 축소 변환**이 일어난다.  
byte형이 int형으로 확장 후 char형으로 축소 변환을 하게 된다.

> 원래 값이 부호가 있는 자료형이라면 부호 확장이 일어나고,  
char자료형은 0의 확장이 일어난다.


byte -> char 에서 원래 byte값이 음수이기 때문에  
1111 1111 -> 1111 1111 1111 1111  
상위 자리에 1이 채워짐.   

따라서 byte를 char형으로 변환하게 되면  
2^16-1 = 65535 가 나오게 된다.

- 만약 char자료형을 다른 자료형으로 바꿀 때 부호 확장을 원하지 않으면 **비트 마스크**를 사용하자.

    - 비트 마스크(Bit Mask) : 원하는 비트값을 알고 싶을 경우에 사용
    
    ```java
    int i = c & 0xffff;
    ```

### Solution

```java
public class Multicast{
    public static void main(String args[]){
        byte b = (byte) -1;
        //char c = (char)b; //부호 확장 o
        char c = (char) (b & 0xffff); //부호 확장 x
        int i = (short)c; 
        // short형은 char형처럼 16bit이면서 부호가 있다.
        System.out.println(i);
    }
}
```

### 역자 퍼즐
```java
public class Multicast{
    public static void main(String args[]{
        System.out.println((long)(int)(byte)-1);
    }
}
// 결과값: - 1 
```

1111 1111 1111 1111 1111 1111 1111 1111

1111 1111

1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111

long자료형 : 부호가 있고, 64bit (8byte)


## 7. 변수 교환

### Question
```java
public class CleverSwap{
    public static void main(String args[]){
        int x = 1984; // 0x7c0
        int y = 2001; // 0x7d1
        x ^= y ^= x ^= y;
        System.out.println(x);
        System.out.println(y);
        // x = 0
        // y = 1984
    }
}
```

```
// 추가적인 변수 없이 두 변수를 교환하는 방법
// 절대 사용하지 말 것!
x = x ^ y;
y = y ^ x;
x = x ^ y;
```

### Solution

- 하나의 표현식에 동일한 변수를 여러 번 할당하지 말자.

```java
int tmp = x;
x = y;
y = tmp;
```

## 8. Dos Equis
### Question
```java
char x = 'X';
int i = 0;

System.out.print(true ? x:0);
System.out.print(false ? i:x);

// X88
```

여러 자료형을 혼합해서 계산하면 매우 복잡하다.

마지막에 88로 출력된 이유? int 자료형 타입으로 출력되었기 때문.

- 조건연산자의 최종 자료형을 결정하는 규칙
    1. byte, short, char 자료형을 T자료형이라고 하면,  
    피연산자 중 하나가 T 자료형이고 다른 하나가 T 자료형으로 변환 가능한 int 상수라면 T 자료형으로 결과를 낸다.

    2. 만약 이것이 아니라면 `이항 숫자 확산(binary numeric promotion)`을 적용하고, 더 큰 자료형으로 결과를 도출함.

`true ? x:0`의 경우는 **1번 조건**에 해당하여, 결과값이 char 타입으로 결정되었고,  
`false ? i:x`는 i가 변수이기 때문에 **2번 조건**에 해당하여, 결과값이 int 타입으로 결정된 것이다.


### Solution

```java
final int i = 0;
```
i를 final로 선언하여 상수로 만들어 준다.

- 조건 연산자를 사용할 때는,  
두번째와 세번째 피연산자의 자료형을 일치시키고 사용할 것! 

## 9. 같은 것 같으면서도 다른 것 (1)

### Question

`x += i` 는 정상 컴파일,  
`x = x + i`는 컴파일 에러가 발생하도록  
변수 x와 i의 선언문 작성해보자.

### Solution

> `x += i`와 `x = x + i`는 모두 할당 표현식.
>> `x = x + i`는 단순 할당 연산자(=)를 사용하지만,  
>> `x += i`는 복합 할당 연산자(+=)를 사용하는 차이점.

- 복합 할당 연산자는 연산 결과를 왼쪽 변수의 자료형으로 자동 변환한다.  
    - `T`가 `E1`의 자료형일 때,
    - `E1 = (T)( E1 op E2 )`

연산 결과가  왼쪽 자료형보다 커지면 기본 자료형 축소가 일어나지만,  
단순 할당의 경우에는, 컴파일 에러가 발생함.

```java
short x = 0;
int i = 123456;

x += i; // 자동 자료형 변환 발생
// 결과값 : -7616
// int 자료형이 더 크기 때문에 over flow가 발생함.

x = x + i; // 컴파일 에러 발생
```

- byte, short, char 자료형의 변수에 복합 할당 연산자 사용하지 말 것!

> 복합 할당 연산자는 **자동 자료형 변환**이 일어난다.

## 10. 같은 것 같으면서도 다른 것 (2)
### Question

`x = x + i` 는 정상 컴파일,  
`x += i`는 컴파일 에러가 발생하도록  
변수 x와 i의 선언문 작성해보자.

### Solution

딱 한가지 방법에서는 단순 할당 연산자가 더 유연하다.

+= 연산자의 왼쪽이 String 타입이라면 오른쪽에 모든 자료형이 올 수 있다.  

```java
Object x = "Buy";

String i = " banana";

x = x + i;

x += i;
```


?? intellij 로 해봤는데 둘 다 컴파일 됨!

