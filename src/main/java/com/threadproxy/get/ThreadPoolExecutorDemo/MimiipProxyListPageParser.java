package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class MimiipProxyListPageParser implements ProxyListPageParser {

	public List<Proxy> parse(String content) {
		Document document = Jsoup.parse(content);
		Elements elements = document.select("table[class=list] tr");
		List<Proxy> proxyList = new ArrayList<Proxy>(elements.size());
		for (int i = 1; i < elements.size(); i++) {
			String isAnonymous = elements.get(i).select("td:eq(3)").first().text();
			if (!anonymousFlag || isAnonymous.contains("匿")) {
				String ip = elements.get(i).select("td:eq(0)").first().text();
				String port = elements.get(i).select("td:eq(1)").first().text();
				proxyList.add(new Proxy(ip, Integer.valueOf(port)));
			}
		}
		return proxyList;
	}

}
