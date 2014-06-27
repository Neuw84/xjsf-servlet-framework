package org.xjsf;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

@SuppressWarnings("serial")
public class ListServicesService extends Service {

    private final Comparator<String> groupNameComparator = (String s1, String s2) -> {
        if (s1.equals(s2)) {
            return 0;
        }
        //always put core first and meta last
        if (s2.equals("core") || s1.equals("meta")) {
            return 1;
        }
        if (s1.equals("core") || s2.equals("meta")) {
            return -1;
        }
        return s1.compareTo(s2);
    };

    public ListServicesService() {
        super("meta", "Lists available services",
                "<p>This service lists the different services that are available.</p>", false);
    }

    @Override
    public Message buildWrappedResponse(HttpServletRequest request) throws Exception {

        TreeMap<String, ServiceGroup> serviceGroupsByName = new TreeMap<>(groupNameComparator);
    //    getHub().getServiceNames().stream().collect(Collectors.groupingBy(null, request))
        for (String serviceName : getHub().getServiceNames()) {
            Service service = getHub().getService(serviceName);
            String groupName = service.getGroupName();
            ServiceGroup sg = serviceGroupsByName.get(groupName);

            if (sg == null) {
                sg = new ServiceGroup(groupName);
            }

            sg.addService(serviceName, service);
            serviceGroupsByName.put(groupName, sg);
        }

        ArrayList<ServiceGroup> serviceGroups = new ArrayList<>();
        serviceGroups.addAll(serviceGroupsByName.values());

        return new Message(request, serviceGroups);
    }

    public static class Message extends Service.Message {

        @Expose
        @ElementList
        private final ArrayList<ServiceGroup> serviceGroups;

        private Message(HttpServletRequest request, ArrayList<ServiceGroup> serviceGroups) {
            super(request);
            this.serviceGroups = serviceGroups;
        }

        public List<ServiceGroup> getServiceGroups() {
            return Collections.unmodifiableList(serviceGroups);
        }
    }

    public static class ServiceGroup {

        @Expose
        @Attribute
        private final String name;
        @Expose
        @SerializedName(value = "services")
        @ElementMap(inline = true, attribute = true, entry = "service", key = "name", data = true)
        private final TreeMap<String, String> serviceDescriptionsByName;

        private ServiceGroup(String name) {
            this.name = name;
            this.serviceDescriptionsByName = new TreeMap<>();
        }

        private void addService(String name, Service s) {
            serviceDescriptionsByName.put(name, s.getShortDescription());
        }

        public String getName() {
            return name;
        }

        public SortedMap<String, String> getServiceDescriptionsByName() {
            return Collections.unmodifiableSortedMap(serviceDescriptionsByName);
        }
    }
}
