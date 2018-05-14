# 1번째 퍼즐 - 홀수 확인 

## Question
~~~ java

    public boolean isOdd (int i){
        return i%2 == 1;
    }

~~~

홀수인지 확인하는 위 메소드는 **문제점**이 있다. 
매개변수 i에 음수값이 들어 온다면 항상 **false**를 리턴함.

`자바의 나머지 연산의 반환 값은 왼쪽 피연산자의 부호와 같다`

## Solution

~~~ java
    public boolean isOdd (int i){
        return i % 2 != 0;
    }
~~~

~~~ java
    //비트연산자가 속도 측면에서 훨씬 좋다.
    public boolean isOdd (int i){
        return (i & 1) != 0;
    }
~~~


# 2번째 퍼즐 - 변화를 위한 시간

## Question
~~~ java
    public class Change{
        
        public static void main(String args[]){    
            System.out.println(2.00-1.10);
        }
    }
~~~

0.9가 출력되기를 원하지만, 실제 실행 결과는 0.8999999999999999가 나온다.
자바는 1.1을 정확한 1.1로 표현할 수 없다. 

`자바는 이진 부동소수점 연산을 사용한다.`


## Solution

**BigDecimal(String)** 을 사용하자. (단, BigDecimal(double) 절대 사용 x)
~~~ java
    public class Change{
        
        public static void main(String args[]){    
            
            System.out.println(
                new BigDecimal("2.00").substract(new BigDecimal("1.10")));
        }
    }
~~~


# 3번째 퍼즐 - Long 자료형 나눗셈

## Question



