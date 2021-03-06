package org.xjsf.param;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

/**
 * A Parameter that expects enum values. The values must match the enum
 * @param <T> the expected enum type
 */
public class EnumParameter<T extends Enum<T>> extends Parameter<T> {

	
	private HashMap<String, T> valuesByName ;
	
	/**
	 * Initialises a new EnumParameter
	 * 
	 * @param name the name of the parameter
	 * @param description a short description of what this parameter does
	 * @param defaultValue the value to use when requests do not specify a value for this parameter (may be null)
	 * @param allValues a list of all values for this enum
	 * @param valueDescriptions an array of short descriptions for each possible value 
	 */
	public EnumParameter(String name, String description, T defaultValue, T[] allValues, String[] valueDescriptions) {
		super(name, description, defaultValue, "enum");
		
		if (allValues.length != valueDescriptions.length) 
			throw new IllegalArgumentException("the number of values and valueDescriptions does not match!") ;
		
		valuesByName = new HashMap<>() ;
		valueDescriptionsByName = new HashMap<>() ;
		for (int i=0 ; i<allValues.length ; i++) {
			valuesByName.put(allValues[i].name().toLowerCase(), allValues[i]) ;
			valueDescriptionsByName.put(allValues[i].name().toLowerCase(), valueDescriptions[i]) ;
		}
	}

	@Override
	public T getValue(HttpServletRequest request) throws IllegalArgumentException{
		
		String s = request.getParameter(getName()) ;
		
		if (s == null) {
                return getDefaultValue() ;
            }
		
		T val = valuesByName.get(s.trim().toLowerCase()) ;
		
		if (val == null) {
                throw new IllegalArgumentException("Invalid value for " + getName() + " parameter") ;
            }
		
		return val ;
	}

}
