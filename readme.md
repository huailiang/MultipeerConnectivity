
## 端对端直连技术


不需要通过远程的服务器，只在局域网内通过wifi、蓝牙建立起来的通信，有点类似于Apple设备上的AirDrop。

<br><img src='https://penghuailiang.gitee.io/img/post-vr/ar1.jpg'><br>


## iOS

你需要在生成的xcode工程添加：

	MultipeerConnectivity.framework

<br><img src='image/4.jpg'><br>


## Android

Unity导出apk, 手机运行如图所示：
<br><img src='image/screen.jpg'><br>


主要是对Wi-Fi Direct的封装, 需要在AndroidMenifest.xml添加如下权限：

```xml
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

除了在unity运行版本，我们还支持生成原生应用构建：

<br><img src='image/screen2.jpg'><br>
