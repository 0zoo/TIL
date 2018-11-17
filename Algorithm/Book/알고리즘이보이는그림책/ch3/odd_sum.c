#include <stdio.h>

int main(){
    int n=0;
    int sum=0;

    printf("숫자 입력\n");
    scanf("%d", &n);
    
    for(int i = 1; i<=n; i+=2){
        sum += i;
        printf("%d\n",sum);
    }

    printf("1부터 %d까지 홀수의 합은 %d \n", n, sum);
    
}
