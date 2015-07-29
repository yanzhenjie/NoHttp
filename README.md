# NoHttp
It provides super convenient API for developers to APP access to the network, it is a secondary packaging of java.net.URL, here you will never see HTTP, so I called the project NoHttp
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
