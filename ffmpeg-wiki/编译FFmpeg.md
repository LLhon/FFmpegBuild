# 编译FFmpeg在Android中使用

###### 编译环境
```
系统：Windows 11
终端：msys2
Android Studio 版本：Hedgehog
NDK版本：21.4.7075529
FFmpeg版本：6.1
```

**强烈建议：windows系统还是安装一个ubuntu的虚拟机吧，少踩很多坑**

## 一、修改FFmpeg源码

既然要编译FFmpeg源码，那么就需要更改一些配置，大概就两步：

* 下载[FFmpeg](https://www.ffmpeg.org/)源码，以及[ndk-r22b](https://developer.android.com/ndk/downloads/older_releases)

* 进入下载的源码，编辑`configure`

将如下内容
```
SLIBNAME_WITH_MAJOR='$(SLIBNAME).$(LIBMAJOR)'
LIB_INSTALL_EXTRA_CMD='$$(RANLIB) "$(LIBDIR)/$(LIBNAME)"'
SLIB_INSTALL_NAME='$(SLIBNAME_WITH_VERSION)'
SLIB_INSTALL_LINKS='$(SLIBNAME_WITH_MAJOR) $(SLIBNAME)'
```
替换为
```
SLIBNAME_WITH_MAJOR='$(SLIBPREF)$(FULLNAME)-$(LIBMAJOR)$(SLIBSUF)'
LIB_INSTALL_EXTRA_CMD='$$(RANLIB)"$(LIBDIR)/$(LIBNAME)"'
SLIB_INSTALL_NAME='$(SLIBNAME_WITH_MAJOR)'
SLIB_INSTALL_LINKS='$(SLIBNAME)'
```

* 在当前目录下新建`build_android.sh`文件，并写入以下内容：

```
#!/bin/bash
# 清空上次的编译
make clean
#你自己的NDK路径.
NDK=/home/anjoiner/Android/Sdk/ndk-bundle
TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/linux-x86_64
SYSROOT=$TOOLCHAIN/sysroot
API=21

function build_android
{
echo "Compiling FFmpeg for $CPU"
./configure \
    --prefix=$PREFIX \
    --disable-static \
    --disable-doc \
    --disable-ffmpeg \
    --disable-ffplay \
    --disable-ffprobe \
    --disable-doc \
    --disable-symver \
    --enable-nonfree \
    --enable-gpl \
    --enable-small \
    --enable-neon \
    --enable-hwaccels \
    --enable-avdevice \
    --enable-postproc \
    --enable-shared \
    --enable-jni \
    --enable-mediacodec \
    --enable-decoder=h264_mediacodec \
    --cross-prefix=$CROSS_PREFIX \
    --target-os=android \
    --arch=$ARCH \
    --cpu=$CPU \
    --nm=$NM \
    --strip=$STRIP \
    --cc=$CC \
    --cxx=$CXX \
    --enable-cross-compile \
    --sysroot=$SYSROOT \
    --extra-cflags="-Os -fpic $OPTIMIZE_CFLAGS" \
    --extra-ldflags="$ADDI_LDFLAGS" \
    $ADDITIONAL_CONFIGURE_FLAG
make clean
make
make install
echo "The Compilation of FFmpeg for $CPU is completed"
}

#armv8-a
ARCH=aarch64-linux-android-
VERSION=arm64
CPU=armv8-a
CROSS_PREFIX=$TOOLCHAIN/bin/${ARCH}
CC=$TOOLCHAIN/bin/aarch64-linux-android$API-clang
CXX=$TOOLCHAIN/bin/aarch64-linux-android$API-clang++
NM=$TOOLCHAIN/bin/${ARCH}nm
STRIP=$TOOLCHAIN/bin/${ARCH}strip
PREFIX=$(pwd)/android/$CPU
OPTIMIZE_CFLAGS="-march=$CPU"
build_android

#armv7-a
ARCH=arm-linux-androideabi-
VERSION=arm
CPU=armv7-a
CROSS_PREFIX=$TOOLCHAIN/bin/${ARCH}
CC=$TOOLCHAIN/bin/armv7a-linux-androideabi$API-clang
CXX=$TOOLCHAIN/bin/armv7a-linux-androideabi$API-clang++
NM=$TOOLCHAIN/bin/${ARCH}nm
STRIP=$TOOLCHAIN/bin/${ARCH}strip
PREFIX=$(pwd)/android/$CPU
OPTIMIZE_CFLAGS="-mfloat-abi=softfp -mfpu=neon -march=$CPU"
build_android
```
## 二、开始编译

在编译之前需要为刚刚我们创建的文件赋予执行权限

```
sudo chmod +x build_android.sh
```

然后就开始编译

```
./build_android.sh
```
经过漫长的等待...就会在源码目录下得到如下内容

![](../images/)

## 三、ndk编译

上面得到的一系列so文件还不能直接使用，还需要进行如下配置

### 配置

* 在任意目录下创建`jni`文件夹

* 将刚刚编译出来的`android/armv7-a/include`下的所有目录拷贝进`jni`文件夹

![](../images/)

* 在`jni`下创建一个`prebuilt`文件夹

* 将`android/armv7-a/lib`下所有的so文件拷贝进入`prebuilt`

![](../images/)

* 将源码下`fftools`文件夹下面的如下文件拷贝进入`jni`

    * cmdutils.c
    * cmdutils.h
    * ffmpeg_filter.c
    * ffmpeg_hw.c
    * ffmpeg_opt.c
    * ffmpeg.c
    * ffmpeg.h

* 将源码下的`config.h`进入`jni`

### 编辑

* 编辑 `ffmpeg.c`

将如下代码
```c
int main(int argc, char **argv)
```
替换为
```c
int run(int argc, char **argv)
```

* 编辑 `ffmpeg.h`

在`ffmpeg.h`中增加`int int run(int argc, char **argv);`

```c
...
int hw_device_setup_for_encode(OutputStream *ost);

int hwaccel_decode_init(AVCodecContext *avctx);
// add it
int run(int argc, char **argv);

#endif /* FFTOOLS_FFMPEG_H */
```

* 编辑`cmdutils.h`
将如下内容
```c
void show_help_children(const AVClass *class, int flags);
```
替换为
```c
void show_help_children(const AVClass *clazz, int flags);
```

### JNI

接下来就是开始使用jni去调用ffmpeg中的方法了

* 在jni文件夹中创建`ffmpeg-invoke.cpp`，但是需要使用你自己的文件路径替换`com_llhon_ffmpeg_FFmpegCmd`

```c
#include <jni.h>
#include <string.h>
#include "android/log.h"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "ffmpeg-invoke", __VA_ARGS__)

extern "C"{
#include "ffmpeg.h"
#include "libavcodec/jni.h"
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_llhon_ffmpeg_FFmpegCmd_run(JNIEnv *env, jclass type, jint cmdLen,
                                             jobjectArray cmd) {
    //set java vm
    JavaVM *jvm = NULL;
    env->GetJavaVM(&jvm);
    av_jni_set_java_vm(jvm, NULL);

    char *argCmd[cmdLen] ;
    jstring buf[cmdLen];

    for (int i = 0; i < cmdLen; ++i) {
        buf[i] = static_cast<jstring>(env->GetObjectArrayElement(cmd, i));
        char *string = const_cast<char *>(env->GetStringUTFChars(buf[i], JNI_FALSE));
        argCmd[i] = string;
        LOGD("argCmd=%s",argCmd[i]);
    }

    int retCode = run(cmdLen, argCmd);
    LOGD("ffmpeg-invoke: retCode=%d",retCode);

    return retCode;

}
```

* 在jni文件夹中创建`Android.mk`，需要更改`LOCAL_C_INCLUDES`

```
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE :=  libavdevice
LOCAL_SRC_FILES := prebuilt/libavdevice.so
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
LOCAL_MODULE := libpostproc
LOCAL_SRC_FILES := prebuilt/libpostproc.so
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := ffmpeg-invoke

LOCAL_SRC_FILES :=ffmpeg-invoke.cpp \
                 cmdutils.c \
                 ffmpeg_filter.c \
                 ffmpeg_opt.c \
                 ffmpeg_hw.c \
                 ffmpeg.c

# 将此处的路径改为你ffmepg源码所在位置
LOCAL_C_INCLUDES := /home/llhon/develop/FFmpeg/ffmpeg-6.1

LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid -lm -pthread -L$(SYSROOT)/usr/lib
LOCAL_SHARED_LIBRARIES := libavdevice libavcodec libavfilter libavformat libavutil libswresample libswscale libpostproc

include $(BUILD_SHARED_LIBRARY)
```

* 在jni文件夹中创建`Application.mk`

```
APP_ABI := armeabi-v7a
APP_PLATFORM := android-21
APP_OPTIM := release
APP_STL := c++_static
```

* 在`jni`文件夹中执行`ndk-build`命令

```
/home/llhon/develop/Android/Sdk/ndk-bundle/ndk-build
```

如果发生以下错误：

```
1. D:/develop/workspace/android_ws/FFmpegBuild/app/src/main/jni/ffmpeg.h:896:35: error: invalid suffix on literal; C++11 requires a space between literal and identifier [-Wreserved-user-defined-literal]
```
那就编辑`ffmpeg.h`，将如下内容
```c
#define SPECIFIER_OPT_FMT_i64 "%"PRId64
#define SPECIFIER_OPT_FMT_ui64 "%"PRIu64
```
替换为
```c
#define SPECIFIER_OPT_FMT_i64  "%" PRId64
#define SPECIFIER_OPT_FMT_ui64 "%" PRIu64
```

```
2. D:/develop/workspace/android_ws/FFmpegBuild/app/src/main/jni/ffmpeg.h:311:20: error: expected member name or ';' after declaration specifiers
```
编辑`ffmpeg.h`，将如下内容
```c
typedef struct FilterGraph {
    const AVClass *class;
```
替换为
```c
typedef struct FilterGraph {
    const AVClass *clazz;
```

```
3. D:/develop/workspace/android_ws/FFmpegBuild/app/src/main/jni/libavcodec/packet.h:787:6: note: candidate function not viable: cannot convert argument of incomplete type 'void *' to 'AVPacket *' for 1st argument
```
编辑`libavcodec/packet.h`，将如下内容
```c
static inline void pkt_move(void *dst, void *src)
```
改为
```c  
static inline void pkt_move(AVPacket *dst, AVPacket *src)
```

```
4. D:/develop/workspace/android_ws/FFmpegBuild/app/src/main/jni/ffmpeg.c:795: error: undefined reference to 'fix_sub_duration_heartbeat'
```
未链接到某些资源文件：
可能的情况是jni文件夹内缺少 ffmpeg 某个源码文件，可以进入D:\develop\workspace\android_ws\FFmpeg-release-6.1\fftools文件夹内是否有需要链接的文件 copy 到jni文件夹


再次执行`ndk-build`命令，大概会得到如下内容，那就说明编译完成了

![](../images/)

## 四、如何将多个so文件合并成一个独立的so文件

### 1. 在 build_android.sh 文件里添加以下代码：
```
# 打包，以下代码是为了把7个so包合成一个so包，如果不需要合成可以去掉这段代码
echo "开始编译libffmpeg-org.so"
$TOOLCHAIN/bin/$ARC-ld \
    -rpath-link=$SYSROOT/usr/lib/$ARC/$API \
    -L$SYSROOT/usr/lib/$ARC/$API \
    -L$TOOLCHAIN/lib/gcc/$ARC/4.9.x \
    -L$PREFIX/lib -soname libffmpeg-org.so \
    -shared -Bsymbolic --whole-archive --no-undefined -o \
    $PREFIX/libffmpeg-org.so \
    $PREFIX/lib/libavcodec.a \
    $PREFIX/lib/libavfilter.a \
    $PREFIX/lib/libswresample.a \
    $PREFIX/lib/libavformat.a \
    $PREFIX/lib/libavutil.a \
    $PREFIX/lib/libswscale.a \
    $PREFIX/lib/libavdevice.a \
    $PREFIX/lib/libpostproc.a \
    -lc -lm -lz -ldl -llog -landroid --dynamic-linker=/system/bin/linker \
    $TOOLCHAIN/lib/gcc/$ARC/4.9.x/libgcc_real.a || exit 1

echo "完成编译libffmpeg-org.so"
```

### 2. 在 MSYS2 CLANG64 终端命令行中执行 ./build_android.sh 脚本文件
完整的脚本代码参考`scripts/build_android.sh`

### 3. 如果遇到以下错误：

```
1. libswscale.a(half2float.o): multiple definition of 'ff_init_half2float_tables'; libavcodec.a(half2float.o): previous definition here
```
编译.a文件时，libavcodec.a和libswscale.a包含同一个half2float.o，在链接 libavcodec.a 和 libswscale.a 时会出现函数重定义的错误。需要在执行脚本之前修改libswscale 文件夹下的 Makefile ，将 OBJS 下的 half2float.o 删除即可

```
2. libavfilter/vf_pp.c:59: error: undefined reference to 'pp_get_mode_by_name_and_quality'；libavfilter/vf_pp.c:164: error: undefined reference to 'pp_free_mode'
```
这一般是由于链接时找不到对应的库报的错误，在 build_android.sh 脚本文件里检查下是否有链接 libpostproc.a 文件