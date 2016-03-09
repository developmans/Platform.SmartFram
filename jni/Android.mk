LOCAL_PATH := $(call my-dir)

#libserialport.so
include $(CLEAR_VARS)
TARGET_PLATFORM := android-10
LOCAL_MODULE    := serialport
LOCAL_SRC_FILES := serialport.c
LOCAL_LDLIBS    := -llog
include $(BUILD_SHARED_LIBRARY)
