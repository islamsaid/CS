/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.CS.BridgingDequeuerService.configuration;

import com.asset.CS.BridgingDequeuerService.Threads.MonitorThread;
import com.asset.CS.BridgingDequeuerService.models.QueueNeedsHolder;
import com.asset.CS.BridgingDequeuerService.Threads.ReloadingThread;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.asset.CS.BridgingDequeuerService.beans.InitializationBean;
import com.asset.CS.BridgingDequeuerService.beans.ManagerBean;
import com.asset.CS.BridgingDequeuerService.services.MainService;
import javax.sql.DataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import com.asset.contactstrategy.common.controller.DataSourceManger;

/**
 *
 * @author mostafa.kashif
 */
@Configuration
public class SpringConfiguration implements ApplicationContextAware{

    @Bean
    public InitializationBean initializationBean() {
        return new InitializationBean();
    }
    
    
    @Bean
    public ReloadingThread reloadingThread() {
        return new ReloadingThread();
    }
      @Bean
    public MonitorThread monitorThread() {
        return new MonitorThread();
    }

    @Bean(name="dataSource")
    @DependsOn({"initializationBean"})
    @Primary
    public DataSource dataSource() {

            //return DataSourceManger.getC3p0ConnectionPool();
            return DataSourceManger.getHikariDataSourse();

    }

    @Bean
     @Scope(value=BeanDefinition.SCOPE_PROTOTYPE)//New
    public JdbcTemplate jdbcTemplate(DataSource  dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Scope(value=BeanDefinition.SCOPE_PROTOTYPE)//New
    public MainService databaseService(JdbcTemplate jdbcTemplate) {
        return new MainService(jdbcTemplate);
    }

    @Bean
    @DependsOn({"queueHolder"})
    public ManagerBean managerBean(MainService databaseService) {
        return new ManagerBean(databaseService);
    }

    

    @Bean
    @Scope(value=BeanDefinition.SCOPE_PROTOTYPE)
    public QueueNeedsHolder queueHolder() {
        return new QueueNeedsHolder();
    }

    public static ApplicationContext applicationContext = null;
    
    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext=ac; //To change body of generated methods, choose Tools | Templates.
    }
    
    


}
