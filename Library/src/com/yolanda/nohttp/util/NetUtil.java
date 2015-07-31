/**
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yolanda.nohttp.util;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

import com.yolanda.nohttp.Logger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created in Jul 31, 2015 1:19:47 PM
 * 
 * @author YOLANDA
 */
public class NetUtil {

	/**
	 * Check the netwoek is enable
	 * 
	 * @param context
	 *        Access to <code>ConnectivityManager</code> services
	 * @return Available returns true, unavailable returns false
	 */
	public static boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) {
				return false;
			} else {
				NetworkInfo[] networkInfos = connectivity.getAllNetworkInfo();
				for (NetworkInfo networkInfo : networkInfos) {
					if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
						Logger.d("Print NetworkInfo:" + "\ngetExtraInfo():" + networkInfo.getExtraInfo()
								+ "\ngetReason():" + networkInfo.getReason() + "\ngetSubtype():"
								+ networkInfo.getSubtype() + "\ngetSubtypeName():" + networkInfo.getSubtypeName()
								+ "\ngetType():" + networkInfo.getType() + "\ngetTypeName():"
								+ networkInfo.getTypeName() + "\ngetDetailedState():" + networkInfo.getDetailedState()
								+ "\ngetState():" + networkInfo.getState());
						return true;
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * To determine whether a WiFi network is available
	 * 
	 * @param context
	 *        Access to <code>ConnectivityManager</code> services
	 * @return Open return true, close returns false
	 */
	public boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable() && mWiFiNetworkInfo.isConnected();
			}
		}
		return false;
	}

	/**
	 * To determine whether a mobile phone network is available
	 * 
	 * @param context
	 *        Access to <code>ConnectivityManager</code> services
	 * @return Open return true, close returns false
	 */
	public boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable() && mMobileNetworkInfo.isConnected();
			}
		}
		return false;
	}

	/**
	 * Check the GPRS whether available
	 * 
	 * @param context
	 *        Access to <code>ConnectivityManager</code> services
	 * @return Open return true, close returns false
	 */
	public static boolean isGPRSOpen(Context context) {
		Boolean isOpen = false;
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			Class<?> cmClass = connectivityManager.getClass();
			Class<?>[] argClasses = null;
			Method method = cmClass.getMethod("getMobileDataEnabled", argClasses);
			Object[] argObject = null;
			isOpen = (Boolean) method.invoke(connectivityManager, argObject);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return isOpen;
	}

	/**
	 * Open or close the GPRS
	 * 
	 * @param context
	 *        Access to <code>ConnectivityManager</code> services
	 * @param isEnable
	 *        Open to true, close to false
	 */
	public static void setGPRSEnable(Context context, boolean isEnable) {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			Class<?> cmClass = connectivityManager.getClass();
			Class<?>[] argClasses = new Class[1];
			argClasses[0] = boolean.class;
			Method method = cmClass.getMethod("setMobileDataEnabled", argClasses);
			method.invoke(connectivityManager, isEnable);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tet local ip adress
	 * 
	 * @return Such as：192.168.1.1
	 */
	public static String getLocalIPAddress() {
		String ipAddress = "";
		try {
			Enumeration<NetworkInterface> netfaces = NetworkInterface.getNetworkInterfaces();
			// 遍历所用的网络接口
			while (netfaces.hasMoreElements()) {
				NetworkInterface nif = netfaces.nextElement();// 得到每一个网络接口绑定的地址
				Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
				// 遍历每一个接口绑定的所有ip
				while (inetAddresses.hasMoreElements()) {
					InetAddress ip = inetAddresses.nextElement();
					if (!ip.isLoopbackAddress() && isIPv4Address(ip.getHostAddress())) {
						ipAddress = ip.getHostAddress();
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return ipAddress;
	}

	/**
	 * Ipv4 address check
	 */
	private static final Pattern IPV4_PATTERN = Pattern.compile(
			"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

	/**
	 * Check if valid IPV4 address
	 * 
	 * @param input
	 *        the address string to check for validity
	 * @return true if the input parameter is a valid IPv4 address
	 */
	public static boolean isIPv4Address(String input) {
		return IPV4_PATTERN.matcher(input).matches();
	}

	/* ===========以下是IPv6的检查，暂时用不到========== */

	// 未压缩过的IPv6地址检查
	private static final Pattern IPV6_STD_PATTERN = Pattern.compile("^[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){7}$");
	// 压缩过的IPv6地址检查
	private static final Pattern IPV6_HEX_COMPRESSED_PATTERN = Pattern
			.compile("^(([0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4}){0,5})?)" +  // 0-6
					"::" + "(([0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4}){0,5})?)$");// 0-6 hex fields

	/**
	 * Check whether the parameter is effective standard (uncompressed) IPv6 address
	 * 
	 * @param input IPV6 adress
	 * @see #isIPv6HexCompressedAddress(String)
	 */
	public static boolean isIPv6StdAddress(final String input) {
		return IPV6_STD_PATTERN.matcher(input).matches();
	}

	/**
	 * Check whether the parameter is effective compression IPv6 address
	 * 
	 * @param input IPV6 adress
	 * @see #isIPv6StdAddress(String)
	 */
	public static boolean isIPv6HexCompressedAddress(final String input) {
		int colonCount = 0;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == ':') {
				colonCount++;
			}
		}
		return colonCount <= 7 && IPV6_HEX_COMPRESSED_PATTERN.matcher(input).matches();
	}

	/**
	 * Check whether the IPV6 address of compressed or uncompressed
	 * 
	 * @param input IPV6 adress
	 * @see #isIPv6HexCompressedAddress(String)
	 * @see #isIPv6StdAddress(String)
	 */
	public static boolean isIPv6Address(final String input) {
		return isIPv6StdAddress(input) || isIPv6HexCompressedAddress(input);
	}

}
