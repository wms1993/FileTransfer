# KuaiChuan(仿茄子快传)

仿茄子快传的一款文件传输应用， 涉及到Socket通信，包括TCP，UDP通信。（喜欢的给一个star, 有帮助的给一个fork， 欢迎Star和Fork ^_^）
也欢迎去我的github上面！哈哈哈！谢谢各位小伙伴！动动您的小指头，谢谢O(∩_∩)O谢谢！ https://github.com/mayubao/KuaiChuan/<https://github.com/mayubao/KuaiChuan/ />

## 效果预览

### 主页 ###
![Alt text](http://img.blog.csdn.net/20161215211058219?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveXViYW9tYTIwMTQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

### 文件发送端 ###
![Alt text](http://img.blog.csdn.net/20161215211350691?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveXViYW9tYTIwMTQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
![Alt text](http://img.blog.csdn.net/20161215211444724?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveXViYW9tYTIwMTQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
![Alt text](http://img.blog.csdn.net/20161215211554850?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveXViYW9tYTIwMTQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

### 文件接收端 ###
![Alt text](http://img.blog.csdn.net/20161215211700461?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveXViYW9tYTIwMTQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
![Alt text](http://img.blog.csdn.net/20161215211731440?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveXViYW9tYTIwMTQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

### 网页传(20161218新增) ###
![Alt text](http://img.blog.csdn.net/20161219181840389?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveXViYW9tYTIwMTQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

![Alt text](http://img.blog.csdn.net/20161219181853340?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveXViYW9tYTIwMTQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
![Alt text](http://img.blog.csdn.net/20161219181906670?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveXViYW9tYTIwMTQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

## 原理

快传是模仿 茄子快传来实现的,主要是是通过设备间发送文件。 文件传输在文件发送端或者是文件接收端通过自定义协议的Socket通信来实现。
由于文件接收方和文件发送方都是文件的缩略图
这里采用了header + body的自定义协议, header部分包括了文件的信息（长度，大小，缩略图）， body部分就是文件

## 测试

（必须在真机下测试）
在Android测试机 分别是 魅蓝2 与  华为 SCL-TL00， Vivo xs1 运行正常

## 感谢

google: <http://www.google.com>

stackoverflow  <http://stackoverflow.com/>


## 版本

### v1.0 ###
完成了Android端到Android端的文件传输

### v1.1 ###
完成了网页传模块的功能


## issue
QQ:345269374

Emial:345269374@qq.com