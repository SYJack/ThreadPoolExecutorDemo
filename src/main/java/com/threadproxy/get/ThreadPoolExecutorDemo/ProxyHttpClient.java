package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProxyHttpClient extends AbstractHttpClient {

	// 定义一个静态私有变量(不初始化，不使用final关键字，使用volatile保证了多线程访问时instance变量的可见性，避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用)
	private static volatile ProxyHttpClient instance;
	private ThreadPoolExecutor proxyDownloadThreadExecutor;
	private ThreadPoolExecutor proxyTestThreadExecutor;

	public static ProxyHttpClient getInstance() {
		if (instance == null) {
			synchronized (ProxyHttpClient.class) {
				if (instance == null) {
					instance = new ProxyHttpClient();
				}
			}
		}
		return instance;
	}

	public ProxyHttpClient() {
		initThread();
	}

	private void initThread() {
		proxyTestThreadExecutor = new ThreadPoolExecutor(100, 100, 0, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(10000), new ThreadPoolExecutor.DiscardPolicy());
		proxyDownloadThreadExecutor = new ThreadPoolExecutor(10, 10, 0, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	public void startDownLoadProxy() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					for (String url : ProxyPool.proxyMap.keySet()) {
						proxyDownloadThreadExecutor.execute(new ProxyDownTask(url, false));
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}
			}
		}).start();
		// new Thread(new SaveProxyTask()).start();
	}

	public ThreadPoolExecutor getProxyDownloadThreadExecutor() {
		return proxyDownloadThreadExecutor;
	}

	public ThreadPoolExecutor getProxyTestThreadExecutor() {
		return proxyTestThreadExecutor;
	}
}
