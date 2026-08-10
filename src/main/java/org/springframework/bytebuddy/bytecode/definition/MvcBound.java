package org.springframework.bytebuddy.bytecode.definition;

/**
 * Data binding object used to associate data with a dynamically generated
 * endpoint method or class via the {@code @WebBound} annotation. Carries
 * a unique identifier and an optional JSON payload.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see org.springframework.bytebuddy.annotation.WebBound
 * @see org.springframework.bytebuddy.utils.EndpointApiAnnotationUtils#annotBound(MvcBound)
 */
public class MvcBound {

    /**
     * Creates a new {@code MvcBound} with the specified uid and
     * an empty JSON payload.
     *
     * @param uid the unique identifier for the data binding
     */
    public MvcBound(String uid) {
    	this.uid = uid;
	}

    /**
     * Creates a new {@code MvcBound} with the specified uid and JSON payload.
     *
     * @param uid  the unique identifier for the data binding
     * @param json the JSON-formatted data payload
     */
	public MvcBound(String uid, String json) {
		this.uid = uid;
		this.json = json;
	}

	/**
	 * A unique identifier (e.g. a primary key) that can be used by
	 * the handler implementation to look up data.
	 */
	private String uid = "";

	/**
	 * A JSON-formatted data payload bound to the endpoint for use
	 * during request handling.
	 */
	private String json = "";

	/**
	 * Returns the unique identifier for this data binding.
	 *
	 * @return the uid string
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * Sets the unique identifier for this data binding.
	 *
	 * @param uid the uid string to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * Returns the JSON data payload for this data binding.
	 *
	 * @return the JSON string
	 */
	public String getJson() {
		return json;
	}

	/**
	 * Sets the JSON data payload for this data binding.
	 *
	 * @param json the JSON string to set
	 */
	public void setJson(String json) {
		this.json = json;
	}

}
