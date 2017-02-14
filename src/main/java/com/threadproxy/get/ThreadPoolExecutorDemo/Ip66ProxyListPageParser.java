package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Ip66ProxyListPageParser implements ProxyListPageParser {

	public List<Proxy> parse(String content) {
		Document document = Jsoup.parse(content);
		Elements elements = document.select("table tr:gt(1)");
		List<Proxy> proxyList = new ArrayList<Proxy>(elements.size());
		for (Element element : elements) {
			String ip = element.select("td:eq(0)").first().text();
			String port = element.select("td:eq(1)").first().text();
			String isAnonymous = element.select("td:eq(3)").first().text();
			// if(!anonymousFlag || isAnonymous.contains("匿")){
			proxyList.add(new Proxy(ip, Integer.valueOf(port)));
			// }
		}
		return proxyList;
	}

}
