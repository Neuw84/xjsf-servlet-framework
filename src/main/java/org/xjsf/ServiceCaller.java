package org.xjsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.xjsf.UtilityMessages.ErrorMessage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * A utility class for calling XJSF web services from within Java.
 */

public class ServiceCaller {
	
	//TODO:  how to get around a proxy?
	
	private final URL _wmServer ;
	private final Gson _gson = new Gson() ;
	
	/**
	 * 
	 * @param wmServer the URL of the wikipedia miner server to call.
	 */
	public ServiceCaller(URL wmServer) {
		_wmServer = wmServer ;
	}
	
	
	/**
	 * Calls the given service with the given parameters, and retrieves the response, parsed into a convenient Java object.
	 * 
	 * @param <M> The type of message expected from the given service
	 * @param serviceName The name of the service to call
	 * @param messageClass The expected class of message this service returns
	 * @param params Your query parameters. Note that 'responseFormat' will be ignored.
	 * @return the response to your request
	 * @throws IOException If there was a problem communicating with the Wikipedia Miner server
	 * @throws ServiceException If there was a problem processing your request (missing parameters, invalid ids, etc)
	 */
	public <M extends Service.Message> M callService(String serviceName, Class<M> messageClass, HashMap<String,String>params) throws IOException, ServiceException {
		
		String paramData = getParamData(params) ;
		
		String json = getResponse(serviceName, paramData) ;
		
		//try parsing as error first
		ErrorMessage errorMsg = null ;
		try {
			errorMsg = _gson.fromJson(json, ErrorMessage.class) ;
		} catch (JsonSyntaxException e) {
			errorMsg = null ;
		}
		if (errorMsg != null && errorMsg.getError() != null && errorMsg.getError().trim().length() > 0) {
                throw new ServiceException(errorMsg, json) ;
            }
		
		return _gson.fromJson(json, messageClass) ;
	}
	
	private String getParamData(HashMap<String, String> params) throws UnsupportedEncodingException {
		
		//build up param data
		StringBuilder paramData = new StringBuilder("responseFormat=JSON") ;
		for(Map.Entry<String,String> param:params.entrySet()) {
			
			if (param.getKey().equalsIgnoreCase("responseFormat")) {
                        continue ;
                    }
			
			paramData.append("&") ;
			paramData.append(param.getKey()) ;
			paramData.append("=") ;
			paramData.append(URLEncoder.encode(param.getValue(), "UTF-8")) ;
		}
		
		return paramData.toString() ;
	}
	
	private String getResponse(String serviceName, String paramData) throws IOException {
		
		//build url
		URL url = new URL(_wmServer.toString() + "/services/" + serviceName) ;
		
		//make connection
		URLConnection conn = url.openConnection();
	    conn.setDoOutput(true);
            StringBuilder response;
            BufferedReader rd;
            try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())) {
                wr.write(paramData.toString());
                wr.flush();
                response = new StringBuilder();
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line) ;
                    response.append("\n") ;
                }
            }
	    rd.close();
		
		return response.toString() ;
	}

	@SuppressWarnings("serial")
	public class ServiceException extends Exception {
		
		private final String _json ;
		
		public ServiceException(ErrorMessage msg, String json) {
			super(msg.getError()) ;
			_json = json ;
		}
		
		public String getJson() {
			return _json ;
		}
	}
	
}
