package com.baoyuan.weixin.parse;

/**
 * 微信2代表女性，0表示未知。暂且把微信的未知看作女性，
 * 如同新浪微博用户没有设置性别的时候会返回男性一样
 * 
 * @author Administrator
 *
 */
public enum Gender {

	MALE(1, "m", "男", "Male"), FEMALE(0, "f", "女", "Female"), UNKNOWN(-1, "n",
			"未知", "Unknown");

	int intValue;
	String code;
	String zhValue;
	String enValue;

	private Gender(int intValue, String code, String zhValue, String enValue) {
		this.intValue = intValue;
		this.code = code;
		this.zhValue = zhValue;
		this.enValue = zhValue;
	}

	public int value() {
		return intValue;
	}

	public String code() {
		return code;
	}

	public String text() {
		return zhValue;
	}

	public String enText() {
		return enValue;
	}

	@Override
	public String toString() {
		return zhValue;
	}

	public static Gender parse(Integer val) {
		if (val == null) {
			return null;
		}
		if (new Integer(1).equals(val)) {
			return MALE;
		}
		if (new Integer(0).equals(val) || new Integer(2).equals(val)) {
			// FIXME 微信2代表女性，0表示未知。暂且把微信的未知看作女性，如同新浪微博用户没有设置性别的时候会返回男性一样
			return FEMALE;
		}
		return UNKNOWN;
	}

	public static Gender parse(String val) {
		if (val == null) {
			return null;
		}
		if ("男".equals(val) || "m".equalsIgnoreCase(val)
				|| "male".equalsIgnoreCase(val) || "b".equalsIgnoreCase(val)
				|| "boy".equalsIgnoreCase(val)) {
			return MALE;
		}
		if ("女".equals(val) || "f".equalsIgnoreCase(val)
				|| "female".equalsIgnoreCase(val) || "g".equalsIgnoreCase(val)
				|| "girl".equalsIgnoreCase(val)) {
			return FEMALE;
		}
		return UNKNOWN;
	}

}