package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.io.RandomAccessFile;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class App {
	// ip BloomFilter,过滤相同ip
	public static BloomFilter filterIP = new BloomFilter();
	// 过滤相同usertoken
	public static BloomFilter filterUserToken = new BloomFilter();
	// usetoken 队列
	public static BlockingDeque<String> userTokenQueue = new LinkedBlockingDeque<String>();
	// image urlId deque
	public static BlockingDeque<String> imageUrlIdQueue = new LinkedBlockingDeque<String>();

	public static void main(String[] args) {
		ProxyHttpClient.getInstance().startDownLoadProxy();
		// ZhiHuHttpClient.getInstance().startCrawl();
		long start = System.currentTimeMillis();
		ZhiHuImageHttpClient.getInstance().startDownImage();
		long end = System.currentTimeMillis();
		System.out.println((end - start) / 1000 + "s");
	}

	public static void save() {
		try {
			// 打开一个随机访问文件流，按读写方式
			RandomAccessFile randomFile = new RandomAccessFile("src/main/resources/proxyip", "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);

			Proxy[] proxyArray = new Proxy[ProxyPool.proxySet.size()];
			int i = 0;
			for (Proxy proxy : ProxyPool.proxySet) {
				if (proxy != null) {
					proxyArray[i++] = proxy;
				}
			}
			for (int j = 0; j < proxyArray.length; j++) {
				if (filterIP.contains(proxyArray[j].getProxyStr())) {
					continue;
				}
				filterIP.add(proxyArray[j].getProxyStr());
				randomFile.writeBytes(proxyArray[j].getProxyStr() + "\r\n");
			}

			randomFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
