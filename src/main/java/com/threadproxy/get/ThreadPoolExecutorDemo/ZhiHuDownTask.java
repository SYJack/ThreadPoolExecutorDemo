package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

public class ZhiHuDownTask implements Runnable {

	protected HttpRequestBase requestBase;
	protected static ZhiHuHttpClient zhiHuHttpClient = ZhiHuHttpClient.getInstance();
	/**
	 * Thread-数据库连接
	 */
	private static Map<Thread, Connection> connectionMap = new ConcurrentHashMap<Thread, Connection>();
	private static MongoDBDaoImpl mongoDBDaoImpl = MongoDBDaoImpl.getInstance("127.0.0.1", 27017);

	public ZhiHuDownTask() {
	}

	public void run() {
		Page page = null;
		String userToken = getUserTokenFormQueue();
		if (userToken != null) {
			try {
				String url = "https://www.zhihu.com/api/v4/members/%s/followees?"
						+ "include=data[*].educations,employments,answer_count,business,locations,articles_count,follower_count,"
						+ "gender,following_count,question_count,voteup_count,thanked_count,is_followed,is_following,"
						+ "badge[?(type=best_answerer)].topics&offset=%d&limit=20";
				String startUrl = String.format(url, userToken, 0);
				HttpGet request = new HttpGet(startUrl);
				request.setHeader("authorization", "oauth " + ZhiHuHttpClient.getAuthorization());

				if (request != null) {
					Proxy proxy = ProxyPool.proxyQueue.take();
					this.requestBase = request;
					HttpHost host = new HttpHost(proxy.getIp(), proxy.getPort());
					this.requestBase.setConfig(HttpClientUtil.getRequestConfigBuilder().setProxy(host).build());
					page = zhiHuHttpClient.getWebPage(requestBase);
					if (page.getStatusCode() == HttpStatus.SC_OK) {
						if (page.getHtml() != null) {
							parserJson(page.getHtml());
						}
					} else {
						zhiHuHttpClient.getZhiHuDownLoadThreadPoolExecutor().execute(new ZhiHuDownTask());
					}
					System.out.println("设置代理------->>>" + proxy.getProxyStr());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (requestBase != null) {
					requestBase.releaseConnection();
				}
			}
		}
	}

	private void parserJson(String content) {
		List<ZhihuUserInfo> userList = new ArrayList<ZhihuUserInfo>();
		String baseJsonPath = "$.data.length()";
		DocumentContext dc = JsonPath.parse(content);
		Integer userCount = dc.read(baseJsonPath);
		for (int i = 0; i < userCount; i++) {
			ZhihuUserInfo userInfo = new ZhihuUserInfo();

			String userBaseJsonPath = "$.data[" + i + "]";
			String userToken = dc.read(userBaseJsonPath + ".url_token");
			if (App.filterUserToken.contains(userToken)) {
				continue;
			}
			if (userToken != null && userToken != "null") {
				setUserInfoByJsonPth(userInfo, "userToken", dc, userBaseJsonPath + ".url_token");
				setUserInfoByJsonPth(userInfo, "userName", dc, userBaseJsonPath + ".name");// username
				setUserInfoByJsonPth(userInfo, "gender", dc, userBaseJsonPath + ".gender");// 性别
				setUserInfoByJsonPth(userInfo, "followingNum", dc, userBaseJsonPath + ".following_count");// 关注人数
				setUserInfoByJsonPth(userInfo, "location", dc, userBaseJsonPath + ".locations[0].name");// 位置
				setUserInfoByJsonPth(userInfo, "headline", dc, userBaseJsonPath + ".headline");
				setUserInfoByJsonPth(userInfo, "business", dc, userBaseJsonPath + ".business.name");// 行业
				setUserInfoByJsonPth(userInfo, "company", dc, userBaseJsonPath + ".employments[0].company.name");// 公司
				setUserInfoByJsonPth(userInfo, "position", dc, userBaseJsonPath + ".employments[0].job.name");// 职位
				setUserInfoByJsonPth(userInfo, "education", dc, userBaseJsonPath + ".educations[0].school.name");// 学校
				setUserInfoByJsonPth(userInfo, "major", dc, userBaseJsonPath + ".educations[0].major.name");// 专业
				setUserInfoByJsonPth(userInfo, "answersNum", dc, userBaseJsonPath + ".answer_count");// 回答数
				setUserInfoByJsonPth(userInfo, "questions", dc, userBaseJsonPath + ".question_count");// 提问数
				setUserInfoByJsonPth(userInfo, "articlesNum", dc, userBaseJsonPath + ".articles_count");// 文章数
				setUserInfoByJsonPth(userInfo, "followersNum", dc, userBaseJsonPath + ".follower_count");// 粉丝数
				setUserInfoByJsonPth(userInfo, "starsNum", dc, userBaseJsonPath + ".voteup_count");// 赞同数
				setUserInfoByJsonPth(userInfo, "thxNum", dc, userBaseJsonPath + ".thanked_count");// 感谢数

				userInfo.setUrl("https://www.zhihu.com/people/" + userToken);

				Integer gender = dc.read(userBaseJsonPath + ".gender");
				if (gender != null && gender == 0) {
					String protrait = dc.read(userBaseJsonPath + ".avatar_url_template");
					String protraitReplace = protrait.replace("{size}", "xl");
					userInfo.setPortrait(protraitReplace);
					InputStream in = zhiHuHttpClient.getWebPageInputStream(protraitReplace);
					savePicToDisk(in, "D:/zhihu/", userToken + ".jpg");
				}
				userList.add(userInfo);
				mongoDBDaoImpl.insert("zhihudb", "zhihuuserinfosss", userInfo);

				try {
					App.filterUserToken.add(userToken);
					App.userTokenQueue.put(userToken);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * jsonPath获取值，并通过反射直接注入到user中
	 * 
	 * @param user
	 * @param fieldName
	 * @param dc
	 * @param jsonPath
	 */
	private void setUserInfoByJsonPth(ZhihuUserInfo userInfo, String fieldName, DocumentContext dc, String jsonPath) {
		try {
			Object o = dc.read(jsonPath);
			Field field = userInfo.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(userInfo, o);
		} catch (Exception e) {
			e.printStackTrace();
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

	private static void savePicToDisk(InputStream in, String dirPath, String filePath) {

		try {
			File dir = new File(dirPath);
			if (dir == null || !dir.exists()) {
				dir.mkdirs();
			}
			// 文件真实路径
			String realPath = dirPath.concat(filePath);
			File file = new File(realPath);
			if (file == null || !file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = in.read(buf)) != -1) {
				fos.write(buf, 0, len);
			}
			fos.flush();
			fos.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
