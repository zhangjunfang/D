package com.baoyuan.weixin.bean;

import org.json.JSONObject;

import com.baoyuan.weixin.parse.AbstractParser;
import com.baoyuan.weixin.parse.ResultParser;

/**
 * 网页授权接口调用凭证
 * 
 * @author 张莹
 * 
 */
public class AccessToken extends AbstractParser {

	public AccessToken() {
	}

	private AccessToken(JSONObject jsonObject) {
		super(jsonObject);
	}

	private String token;// 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
	private Long expiresIn;// access_token接口调用凭证超时时间，单位（秒）
	private String refreshToken;// 用户刷新access_token
	private String openId;// 用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
	private Scope scope;// 用户授权的作用域，使用逗号（,）分隔

	/**
	 * 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
	 */
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * access_token接口调用凭证超时时间，单位（秒）
	 */
	public Long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}

	/**
	 * 用户刷新access_token
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * 用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
	 */
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	/**
	 * 用户授权的作用域，使用逗号（,）分隔
	 */
	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public static AccessToken parse(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}
		AccessToken obj = new AccessToken(jsonObject);
		obj.token = jsonObject.getString("access_token");
		obj.openId = ResultParser.toString(jsonObject.opt("openid"));
		obj.expiresIn = ResultParser.parseLong(jsonObject.opt("expires_in"));
		obj.refreshToken = ResultParser.toString(jsonObject.opt("refresh_token"));
		obj.scope = Scope.parse(jsonObject.opt("scope"));
		return obj;
	}
}
