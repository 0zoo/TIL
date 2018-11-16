#include <stdio.h>
#include <string.h>

int main(){
    // 학생 50명의 이름, 국어, 영어, 수학 점수를 저장하는 구조체 정의
    // 그 구초제를 요소로 하는 배열 정의
    // 첫번째 학생 정보를 할당하고 평균 점수 출력하기

    typedef struct _STUDENT{
        char name[20];
        int kor;
        int eng;
        int math;
    } STUDENT;

    STUDENT students[50];

    strcpy(students[0].name, "이영주");
    students[0].kor = 90;
    students[0].eng = 80;
    students[0].math = 100;

    int avg = (int) (students[0].kor + students[0].eng + students[0].math) / 3;

    printf("%s : %d\n", students[0].name, avg); 
    
    // students[1] == *(students+1)
    // students[1].name == (students+1)->name

    // (*p).m == p->m
}

