# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#####################################################
#                      基本配置                      #
#####################################################

# 指定代码的压缩级别 0 - 7
-optimizationpasses 5
# 不使用大小写混合类名
-dontusemixedcaseclassnames
# 如果应用程序引入的有jar包，并且想混淆jar包里面的class
-dontskipnonpubliclibraryclasses
# 混淆时是否做预校验（可去掉加快混淆速度）
-dontpreverify
#-dontoptimize
# 混淆时是否记录日志（混淆后生产映射文件 map 类名 -> 转化后类名的映射
-verbose
# 淆采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#指定外部模糊字典
#-obfuscationdictionary ./dictionary/proguard-1il.txt
#指定class模糊字典
#-classobfuscationdictionary ./dictionary/proguard-o0O.txt
#指定package模糊字典
#-packageobfuscationdictionary ./dictionary/proguard-socialism.txt


# 所有 Android 控件的子类不要去混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
# 所有 Android 支持库子类不混淆
-keep public class * extends com.support.**
-keep public class * extends androidx.**
-dontwarn javax.lang.model.element.Element

# 保持 native 的方法不去混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保持自定义控件类不被混淆，指定格式的构造方法不去混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
# Android layout 布局文件中为控件配置的onClick方法不能混淆
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

 # 保持自定义控件指定规则的方法不被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# 保持枚举 enum 不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持 Parcelable 不被混淆（aidl文件不能去混淆）
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 需要序列化和反序列化的类不能被混淆（注：Java反射用到的类也不能被混淆）
-keepnames class * implements java.io.Serializable
# 保护实现接口Serializable的类中，指定规则的类成员不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 过滤泛型（不写可能会出现类型转换错误，一般情况把这个加上就是了）
-keepattributes Signature

# 假如项目中有用到注解，应加入这行配置
-keepattributes *Annotation*

# 保持 R 文件不被混淆，否则，你的反射是获取不到资源id的
-keep class **.R$* { *; }

# 保持ViewModel和ViewBinding不混淆，否则无法反射自动创建
-keep class * implements androidx.viewbinding.ViewBinding { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }

#####################################################
#                      自定义配置                    #
#####################################################

# 保护 WebView 对 HTML 页面的API不被混淆
-keep class **.WebView2JsInterface { *; }

# 数据 Entity 类
######

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

-keep class androidx.lifecycle.** { *; }
-keep class androidx.arch.core.** { *; }


# kotlin serialization
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}


#Android X 混淆
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
# Compose Rules
-keep class androidx.compose.** { *; }
-keep class androidx.documentfile.** { *; }
-keepclassmembers class androidx.documentfile.** { *; }
# ...

# Lifecycle
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.LifecycleObserver { *; }

# Paging
-keep class androidx.paging.** { *; }

# Navigation
-keep class androidx.navigation.** { *; }

# accompanist
-keep class com.google.accompanist.** { *; }

# Dagger Hilt
-keep class * extends com.google.dagger.hilt.android.lifecycle.*
-keepclassmembers class * {
    javax.inject.* *;
}

# Multidex
-keep class * extends android.app.Application {
    void attachBaseContext(...);
}

#kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep class **.R$* {*;}
-keepclassmembers enum * { *;}

#Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

#BRVAH
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.viewholder.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.viewholder.BaseViewHolder {
     <init>(...);
}

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
# converters and adapters. 是否要注释
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn javax.annotation.**
-dontwarn javax.inject.**

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

# Gson
-keepattributes *Annotation*
-keepattributes Signature
-dontwarn sun.misc.**
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

#XPopup
-dontwarn com.lxj.xpopup.widget.**
-keep class com.lxj.xpopup.widget.**{*;}
-keep class com.lxj.xpopupext.bean.** {*;}

#banner
-keep class com.youth.banner.** {
    *;
 }

#XXPermission混淆
-keep class com.hjq.permissions.** {*;}
-keep class com.hjq.toast.** {*;}
# Gson 解析容错框架
-keep class com.hjq.gson.factory.** {*;}
# shape 框架
-keep class com.hjq.shape.** {*;}
# Toast 框架
-keep class com.hjq.toast.** {*;}

##腾讯
-keep class com.tencent.** { *; }


# 图片选择处理 PictureSelector
-keep class com.luck.picture.lib.** { *; }
# 如果引入了Camerax库请添加混淆
-keep class com.luck.lib.camerax.** { *; }
# 如果引入了Ucrop库请添加混淆
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

#TheRouter
-keep class androidx.annotation.Keep
-keep @androidx.annotation.Keep class * {*;}
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}
-keepclasseswithmembers class * {
    @com.therouter.router.Autowired <fields>;
}