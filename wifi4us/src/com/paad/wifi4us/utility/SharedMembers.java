package com.paad.wifi4us.utility;

/**
 * Here is some shared members used by multiple components.
 * @author yangshi
 *
 */
public class SharedMembers {
	private static SharedMembers instance = null;

	synchronized public static SharedMembers getInstance() {
		if (instance == null) {
			instance = new SharedMembers();
			instance.init();
		}
		return instance;
	}

	public String getIMEI() {
		return "";
	}

	public String getUserId() {
		return "";
	}

	private SharedMembers() {
	}

	protected void init() {
		// TODO: add the initial code
	}

}
