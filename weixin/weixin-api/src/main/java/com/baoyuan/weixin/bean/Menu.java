package com.baoyuan.weixin.bean;

import java.util.List;

import org.json.JSONObject;

import com.baoyuan.weixin.parse.AbstractParser;
import com.baoyuan.weixin.parse.ResultParser;

/**
 * 自定义菜单
 */
public class Menu extends AbstractParser {

	public Menu() {
	}

	private Menu(JSONObject jsonObject) {
		super(jsonObject);
	}

	private MenuType type;
	private String key;
	private String url;
	private String name;
	private List<Menu> subs;

	public MenuType getType() {
		return type;
	}

	public void setType(MenuType type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Menu> getSubs() {
		return subs;
	}

	public void setSubs(List<Menu> subs) {
		this.subs = subs;
	}

	public static Menu parse(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}
		Menu obj = new Menu(jsonObject);
		obj.name = ResultParser.toString(jsonObject.get("name"));
		obj.key = ResultParser.toString(jsonObject.opt("key"));
		obj.url = ResultParser.toString(jsonObject.opt("url"));
		obj.type = MenuType.parse(jsonObject.opt("type"));
		obj.subs = ResultParser.parse(jsonObject.optJSONArray("sub_button"),
				Menu.class);
		return obj;
	}

}
