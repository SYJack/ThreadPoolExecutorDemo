package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class ZhiHuImageDownTask implements Runnable {

	protected ZhiHuImageHttpClient imageHttpClient = ZhiHuImageHttpClient.getInstance();

	protected static ZhiHuHttpClient zhiHuHttpClient = ZhiHuHttpClient.getInstance();

	protected HttpRequestBase requestBase;

	public void run() {
		String urlId = getUrlIdFormQueue();
		Page page = null;
		List<ZhiHuImageBean> imageList = null;
		if (urlId != null) {
			try {
				String url = "https://www.zhihu.com/api/v4/questions/%s/answers?sort_by=default&"
						+ "include=data[*].is_normal,is_sticky,collapsed_by,suggest_edit,comment_count,collapsed_counts,reviewing_comments_count,can_comment,content,"
						+ "editable_content,voteup_count,reshipment_settings,comment_permission,mark_infos,created_time,"
						+ "updated_time,relationship.is_author,voting,is_thanked,is_nothelp,upvoted_followees;"
						+ "data[*].author.is_blocking,is_blocked,is_followed,voteup_count,message_thread_token,"
						+ "badge[?(type=best_answerer)].topics&limit=20&offset=%d";

				int offset = 0;
				while (true) {
					String startUrl = String.format(url, urlId, offset);
					HttpGet request = new HttpGet(startUrl);
					request.setHeader("authorization", "oauth " + ZhiHuHttpClient.getAuthorization());
					if (request != null) {
						Proxy proxy = ProxyPool.proxyQueue.take();
						this.requestBase = request;
						HttpHost httpHost = new HttpHost(proxy.getIp(), proxy.getPort());
						this.requestBase.setConfig(HttpClientUtil.getRequestConfigBuilder().setProxy(httpHost).build());
						page = imageHttpClient.getWebPage(requestBase);
						if (page.getStatusCode() == HttpStatus.SC_OK) {
							if (page.getHtml() != null) {
								System.out.println(page.getHtml());
								imageList = parserJson(page.getHtml());
							}
						}
					}
					offset += 20;
					System.out.println("OFFSET---->" + offset);
					if (imageList.size() == 0) {
						break;
					}
					imageList.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private List<ZhiHuImageBean> parserJson(String content) {
		List<ZhiHuImageBean> imageList = new ArrayList<ZhiHuImageBean>();
		String baseJsonPath = "$.data.length()";
		DocumentContext dc = JsonPath.parse(content);
		Integer userAnswerCount = dc.read(baseJsonPath);

		// 获取某个问题下的回答总数
		int total = dc.read("$.paging.totals");

		for (int i = 0; i < userAnswerCount; i++) {
			ZhiHuImageBean imageBean = new ZhiHuImageBean();
			String userBaseJsonPath = "$.data[" + i + "]";

			// 获取内容
			String imageUrlcontent = dc.read(userBaseJsonPath + ".content");
			HashSet<String> urlSet = getImageUrl(imageUrlcontent);
			// 获取标题
			String title = dc.read(userBaseJsonPath + ".question.title");

			if (urlSet.size() != 0) {
				setImageInfo(imageBean, "imageUrl", urlSet);
				setImageInfo(imageBean, "title", title);
				imageList.add(imageBean);
				downLoadPics(imageBean, "D:/知乎_开车/" + imageBean.getTitle());
			}
		}
		return imageList;
	}

	private void setImageInfo(ZhiHuImageBean imageBean, String fieldName, Object o) {
		try {
			Field field = imageBean.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(imageBean, o);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getUrlIdFormQueue() {
		try {
			return App.imageUrlIdQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private HashSet<String> getImageUrl(String content) {
		HashSet<String> urls = new HashSet<String>();
		Pattern pattern = Pattern.compile("data-original.+?\"(.*?)\"");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			urls.add(matcher.group(1));
		}
		return urls;
	}

	private String getIdentification(String s) {
		Pattern p = Pattern.compile("-(.*?)_r");
		Matcher matcher = p.matcher(s);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	// 传入zhiHuPicBean，创建文件夹，并下载图片
	public void downLoadPics(ZhiHuImageBean imageBean, String filePath) {
		try {
			boolean isSuccess = true;
			// 文件路径+标题
			String dir = filePath + imageBean.getTitle();
			// 创建
			File fileDir = new File(dir);
			fileDir.mkdirs();
			// 获取所有图片路径集合
			HashSet<String> zhiHuPics = imageBean.getImageUrl();
			// 循环下载图片
			for (String zhiHuPic : zhiHuPics) {
				String s = getIdentification(zhiHuPic);
				URL url = new URL(zhiHuPic);
				// 打开网络输入流
				DataInputStream dis = new DataInputStream(url.openStream());
				String newImageName = dir + "/" + "图片" + s + ".jpg";
				// 建立一个新的文件
				FileOutputStream fos = new FileOutputStream(new File(newImageName));
				byte[] buffer = new byte[1024];
				int length;
				System.out.println("正在下载...... " + s + ":图片......请稍后");
				// 开始填充数据
				while ((length = dis.read(buffer)) > 0) {
					fos.write(buffer, 0, length);
				}
				dis.close();
				fos.close();
				System.out.println(s + ":图片下载完毕......");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
