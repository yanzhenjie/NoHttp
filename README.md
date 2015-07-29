# NoHttp
It provides very simple API for developers to access the HTTP, support asynchronous and synchronous request, you will never have to touch the complex HttpURLConnection, so Its name is NoHttp

Using an asynchronous request, within the response method can update the UI directly, not to use Handler

API is almost consistent with OKHttp, support in the future image cache, the HTTP request and download files
Request can support:
The Get, Post, file upload, custom request header, custom parameters
The returned result includes:
If successful, the response data, length, bytes data collection, the HTTP response headers

API is a bit simple, a few requests to provide all the request parameters, provides all of the response.And you want to get unexpected results

Here is a simple to use, there are a lot of usage need yourself to explore:
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
