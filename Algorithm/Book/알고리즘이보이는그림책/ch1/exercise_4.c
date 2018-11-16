#include <stdio.h>

int main(){
    FILE *fp;
    char a[] = "hello, world.\n";
    char s[30];

    fp = fopen("abc.txt", "a"); // 파일 추가해서 쓰기 모드
    if(fp==NULL) 
        return -1;
    fprintf(fp, "%s", a);
    fclose(fp);

    fp = fopen("abc.txt", "r");
    if(fp==NULL)
        return -1;
    
    char buf[1024];
    while(fgets(buf, sizeof(buf), fp) != NULL){
        printf("%s", buf);
    }
    fclose(fp);

}
