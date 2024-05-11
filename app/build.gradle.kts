plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.llhon.ffmpeg"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.llhon.ffmpeg"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            //设置支持的SO库架构
//            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
            abiFilters += listOf("armeabi-v7a")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        //这2个为非必选，想用哪个就保留那个 用的话一定要加上项目中的 ViewBinding & DataBinding 混淆规则
//        dataBinding = true
        viewBinding = true
    }

//    externalNativeBuild {
//        cmake {
//            path("CMakeLists.txt")
//            path("src/main/cpp/CMakeLists.txt")
//            version = "3.10.2"
//        }
//    }

    ndkVersion = "20.0.5594570"
}

dependencies {

    add("implementation", fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.blankj:utilcodex:1.31.0")
    implementation("com.squareup.okhttp3:okhttp:3.10.0")
    implementation("org.eclipse.ecf:org.apache.commons.codec:1.9.0.v20170208-1614")
    implementation("com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.11")
    implementation("io.github.razerdp:BasePopup:3.2.1")
    implementation("com.github.getActivity:XXPermissions:18.5")

    implementation("org.videolan.android:libvlc-all:3.6.0-eap8")
}