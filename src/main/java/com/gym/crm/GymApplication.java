package com.gym.crm;

import com.gym.crm.config.AppConfig;
import com.gym.crm.config.WebAppConfig;
import com.gym.crm.interceptor.TransactionLoggingFilter;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class GymApplication {
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class, WebAppConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        Context ctx = tomcat.addContext("", null);
        Tomcat.addServlet(ctx, "dispatcher", dispatcherServlet);
        ctx.addServletMappingDecoded("/", "dispatcher");

        TransactionLoggingFilter filter = new TransactionLoggingFilter();
        ctx.addFilterDef(createFilterDef(filter));
        ctx.addFilterMap(createFilterMap());

        tomcat.start();
        System.out.println("Server started on http://localhost:8080");
        tomcat.getServer().await();
    }

    private static org.apache.tomcat.util.descriptor.web.FilterDef createFilterDef(
            TransactionLoggingFilter filter) {
        org.apache.tomcat.util.descriptor.web.FilterDef filterDef =
                new org.apache.tomcat.util.descriptor.web.FilterDef();
        filterDef.setFilterName("transactionLoggingFilter");
        filterDef.setFilter(filter);
        return filterDef;
    }

    private static org.apache.tomcat.util.descriptor.web.FilterMap createFilterMap() {
        org.apache.tomcat.util.descriptor.web.FilterMap filterMap =
                new org.apache.tomcat.util.descriptor.web.FilterMap();
        filterMap.setFilterName("transactionLoggingFilter");
        filterMap.addURLPattern("/*");
        return filterMap;
    }
}