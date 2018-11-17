#include <stdio.h>

int main(){

    FILE *fp;
    char s[256];
    int i = 1;
    
    fp = fopen("abc.txt", "r");
    if(fp==NULL)
        return -1;

    while(feof(fp)==0){
        fgets(s, 255, fp);
        printf("%02d: %s", i++, s);
    }

    fclose(fp);
}
