package com.baoyuan.weixin.parse;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baoyuan.weixin.exception.WeixinException;

public final class ResultParser<T> {

	private ErrorParser error;
	private T result;
	private List<T> results;

	public ResultParser(ErrorParser error) {
		this.error = error;
	}

	public ResultParser(T result) {
		this.result = result;
	}

	public ResultParser(List<T> results) {
		this.results = results;
	}

	public boolean success() {
		return error == null;
	}

	public ErrorParser getError() {
		return error;
	}

	public T getResult() {
		return result;
	}

	public List<T> getResults() {
		return results;
	}

	public static <T> ResultParser<T> parse(String json, Class<T> resultType) {
		try {
			if (json.matches("^\\s*\\[.*$")) {
				return new ResultParser<T>(parse(new JSONArray(json),
						resultType));
			} else {
				return parse(new JSONObject(json), resultType);
			}
		} catch (Exception e) {
			throw new WeixinException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> ResultParser<T> parse(JSONObject jsonObject,
			Class<T> resultType) {
		try {
			ErrorParser error = ErrorParser.parse(jsonObject);

			if (error == null) {
				Method parse = resultType.getMethod("parse", JSONObject.class);
				T obj = (T) parse.invoke(null, jsonObject);
				return new ResultParser<T>(obj);
			}
			return new ResultParser<T>(error);
		} catch (Exception e) {
			throw new WeixinException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> parse(JSONArray jsonArray, Class<T> resultType) {
		List<T> list = new ArrayList<T>();
		if (jsonArray == null) {
			return list;
		}
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				if (resultType.isAssignableFrom(String.class)) {
					list.add((T) toString(jsonArray.get(i)));
				} else if (resultType.isAssignableFrom(Integer.class)) {
					list.add((T) parseInteger(jsonArray.get(i)));
				} else if (resultType.isAssignableFrom(Long.class)) {
					list.add((T) parseLong(jsonArray.get(i)));
				} else if (resultType.isAssignableFrom(Double.class)) {
					list.add((T) parseDouble(jsonArray.get(i)));
				} else {
					Method parse = resultType.getMethod("parse",
							JSONObject.class);
					list.add((T) parse.invoke(null, jsonArray.getJSONObject(i)));
				}
			}
			return list;
		} catch (Exception e) {
			throw new WeixinException(e);
		}
	}

	public static String toString(Object obj) {
		if (obj == null) {
			return null;
		}

		return obj.toString();
	}

	public static Long parseLong(Object obj) {
		if (obj == null) {
			return null;
		}

		Long result = null;
		if (obj instanceof Number) {
			result = ((Number) obj).longValue();
		} else if (obj instanceof String) {
			result = Long.valueOf((String) obj);
		}

		return result;
	}

	public static Integer parseInteger(Object obj) {
		if (obj == null) {
			return null;
		}

		Integer result = null;
		if (obj instanceof Number) {
			result = ((Number) obj).intValue();
		} else if (obj instanceof String) {
			result = Integer.valueOf((String) obj);
		}

		return result;
	}

	public static Double parseDouble(Object obj) {
		if (obj == null) {
			return null;
		}

		Double result = null;
		if (obj instanceof Number) {
			result = ((Number) obj).doubleValue();
		} else if (obj instanceof String) {
			result = Double.valueOf((String) obj);
		}

		return result;
	}

	public static Boolean parseBoolean(Object obj) {
		if (obj == null) {
			return null;
		}

		Boolean result = null;
		if (obj instanceof Boolean) {
			result = (Boolean) obj;
		} else if (obj instanceof Integer) {
			result = ((Integer) obj).intValue() == 1;
		} else if (obj instanceof String) {
			result = Boolean.valueOf(obj.toString());
		}

		return result;
	}

	public static Date parseDate(Object obj, String pattern, Locale locale) {
		if (obj == null) {
			return null;
		}

		Date result = null;
		if (obj instanceof Date) {
			result = new Date(((Date) obj).getTime());
		} else if (obj instanceof String) {
			try {
				SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
				result = format.parse((String) obj);
			} catch (ParseException e) {
				throw new WeixinException(e);
			}
		}

		return result;
	}

	public static Date parseTimeSeconds(Object obj) {
		Integer seconds = ResultParser.parseInteger(obj);
		if (seconds == null || seconds == 0) {
			return null;
		}
		return new Date(seconds * 1000);
	}

}
