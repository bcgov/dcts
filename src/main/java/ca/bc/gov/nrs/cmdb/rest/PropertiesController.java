/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.bc.gov.nrs.cmdb.rest;

import com.google.gson.Gson;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

/**
 *
 * @author George
 */

@RestController
@RequestMapping("/properties")

public class PropertiesController {
    
    private static Gson gson;

    @Autowired
    private OrientGraphFactory factory;

    @RequestMapping(value="/value", method = RequestMethod.GET)
    /**
     * Returns the value for a given property.
     */

    public String GetValue(
        @RequestParam(required = false) String project,
        @RequestParam(required = false) String component,
        @RequestParam(required = false) String environment,
        @RequestParam(required = false) String property
        )
    {
        gson = new Gson();
        String result = "";
        // get a graph database connection
        OrientGraphNoTx graph =  factory.getNoTx();

        if (graph.getVertexType("PropertyValue") == null)
        {
            graph.createVertexType("PropertyValue");
        }

        // if all properties are present, get the single value.

        if (project != null && component != null && environment != null && property != null) {
            // construct the property key
            String ComponentKey = project.toUpperCase() + "_" + component.toUpperCase();
            String executionEnvironmentKey = ComponentKey + "_" + environment.toUpperCase();
            String propertyKey = executionEnvironmentKey + "_" + property.toUpperCase();

            // get a list of matching values
            Iterable<Vertex> properties = graph.getVertices("PropertyValue.key", propertyKey);
            OrientVertex vProperty = null;
            if (properties != null && properties.iterator().hasNext()) {
                vProperty = (OrientVertex) properties.iterator().next();
                result = vProperty.getProperty("value");
            }

        }
        else // it is a query for multiple records.
        {
            Iterable<Vertex> properties = graph.getVerticesOfClass("PropertyValue", false);
            OrientVertex vProperty = null;

            HashMap<String,String> results = new HashMap<String,String>();

            Iterator<Vertex> looper = properties.iterator();
            while (looper.hasNext())
            {
                vProperty = (OrientVertex) looper.next();

                String key = vProperty.getProperty("key");
                String [] parts = key.split("_");
                String propertyProject = parts[0];
                String propertyComponent = parts[1];
                String propertyEnvironment = parts[2];
                String propertyName = parts[3];

                boolean  matched = true;
                if (project != null && !project.equalsIgnoreCase(propertyProject))
                {
                    matched = false;
                }
                if (matched && component != null && !component.equalsIgnoreCase(propertyComponent))
                {
                    matched = false;
                }
                if (matched && environment != null && !environment.equalsIgnoreCase(propertyEnvironment))
                {
                    matched = false;
                }
                if (matched && property != null && !property.equalsIgnoreCase(propertyName))
                {
                    matched = false;
                }

                if (matched)
                {
                    String value = vProperty.getProperty("value");

                    results.put(propertyName, value);
                }
            }
            if (results.size() > 0)
            {
                result = gson.toJson(results);
            }
            else
            {
                result = "[]";
            }


        }

        // shutdown the graph database connection
        graph.shutdown();
        return result;
    }

    @RequestMapping(value = "/value", method = RequestMethod.PUT)
    /**
     * Returns the value for a given property.
     */

    public String SetValue(
            @RequestParam(required = true) String project,
            @RequestParam(required = true) String component,
            @RequestParam(required = true) String environment,
            @RequestParam(required = true) String property,
            @RequestParam(required = true) String value
    )
    {
        gson = new Gson();
        String result = "";
        // get a graph database connection
        OrientGraphNoTx graph =  factory.getNoTx();

        if (graph.getVertexType("PropertyValue") == null)
        {
            graph.createVertexType("PropertyValue");
        }

        // if all properties are present, get the single value.

        // construct the property key
        String ComponentKey = project.toUpperCase() + "_" + component.toUpperCase();
        String executionEnvironmentKey = ComponentKey + "_" + environment.toUpperCase();
        String propertyKey = executionEnvironmentKey + "_" + property.toUpperCase();

        // get a list of matching values
        Iterable<Vertex> properties = graph.getVertices("PropertyValue.key", propertyKey);
        OrientVertex vProperty = null;
        if (properties != null && properties.iterator().hasNext()) {
            vProperty = (OrientVertex) properties.iterator().next();
            // set the value
            vProperty.setProperty("value", value);
        }

        // shutdown the graph database connection
        graph.shutdown();

        return gson.toJson(vProperty);
    }

    @RequestMapping(value = "/value", method = RequestMethod.POST)
    /**
     * Returns the value for a given property.
     */

    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String CreateValue(
            @RequestParam(required = true) String project,
            @RequestParam(required = true) String component,
            @RequestParam(required = true) String environment,
            @RequestParam(required = true) String property,
            @RequestParam(required = true) String value
    ) {
        gson = new Gson();
        String result = "";
        // get a graph database connection
        OrientGraphNoTx graph = factory.getNoTx();

        if (graph.getVertexType("PropertyValue") == null)
        {
            graph.createVertexType("PropertyValue");
        }

        // if all properties are present, get the single value.

        // construct the property key
        String ComponentKey = project.toUpperCase() + "_" + component.toUpperCase();
        String executionEnvironmentKey = ComponentKey + "_" + environment.toUpperCase();
        String propertyKey = executionEnvironmentKey + "_" + property.toUpperCase();

        // get a list of matching values
        Iterable<Vertex> properties = graph.getVertices("PropertyValue.key", propertyKey);
        OrientVertex vProperty = null;
        if (properties != null && properties.iterator().hasNext()) {
            vProperty = (OrientVertex) properties.iterator().next();
            // set the value
            vProperty.setProperty("value", value);
        } else // create the property.
        {
            vProperty = graph.addVertex("class:PropertyValue" );
            vProperty.setProperty("key", propertyKey);
            vProperty.setProperty("value", value);
        }

        // shutdown the graph database connection
        graph.shutdown();

        return gson.toJson(vProperty);
    }



}
