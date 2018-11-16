#include <stdio.h>

int main(){
	int _abc, abc, abc1;
	int a, b, tmp;
	int num[2];

	printf("비교할 두 수를 입력하세요.\n");
	scanf("%d %d", &a, &b);

	if(a>b){
		printf("%d는 %d보다 크다.\n", a, b);
		tmp = a;
		a = b;
		b = tmp;
	}else{
		printf("%d는 %d보다 크지 않다.\n", a, b);
	}

	num[0]=a;
	num[1]=b;
	
	printf("%d < %d = True\n", num[0], num[1]);
}
