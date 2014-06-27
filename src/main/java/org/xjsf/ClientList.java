package org.xjsf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ClientList {
	
	private enum ParamName{client, authentication, unknown} ;
	
	private static Logger _logger = LoggerFactory.getLogger(ClientList.class) ;

	
	private HashMap<String,Client> clients ;
	private Client defaultClient = null ;
	
	private String cookieUserName ;
	private String cookiePassword ;
	
	public String getCookieForUsername() {
		return cookieUserName ;
	}
	
	public String getCookieForPassword() {
		return cookiePassword ;
	}
	
	public String[] getClientNames() {
		Set<String> clientNames = clients.keySet() ;
		return clientNames.toArray(new String[clientNames.size()]) ;
	}
	
	public Client getClient(String name) {
		return clients.get(name) ;
	}
	
	public Client getDefaultClient() {
		return defaultClient ;
	}
	
	public HashMap<String, Client> getClientsByName() {
		return clients ;
	}
	
	public ClientList() {
		clients = new HashMap<>() ;
		
		defaultClient = new Client("anonymous", null, -1, -1, -1) ;
	}

	public ClientList(File listFile) throws ParserConfigurationException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SAXException {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(listFile);
		doc.getDocumentElement().normalize();
		
		initFromXml(doc.getDocumentElement()) ;
		
	}
	
    @SuppressWarnings("empty-statement")
	private void initFromXml(Element xml) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		clients = new HashMap<>() ;
				
		NodeList children = xml.getChildNodes() ;
		
		for (int i=0 ; i<children.getLength() ; i++) {
			
			Node xmlChild = children.item(i) ;
			
			if (xmlChild.getNodeType() == Node.ELEMENT_NODE) {
				
				Element xmlParam = (Element)xmlChild ;
				
				String paramName = xmlParam.getNodeName() ;
				//String paramValue = getParamValue(xmlParam) ;
				
				switch(resolveParamName(xmlParam.getNodeName())) {
				
				
				case client:

					String clientName = null ;
					if (xmlParam.hasAttribute("name")) {
                                clientName = xmlParam.getAttribute("name") ;
                            }
					
					String password = null ;
					if (xmlParam.hasAttribute("password")) {
                                password = xmlParam.getAttribute("password") ;
                            }
					
					int minLimit = -1 ;
					if (xmlParam.hasAttribute("minLimit")) {
                                minLimit = Integer.parseInt(xmlParam.getAttribute("minLimit"));
                            }
					
					int hourLimit = -1 ;
					if (xmlParam.hasAttribute("hourLimit")) {
                                hourLimit = Integer.parseInt(xmlParam.getAttribute("hourLimit"));
                            }
					
					int dayLimit = -1 ;
					if (xmlParam.hasAttribute("dayLimit")) {
                                dayLimit = Integer.parseInt(xmlParam.getAttribute("dayLimit"));
                            }	
					
					if (clientName == null) {
                                defaultClient = new Client("anonymous", password, minLimit, hourLimit, dayLimit) ;
                            }
					else {
                                this.clients.put(clientName, new Client(clientName, password, minLimit, hourLimit, dayLimit)) ;
                            }	
					break ;
				case authentication:
					cookieUserName = xmlParam.getAttribute("nameCookie") ;
					cookiePassword = xmlParam.getAttribute("passwordCookie") ;
					break ;
				default:
					_logger.warn("Ignoring unknown parameter: '" + paramName + "'") ;
				} ;
			}
						
			//if there is no default client, make one with no access limits
			if (defaultClient == null) {
                        defaultClient = new Client("anonymous", null, 0,0,0) ;
                    }	
		}
	}
	
	/*
	private String getParamValue(Element xmlParam) {
		
		Node nodeContent = xmlParam.getChildNodes().item(0) ;
		
		if (nodeContent == null)
			return null ;
		
		if (nodeContent.getNodeType() != Node.TEXT_NODE)
			return null ;
		
		String content = nodeContent.getTextContent().trim() ;
		
		if (content.length() == 0)
			return null ;
		
		return content ;
	}*/
		
	private ParamName resolveParamName(String name) {
		try {
			return ParamName.valueOf(name.trim()) ;
		} catch (Exception e) {
			return ParamName.unknown ;
		}
	}
	
}
