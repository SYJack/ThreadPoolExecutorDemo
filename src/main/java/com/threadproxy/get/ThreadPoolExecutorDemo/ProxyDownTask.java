package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpStatus;

public class ProxyDownTask implements Runnable {

	private String url;
	private boolean proxyFlag;// 是否通过代理下载
	protected static ProxyHttpClient proxyHttpClient = ProxyHttpClient.getInstance();

	public ProxyDownTask(String url, boolean proxyFlag) {
		this.url = url;
		this.proxyFlag = proxyFlag;
	}

	public void run() {
		long requestStartTime = System.currentTimeMillis();
		try {
			Page page = proxyHttpClient.getWebPage(url);
			if (page.getStatusCode() == HttpStatus.SC_OK) {
				System.out.println("当前下载线程--->" + Thread.currentThread().getName() + "url-->" + url);
				ProxyListPageParser parser = ProxyListPageParserFactory
						.getProxyListPageParser(ProxyPool.proxyMap.get(url));
				List<Proxy> proxyList = parser.parse(page.getHtml());
				for (Proxy p : proxyList) {
					if (!ProxyPool.proxySet.contains(p.getProxyStr())) {
						// proxyHttpClient.getProxyTestThreadExecutor().execute(new
						// ProxyTestTask(p));
						proxyHttpClient.getProxyTestThreadExecutor().execute(new ProxyTestTask(p));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			retry();
		}
		long requestEndTime = System.currentTimeMillis();
		System.out.println("耗时--->" + (requestEndTime - requestStartTime) / 1000 + "s");
	}

	private void retry() {
		proxyHttpClient.getProxyDownloadThreadExecutor().execute(new ProxyDownTask(url, false));
	}

}
