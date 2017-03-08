package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
	}

	public static void main(String[] args) {
		try {
			String content = HttpClientUtil.getWebPage(
					"https://www.zhihu.com/api/v4/members/sheng-fan-jin-yi/followees?include=data[*].educations,employments,answer_count,business,locations,articles_count,follower_count,gender,following_count,question_count,voteup_count,thanked_count,is_followed,is_following,badge[?(type=best_answerer)].topics&offset=0&limit=20");
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
