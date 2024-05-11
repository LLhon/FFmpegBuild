#include <jni.h>
#include <string.h>
#include <unistd.h>
#include "android/log.h"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "ffmpeg_native_cmd", __VA_ARGS__)

extern "C"{
#include "ffmpeg.h"
#include "libavcodec/jni.h"
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
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
  LOGD("ffmpeg_native_cmd: retCode=%d",retCode);

  return retCode;

}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_llhon_ffmpeg_FFmpegCmd_ffmpegVersion(JNIEnv *env, jobject thiz) {
  const char *version = av_version_info();
  return env->NewStringUTF(version);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_llhon_ffmpeg_FFmpegCmd_configuration(JNIEnv *env, jobject thiz) {
  return env->NewStringUTF(avcodec_configuration());
}
