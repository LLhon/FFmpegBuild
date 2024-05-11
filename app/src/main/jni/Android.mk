# 代表需要编译的文件,这里就是返回当前文件路径
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
# .so文件名称，去除前缀lib和-后的内容
LOCAL_MODULE :=  libavdevice
# .so文件路径
LOCAL_SRC_FILES := prebuilt/libavdevice.so
# 已编译好的库使用PREBUILT_SHARED_LIBRARY
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  libavutil
LOCAL_SRC_FILES := prebuilt/libavutil.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  libswresample
LOCAL_SRC_FILES := prebuilt/libswresample.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  libswscale
LOCAL_SRC_FILES := prebuilt/libswscale.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavcodec
LOCAL_SRC_FILES := prebuilt/libavcodec.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavformat
LOCAL_SRC_FILES := prebuilt/libavformat.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libavfilter
LOCAL_SRC_FILES := prebuilt/libavfilter.so
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)
# module 名称
LOCAL_MODULE := ffmpeg-cmd

# module 中的源文件
LOCAL_SRC_FILES :=ffmpeg_native_cmd.cc \
                 cmdutils.c \
                 thread_queue.c \
                 ffmpeg_dec.c \
                 ffmpeg_enc.c \
                 ffmpeg.c \
                 ffmpeg_demux.c \
                 ffmpeg_mux.c \
                 ffmpeg_mux_init.c \
                 ffmpeg_filter.c \
                 ffmpeg_opt.c \
                 ffmpeg_hw.c \
                 sync_queue.c \
                 opt_common.c \
                 objpool.c

# ffmpeg 源码路径
LOCAL_C_INCLUDES := D:/develop/workspace/android_ws/FFmpeg-release-6.1

LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid -lm -pthread -L$(SYSROOT)/usr/lib
# 链接的动态库文件
LOCAL_SHARED_LIBRARIES := libavdevice libavcodec libavfilter libavformat libavutil libswresample libswscale

# 编译生成的库使用BUILD_SHARED_LIBRARY
include $(BUILD_SHARED_LIBRARY)