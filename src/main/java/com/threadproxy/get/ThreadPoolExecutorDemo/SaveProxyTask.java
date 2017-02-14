package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.io.RandomAccessFile;

public class SaveProxyTask implements Runnable {

	public void run() {
		while (true) {
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
				for (Proxy proxy : proxyArray) {
					randomFile.writeBytes(proxy.getProxyStr() + "\r\n");
				}
				randomFile.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
