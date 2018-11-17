#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <string.h>

int main(){
    
    srand(time(NULL));
    
    char words[][10] = {"앞면", "뒷면"};
    int user;
    int coin;

    while(1){
        printf("앞면은 1 뒷면은 2를 입력하시오.\n");

        scanf("%d",&user);
        
        if(user<1 || user>2)
            break;
        else{
            coin = rand()%2 + 1;
            printf("user: %s, coin: %s\n", words[user-1], words[coin-1]);
            printf("%s\n", (user==coin) ? "정답\n": "땡\n" );

        }
    }
}
