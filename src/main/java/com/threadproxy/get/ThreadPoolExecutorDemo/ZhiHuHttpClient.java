package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZhiHuHttpClient extends AbstractHttpClient {

	private volatile static ZhiHuHttpClient instance;
	/**
	 * request header 获取列表页时，必须带上
	 */
	private static String authorization;

	private ZhiHuHttpClient() {
		authorization = initAuthorization();
		// 初始化线程池
		initThreadPoo();
	}

	public static ZhiHuHttpClient getInstance() {
		if (instance == null) {
			synchronized (ZhiHuHttpClient.class) {
				if (instance == null) {
					instance = new ZhiHuHttpClient();
				}
			}
		}
		return instance;
	}

	private ThreadPoolExecutor zhihuDownLoadThreadPooExecutor;

	private void initThreadPoo() {

		zhihuDownLoadThreadPooExecutor = new ThreadPoolExecutor(100, 100, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(2000), new ThreadPoolExecutor.DiscardPolicy());
	}

	// 开始
	public void startCrawl() {
		try {
			App.userTokenQueue.put("sheng-fan-jin-yi");
			if (!App.filterUserToken.contains("sheng-fan-jin-yi")) {
				App.filterUserToken.add("sheng-fan-jin-yi");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (true) {
			zhihuDownLoadThreadPooExecutor.execute(new ZhiHuDownTask());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public String getUserTokenFormQueue() {
		try {
			return App.userTokenQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 初始化authorization
	 * 
	 * @return
	 */
	private String initAuthorization() {
		String content = null;
		try {
			content = HttpClientUtil.getWebPage("https://www.zhihu.com/people/sheng-fan-jin-yi");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Pattern pattern = Pattern.compile("https://static\\.zhihu\\.com/heifetz/main\\.app\\.([0-9]|[a-z])*\\.js");
		Matcher matcher = pattern.matcher(content);
		String jsSrc = null;
		if (matcher.find()) {
			jsSrc = matcher.group(0);
		} else {
			throw new RuntimeException("not find javascript url");
		}
		String jsContent = null;
		try {
			jsContent = HttpClientUtil.getWebPage(jsSrc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pattern = Pattern.compile("CLIENT_ALIAS=\"(([0-9]|[a-z])*)\"");
		matcher = pattern.matcher(jsContent);
		if (matcher.find()) {
			String authorization = matcher.group(1);
			return authorization;
		}
		throw new RuntimeException("not get authorization");
	}

	public static String getAuthorization() {
		return authorization;
	}

	public ThreadPoolExecutor getZhiHuDownLoadThreadPoolExecutor() {
		return zhihuDownLoadThreadPooExecutor;
	}
}
