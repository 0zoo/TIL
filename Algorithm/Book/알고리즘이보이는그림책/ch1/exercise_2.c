#include <stdio.h>

int main(){
	int a, b = 1;
    
    // a = 0, b = 1
    a = b++; // a = 1, b = 2
    a = ++b; // a = 3, b = 3
    
    a = a++ + ++b; // a = 8 ? 7 , b = 4
    // 컴파일러마다 결과가 다름.

    a = ++a + b++; // a = 13, b = 5
    
    printf("a = %d, b = %d\n", a, b);
}
