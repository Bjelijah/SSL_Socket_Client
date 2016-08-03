LOCAL_PATH := $(call my-dir)



include $(CLEAR_VARS)
LOCAL_MODULE := hiPlay
LOCAL_SRC_FILES := libHW_H265dec_Andr.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := hwtrans
LOCAL_SRC_FILES := libhwtrans.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := hwplay
LOCAL_SRC_FILES := libhwplay.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := hwnet
LOCAL_SRC_FILES := libhwnet.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := hw_jni
# Add your application source files here...
LOCAL_SRC_FILES := HiPlayDemo.cpp 
LOCAL_SHARED_LIBRARIES :=hwplay hwnet hiPlay hwtrans
LOCAL_LDFLAGS := -LE:/Android/android-ndk-r10e/sources/cxx-stl/gnu-libstdc++/4.8/libs/armeabi-v7a
LOCAL_LDFLAGS += -fuse-ld=bfd
LOCAL_LDLIBS := -llog -lgnustl_static -lGLESv2 -lz -ldl -lgcc -lm
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
#	-L$(NDK_PLATFORMS_ROOT)/$(TARGET_PLATFORM)/arch-arm/usr/lib -L$(LOCAL_PATH) -lz -ldl -lgcc 
include $(BUILD_SHARED_LIBRARY)





