# NoHttp升级日志

## 1.1.3 
**特别说明**：本次升级不影响API的使用，请大家放心升级，如果升级之后有问题，那你顺着网线来打我啊。嘿嘿，开个玩笑，如果升级后有问题，请立刻通过issue或者qq反馈给我，保证分分钟修复并发布新版。

1. 兼容`Android4.0`以下使用`Https`不支持`TLSv1.1`，`TLSv1.2`的系统问题。
2. 优化下载模块，根据url和相应头自动命名时也支持断点续传下载。
3. 优化下载时多次重定向后不支持断点续传的问题。
4. 初始化增加全局参数、全局请求头、全局`SSLSocketFactory`、全局`HostnameVerifier`、全局重试次数。
5. 初始化由原来的`Config`类变为`InitializationConfig`类，并使用`Build`模式。
6. 修复4.x手机上发生的内存泄漏问题。
7. 去掉`HttpHeaders`类，把`Header`接口改为类并实现所有原接口方法。
8. 去掉`DefaultDownloadRequest`类，把`RequestDownload`接口改为类并实现所有接口方法。
9. 去掉`IProtocolRequest`接口，直接使用`ProtocolRequst`类。
10. 把`Request`接口改为抽象类，提供结合队列使用的方法。
11. 去掉`IBasicRequest`接口，直接向外提供`BasicRequest`类。
12. `HeaderUtil`改为`HeaderUtils`，`NetUtil`改为`NetUtils`。
13. 升级`OkHttp`为3.8.0，`OkHttp`原项目：[https://github.com/square/okhttp](https://github.com/square/okhttp)

另外需要说明原来的`Request#add(Map<String, String>)`更新为`Request#add(Map<String, Object>)`，这样做的好处是喜欢使用`Map`封装参数的同学，可以在`Map`中添加以下几种类型的参数了：  
```java
String、File、Binary、List<String>、List<Binary>、List<File>、List<Object>
```

代码举例说明：
```java
Map<String, Object> params = new HashMap<>();

params.put("name", "yanzhenjie");
params.put("head", new File(path));
params.put("logo", new FileBinary(file));
params.put("age", 18);
params.put("height", 180.5);

List<String> hobbies = new ArrayList<>();
hobbies.add("篮球");
hobbies.add("帅哥");
params.put("hobbies", hobbies);

List<File> goods = new ArrayList<>();
goods.add(file1);
goods.add(file2);
params.put("goods", goods);

List<Object> otherParams = new ArrayList<>();
otherParams.add("yanzhenjie");
otherParams.add(1);
otherParams.add(file);
otherParams.add(new FileBinary(file));

params.put("other", otherParams);
```

当然，真实开发中第三种和文件一起使用同一个`key`请求，几乎不会存在，但是难免会`String`、`int`等使用同一个`key`请求。

本次升级的一个亮点，增加拼装URL的方法，比如服务器是RESTFUL风格的API，请求用户信息时可能是这样一个URL：  
```
http://api.nohttp.net/rest/<userid>/userinfo
```
这里的`<userid>`就是用户名或者用户id，需要开发者动态替换，然后获取用户信息。以前是这样做的：
```
String userName = AppConfig.getUserName();

String url = "http://api.nohttp.net/rest/%1$s/userinfo";
url = String.format(Locale.getDefault(), url, userName);

StringRequest request = new StringRequest(url);
...
```

现在可以这样做：
```
String url = "http://api.nohttp.net/rest/";

StringRequest request = new StringRequest(url)
request.path(AppConfig.getUserName())
request.path("userinfo")
...
```


## 1.1.2
 是对1.1.1的bug修复，无api的改动。  

## 1.1.1
 1. 修改包名为`com.yanzhenjie.com`，开发者从旧版升级后会发生编译错误，请使用全局替换，将`com.yolanda.nohttp`替换为`com.yanzhenjie.nohttp`即可。
 因此依赖方式也由`com.yolanda.nohttp:nohttp:version`变为`com.yanzhenjie.nohttp:nohttp:version`
 2. 新增一种非队列的异步请求方式，调用后会立即发起请求：`AsyncRequestExecutor`。
 3. 修复StringRequest中按照服务器ContentType指定编码集解码（原来是由于解析contentType错误，默认utf-8解码）。
 4. 优化缓存逻辑，在不使用缓存的时候可提升请求速度与性能。
 5. 下载模块增加回调下载速度，具体可以参考[Sample](https://github.com/yanzhenjie/NoHttp)或者[使用文档](http://doc.nohttp.net)。
 6. 兼容服务器要求重定向时Location中返回不完整url的问题。
 7. 升级okhttp为3.6.0。