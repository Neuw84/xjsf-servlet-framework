package org.xjsf.param;

import javax.servlet.http.HttpServletRequest;

/**
 * A Parameter that expects String values
 */
public class StringParameter extends Parameter<String> {

    private boolean caseSensitive;

    /**
     * Initialises a new (case sensitive) StringParameter
     *
     * @param name the name of the parameter
     * @param description a short description of what this parameter does
     * @param defaultValue the value to use when requests do not specify a value
     * for this parameter (may be null)
     */
    public StringParameter(String name, String description, String defaultValue) {
        super(name, description, defaultValue, "string");
        caseSensitive = true;
    }

    /**
     * Initialises a new StringParameter
     *
     * @param name the name of the parameter
     * @param description a short description of what this parameter does
     * @param defaultValue the value to use when requests do not specify a value
     * for this parameter (may be null)
     * @param caseSensitive true if you care about capitalisation of values
     */
    public StringParameter(String name, String description, String defaultValue, boolean caseSensitive) {
        super(name, description, defaultValue, "string");
        caseSensitive = false;
    }

    @Override
    public String getValue(HttpServletRequest request) throws IllegalArgumentException {

        String val = request.getParameter(getName());

        if (!caseSensitive && val != null) {
            val = val.toLowerCase();
        }

        return val;
    }

    @Override
    public String getDefaultValue() {

        String val = super.getDefaultValue();

        if (!caseSensitive && val != null) {
            val = val.toLowerCase();
        }

        return val;
    }
}
