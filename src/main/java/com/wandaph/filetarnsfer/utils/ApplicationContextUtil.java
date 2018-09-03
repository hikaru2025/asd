package com.wandaph.filetarnsfer.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.util.Map;

@Component
public class ApplicationContextUtil implements ApplicationContextAware {

	private static ApplicationContext context = null;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ApplicationContextUtil.context = applicationContext;

	}

	public static <T> T getBean(String name) {
		if (name == null || name.length () == 0) return null;
		if (context == null)
			return null;
		else {
			@SuppressWarnings ("unchecked")
			T bean = (T) context.getBean (name);
			return bean;
		}
	}

	@SuppressWarnings ("unchecked")
	public static <T> T getSingletonBean(Class<T> clazz) {
		Map<String, T> map = getSingletonBeans (clazz);
		if (map != null) {
			for (Object t : map.values ()) {
				return (T) t;
			}
		}
		return null;
	}

	public static <T> Map<String, T> getSingletonBeans(Class<T> clazz) {
		if (context == null)
			return null;
		else {
			Map<String, T> map = (Map<String, T>) context.getBeansOfType (clazz, false, true);
			return map;
		}
	}

	public static ApplicationContext getContext() {
		return ApplicationContextUtil.context;
	}

}
