## TweakJar
TweakJar是一个轻量级的运行时Jar包修改框架，它主要面向Android正向开发者（当然，逆向开发者也同样适用）。 <br>
他的主要功能是：在运行时hook任意的java方法，动态调整其方法逻辑。 <br>
框架专为希望微调或增强jar包中某些方法的功能又没有jar包源码的场景而设计。 <br>
你可以认为他是一个微缩版本的xposed，又没有它的笨重、需要root权限等限制，主打一个极简与方便集成。 <br>
此框架从我的另一个开源逆向框架**TweakMe**裁剪而来，剥离出其中的java拦截部分功能。 <br>
因为TweakMe是一个相对较重的逆向框架，而java拦截部分实际上可以单独提取以让其正逆两用，极大的方便使用者在自己的项目中集成开发。 <br>
目前TweakJar框架在5.0到14.0的android手机上测试通过。 <br>
 <br>
## 使用前准备
需要eclipse for android 环境 <br>
如果只有android studio 环境，则需自己将工程转化AS工程。 <br>
 <br>
## 框架使用
详细使用请浏览MainActivity.java中的测试代码。 <br>
如果你没有eclipse for android环境，可以直接安装**TweakJar.apk**查看运行效果。 <br>
logcat中的日志过滤tag标签为 **TweakJar** <br>
 <br>
## 框架集成
如果你想要将TweakJar框架集成到你自己的项目中，你只需要做如下两步操作： <br>
1、将下面的两个包中的所有java代码拷贝到你自己项目的src源码目录中。 <br>
**package com.android.guobao.liao.apptweak.util;** <br>
**package com.android.guobao.liao.apptweak;** <br>
<br> 
2、将**libtweakjar.so**拷贝到你自己项目的lib库目录中。 <br>
 <br>  
 <br>
 <br>
 **如果本框架对你有帮助，记得github上为我点赞加星哦！！！** <br>
 <br> 
 ## 免责声明
**本框架为个人作品，任何人的复制、拷贝、使用等，只可用于正常的技术交流与学习，不可用于灰黑产业，不可从事违法犯罪行为。否则，后果自负！！！**
 <br> 
