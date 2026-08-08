package org.springframework.bytebuddy.bytecode.definition;

/**
 * Describes a parameter for a dynamically generated Spring MVC handler method.
 * Encapsulates the parameter's Java type, name, source annotation type
 * ({@link MvcParamFrom}), whether it is required, and an optional default value.
 *
 * <p>This is used by {@link org.springframework.bytebuddy.bytecode.EndpointApiBuilder}
 * to define method parameters with the appropriate Spring MVC binding annotations
 * such as {@code @RequestParam}, {@code @PathVariable}, {@code @RequestBody}, etc.</p>
 *
 * @param <T> the Java type of the method parameter
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see MvcParamFrom
 * @see org.springframework.bytebuddy.utils.EndpointApiAnnotationUtils#annotParam(MvcParam)
 */
public class MvcParam<T> {

	/**
	 * The Java type of the parameter.
	 */
	private Class<T> type;

	/**
	 * The name used to bind the parameter (e.g. the request parameter name
	 * or path variable name).
	 */
	private String name;

	/**
	 * The source annotation type indicating how the parameter value is resolved.
	 *
	 * @see MvcParamFrom
	 */
	private MvcParamFrom from = MvcParamFrom.PARAM;

	/**
	 * Whether the parameter is required.
	 * <p>Defaults to {@code true}, leading to an exception being thrown
	 * if the parameter is missing in the request. Switch this to
	 * {@code false} if you prefer a {@code null} value if the parameter is
	 * not present in the request.
	 * <p>Alternatively, provide a {@link #def}, which implicitly
	 * sets this flag to {@code false}.
	 */
	private boolean required = true;

	/**
	 * Defines the default value of request meta-data that is bound using one of the
	 * following annotations:
	 * The default value is used if the corresponding meta-data is not present in the request.
	 */
	private String def;

	/**
	 * Creates a new {@code MvcParam} with the specified type and name.
	 *
	 * @param type the Java type of the parameter
	 * @param name the parameter binding name
	 */
	public MvcParam(Class<T> type, String name) {
		this.type = type;
		this.name = name;
	}

	/**
	 * Creates a new {@code MvcParam} with the specified type, name, and source.
	 *
	 * @param type the Java type of the parameter
	 * @param name the parameter binding name
	 * @param from the parameter source annotation type
	 */
	public MvcParam(Class<T> type, String name, MvcParamFrom from) {
		this.type = type;
		this.name = name;
	}

	/**
	 * Creates a new {@code MvcParam} with the specified type, name,
	 * source, and default value.
	 *
	 * @param type the Java type of the parameter
	 * @param name the parameter binding name
	 * @param from the parameter source annotation type
	 * @param def  the default value for the parameter
	 */
	public MvcParam(Class<T> type, String name, MvcParamFrom from, String def ) {
		this.type = type;
		this.name = name;
		this.def = def;
	}

	/**
	 * Creates a new {@code MvcParam} with the specified type, name,
	 * and default value.
	 *
	 * @param type the Java type of the parameter
	 * @param name the parameter binding name
	 * @param def  the default value for the parameter
	 */
	public MvcParam(Class<T> type, String name, String def ) {
		this.type = type;
		this.name = name;
		this.def = def;
	}

	/**
	 * Returns the Java type of the parameter.
	 *
	 * @return the parameter type class
	 */
	public Class<T> getType() {
		return type;
	}

	/**
	 * Sets the Java type of the parameter.
	 *
	 * @param type the parameter type class to set
	 */
	public void setType(Class<T> type) {
		this.type = type;
	}

	/**
	 * Returns the parameter binding name.
	 *
	 * @return the parameter name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the parameter binding name.
	 *
	 * @param name the parameter name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the parameter source annotation type.
	 *
	 * @return the {@link MvcParamFrom} value
	 */
	public MvcParamFrom getFrom() {
		return from;
	}

	/**
	 * Sets the parameter source annotation type.
	 *
	 * @param from the {@link MvcParamFrom} value to set
	 */
	public void setFrom(MvcParamFrom from) {
		this.from = from;
	}

	/**
	 * Returns whether the parameter is required.
	 *
	 * @return {@code true} if the parameter is required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Sets whether the parameter is required.
	 *
	 * @param required {@code true} if the parameter should be required
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Returns the default value for this parameter.
	 *
	 * @return the default value string, or {@code null} if not set
	 */
	public String getDef() {
		return def;
	}

	/**
	 * Sets the default value for this parameter.
	 *
	 * @param def the default value string to set
	 */
	public void setDef(String def) {
		this.def = def;
	}

}
