package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ZhiHuImageHttpClient extends AbstractHttpClient {

	private volatile static ZhiHuImageHttpClient instance;

	public ZhiHuImageHttpClient() {
		initThreadPool();
	}

	public static ZhiHuImageHttpClient getInstance() {
		if (instance == null) {
			synchronized (ZhiHuImageHttpClient.class) {
				if (instance == null) {
					instance = new ZhiHuImageHttpClient();
				}
			}
		}
		return instance;
	}

	private ThreadPoolExecutor zhiHuImageDownThreadPoolExecutor;

	private void initThreadPool() {
		zhiHuImageDownThreadPoolExecutor = new ThreadPoolExecutor(100, 100, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(2000), new ThreadPoolExecutor.DiscardPolicy());
	}

	public void startDownImage() {
		zhiHuImageDownThreadPoolExecutor.execute(new ZhiHuImageDownTask());
	}

	public ThreadPoolExecutor getZhiHuImageDownThreadPoolExecutor() {
		return zhiHuImageDownThreadPoolExecutor;
	}
}
