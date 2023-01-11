/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.enqueue.rest.common;

import com.asset.contactstrategy.common.exception.CommonException;
import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author esmail.anbar
 */
@javax.ws.rs.ApplicationPath("/")
public class ApplicationConfig extends Application {

    public ApplicationConfig() throws CommonException {
        ConfigurationManager.init();
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
        // following code can be used to customize Jersey 1.x JSON provider:
        try {
            Class jacksonProvider = Class.forName("com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider");
            resources.add(jacksonProvider);
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.asset.contactstrategy.enqueue.rest.api.CheckInterface.class);
        resources.add(com.asset.contactstrategy.enqueue.rest.api.EnqueueBridgeSMS.class);
        resources.add(com.asset.contactstrategy.enqueue.rest.api.EnqueueSMS.class);
        resources.add(com.asset.contactstrategy.enqueue.rest.api.ReadyCheck.class);
        resources.add(com.asset.contactstrategy.enqueue.rest.api.Refresh.class);
        resources.add(com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider.class);
        resources.add(com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider.class);
    }
    
}
