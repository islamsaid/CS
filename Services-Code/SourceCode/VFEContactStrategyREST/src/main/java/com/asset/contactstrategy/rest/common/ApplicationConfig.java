/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.rest.common;

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
        Set<Class<?>> resources = new java.util.HashSet<>();
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
//        resources.add(com.asset.contactstrategy.rest.api.AdvertisementConsult.class);
//        resources.add(com.asset.contactstrategy.rest.api.CheckInterface.class);
//        resources.add(com.asset.contactstrategy.rest.api.Consult.class);
//        resources.add(com.asset.contactstrategy.rest.api.ReadyCheck.class);
        resources.add(com.asset.contactstrategy.rest.api.AdvertisementConsult.class);
//        resources.add(com.asset.contactstrategy.rest.api.SendBulkSmsOffline.class);
        resources.add(com.asset.contactstrategy.rest.api.CheckInterface.class);
        resources.add(com.asset.contactstrategy.rest.api.Consult.class);
        resources.add(com.asset.contactstrategy.rest.api.ReadyCheck.class);
        resources.add(com.asset.contactstrategy.rest.api.Refresh.class);
        resources.add(com.asset.contactstrategy.rest.api.SendBulkSmsOffline.class);
        resources.add(com.asset.contactstrategy.rest.api.SendSms.class);
        resources.add(com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider.class);
        resources.add(com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider.class);
    }
    
}
