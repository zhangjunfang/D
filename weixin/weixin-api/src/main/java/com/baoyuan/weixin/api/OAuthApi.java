package com.baoyuan.weixin.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;

import com.baoyuan.weixin.API;
import com.baoyuan.weixin.bean.AccessToken;
import com.baoyuan.weixin.bean.Scope;
import com.baoyuan.weixin.parse.ResultParser;

public final class OAuthApi extends API {

	protected OAuthApi(WeixinApi weixin) {
		super(weixin);
	}

	/**
	 * 获取Authorization Code
	 * 
	 * 从 {@link Weixin} 从获取clientId，redirectUri，responseType为code ，其余参数默认
	 * 
	 * @see OAuth2#authorize(String, String, String, String, String, Boolean)
	 */
	public String authorize() {
		return authorize(false);
	}

	/**
	 * 获取Authorization Code
	 * 
	 * 从 {@link Weixin} 从获取clientId，redirectUri，responseType为code ，其余参数默认
	 * 
	 * @see OAuth2#authorize(String, String, String, String, String, Boolean)
	 */
	public String authorize(Boolean wechatRedirect) {
		return authorize(weixin.getRedirectUri(), wechatRedirect);
	}

	/**
	 * 获取Authorization Code
	 * 
	 * 从 {@link Weixin} 从获取clientId，responseType为code
	 * ，scope为snsapi_userinfo，其余参数默认
	 * 
	 * @see OAuth2#authorize(String, String, String, String, String, Boolean)
	 */
	public String authorize(String redirectUri) {
		return authorize(redirectUri, false);
	}

	/**
	 * 获取Authorization Code
	 * 
	 * 从 {@link Weixin} 从获取clientId，responseType为code
	 * ，scope为snsapi_userinfo，其余参数默认
	 * 
	 * @see OAuth2#authorize(String, String, String, String, String, Boolean)
	 */
	public String authorize(String redirectUri, Boolean wechatRedirect) {
		return authorize(weixin.getAppId(), redirectUri, "code",
				Scope.SNSAPI_USERINFO, null, wechatRedirect);
	}

	/**
	 * 获取Authorization Code
	 * 
	 * 文档地址：http://mp.weixin.qq.com/wiki/index.php?title=网页授权获取用户基本信息
	 * 
	 * @param appId
	 *            必须，公众号的唯一标识
	 * @param redirectUri
	 *            必须，授权后重定向的回调链接地址
	 * @param responseType
	 *            必须，返回类型，请填写code
	 * @param scope
	 *            必须，应用授权作用域
	 * @param state
	 *            重定向后会带上state参数，开发者可以填写任意参数值
	 * @param wechatRedirect
	 *            直接在微信打开链接，可以不填此参数。做页面302重定向时候，必须带此参数
	 */
	public String authorize(String appId, String redirectUri,
			String responseType, Scope scope, String state,
			Boolean wechatRedirect) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		weixin.addParameter(params, "appid", appId);
		weixin.addParameter(params, "redirect_uri", redirectUri);
		weixin.addParameter(params, "response_type", responseType);
		weixin.addParameter(params, "scope", scope);
		weixin.addNotNullParameter(params, "state", state);
		String result = "https://open.weixin.qq.com/connect/oauth2/authorize?"
				+ StringUtils.join(params, "&");
		if (Boolean.TRUE.equals(wechatRedirect)) {
			result = result + "#wechat_redirect";
		}
		return result;
	}

	/**
	 * 通过code换取网页授权access_token。从{@link Weixin}中获取appId和secret。
	 * 
	 * @param code
	 *            填写第一步获取的code参数
	 */
	public ResultParser<AccessToken> accessToken(String code) {
		return accessToken(weixin.getAppId(), weixin.getSecret(), code);
	}

	/**
	 * 通过code换取网页授权access_token。grantType值为authorization_code。
	 * 
	 * @param appId
	 *            公众号的唯一标识
	 * @param secret
	 *            公众号的appsecret
	 * @param code
	 *            填写第一步获取的code参数
	 */
	public ResultParser<AccessToken> accessToken(String appId, String secret,
			String code) {
		return accessToken(appId, secret, code, "authorization_code");
	}

	/**
	 * 通过code换取网页授权access_token
	 * 
	 * @param appId
	 *            公众号的唯一标识
	 * @param secret
	 *            公众号的appsecret
	 * @param code
	 *            填写第一步获取的code参数
	 * @param grantType
	 *            填写为authorization_code
	 */
	public ResultParser<AccessToken> accessToken(String appId, String secret,
			String code, String grantType) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		weixin.addParameter(params, "appid", appId);
		weixin.addParameter(params, "secret", secret);
		weixin.addParameter(params, "code", code);
		weixin.addParameter(params, "grant_type", grantType);
		String result = weixin.get(
				"https://api.weixin.qq.com/sns/oauth2/access_token", params);
		return ResultParser.parse(result, AccessToken.class);
	}

	/**
	 * 刷新access_token（如果需要）。从{@link Weixin}中获取appId
	 * 
	 * @param refreshToken
	 *            填写通过access_token获取到的refresh_token参数
	 */
	public ResultParser<AccessToken> refreshAccessToken(String refreshToken) {
		return refreshAccessToken(weixin.getAppId(), "refresh_token",
				refreshToken);
	}

	/**
	 * 刷新access_token（如果需要）
	 * 
	 * @param appId
	 *            公众号的唯一标识
	 * @param refreshToken
	 *            填写通过access_token获取到的refresh_token参数
	 */
	public ResultParser<AccessToken> refreshAccessToken(String appId,
			String refreshToken) {
		return refreshAccessToken(appId, "refresh_token", refreshToken);
	}

	/**
	 * 刷新access_token（如果需要）
	 * 
	 * @param appId
	 *            公众号的唯一标识
	 * @param grantType
	 *            填写为refresh_token
	 * @param refreshToken
	 *            填写通过access_token获取到的refresh_token参数
	 */
	public ResultParser<AccessToken> refreshAccessToken(String appId,
			String grantType, String refreshToken) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		weixin.addParameter(params, "appid", appId);
		weixin.addParameter(params, "grant_type", grantType);
		weixin.addParameter(params, "refresh_token", refreshToken);
		String result = weixin.get(
				"https://api.weixin.qq.com/sns/oauth2/refresh_token", params);
		return ResultParser.parse(result, AccessToken.class);
	}
}
