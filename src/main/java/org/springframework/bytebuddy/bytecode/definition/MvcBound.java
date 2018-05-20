package org.springframework.bytebuddy.bytecode.definition;

/**
 * 数据绑定对象，用于通过<code>@WebBound</code>注解实现与方法相关数据的绑定
 */
public class MvcBound {
    
    public MvcBound(String uid) {
    	this.uid = uid;
	}
    
	public MvcBound(String uid, String json) {
		this.uid = uid;
		this.json = json;
	}

	/**
	 * 1、uid：某个数据主键，可用于传输主键ID在实现对象中进行数据提取
	 */
	private String uid = "";

	/**
	 * 2、json：绑定的数据对象JSON格式，为了方便，这里采用json进行数据传输
	 */
	private String json = "";

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

}

