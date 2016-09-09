package com.sqa.jf;

import com.sqa.jf.util.helpers.AppBasics;

public class BasicJavaApp {
	public static void main(String[] args) {
		BasicJavaApp myApp = new BasicJavaApp();
		start(myApp);
	}

	private static void start(BasicJavaApp myApp) {
		myApp.init();
		myApp.main();
		myApp.tearDown();
	}

	private String appName = "Basic Java App";
	private String user;

	public String getAppName() {
		return appName;
	}

	public String getUser() {
		return user;
	}

	private void init() {
		user = AppBasics.welcomeUserAndGetUsersName(appName);
	}

	public void main() {
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setUser(String user) {
		this.user = user;
	}

	private void tearDown() {
		AppBasics.farewellUser(user, appName);
	}
}
