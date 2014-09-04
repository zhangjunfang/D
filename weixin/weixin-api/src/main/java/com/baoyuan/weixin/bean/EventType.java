package com.baoyuan.weixin.bean;

/**
 * 事件类型
 */
public enum EventType {

	/**
	 * 订阅
	 */
	SUBSCRIBE("subscribe"),

	/**
	 * 取消订阅
	 */
	UNSUBSCRIBE("unsubscribe"),

	/**
	 * 扫描二维码：用户已关注时的事件推送
	 */
	SCAN("scan"),

	/**
	 * 上报地理位置事件
	 */
	LOCATION("LOCATION"),

	/**
	 * 自定义菜单事件
	 */
	CLICK("CLICK");

	private String type;

	private EventType(String type) {
		this.type = type;
	}

	public String value() {
		return type;
	}

	@Override
	public String toString() {
		return type;
	}

	public static EventType parse(Object val) {
		if (SUBSCRIBE.type.equals(val)) {
			return SUBSCRIBE;
		}
		if (UNSUBSCRIBE.type.equals(val)) {
			return UNSUBSCRIBE;
		}
		if (SCAN.type.equals(val)) {
			return SCAN;
		}
		if (LOCATION.type.equals(val)) {
			return LOCATION;
		}
		if (CLICK.type.equals(val)) {
			return CLICK;
		}
		return null;
	}
}
