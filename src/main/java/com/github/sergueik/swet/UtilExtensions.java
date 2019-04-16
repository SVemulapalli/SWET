package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2019 Serguei Kouzmine
 */

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// generic JSONobject (de-)serializer
// origin: https://www.codeproject.com/Tips/709552/Google-App-Engine-JAVA

public class UtilExtensions {
	public static JSONObject getJsonObject(String payload) throws JSONException {
		JSONObject jsonObject = new JSONObject(payload);
		return jsonObject;
	}

	public static JSONArray getJsonArray(String payload) throws JSONException {
		JSONArray jsonArray = new JSONArray(payload);
		return jsonArray;
	}

	public static JSONObject modelToJSON(Model model, Mapper mapper)
			throws NoSuchFieldException, IllegalAccessException, JSONException,
			NoSuchMethodException, InvocationTargetException {
		JSONObject jsonObject = new JSONObject();
		if (mapper.size() > 0) {
			for (@SuppressWarnings("rawtypes")
			Entry entry : mapper.getEntrySet()) {
				String value = entry.getValue().toString();
				String key = entry.getKey().toString();
				jsonObject.put(value, model.getProperty(key));
			}
			return jsonObject;
		} else {
			for (String property : model.getProperties()) {
				jsonObject.put(property, model.getProperty(property));
			}
			return jsonObject;
		}
	}

	public static JSONArray modelsToJSON(
			@SuppressWarnings("rawtypes") List models, Mapper mapper)
			throws IllegalAccessException, NoSuchFieldException, JSONException,
			NoSuchMethodException, InvocationTargetException {
		JSONArray jsonArray = new JSONArray();
		for (Object model : models) {
			jsonArray.put(modelToJSON((Model) model, mapper));
		}
		return jsonArray;
	}

	public static Model jsonObjectToModel(JSONObject jsonObject,
			@SuppressWarnings("rawtypes") Class model, Mapper mapper)
			throws IllegalAccessException, InstantiationException, JSONException,
			NoSuchFieldException {
		Model m = null;
		if (mapper.size() > 0) {
			m = (Model) model.newInstance();
			for (@SuppressWarnings("rawtypes")
			Entry entry : mapper.getEntrySet()) {
				String value = entry.getValue().toString();
				String key = entry.getKey().toString();
				String jValue = jsonObject.get(value).toString();
				m.setProperty(key, jValue);
			}
			return m;
		} else {
			m = (Model) model.newInstance();
			for (String property : m.getProperties()) {
				String jValue = jsonObject.get(property).toString();
				m.setProperty(property, jValue);
			}
			return m;
		}
	}

	public static List<Model> jsonArrayToModel(JSONArray jsonArray,
			@SuppressWarnings("rawtypes") Class model, Mapper mapper)
			throws JSONException, IllegalAccessException, NoSuchFieldException,
			InstantiationException {
		List<Model> list = new ArrayList<>();
		int length = jsonArray.length();
		for (int index = 0; index < length; index++) {
			list.add(
					jsonObjectToModel(jsonArray.getJSONObject(index), model, mapper));
		}
		return list;
	}

	private static abstract class Mapper {

		protected Map<String, String> mapper = new HashMap<>();

		abstract public void init();

		public Mapper() {
			init();
		}

		public Set<Entry<String, String>> getEntrySet() {
			return this.mapper.entrySet();
		}

		public int size() {
			return this.mapper.size();
		}

		public String get(String key) {
			return this.mapper.get(key);
		}

	}

	private static abstract class Model {

		abstract public String keyToString();

		public List<String> getProperties() {
			List<String> list = new ArrayList<>();
			for (Field field : this.getClass().getDeclaredFields()) {
				list.add(field.getName());
			}
			return list;
		}

		public Object getProperty(String property)
				throws NoSuchFieldException, IllegalAccessException,
				NoSuchMethodException, InvocationTargetException {
			Field f = this.getClass().getDeclaredField(property);
			f.setAccessible(true);
			/*
				if (f.getType() == Key.class) {
					Method method = this.getClass().getDeclaredMethod("keyToString");
					return method.invoke(this);
				}
			*/
			return f.get(this);
		}

		public void setProperty(String property, Object value)
				throws IllegalAccessException, NoSuchFieldException {
			Field f = this.getClass().getDeclaredField(property);
			f.setAccessible(true);
			f.set(this, value);
		}
	}
}