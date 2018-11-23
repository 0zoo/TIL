# 메모리 누출

> the most dangerous **memory leaks** are caused when the reference to Activity is kept by something that may **live longer than Activity**. 

## What may cause memory leak?

1. **Static context**  
    - 되도록이면 사용 X. 꼭 필요하다면 Application Context를 사용하자.
2. **Inner Classes**  
    - inner class는 top-level class에 접근이 가능하다. (fields,methods,context!)
    - static nested class를 사용하라. 이것은 자신의 생명주기를 갖고 top-level class에 독립적이다.
    - non-static nested classes는 만들지 말자!
3. **Anonymous classes**
    - inner class의 경우와 비슷.
    ```java
    public class AnonymousClassLeakActivity extends AppCompatActivity {
        @Override
        protected void onCreate(@Nullable final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(final Void... voids) {
                    // Here is operation, which takes a long time...
                    Context context = AnonymousClassLeakActivity.this;
                    loadMillionCatsPhotos(context);
                    return null;
                }
            }.execute();
        }

        private void loadMillionCatsPhotos(Context context) {
            // Cat 1 loading...
            // Cat 2 loading...
        }
    }
    ```
    - 익명 클래스는 outer class가 살아있는 동안에만 살아있지만,  
    AsyncTask가 완료되지 않는 이상 익명 클래스뿐만 아니라 AnonymousClassLeakActivity또한 살아있기 떄문에 좋지 않다.

4. **System services**
    - 액티비티가 끝났을 때 unregister 해주지 않으면 시스템 서비스가 살아있는 한 액티비티도 존재함.
    - 모든 active listeners의 사용이 끝나면 꼭 unregister 해주자.

5. **RxJava**
    - subscription들을 만드는 것은 쉽다. 하지만 더 이상 필요 없어질 때 관리하는 것이 중요함.
    - 사용이 끝나면 unsubscribe 해주자


## How to avoid memory leaks?

```java
// strong reference
ImageView imageView = new ImageView(this);
```
strong reference가 하나라도 있다면 GC는 메모리에서 해제하지 못하고 이것은 memory leak으로 이어질 수 있다.

1. **Weak reference**
    -   
    ```java
    WeakReference<Context> = new WeakReference(context);
    ```
    - 약한 참조. GC가 실행될 때 회수됨.  
    ```java
    public class AnonymousClassFixedLeakActivity extends AppCompatActivity {
        @Override
        protected void onCreate(@Nullable final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            new CatHelperAsyncTask(this).execute();
        }

        private static class CatHelperAsyncTask extends AsyncTask<Void, Void, Void> {
            private WeakReference<Context> contextWeakReference;

            public CatHelperAsyncTask(final Context context) {
                this.contextWeakReference = new WeakReference<>(context);
            }

            @Override
            protected Void doInBackground(final Void... voids) {
                // Here is operation, which takes a long time...
                Context context = contextWeakReference.get();
                if (context != null) {
                    loadMillionCatsPhotos(context);
                }
                return null;
            }
        }
    }
    ```
    - 단점: 메모리에서 언제 소멸될 지 모르기떄문에 항상 null 체크를 해주어야 한다.

2. **Soft reference**
    -  
    ```java
    SoftReference<Context> = new SoftReference(context);
    ```
    - soft가 weak보다 더 소멸되기 힘들다.  
    - soft references는 오직 메모리 해제밖에 방법이 없을 경우에만 GC에 의해 소멸된다. 



[참고](https://medium.com/@pszklarska/catch-leak-if-you-can-608a99537d8a)