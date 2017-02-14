package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;

/**
 * 代理检测task 通过访问知乎首页，能否正确响应 将可用代理添加到DelayQueue延时队列中
 */
public class ProxyTestTask implements Runnable {
	private final static Logger logger = Logger.getLogger(ProxyTestTask.class);
	private Proxy proxy;

	private Map<Thread, BloomFilter> filter = new ConcurrentHashMap<Thread, BloomFilter>();

	public ProxyTestTask(Proxy p) {
		this.proxy = p;
	}

	public void run() {
		long startTime = System.currentTimeMillis();
		HttpGet request = new HttpGet("https://www.zhihu.com");
		try {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000)
					.setConnectionRequestTimeout(10000).setProxy(new HttpHost(proxy.getIp(), proxy.getPort()))
					.setCookieSpec(CookieSpecs.STANDARD).build();
			request.setConfig(requestConfig);
			Page page = ProxyHttpClient.getInstance().getWebPage(request);
			long endTime = System.currentTimeMillis();
			String logStr = Thread.currentThread().getName() + " " + proxy.getProxyStr() + "  executing request "
					+ page.getUrl() + " response statusCode:" + page.getStatusCode() + "  request cost time:"
					+ (endTime - startTime) + "ms";
			if (page == null || page.getStatusCode() != 200) {
				logger.warn(logStr);
				return;
			}
			if (!ProxyPool.proxySet.contains(proxy)) {
				logger.debug(proxy.toString() + "----------代理可用--------请求耗时:" + (endTime - startTime) + "ms");
				System.out.println(Thread.currentThread().getName() + proxy.toString() + "----------代理可用--------请求耗时:"
						+ (endTime - startTime) + "ms");
				ProxyPool.lock.writeLock().lock();
				try {
					ProxyPool.proxySet.add(proxy);
				} finally {
					ProxyPool.lock.writeLock().unlock();
				}
				ProxyPool.proxyQueue.add(proxy);
				// 保存数据到文件
				App.save();
			}
			request.releaseConnection();
		} catch (IOException e) {
			logger.debug("IOException:", e);
		} finally {
			if (request != null) {
				request.releaseConnection();
			}
		}
	}

}
