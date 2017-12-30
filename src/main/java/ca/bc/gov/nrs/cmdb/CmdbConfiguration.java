package ca.bc.gov.nrs.cmdb;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author George
 */

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
//import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.orient.object.OrientObjectDatabaseFactory;
import org.springframework.data.orient.commons.core.OrientTransactionManager;
import org.springframework.data.orient.object.OrientObjectTemplate;

@Configuration
//@EnableTransactionManagement

public class CmdbConfiguration {

    /* only needed for local testing.
    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory =
                new TomcatEmbeddedServletContainerFactory();
        return factory;
    }
    */

    @Bean
    public com.tinkerpop.blueprints.impls.orient.OrientGraphFactory factory() {

        String orientDBServer = System.getenv("ORIENTDB_SERVER");
        String orientDBUser = System.getenv("ORIENTDB_USER");
        String orientDBPass = System.getenv("ORIENTDB_PASS");
        String orientDBName = System.getenv("ORIENTDB_NAME");

        com.tinkerpop.blueprints.impls.orient.OrientGraphFactory factory =  new OrientGraphFactory("remote:"+ orientDBServer +"/" + orientDBName,
                orientDBUser,orientDBPass);

        return factory;
    }

    @Bean
    public OrientObjectDatabaseFactory ofactory() {
        OrientObjectDatabaseFactory factory =  new OrientObjectDatabaseFactory();

        // get connection details from the environment.

        String orientDBServer = System.getenv("ORIENTDB_SERVER");
        String orientDBUser = System.getenv("ORIENTDB_USER");
        String orientDBPass = System.getenv("ORIENTDB_PASS");
        String orientDBName = System.getenv("ORIENTDB_NAME");

                factory.setUrl("remote:"+ orientDBServer +"/" + orientDBName);
        factory.setUsername(orientDBUser);
        factory.setPassword(orientDBPass);

        return factory;
    }



    @Bean
    public OrientTransactionManager transactionManager() {
        return new OrientTransactionManager(ofactory());
    }

    @Bean
    public OrientObjectTemplate objectTemplate() {
        return new OrientObjectTemplate(ofactory());
    }

}