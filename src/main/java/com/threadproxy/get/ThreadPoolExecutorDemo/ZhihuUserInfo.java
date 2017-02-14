package com.threadproxy.get.ThreadPoolExecutorDemo;

public class ZhihuUserInfo {
	private int id;// id

	private String userToken;

	private String userName;// 姓名

	private int gender;// 性别

	private String business;// 行业

	private String company;// 公司

	private String position;// 职位;

	private String education;// 大学

	private String major;// 专业

	private String location;// 所在地

	private String headline;// 简介介绍

	private int answersNum;// 回答数量

	private int questions;// 提问数

	private int articlesNum;// 文章数

	private int starsNum;// 获得赞同数

	private int thxNum;// 获得感谢数

	private int followingNum;// 关注的人

	private int followersNum;// 关注者数量

	private String portrait;// 头像url

	private String url;// 用户url

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public int getAnswersNum() {
		return answersNum;
	}

	public void setAnswersNum(int answersNum) {
		this.answersNum = answersNum;
	}

	public int getQuestions() {
		return questions;
	}

	public void setQuestions(int questions) {
		this.questions = questions;
	}

	public int getArticlesNum() {
		return articlesNum;
	}

	public void setArticlesNum(int articlesNum) {
		this.articlesNum = articlesNum;
	}

	public int getStarsNum() {
		return starsNum;
	}

	public void setStarsNum(int starsNum) {
		this.starsNum = starsNum;
	}

	public int getThxNum() {
		return thxNum;
	}

	public void setThxNum(int thxNum) {
		this.thxNum = thxNum;
	}

	public int getFollowingNum() {
		return followingNum;
	}

	public void setFollowingNum(int followingNum) {
		this.followingNum = followingNum;
	}

	public int getFollowersNum() {
		return followersNum;
	}

	public void setFollowersNum(int followersNum) {
		this.followersNum = followersNum;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "ZhihuUserInfo [id=" + id + ", userToken=" + userToken + ", 用户名=" + userName + ", 性别=" + gender + ", 行业="
				+ business + ", 公司=" + company + ", 职位=" + position + ", 大学=" + education + ", 专业=" + major + ", 所在地="
				+ location + ", 回答数=" + answersNum + ", 提问数=" + questions + ", 文章数=" + articlesNum + ", 赞同数=" + starsNum
				+ ", 感谢数=" + thxNum + ", 关注人数=" + followingNum + ", 关注者数量=" + followersNum + ", portrait=" + portrait
				+ ", url=" + url + "]";
	}

}
