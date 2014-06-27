package org.xjsf.param;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

public class StringListParameter extends Parameter<String[]> {
    
    private boolean caseSensitive ;
    
    
/**
     * Initialises a new (case sensitive) StringParameter
     *
     * @param name the name of the parameter
     * @param description a short description of what this parameter does
     * @param defaultValue the value to use when requests do not specify a value
     * for this parameter (may be null)
     */
    public StringListParameter(String name, String description, String[] defaultValue) {
        super(name, description, defaultValue, "string list");
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
    public StringListParameter(String name, String description, String[] defaultValue, boolean caseSensitive) {
        super(name, description, defaultValue, "string");
        caseSensitive = false;
    }
    
    
    @Override
    public String getValueForDescription(String[] val) {

        if (val.length == 0) {
            return "empty list";
        }

        StringBuilder sb = new StringBuilder();
        for (String v : val) {
            sb.append(v);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    @Override
    public String[] getValue(HttpServletRequest request) throws IllegalArgumentException {

        String s = request.getParameter(getName());

        if (s == null) {
            return getDefaultValue();
        }

        ArrayList<String> values = new ArrayList<>();
        for (String val : s.split("[,;:]")) {
            values.add((val.trim()));
        }
        return values.toArray(new String[values.size()]);
    }
}
