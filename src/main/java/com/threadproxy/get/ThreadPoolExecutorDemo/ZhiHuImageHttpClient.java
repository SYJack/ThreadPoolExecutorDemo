package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
		List<String> urls = getCollectionUrlLists();
		for (String urlId : urls) {
			App.imageUrlIdQueue.add(urlId);
		}
		for (int i = 0; i < urls.size(); i++) {
			zhiHuImageDownThreadPoolExecutor.execute(new ZhiHuImageDownTask());
		}

	}

	public ThreadPoolExecutor getZhiHuImageDownThreadPoolExecutor() {
		return zhiHuImageDownThreadPoolExecutor;
	}

	// https://www.zhihu.com/collection/38123480
	private List<String> getCollectionUrlLists() {
		List<String> imageUrls = new ArrayList<String>();
		String[] collectionId = { "62864589", "102112319" };
		int i = 1;
		try {
			for (String id : collectionId) {
				String url = "https://www.zhihu.com/collection/" + id + "?page=";

				String pageCountContent = HttpClientUtil.getWebPage(url + i);
				Document doc = Jsoup.parse(pageCountContent);
				int pageCounts = 0;
				// 获取页数
				Elements pages = doc.select("[class=zm-invite-pager]");
				for (Element page : pages) {
					Elements a = page.getElementsByTag("a");
					pageCounts = Integer.parseInt(a.get(2).text());
				}
				for (int j = 1; j < pageCounts; j++) {
					String urlListContent = HttpClientUtil.getWebPage(url + j);
					Document urlDoc = Jsoup.parse(urlListContent);
					Elements urls = urlDoc.select("[class=zm-item-rich-text expandable js-collapse-body]");
					for (Element element : urls) {
						imageUrls.add(getRealUrlId(element.attr("data-entry-url")));
					}

				}
				return imageUrls;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getRealUrlId(String url) {
		// 将http://www.zhihu.com/question/22355264/answer/21102139
		// 转化成http://www.zhihu.com/question/21102139
		// 否则不变
		Pattern pattern = Pattern.compile("question/(.*?)/");
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	public static void main(String[] args) {
		String s = "https://pic3.zhimg.com/v2-79208bcacabd545f9b34eafef9e5a962_r.jpg";
		Pattern p = Pattern.compile("-(.*?)_r");
		Matcher matcher = p.matcher(s);
		if (matcher.find()) {
			System.out.println(matcher.group(1));
		}

	}
}
