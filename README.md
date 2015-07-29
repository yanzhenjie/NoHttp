# NoHttp
开发这个项目的宗旨就是让API使用简单起来

它为开发人员提供了非常简单的API来访问HTTP/HTTPS、异步请求、同步请求、文件上传、自定义请求头、自定义参数，你不用理睬复杂的HttpURLConnection,所以它的名字是NoHttp。

使用异步请求,在响应方法可以直接更新UI,不用再使用Handler。

API与OKHttp几乎是一致的,在未来支持图像缓存、文件下载。

目前的功能有：Get、Post、文件上传、自定义请求头、自定义参数，响应结果包括:是否成功、响应数据、数据长度、HTTP响应头集合。

API非常样简单，一个Request对象提供所有请求参数，一个Response对象提供了所有响应。

这是一个简单的Demo，有很多用法需要自己去探索:

Development of the project objective is to make the API is simple to use

A very simple API for developers to access the HTTP/HTTPS, support asynchronous and synchronous request, You will not ignore complex HttpURLConnection, so its name is NoHttp.

When you use an asynchronous request, in the response method can update the UI directly, need not use Handler.

API and OKHttp is almost consistent, Future will support image cache file download.

Now is the function of: Get, Post, file upload, custom request header.Response results including: Success and failure, response data, the length of the byte data, the HTTP response headers collection,…….

API is very simple, a Request object provides all of the Request parameters, a Response object provides all the response information.

This is a simple Demo, Need to go to explore other use method:
```
public class MainActivity implements OnResponseListener {
	
	//OnResponseListener
	//When can update the UI directly, after receive the response result OnResponseListener used to accept the response as a result, whether success or failure

	/**
	 * Mark request "baidu"
	 */
	private static final int REQUST_BAIDU = 0x001;
	/**
	 * NoHttp angel
	 */
	private NoHttp noHttp = NoHttp.getInstance();

	/**
	 * 异步请求
	 * ——————————
	 * An asynchronous request
	 */
	private void asyncRequest() {
		Request request = buildRequest();
		noHttp.requestAsync(request, REQUST_BAIDU, this);
	}

	/**
	 * 同步请求
	 * ——————————
	 * An synchronous request
	 */
	private void syncRequest() {
		new Thread() {
			@Override
			public void run() {
				Request request = buildRequest();
				BaseResponse baseResponse = noHttp.requestSync(request);
				if (baseResponse.isSuccessful()) {
					Response response = (Response) baseResponse;
					Logger.i("Request Result:" + response);
				} else {
					ResponseError responseError = (ResponseError) baseResponse;
					Logger.i("Request filed:" + responseError.getErrorInfo());
				}
			};
		}.start();
	}		

	@Override
	public void onNoHttpResponse(int what, Response response) {
		switch (what) {
		case REQUST_BAIDU:
			Logger.i("Baidu Request Result：\n" + response.string());
			Logger.i("Baidu toString() Result：\n" + response.toString());
			break;
		default:
			break;
		}
	}

	@Override
	public void onNoHttpError(int what, ResponseError responseError) {
		Logger.e("Request Filed：" + responseError.getErrorInfo());
		Toast.makeText(this, responseError.getErrorInfo(), Toast.LENGTH_LONG).show();
	}

	/**
	 * For other methods to build an HTTP request
	 */
	private Request buildRequest() {
		// 1. The requested address
		// 2. Choose the way to the request
		Request request = new Request("http://www.baidu.com/s", RequestMethod.GET);

		// 3. Add request header,If you don't need to add can remove the following code
		request.addHeader("Accept-Encoding", "gzip,deflate,sdch");
		request.addHeader("Author", "name|pass");
		// The two effect is the same, And the default KeepAlive
		request.addHeader("Connection", "Keep-Alive");
		request.setKeppAlive(true);

		// 4. Add the request parameters, If you don't need to add can remove the following code
		request.add("wd", "Android Develop");
		// 5. Upload a file,If you want to upload files can be added directly
		// Bitmap bitmap = null;
		// request.add("picture", bitmap, "head.jpg");
		return request;
	}
}
```
