package com.threadproxy.get.ThreadPoolExecutorDemo;

import java.io.Serializable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Proxy implements Delayed, Serializable {

	private static final long serialVersionUID = 1L;
	private String ip;
	private int port;

	public Proxy(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int compareTo(Delayed arg0) {
		return 0;
	}

	public long getDelay(TimeUnit arg0) {
		return 0;
	}

	public String getProxyStr() {
		return ip + ":" + port;
	}

}
