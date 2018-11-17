#include <stdio.h>
#include <time.h>
#include <stdlib.h>
 
int main(){
    /*
        컴퓨터와 사용자가 가위바위보를 5번 하고 
        이긴 횟수를 보여준다.

        1. 난수를 사용해 컴퓨터의 손을 정한다.
        2. 사용자의 손을 입력받는다.
        3. 누가 이겼는지 출력
        4. 최종 승자를 출력
    */
    srand(time(NULL));
    int userRecord[3] = {0,0,0};
    char words[][10] = {"보", "가위", "바위"}; 
        
    for(int i=0; i<5; i++){
        int computer = rand()%3;
        int user = -1;
        printf("가위:1, 바위:2, 보:0\n");
        do{
            scanf("%d", &user);
        }while(user!=1&&user!=2&&user!=0);

        printf("user:%s, computer:%s\n", words[user], words[computer]);
        
        if(user==computer){
            userRecord[1]++;
        }else{
            if((user+2)%3==computer) userRecord[0]++;
            else userRecord[2]++;
        }
    }
    printf("%d승 %d무 %d패\n", userRecord[0], userRecord[1], userRecord[2]);
    if(userRecord[0]>userRecord[2])
        printf("승\n");
    else
        printf("패\n");
}
// 1 - 2 lose 
// 1 - 0 win 
// 2 - 1 win 
// 2 - 0 lose
// 0 - 1 lose
// 0 - 2 win 


