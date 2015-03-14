package com.d_project.xprint.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class XGraphicsPlayer implements InvocationHandler {

	private IXGraphics g;

	private List records;
	
	public XGraphicsPlayer() {

		g = (IXGraphics)Proxy.newProxyInstance(
				XGraphicsPlayer.class.getClassLoader(),
				new Class[]{IXGraphics.class},
				this);

		records = new ArrayList();
	}

	public IXGraphics getGraphics() {
		return g;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass().equals(IXGraphics.class) ) {
			records.add(new Record(method, args) );
			return Void.TYPE;
		}
		return method.invoke(this, args);
	}

	public void play(IXGraphics g) {
		try {
			for (int i = 0; i < records.size(); i++) {
				Record record = (Record)records.get(i);
				record.getMethod().invoke(g, record.getArguments() );
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static class Record {
		
		private Method method;
		private Object[] args;
		
		public Record(Method method, Object[] args) {
			this.method = method;
			this.args = args;
		}

		public Method getMethod() {
			return method;
		}
		
		public Object[] getArguments() {
			return args;
		}
	}
	
}