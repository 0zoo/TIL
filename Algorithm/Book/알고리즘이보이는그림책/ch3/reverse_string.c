#include <stdio.h>
#include <string.h>

int main(){

    char a[] = "hello";
    int n = strlen(a);
    char b[n+1];

    for(int i=0; i<n; i++){
        b[i] = a[n-1-i];
    }
    b[n] = '\0';

    printf("%d %s\n",n, b);
}

