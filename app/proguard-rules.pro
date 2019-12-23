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
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#------------------------------------基本指令--------------------------------------------------------

# 代码混淆压缩比，在0~7之间，默认为5,一般不下需要修改
-optimizationpasses 5
#-dontoptimize


# 混淆时不使用大小写混合，混淆后的类名为小写
# windows下的同学还是加入这个选项吧(windows大小写不敏感)
-dontusemixedcaseclassnames

# 指定不去忽略非公共的库的类
# 默认跳过，有些情况下编写的代码与类库中的类在同一个包下，并且持有包中内容的引用，此时就需要加入此条声明
-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

# 不做预检验，preverify是proguard的四个步骤之一
# Android不需要preverify，去掉这一步可以加快混淆速度
-dontpreverify

# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
-verbose
-printmapping priguardMapping.txt

# 指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/artithmetic,!field/*,!class/merging/*

# 保护代码中的Annotation不被混淆
# 这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*

# 避免混淆泛型
# 这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

#解决eventbus打包混淆后运行出错的问题
-keepattributes *Annotation*
-keepclassmembers class ** {
@org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
<init>(java.lang.Throwable);
}




#-ignorewarnings  # 抑制警告------------------------------------------------------

#-------------------------------------需要保留的东西-------------------------------------------------

# 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留了继承自Activity、Application这些类的子类
# 因为这些子类有可能被外部调用
# 比如第一行就保证了所有Activity的子类不要被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

# 如果有引用android-support-v4.jar包，可以添加下面这行
-keep public class com.null.test.ui.fragment.** {*;}

# 保留Activity中的方法参数是view的方法，
# 从而我们在layout里面编写onClick就不会影响
-keepclassmembers class * extends android.app.Activity {
    public void * (android.view.View);
}


# 枚举类不能被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留自定义控件(继承自View)不能被混淆
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(***);
    *** get* ();
}

# 保留Parcelable序列化的类不能被混淆
-keep class * implements android.os.Parcelable{
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable 序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[] serialPersistentFields;
   !static !transient <fields>;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}

# 对R文件下的所有类及其方法，都不能被混淆
-keepclassmembers class **.R$* {
    *;
}

# 对于带有回调函数onXXEvent的，不能混淆
-keepclassmembers class * {
    void *(**On*Event);
}

###针对第三方jar包的解决方案
#-------------------------------------针对andoid-support-v4.jar的解决方案----------------------------

#-libraryjars ./libs/android-support-v4.jar
-dontwarn android.support.v4.**
-dontwarn **CompatHoneycomb
-dontwarn **CompatHoneycombMR2
-dontwarn **CompatCreatorHoneycombMR2
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment


-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**


#####针对App的量身定制

#----------------------------- WebView(项目中没有可以忽略) -----------------------------
#
#webView需要进行特殊处理
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#在app中与HTML5的JavaScript的交互进行特殊处理
#我们需要确保这些js要调用的原生方法不能够被混淆，于是我们需要做如下处理：
-keepclassmembers class com.ljd.example.JSInterface {
    <methods>;
}
