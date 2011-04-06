package com.pigglogic.phomenet.web.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.LoggerFactory;

public class PhomenetConsolePlugin extends HttpServlet implements EventHandler
{
    private Map<String, Event> observationEvents = new HashMap<String, Event>();
    private static String template;
    
    public PhomenetConsolePlugin(BundleContext context) {
        URL resource = context.getBundle().getResource("template/monitor.html");
        if (resource != null) {
            try {
                template = IOUtils.toString(resource.openStream());
            } catch (IOException ex) {
                LoggerFactory.getLogger(PhomenetConsolePlugin.class).error("Error reading template");
            }
        }
        /*
        observationEvents.put("Test1", new Event("phomenet", eventProperties("Test1", 30.2)));
        observationEvents.put("Test2", new Event("phomenet", eventProperties("Test2", 25.7)));
         */
    }

    private Map<String, Object> eventProperties(String location, Double value)
    {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("location.name", location);
        hashMap.put("observedValue", value);
        return hashMap;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        if (req.getMethod().equals("POST"))
        {
            writeJsonData(resp, writer);
            return;
        }
        writer.write(template);
    }

    private void writeJsonData(HttpServletResponse resp, PrintWriter writer) {
        writer.write("<observations>");
        for (Event event : observationEvents.values()) {
            writer.write(String.format("<observation location=\"%s\"  value=\"%.2f\" address=\"%s\"/>",
                    event.getProperty("location.name"),
                    event.getProperty("observedValue"),
                    event.getProperty("location.address")));
        }
        writer.write("</observations>");
    }
    public void handleEvent(Event event) {
        observationEvents.put((String)event.getProperty("location.address"), event);
    }
    
}
