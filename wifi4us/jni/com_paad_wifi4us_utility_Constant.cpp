#include <jni.h>
#include <stdio.h>
extern "C" {   
	JNIEXPORT jstring JNICALL Java_com_paad_wifi4us_utility_Constant_stringFromJNI (JNIEnv *env, jobject obj, jint prompt){
	   	int para = prompt;
	   	char* tmpstr;  
	   	jstring rtstr;  
	   	switch(para){   
			case 0:  
	   			tmpstr = "12345678";  
	   			rtstr = env->NewStringUTF(tmpstr);  
	  			return rtstr;  
			case 1:  
	   			tmpstr = "http://wifi4us.duapp.com/getadid.php";  
	   			rtstr = env->NewStringUTF(tmpstr);  
	  			return rtstr;  
			case 2:  
	   			tmpstr = "https://wifi4us.duapp.com/register.php";  
	   			rtstr = env->NewStringUTF(tmpstr);  
	  			return rtstr;  
			case 3:  
	   			tmpstr = "http://wifi4us.duapp.com/getcredit.php";  
	   			rtstr = env->NewStringUTF(tmpstr);  
	  			return rtstr;  
			case 4:  
	   			tmpstr = "http://wifi4us.duapp.com/get_lottery_info.php";  
	   			rtstr = env->NewStringUTF(tmpstr);  
	  			return rtstr;  
			case 5:  
	   			tmpstr = "http://wifi4us.duapp.com/get_credit_mechanism.php";  
	   			rtstr = env->NewStringUTF(tmpstr);  
	  			return rtstr;  
			case 6:  
	   			tmpstr = "https://wifi4us.duapp.com/reduce.php";  
	   			rtstr = env->NewStringUTF(tmpstr);  
	  			return rtstr;  
			case 7:  
	   			tmpstr = "https://wifi4us.duapp.com/add.php";  
	   			rtstr = env->NewStringUTF(tmpstr);  
	  			return rtstr;  
		} 	
	} 
}


