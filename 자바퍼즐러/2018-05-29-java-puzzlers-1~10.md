# 자바 퍼즐러 1~10

## 1번째 퍼즐 - 홀수 확인
### Question
```java
    public boolean isOdd (int i){
			//홀수인지 확인하는 메소드
        return i%2 == 1;
    }
```

매개변수 i에 음수값이 들어 온다면 항상 false를 반환.
자바의 __나머지 연산의 반환 값__은 __왼쪽 피연산자의 부호__와 같다.

### Solution
```java
    public boolean isOdd (int i){
        return i % 2 != 0;
    }
```

```java
    //비트연산자가 속도 측면에서 훨씬 좋다.
    public boolean isOdd (int i){
        return (i & 1) != 0;
    }
```


## 2번째 퍼즐 - 변화를 위한 시간
### Question
```java
    public class Change{
        public static void main(String args[]){    
            System.out.println(2.00-1.10);
        }
    }
```

0.9가 출력되기를 원하지만, 실제 실행 결과는 0.8999999999999999가 나온다.
자바는 정확한 1.1을 표현할 수 없다.
자바는 __이진 부동소수점__ 연산을 사용한다.

### Solution
__BigDecimal(String)__ 을 사용하자. (단, BigDecimal(double) 절대 사용 x)
```java
    public class Change{
        public static void main(String args[]){    
            System.out.println(
                new BigDecimal("2.00").substract(new BigDecimal("1.10")));
        }
    }
```


## 3번째 퍼즐 - Long 자료형 나눗셈
### Question
```java
    public class LongDivision{
        public static void main(String args[]){
				final long MICROS_PER_DAY = 24*60*60*1000*1000;
				final long MILLIS_PER_DAY = 24*60*60*1000;    
            System.out.println(MICROS_PER_DAY/MILLIS_PER_DAY);
        }
    }
```

1000이 출력되는 것을 기대하지만, MICROS_PER_DAY에서 __오버플로우__가 발생하여 원하는 결과값이 나오지 않는다.

__자바는 타깃 타이핑을 지원하지 않는다.__

- _타깃 타이핑 (Target Typing)_ :
	long = int * int 의 연산을 실행한다면 결과값의 자료형에 맞춰서 int형을 long 타입으로 변환하여 연산해주는 것.

### Solution
```java
    public class LongDivision{
        public static void main(String args[]){
				final long MICROS_PER_DAY = 24L*60*60*1000*1000;
				final long MILLIS_PER_DAY = 24L*60*60*1000;    
            System.out.println(MICROS_PER_DAY/MILLIS_PER_DAY);
        }
    }
```

변수의 자료형이 크다고 해서 오버플로우가 발생하지 않는 것은 아니다.
큰 숫자를 다룰 경우에는 항상 오버플로우를 주의하고, 내부에서 사용하는 변수의 자료형을 확인하자.


## 4번째 퍼즐 - 초등학교 수준의 문제
### Question
```java
    public class Elementary{
        public static void main(String args[]){  
            System.out.println( 12345 + 5432l);
        }
    }
```

long형을 쓸 때에는 대문자 L을 사용하자. 
l 과 1은 구분이 힘들 수 있다.

### Solution
```java
    public class Elementary{
        public static void main(String args[]){  
            System.out.println( 12345 + 5432L);
        }
    }
```


## 5번째 퍼즐 - 16진수의 즐거움
### Question
```java
    public class JoyOfHex{
        public static void main(String args[]){  
            System.out.println(
					Long.toHexString(0x100000000L + 0xcafebabe)
				);
        }
    }
```

16진수 2개를 더해 16진수로 출력하는 코드. 
예상 결과 : 1cafebabe 이지만,
실행 결과 : cafebabe 
1 0000 0000 
  cafe babe
33번째 비트가 손실된 것.
long 자료형으로 연산한 것이 아니라 int 자료형으로 연산한 것.

문제점1. 
	16진수와 8진수값은 상위 비트가 정의되면 음수로 인식한다.
	-> 0xcafebabe는 10진수로는 -889275714가 된다.
문제점2.
	int형이 long형으로 변환될 때, 기본 자료형 확장 변환이 일어난다.
	int형은 부호가 있는 자료형이므로 확장 변환이 일어날 때 부호 확장이 함께 일어난다.
	->  0xcafebabe는 long형으로 변환되며  0xffffffffcafebabe L가 된다.
	
최종적으로 0x1000 0000 L + 

	   1111 1111			//64비트를 넘어가는 범위는 사라진다.
	0x ffff ffff cafe babe L
+	0x 0000 0001 0000 0000 L
- - - -
	0x 0000 0000 cafe babe L

### Solution
```java
    public class JoyOfHex{
        public static void main(String args[]){  
            System.out.println(
					Long.toHexString(0x100000000L + 0xcafebabeL)
					//오른쪽 피연산자에도 L을 붙여주어 부호 확장으로 인한 값 변화가 발생하지 않도록 해준다.
				);
        }
    }
```


## 6번째 퍼즐 - 다중 자료형 변환
### Question
3번 연속해서 자료형을 바꾸면 어떻게 될까?
```java
    public class Multicast{
        public static void main(String args[]){    
            System.out.println((int)(char)(byte)-1);
        }
    }
```

결과값 : 65535
예상외의 값이 나온 이유는 **부호 확장**이 일어났기 때문이다.
// 양수값의 부호 확장: 상위 자리를 0으로 채운다.
// 음수값의 부호 확장: 상위 자리를 1로 채운다.

1. int (4byte)(32bit) -> byte (1byte)(32bit)
2. byte (1byte)(8bit) -> char (2byte)(16bit)
3. char (2byte)(16bit) -> int (4byte)(32bit)

자바는 2의 보수법으로 사칙연산을 수행한다.
// 대부분의 프로그래밍 언어에서는 음수를 2의 보수법으로 표현한다.
// 2의 보수법은 각 자리의 수를 모두 반전하고 , 1을 더한 값.

1. int형 -1의 2의 보수 :
	11111111 11111111 11111111 11111111
여기서 1번째 변환인 int -> byte 과정에서 하위 8bit를 제외한 나머지를 버리는 **작은 자료형으로 변환**이 일어남.

byte형 :
	11111111

2. byte형은 부호가 있지만, char형은 부호가 없다.
-> **기본 자료형 확장 후 축소 변환**이 일어난다.
byte형이 int형으로 확장 후 char형으로 축소 변환을 하게 된다.

`원래 값이 부호가 있는 자료형이라면 부호 확장이 일어나고, char자료형은 0의 확장이 일어난다.`

byte -> char 인데 원래 byte값이 음수이기 때문에
1111 1111 -> 1111 1111 1111 1111
상위 자리에 1이 채워짐. 

따라서 byte를 char형으로 변환하게 되면
2^16-1 = 65535 가 나오게 된다.

++ 만약 char자료형을 다른 자료형으로 바꿀 때 부호 확장을 원하지 않으면 **비트 마스크**를 사용하자.

- _비트 마스크(Bit Mask)_ : 원하는 비트값을 알고싶을 경우에 사용
```
	int i = c & 0xffff;
```

### Solution
```java
    public class Multicast{
        public static void main(String args[]){  
				byte b = (byte) -1;
				//char c = (char)b; //부호 확장 o
				char c = (char) (b & 0xffff); //부호 확장 x
				int i = (short)c; // short형은 char형처럼 16bit이면서 부호가 있다.
				System.out.println(i);
        }
    }
```

### 역자 퍼즐
```java
    public class Multicast{
        public static void main(String args[]){  
				System.out.println((long)(int)(byte)-1);
        }
    }
```

결과값: - 1 

1111 1111 1111 1111 1111 1111 1111 1111

1111 1111

1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111

long자료형 : 부호가 있고, 64bit (8byte)


## 7번째 퍼즐 - 변수 교환
### Question
```java
    public class CleverSwap{
        public static void main(String args[]){
				int x = 1984; // 0x7c0
				int y = 2001; // 0x7d1
				x ^= y ^= x ^= y;    
            System.out.println(x);
            System.out.println(y);
        }
    }
```

### Solution








