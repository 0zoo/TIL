package xyz.youngzz.firebase.chat

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid

/*
    1. AndroidManifest.xml 등록
    2. Application 객체 생성
        => ChatApplication 생성
    3. Application을 상속받아서 만들었다면,
        반드시 manifest에 등록해야 한다.

 */

class ChatApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        // App이 처음 구동할 때 생성되는 인스턴스
        JodaTimeAndroid.init(this);
    }
}