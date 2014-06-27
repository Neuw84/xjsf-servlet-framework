package org.xjsf;

import com.google.gson.annotations.Expose;
import javax.servlet.http.HttpServletRequest;
import org.simpleframework.xml.Element;

@SuppressWarnings("serial")
public class UsageService extends Service{
	

	public UsageService() {
		super("meta","Provides information on how much you have been using the wikipedia miner web services, and what your limits are",
				"<p>Provides information on how much you have been using the wikipedia miner web services, and what your limits are.</p>", false
				);
	}

	@Override
	public Message buildWrappedResponse(HttpServletRequest request) throws Exception {
		
		return new Message(request, getHub().identifyClient(request)) ;
	}

	@Override 
	public int getUsageCost(HttpServletRequest request) {
		return 0 ;
	}
	
	public static class Message extends Service.Message {
		
		@Expose
		@Element
		private Client client ;
		
		private Message(HttpServletRequest request, Client c) {
			super(request) ;
			client = c ;
		}

		public Client getClient() {
			return client;
		}
	}
}
