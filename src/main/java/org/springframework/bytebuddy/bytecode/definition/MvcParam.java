package org.springframework.bytebuddy.bytecode.definition;

/**
 */
public class MvcParam<T> {

	/**
	 * 参数对象类型
	 */
	private Class<T> type;
	
	/**
	 * name ：参数的名称
	 */
	private String name;
	
	/**
	 * from ：参数来源
	 * @see org.springframework.web.bind.annotation.CookieValue
	 * @see org.springframework.web.bind.annotation.MatrixVariable
	 * @see org.springframework.web.bind.annotation.PathVariable
	 * @see org.springframework.web.bind.annotation.RequestAttribute
	 * @see org.springframework.web.bind.annotation.RequestBody
	 * @see org.springframework.web.bind.annotation.RequestHeader
	 * @see org.springframework.web.bind.annotation.RequestParam
	 * @see org.springframework.web.bind.annotation.RequestPart
	 */
	private MvcParamFrom from = MvcParamFrom.PARAM;
	
	/**
	 * Whether the parameter is required.
	 * <p>Defaults to {@code true}, leading to an exception being thrown
	 * if the parameter is missing in the request. Switch this to
	 * {@code false} if you prefer a {@code null} value if the parameter is
	 * not present in the request.
	 * <p>Alternatively, provide a {@link #defaultValue}, which implicitly
	 * sets this flag to {@code false}.
	 */
	private boolean required = true;
	
	/**
	 * Defines the default value of request meta-data that is bound using one of the
	 * following annotations: 
	 * The default value is used if the corresponding meta-data is not present in the request.
	 */
	private String def;

	public MvcParam(Class<T> type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public MvcParam(Class<T> type, String name, MvcParamFrom from) {
		this.type = type;
		this.name = name;
	}

	public MvcParam(Class<T> type, String name, MvcParamFrom from, String def ) {
		this.type = type;
		this.name = name;
		this.name = name;
		this.def = def;
	}
	
	public MvcParam(Class<T> type, String name, String def ) {
		this.type = type;
		this.name = name;
		this.def = def;
	}

	public Class<T> getType() {
		return type;
	}

	public void setType(Class<T> type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MvcParamFrom getFrom() {
		return from;
	}

	public void setFrom(MvcParamFrom from) {
		this.from = from;
	}
	
	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getDef() {
		return def;
	}

	public void setDef(String def) {
		this.def = def;
	}

}
