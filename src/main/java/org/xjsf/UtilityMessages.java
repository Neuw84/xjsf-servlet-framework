package org.xjsf;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.xjsf.Service.Message; 

public class UtilityMessages {

	public static class HelpMessage extends Message {

		@Expose
		@SerializedName(value="serviceDescription")
		@Element(name="serviceDescription")
		private Service service ;
		
		public HelpMessage(HttpServletRequest httpRequest, Service service) {
			super(httpRequest);
			
			this.service = service ;
		}

		public Service getService() {
			return service;
		}
	}
	

	
	public static class ErrorMessage extends Message {
		
		@Expose
		@Attribute (required=false)
		private final String error ;
		
		@Expose
		@Element (required=false)
		private String trace = null ;
		
		public ErrorMessage(HttpServletRequest httpRequest, String message) {
			super(httpRequest) ;
			error = message ;
		}
		
		public ErrorMessage(HttpServletRequest httpRequest, Exception e) {
			super(httpRequest) ;
			error = e.getMessage() ;
					
			ByteArrayOutputStream writer1 = new ByteArrayOutputStream() ;
			PrintWriter writer2 = new PrintWriter(writer1) ;
			
			e.printStackTrace(writer2) ;
			
			writer2.flush() ;
			trace = writer1.toString() ;
		}

		public String getError() {
			return error;
		}

		public String getTrace() {
			return trace;
		}
	}
	
	public static class ParameterMissingMessage extends ErrorMessage {	
		
		public ParameterMissingMessage(HttpServletRequest httpRequest) {
			super(httpRequest, "Parameters missing");
		}
	}
	
	
}
