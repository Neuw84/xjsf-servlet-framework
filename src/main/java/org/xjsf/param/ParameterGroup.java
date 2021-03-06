package org.xjsf.param;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.simpleframework.xml.*;

/**
 * A group of parameters that are somehow related to each other
 */
@SuppressWarnings("rawtypes")
public class ParameterGroup {

    @Expose
    @Attribute
    String name;
    @Expose
    @SerializedName(value = "description")
    @Element(name = "description", data = true)
    String descriptionMarkup;
    @Expose
    @ElementList
    List<Parameter> parameters;

    /**
     * Initialises a parameter group
     *
     * @param name the name of this parameter group
     */
    public ParameterGroup(String name, String descriptionMarkup) {
        this.name = name;
        this.descriptionMarkup = descriptionMarkup;
        this.parameters = new ArrayList<> ();
    }

    /**
     * Adds a parameter to this group
     *
     * @param param the parameter to add
     */
    public void addParameter(Parameter param) {
        parameters.add(param);
    }

    /**
     * Returns the name of this parameter group
     *
     * @return the name of this parameter group
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a vector of parameters within this group
     *
     * @return a vector of parameters within this group
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * Returns true if all mandatory parameters within this group have been
     * specified in the given request, otherwise false
     *
     * @param request the request to check
     * @return true if all mandatory parameters within this group have been
     * specified in the given request
     */
    public boolean isSpecified(HttpServletRequest request) {

        for (Parameter param : parameters) {

            if (param.getValue(request) == null) {
                return false;
            }
        }

        return true;
    }
}
