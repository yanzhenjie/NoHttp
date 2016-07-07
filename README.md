# NoHttp
> NoHttp, feeling spill（NoHttp，情怀四溢）.

![NoHttp Logo][10]

[**中文版文档**][14]  

The author's url: [http://www.yanzhenjie.com][12]  
The author's blog: [http://blog.csdn.net/yanzhenjie1003][3]  

----
# NoHttp home page and doc url
NoHttp home page: [http://www.nohttp.net][5]  
NoHttp document: [http://doc.nohttp.net][6]  
NoHttp source code: [https://github.com/yanzhenjie/NoHttp][1]  
Document also continues to improve, there is a problem can send [issues][7], or send an email to me directly: smallajax@foxmail.com.

# How to use
* Eclipse use [NoHttp Jar][8], if need to rely on the source code, [download][13].
> [download jar [include source code，274k]][11]  
> [download jar [not include source code，147k]][8]  

* AndroidStudio using Gradle build add dependent (recommended)
```groovy
compile 'com.yolanda.nohttp:nohttp:1.0.4'
```

# Download Demo
[Downloa source code of NoHttp, Demo][13]  
[Download Demo APK][9]  

# Permission
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

# NoHttp features
NoHttp implements Http1.1 ([RFC2616][15]), a standard Http framework.

* Request and download queue, average distribution of each thread resources, support for multiple requests concurrently.
* Support the GET, POST, PUT, PATCH, HEAD, DELETE, OPTIONS, such as TRACE request protocol.
* Support for POST, PUT, PATCH, DELETE the file upload (Html form principle).
* File download, upload, download, upload and download the progress of the callback, error correction.
* Provides five kinds of data caching policy for developers to choose to use (see below) in detail.
* Support to cancel a request, specify multiple request cancel, cancel all requests.
* Support custom Request, using NoHttp generics can be parsed into any data you want to format (String, Json, JavaBean, etc.).
* Automatic maintenance of support Session, cookies, restart the App, turn off the boot after also continue to maintain.
* Support Https, self-signed website Https access, support two-way authentication.

## Friendly debug mode
NoHttp provides a debug mode, after open the can see clear request process, such as how to transfer data, basic need not caught. Can see that the request headers, request data, the process of response headers, cookies, etc. You don't have to worry about too many Log will dazzle you, clean and tidy.

## Request
>* Support the request String, Json, FastJson, Gson, Bitmap, javabeans and XML extensions.
>* An asynchronous request, get the results directly to update the UI, support synchronous request.

## Multiple file upload
All download progress callback, error correction and other friendly interface.

>* Large file upload, OOM will not occur.
>* File upload more, multiple key file, a key multiple files（`List<File>`）.
>* Support File, InputStream, ByteArray containing, Bitmap, realize NoHttp Binary interface, anything can theoretically.
>* Support to cancel the upload.

## File download
>* File to download, support for multiple files to download at the same time, and a progress callback, error correction and so on.
>* Support for a moratorium on continue to download, support to cancel the download, support breakpoint continuingly.
>* To make use of much NoHttp file download can do a download manager.

## Cache mode
>* Only request network.
>* Only read cache.
>* The standard Http protocol cache (such as in the case of the response code is 304), need server support, if the server does not support the same way as a common request.
>* First request network, after failing to return to the cache.
>* First read the cache, the cache request network again.

## Cancel Request
All cancelled all support requests are being executed.

>* Support cancel a request。
>* Support to cancel with sign to specify several requests。
>* Support cancel all request。

## Automatically maintain Cookie
>* Maintain support Session, cookies, temporary cookies.
>* Support the App after the restart, shutdown boot continue to persistent.
>* Provides the interface that allows developers to monitor the change of the cookies, can also change the value of a Cookie.

## Redirect
>* For Http301, 302, 303, 307 redirect support.
>* Support multiple redirection nesting.
>* Support disable redirection, NoHttp provides redirection interface operation.

## Proxy
>* The standard Java Api, the ProXy: the designated agent of IP and Port.
>* Such as agent to your computer for caught when debugging, such as use proxy to access Google.

# 1. Request
## String request
```java
// String request object
Request<String> request = NoHttp.createStringRequest(url, requestMethod);

```

## Json request
```java
// JsonObject
Request<JSONObject> request = NoHttp.createJsonObjectRequest(url, reqeustMethod);
...
// JsonArray
Request<JSONArray> request = NoHttp.createJsonArrayRequest(url, reqeustMethod);
```

## Bitmap request
```	java
Request<Bitmap> request = NoHttp.createImageRequest(url, requestMethod);
```

## Add params
```java
Request<JSONObject> request = ...
request.add("name", "yoldada");// String type
request.add("age", 18);// int type
request.add("sex", '0')// char type
request.add("time", 16346468473154); // long type
...
```

## Add to queue
```java
RequestQueue requestQueue = NoHttp.newRequestQueue();
// Or a concurrent value, allowing three concurrent requests at the same time
// RequestQueue requestQueue = NoHttp.newRequestQueue(3);

// start request
requestQueue.add(what, request, responseListener);
```
　　Added to the queue above there is a credit, the credit will be `responseLisetener` response callback to developers，so we can use a `responseLisetener` accept multiple requests the response，with what to distinguish results. Instead, like some framework, each request to the new a callback.

## Synchronous request
　　In request, the current thread by the thread so used.
```java
Request<String> request = ...
Response<String> response = NoHttp.startRequestSync(request);
if (response.isSucceed()) {
    // succeed
} else {
    // failed
}
```

# 2. File upload
　　Support for multiple File upload, multiple key multiple files, a key multiple files (`List < File >`). Support File, InputStream, ByteArray containing, Bitmap, realize NoHttp Binary interface, anything can theoretically.

## A single file
```java
Request<String> request = ...
request.add("file", new FileBinary(file));
```

## Upload multiple files, multiple Key file form
　　Here you can add all kinds of files，File、Bitmap、InputStream、ByteArray：
```java
Request<String> request = ...
request.add("file1", new FileBinary(File));
request.add("file2", new FileBinary(File));
request.add("file3", new InputStreamBinary(InputStream));
request.add("file4", new ByteArrayBinary(byte[]));
request.add("file5", new BitmapStreamBinary(Bitmap));
```

## Upload multiple files, a Key form of multiple files
　　With the same key to add,  the same key will not be covered.
```java
Request<String> request = ...
fileList.add("image", new FileBinary(File));
fileList.add("image", new InputStreamBinary(InputStream));
fileList.add("image", new ByteArrayBinary(byte[]));
fileList.add("image", new BitmapStreamBinary(Bitmap));
```
　Or: 
```java
Request<String> request = ...

List<Binary> fileList = ...
fileList.add(new FileBinary(File));
fileList.add(new InputStreamBinary(InputStream));
fileList.add(new ByteArrayBinary(byte[]));
fileList.add(new BitmapStreamBinary(Bitmap));
request.add("file_list", fileList);
```

# 3. Download file
　　Because the downloaded file code is more, post key part, specific please refer to the sample.
## Request a download
```java
// download file
downloadRequest = NoHttp.createDownloadRequest...
// what To distinguish the download.
// downloadRequest To download the request object.
// downloadListener Listeningof download.
downloadQueue.add(0, downloadRequest, downloadListener);
```

## Pause or stop the download
```java
downloadRequest.cancel();
```

## Listening to the download process
```java
private DownloadListener downloadListener = new DownloadListener() {
	@Override
	public void onStart(int what, boolean resume, long preLenght, Headers header, long count) {
	    // download start.
	}

	@Override
	public void onProgress(int what, int progress, long downCount) {
		// update download progress.
	}

 	@Override
	public void onFinish(int what, String filePath) {
	    // download finish.
 	}

	@Override
	public void onDownloadError(int what, StatusCode code, CharSequence message) {
	    // download have a error.
	}

	@Override
	public void onCancel(int what) {
	    // Download has been cancelled or suspended.
	}
};
```
;
# 4. Cache mode
## 1. Cache Http standard protocols, such as when the response code is 304(redirect)
　　Is realized RFC2616 NoHttp itself, so there need not set or set to DEFAULT.
```java
Request<JSONObject> request = NoHttp.createJsonObjectRequest(url);
request.setCacheMode(CacheMode.DEFAULT);
```

## 2. When the request network failure, and then read the cache
　　Request to the server success is returned data server, if the request to the server fails, read the cache data to return.
```java
Request<JSONObject> request = NoHttp.createJsonObjectRequest(url);
request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);
```

## 3. To read the first cache, there is no cache request to the server again
　　We know that the core of the ImageLoader besides memory optimization, one is found that the mainland has left image is used directly, without the request to the server, so do a ImageLoader NoHttp this very use.
　　If there is no cache request to the server, or use the cache:
```java
Request<JSONObject> request = NoHttp.createJsonObjectRequest(url);
// Change the caching pattern to IF_NONE_CACHE_REQUEST_NETWORK.
request.setCacheMode(CacheMode.IF_NONE_CACHE_REQUEST_NETWORK);
```
　　Request pictures, cache the image:
```java
Request<Bitmap> request = NoHttp.createImageRequest(imageUrl);
request.setCacheMode(CacheMode.IF_NONE_CACHE_REQUEST_NETWORK);
```

## 4. Only request network
　　Here can't read the cache, can't use Http304:
```java
Request<Bitmap> request = NoHttp.createImageRequest(imageUrl);
request.setCacheMode(CacheMode.ONLY_REQUEST_NETWORK);
...
```

## 5. Only read cache
　　Just read the cache, not request network and other operation:
```java
Request<Bitmap> request = NoHttp.createImageRequest(imageUrl);
request.setCacheMode(CacheMode.ONLY_READ_CACHE);
```

# 5. Cancel request
## Cancel a single request
　　User method `cancel()` of request.
```java
request.cancel();
```

## Cancel the specified request from the queue
　　To the request set a sign, to cancel the call queue cancelBySign can cancel all specify the sign of the request.
```java
request.setCancelSign(sign);
...
queue.cancelBySign(sign);
```

## Cancel all requests in the queue
```java
queue.cancelAll();
```

## Stop queue
　　After add the request to the queue in the queue to stop, the request will not be executed.
```java
RequestQueue queue = NoHttp.newRequestQueue();
...
queue.stop();
```

#6. The custom request type: FastJsonRequest
## Define the request object
```java
public class FastJsonRequest extends RestRequestor<JSONObject> {

    public FastJsonRequest(String url) {
	    super(url);
    }

    public FastJsonRequest(String url, RequestMethod requestMethod) {
	    super(url, requestMethod);
    }

    @Override
    public JSONObject parseResponse(String url, Headers headers, byte[] responseBody) {
	    String result = StringRequest.parseResponseString(url, headers, responseBody);
	    JSONObject jsonObject = null;
	    if (!TextUtils.isEmpty(result)) {
		    jsonObject = JSON.parseObject(result);
	    } else {
		    jsonObject = JSON.toJSON("{}");
	    }
	    return jsonObject;
    }

    @Override
    public String getAccept() {
	    return "application/json";
    }
}
```

## Using a custom request and NoHttp default no difference
```java
Request<JSONObject> mRequest = new FastJsonRequest(url, requestMethod);
queue.add(what, mRequest, responseListener);
```

# 7. Proguard

## Need to know
    NoHttp All classes can be confusing.
　　NoHttp1.0.0 Using the leve23 API, so have to use leve23 packaging.
　　NoHttp1.0.1 and higher using the reflection calls the high-level or low-level apis, so as long as it is more than leve9 SDK can be compiled.

## If you don't want to keep
```text
-dontwarn com.yolanda.nohttp.**
-keep class com.yolanda.nohttp.**{*;}
```

#License
```text
Copyright 2016 Yan Zhenjie

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[1]: https://github.com/yanzhenjie/NoHttp/
[2]: https://github.com/yanzhenjie/SkillGroupRule
[3]: http://blog.csdn.net/yanzhenjie1003
[4]: http://pan.baidu.com/s/1miEOtwG
[5]: http://www.nohttp.net
[6]: http://doc.nohttp.net
[7]: https://github.com/yanzhenjie/NoHttp/issues
[8]: https://github.com/yanzhenjie/NoHttp/blob/master/Jar/nohttp1.0.4.jar?raw=true
[9]: https://github.com/yanzhenjie/NoHttp/blob/master/nohttp_sample.apk?raw=true
[10]: http://www.nohttp.net/image/nohttp_logo.svg
[11]: https://github.com/yanzhenjie/NoHttp/blob/master/Jar/nohttp1.0.4-include-source.jar?raw=true
[12]: http://www.yanzhenjie.com
[13]: https://codeload.github.com/yanzhenjie/NoHttp/zip/1.0.4
[14]: https://github.com/yanzhenjie/NoHttp/blob/master/README-cn.md
[15]: http://www.ietf.org/rfc/rfc2616.txt