package org.xjsf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.simpleframework.xml.core.Persister;

public class ServiceHub {

    private static ServiceHub instance;
    private ClientList clientList;
    private HashMap<String, Client> clientsByName;
    private HashMap<String, Service> registeredServices;
    private DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
    private Gson jsonSerializer;
    private Persister xmlSerializer;

    // Protect the constructor, so no other class can call it
    private ServiceHub(ServletContext context) throws ServletException {

        registeredServices = new HashMap<>();

        try {
            String clientFile = context.getInitParameter("clientFile");

            if (clientFile != null) {
                clientList = new ClientList(new File(clientFile));
            } else {
                clientList = new ClientList();
            }

            clientsByName = clientList.getClientsByName();

            jsonSerializer = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setPrettyPrinting()
                    .create();

            xmlSerializer = new Persister();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public static ServiceHub getInstance(ServletContext context) throws ServletException {

        if (instance != null) {
            return instance;
        }

        instance = new ServiceHub(context);
        return instance;

    }

    public void registerService(Service service) {
        registeredServices.put(service.getServletName(), service);
    }

    public void dropService(Service service) {
        registeredServices.remove(service.getServletName());
    }

    public Set<String> getServiceNames() {
        return registeredServices.keySet();
    }

    public Service getService(String serviceName) {
        return registeredServices.get(serviceName);
    }

    public Persister getXmlSerializer() {
        return xmlSerializer;
    }

    public Gson getJsonSerializer() {
        return jsonSerializer;
    }

    public String format(double number) {
        return decimalFormat.format(number);
    }

    public Client identifyClient(HttpServletRequest request) {

        String username = null;
        String password = null;

        //first, look for the cookie name ;
        if (clientList.getCookieForUsername() != null) {

            for (Cookie cookie : request.getCookies()) {

                if (cookie.getName().equals(clientList.getCookieForUsername())) {
                    username = cookie.getValue();
                }

                if (cookie.getName().equals(clientList.getCookieForPassword())) {
                    password = cookie.getValue();
                }
            }
        }
        if (username != null) {
            Client client = clientsByName.get(username);
            if (client == null) {
                return null;
            }

            if (client.passwordMatches(password)) {
                return null;
            }

            return client;
        }

        //failing that, use the remote host ;
        username = request.getRemoteHost();

        Client client = clientsByName.get(username);
        if (client != null) {
            return client;
        }

        //if there is no client with that name, create a new one with no password, and same limits as default.
        client = new Client(username, null, clientList.getDefaultClient());

        clientsByName.put(username, client);

        return client;

    }
}
