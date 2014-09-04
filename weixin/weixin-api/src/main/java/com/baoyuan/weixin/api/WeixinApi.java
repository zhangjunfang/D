package com.baoyuan.weixin.api;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.baoyuan.weixin.SDK;
import com.baoyuan.weixin.bean.AccessToken;
import com.baoyuan.weixin.bean.Message;
import com.baoyuan.weixin.bean.QRCreation;
import com.baoyuan.weixin.bean.QRTicket;
import com.baoyuan.weixin.bean.QRType;
import com.baoyuan.weixin.exception.WeixinException;
import com.baoyuan.weixin.parse.ErrorParser;
import com.baoyuan.weixin.parse.ResultParser;

public final class WeixinApi extends SDK {

	private String identity;

	private String appId;

	private String secret;

	private String redirectUri;

	private String token;

	private AccessToken accessToken;

	private Date accessTokenTime;

	private MenuApi menuApi;

	private OAuthApi oAuthApi;

	private UserApi userApi;

	private GroupApi groupApi;

	private MediaApi mediaApi;

	/**
	 * 通过token、appId和secret实例化微信SDK，支持微信基础和高级接口。推荐适用
	 * 
	 * @param appId
	 *            公众号的唯一标识
	 * @param secret
	 *            公众号的appsecret
	 * @param token
	 *            在公众平台网站的高级功能 –
	 *            开发模式页，点击“成为开发者”按钮，填写URL和Token，其中URL是开发者用来接收微信服务器数据的接口URL
	 *            。Token可由开发者可以任意填写 ，用作生成签名（该Token会和接口URL中包含的Token进行比对，从而验证安全性）。
	 */
	public WeixinApi(String wid, String appId, String secret, String token,
			String redirectUri) {
		this.identity = wid;
		this.appId = appId;
		this.secret = secret;
		this.token = token;
		this.redirectUri = redirectUri;
	}

	/**
	 * 验证消息真实性
	 * 
	 * 文档地址：http://mp.weixin.qq.com/wiki/index.php?title=验证消息真实性
	 * 
	 * @param signature
	 *            微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
	 * @param timestamp
	 *            时间戳
	 * @param nonce
	 *            随机数
	 * @return 消息有效返回true，否则返回false
	 */
	public boolean validate(String signature, String timestamp, String nonce) {
		String[] chars = new String[] { token, timestamp, nonce, };
		Arrays.sort(chars);
		String sha1 = DigestUtils.shaHex(StringUtils.join(chars));
		if (sha1.equals(signature)) {
			return true;
		}

		return false;
	}

	/**
	 * 获取access token
	 * 
	 * access_token是公众号的全局唯一票据，公众号调用各接口时都需使用access_token。
	 * 正常情况下access_token有效期为7200秒， 重复获取将导致上次获取的access_token失效。
	 * 
	 * 公众号可以使用AppID和AppSecret调用本接口来获取access_token。AppID和AppSecret可在开发模式中获得（
	 * 需要已经成为开发者，且帐号没有异常状态）。
	 */
	public AccessToken getAccessToken() {
		if (accessToken == null
				|| accessTokenTime == null
				|| (new Date().getTime() - accessTokenTime.getTime()) / 1000 > accessToken
						.getExpiresIn()) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			addParameter(params, "appid", appId);
			addParameter(params, "secret", secret);
			addParameter(params, "grant_type", "client_credential");

			String json = get("https://api.weixin.qq.com/cgi-bin/token?"
					+ StringUtils.join(params, "&"), params);

			ResultParser<AccessToken> result = ResultParser.parse(json,
					AccessToken.class);
			if (result.success()) {
				accessToken = result.getResult();
				accessTokenTime = new Date();
			}
		}

		return accessToken;
	}

	/**
	 * 自动获取accessToken并创建二维码ticket
	 * 
	 * 每次创建二维码ticket需要提供一个开发者自行设定的参数（scene_id）
	 * 
	 * 文档地址：http://mp.weixin.qq.com/wiki/index.php?title=生成带参数的二维码
	 * 
	 * @param type
	 *            二维码类型，QR_SCENE为临时,QR_LIMIT_SCENE为永久
	 * @param sceneId
	 *            场景值ID，临时二维码时为32位整型，永久二维码时最大值为1000
	 */
	public ResultParser<QRTicket> createQR(QRType type, Integer sceneId) {
		return createQR(getAccessToken().getToken(), type, sceneId);
	}

	/**
	 * 创建二维码ticket
	 * 
	 * 每次创建二维码ticket需要提供一个开发者自行设定的参数（scene_id）
	 * 
	 * 文档地址：http://mp.weixin.qq.com/wiki/index.php?title=生成带参数的二维码
	 * 
	 * @param accessToken
	 *            access_token是公众号的全局唯一票据
	 * @param type
	 *            二维码类型，QR_SCENE为临时,QR_LIMIT_SCENE为永久
	 * @param sceneId
	 *            场景值ID，临时二维码时为32位整型，永久二维码时最大值为1000
	 */
	public ResultParser<QRTicket> createQR(String accessToken, QRType type,
			Integer sceneId) {
		QRCreation request = new QRCreation();
		request.setType(type);
		request.setSceneId(sceneId);
		try {
			String json = post(
					"https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="
							+ accessToken, new StringEntity(request.toString()));
			return ResultParser.parse(json, QRTicket.class);
		} catch (UnsupportedEncodingException e) {
			throw new WeixinException(e);
		}
	}

	/**
	 * 发送客服消息
	 * 
	 * 当用户主动发消息给公众号的时候，微信将会把消息数据推送给开发者，开发者在一段时间内（目前为24小时）可以调用客服消息接口，
	 * 通过POST一个JSON数据包来发送消息给普通用户，
	 * 在24小时内不限制发送次数。此接口主要用于客服等有人工消息处理环节的功能，方便开发者为用户提供更加优质的服务。
	 * 
	 * 文档地址：http://mp.weixin.qq.com/wiki/index.php?title=发送客服消息
	 * 
	 * @param message
	 *            消息
	 */
	public ResultParser<Boolean> sendMessage(Message message) {
		return sendMessage(getAccessToken().getToken(), message);
	}

	/**
	 * 发送客服消息
	 * 
	 * 当用户主动发消息给公众号的时候，微信将会把消息数据推送给开发者，开发者在一段时间内（目前为24小时）可以调用客服消息接口，
	 * 通过POST一个JSON数据包来发送消息给普通用户，
	 * 在24小时内不限制发送次数。此接口主要用于客服等有人工消息处理环节的功能，方便开发者为用户提供更加优质的服务。
	 * 
	 * 文档地址：http://mp.weixin.qq.com/wiki/index.php?title=发送客服消息
	 * 
	 * @param accessToken
	 *            access_token是公众号的全局唯一票据
	 * @param message
	 *            消息
	 */
	public ResultParser<Boolean> sendMessage(String accessToken, Message message) {
		try {
			// {"errcode":0,"errmsg":"ok"}
			// {"errcode":45015,"errmsg":"response out of time limit"}
			String json = post(
					"https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="
							+ accessToken, new StringEntity(message.toJSON(),
							"UTF-8"));
			return new ResultParser<Boolean>(ErrorParser.parse(new JSONObject(
					json)));
		} catch (UnsupportedEncodingException e) {
			throw new WeixinException(e);
		}
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	/**
	 * 在公众平台网站的高级功能 – 开发模式页，点击“成为开发者”按钮，填写URL和Token，其中URL是开发者用来接收微信服务器数据的接口URL。
	 * Token可由开发者可以任意填写，用作生成签名（ 该Token会和接口URL中包含的Token进行比对，从而验证安全性）
	 */
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public MenuApi getMenuApi() {
		if (menuApi == null) {
			menuApi = new MenuApi(this);
		}
		return menuApi;
	}

	public OAuthApi getOAuthApi() {
		if (oAuthApi == null) {
			oAuthApi = new OAuthApi(this);
		}
		return oAuthApi;
	}

	public UserApi getUserApi() {
		if (userApi == null) {
			userApi = new UserApi(this);
		}
		return userApi;
	}

	public GroupApi getGroupApi() {
		if (groupApi == null) {
			groupApi = new GroupApi(this);
		}

		return groupApi;
	}

	public MediaApi getMediaApi() {
		if (mediaApi == null) {
			mediaApi = new MediaApi(this);
		}
		return mediaApi;
	}

}
