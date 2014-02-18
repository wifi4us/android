LOCAL_PATH:=$(call my-dir)     
APP_STL:=stlport_static 
include $(CLEAR_VARS)     
LOCAL_C_INCLUDES:=/Users/mylich119/Downloads/ndk/sources/cxx-stl/stlport/stlport:$(LOCAL_PATH)/include
LOCAL_MODULE:=com_paad_wifi4us_utility_Constant
LOCAL_SRC_FILES:=com_paad_wifi4us_utility_Constant.cpp
LOCAL_LDLIBS:=-llog
include $(BUILD_SHARED_LIBRARY)
