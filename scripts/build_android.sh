#!/bin/bash
# 清空上次的编译
make clean
# 你自己的NDK路径.
NDK=C:/Users/86176/AppData/Local/Android/Sdk/ndk/21.4.7075529
# NDK的工具链路径
TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/windows-x86_64
SYSROOT=$TOOLCHAIN/sysroot
API=21

function build_android
{
echo "Compiling FFmpeg for $CPU"
./configure \
    --prefix=$PREFIX \
    --enable-static \
    --disable-shared \
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
    --enable-jni \
    --enable-mediacodec \
    --enable-decoder=h264_mediacodec \
    --enable-decoder=hevc_mediacodec \
    --enable-encoder=h264_mediacodec \
    --enable-encoder=hevc_mediacodec \
    --enable-decoder=hevc \
    --enable-demuxer=hevc \
    --enable-parser=hevc \
    --enable-encoder=flv \
    --enable-decoder=flv \
    --enable-muxer=flv \
    --enable-decoder=mpeg4 \
    --enable-decoder=mjpeg \
    --enable-decoder=png \
    --enable-decoder=flac \
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
echo "-----------------------The Compilation of FFmpeg for $CPU is completed-----------------------"

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

echo "编译结束!"
}

# armv8-a
ARCH=aarch64-linux-android-
ARC=aarch64-linux-android
VERSION=arm64
CPU=armv8-a
ARCHH=arch-arm64
PLATFORM=$NDK/platforms/android-$API/$ARCHH
CROSS_PREFIX=$TOOLCHAIN/bin/${ARCH}
CC=$TOOLCHAIN/bin/aarch64-linux-android$API-clang
CXX=$TOOLCHAIN/bin/aarch64-linux-android$API-clang++
NM=$TOOLCHAIN/bin/${ARCH}nm
STRIP=$TOOLCHAIN/bin/${ARCH}strip
PREFIX=$(pwd)/android/$CPU
OPTIMIZE_CFLAGS="-march=$CPU"
build_android

# armv7-a
ARCH=arm-linux-androideabi-
ARC=arm-linux-androideabi
VERSION=arm
CPU=armv7-a
ARCHH=arch-arm
PLATFORM=$NDK/platforms/android-$API/$ARCHH
CROSS_PREFIX=$TOOLCHAIN/bin/${ARCH}
CC=$TOOLCHAIN/bin/armv7a-linux-androideabi$API-clang
CXX=$TOOLCHAIN/bin/armv7a-linux-androideabi$API-clang++
NM=$TOOLCHAIN/bin/${ARCH}nm
STRIP=$TOOLCHAIN/bin/${ARCH}strip
PREFIX=$(pwd)/android/$CPU
OPTIMIZE_CFLAGS="-mfloat-abi=softfp -mfpu=neon -march=$CPU"
build_android