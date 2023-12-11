/* DO NOT EDIT THIS FILE - it is machine generated */
/* Header for class Level */
/*
 * Class:     Level
 * Method:    clickKey
 * Signature: (I)V
 */
#include <jni.h>
#include <windows.h>
#include <iostream>
#include <vector>
#include <stdio.h>
#include <thread>
#include <algorithm>

#define KEY_DOWN(key) ((GetAsyncKeyState(key)&0x8000)?1:0)
void simulateKeyPress(int keyCode);
void sleep(double mil);
extern "C"
JNIEXPORT void JNICALL Java_thercn_adofai_helper_Level_start(JNIEnv *env, jclass clazz, jdoubleArray valuesObj) {
    jsize numValues = env->GetArrayLength(valuesObj);
    jdouble* noteTime = env->GetDoubleArrayElements(valuesObj, nullptr);
    std::vector<std::tuple<double,int,bool>>keyEvents;
    const char usedKeys[]="ABCDEFGHIJKLMN";
    const int totKeyCount=strlen(usedKeys);
        for(unsigned i=0;i<numValues;i++){
                keyEvents.push_back(std::tuple<double,int,bool>{noteTime[i],usedKeys[i%totKeyCount],true});
                keyEvents.push_back(std::tuple<double,int,bool>{noteTime[i]+30,usedKeys[i%totKeyCount],false});
        }
        std::sort(keyEvents.begin(),keyEvents.end());

        while(!KEY_DOWN(VK_SPACE)){;}
        while(!KEY_DOWN('W')){;}
        auto startTime=std::chrono::steady_clock().now();
        unsigned eventNumber=1;
        while(eventNumber<keyEvents.size()){
                auto curTime=std::chrono::steady_clock().now();
                auto timeMilliseconds=std::chrono::duration<double>(curTime-startTime).count()*1000+std::get<0>(keyEvents[0]);
                while(eventNumber<keyEvents.size()&&std::get<0>(keyEvents[eventNumber])<=timeMilliseconds){
                        keybd_event(std::get<1>(keyEvents[eventNumber]),0,
                                std::get<2>(keyEvents[eventNumber])?0:KEYEVENTF_KEYUP
                        ,0);
                        eventNumber++;
                        //std::cout<<eventNumber<<" time: "<<timeMilliseconds<<std::endl;
                }
               // sleep(0.001);
        }
    env->ReleaseDoubleArrayElements(valuesObj, noteTime, 0);
}
void simulateKeyPress(int keyCode) {
    keybd_event(keyCode, 0, 0, 0);  // 模拟按下按键
    keybd_event(keyCode, 0, KEYEVENTF_KEYUP, 0);  // 模拟释放按键
}

void sleep(double mil){
	auto startTime=std::chrono::steady_clock().now();
	while(true){
		auto curTime=std::chrono::steady_clock().now();
		double dura=std::chrono::duration<double>(curTime-startTime).count();
		if(dura*1000>=mil){
			break;
		}
	}
}

