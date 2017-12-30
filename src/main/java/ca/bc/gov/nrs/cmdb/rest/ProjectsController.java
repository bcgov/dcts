/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.bc.gov.nrs.cmdb.rest;

import ca.bc.gov.nrs.cmdb.model.Project;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.orient.object.OrientObjectDatabaseFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Properties;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 *
 * @author George
 */

@RestController
@RequestMapping("/projects")

public class ProjectsController {
    
    private static Gson gson;

    @Autowired
    private OrientGraphFactory factory;

    @RequestMapping("/load")    
    public String ProjectsLoad()
    {
        OrientGraphNoTx graph =  factory.getNoTx();

        if (graph.getVertexType("Project") == null)
        {
            graph.createVertexType("Project");
        }

        if (graph.getVertexType("Component") == null)
        {
            graph.createVertexType("Component");
        }

        if (graph.getVertexType("ExecutionEnvironment") == null)
        {
            graph.createVertexType("ExecutionEnvironment");
        }

        if (graph.getVertexType("PropertyValue") == null)
        {
            graph.createVertexType("PropertyValue");
        }

        if (graph.getVertexType("PropertyName") == null)
        {
            graph.createVertexType("PropertyName");
        }


        String configDir = System.getenv("CONFIG_DIR");
        // open the config dir and parse out the subdirectories.
        File directory = new File(configDir);
        String[] apps = directory.list();
        for (String filename : apps) {
            // if it is not a directory, ignore it.
            String filePath = configDir + "\\" + filename;
            File subdir = new File(filePath);
            if (subdir.isDirectory()) {
                System.out.println("Processing app " + filename);
                // create the app


                Vertex vProject = null;

                // lookup the Project.
                Iterable<Vertex> verticies = graph.getVertices("Project.abbreviation", filename);
                if (verticies != null && verticies.iterator().hasNext())
                {
                    vProject = verticies.iterator().next();
                }
                else
                {
                    vProject = graph.addVertex("class:Project");
                    vProject.setProperty("abbreviation", filename);
                }

                
                // now look for Components.
                String[] items = subdir.list();

                for (String item : items) {
                    // parse the item.
                    String[] parts = item.split("[.]");
                    String Component = parts[0];
                    String ExecutionEnvironment = parts[1];

                    String ComponentKey = filename.toUpperCase() + "_" + Component.toUpperCase();

                    OrientVertex vComponent = null;
                    // lookup the Component.
                    Iterable<Vertex> Components = graph.getVertices("Component.key", ComponentKey);
                    if (Components != null && Components.iterator().hasNext())
                    {
                        vComponent = (OrientVertex) Components.iterator().next();
                    }
                    else
                    {
                        vComponent = graph.addVertex("class:Component");
                        vComponent.setProperty("name", Component);
                        vComponent.setProperty("key", ComponentKey);
                        // ensure there is an edge.
                        graph.addEdge(null, vProject, vComponent, "has");
                    }

                    String ExecutionEnvironmentKey = ComponentKey + "_" + ExecutionEnvironment.toUpperCase();

                    OrientVertex vExecutionEnvironment = null;
                    // lookup the ExecutionEnvironment.
                    Iterable<Vertex> ExecutionEnvironments = graph.getVertices("ExecutionEnvironment.name", ExecutionEnvironment);

                    if (ExecutionEnvironments != null && ExecutionEnvironments.iterator().hasNext())
                    {
                        vExecutionEnvironment = (OrientVertex) ExecutionEnvironments.iterator().next();
                    }
                    else
                    {
                        vExecutionEnvironment = graph.addVertex("class:ExecutionEnvironment");
                        vExecutionEnvironment.setProperty("name", ExecutionEnvironment);
                    }

                    // ensure there is an edge between the ExecutionEnvironment and the property.
                    Iterable<com.tinkerpop.blueprints.Edge> edges = vComponent.getEdges( vExecutionEnvironment, Direction.BOTH, "has");
                    if (edges == null) {
                        graph.addEdge(null, vComponent, vExecutionEnvironment, "is in");
                    }

                    // now load the properties.
                    Properties prop = new Properties();
                    InputStream input = null;
                    try {
                        input = new FileInputStream(filePath + File.separatorChar + item);
                        prop.load(input);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        if (input != null) {
                            try {
                                input.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // and add the name / value pairs.
                    Enumeration<?> e = prop.propertyNames();
                    while (e.hasMoreElements()) {
                        String key = (String) e.nextElement();
                        String value = prop.getProperty(key);
                        System.out.println("Key : " + key + ", Value : " + value);

                        String propertyKey = ExecutionEnvironmentKey + "_" + key.toUpperCase();

                        Vertex vProperty = null;
                        // create the PropertyName if it does not exists.
                        Iterable<Vertex> propertyNames = graph.getVertices("PropertyName.name", key);
                        OrientVertex vPropertyName = null;
                        if (propertyNames != null && propertyNames.iterator().hasNext())
                        {
                            vPropertyName = (OrientVertex) propertyNames.iterator().next();
                        }
                        else
                        {
                            vPropertyName = graph.addVertex("class:PropertyName");
                            vPropertyName.setProperty("name", key);
                        }


                        Iterable<Vertex> properties = graph.getVertices("PropertyValue.key", propertyKey);
                        OrientVertex vPropertyValue = null;
                        if (properties != null && properties.iterator().hasNext())
                        {
                            vProperty = properties.iterator().next();
                            vProperty.setProperty("value", value);
                        }
                        else
                        {
                            vPropertyValue = graph.addVertex("class:PropertyValue");
                            vPropertyValue.setProperty("value", value);
                            vPropertyValue.setProperty("key", propertyKey);
                            // connect the value to the property name.
                            graph.addEdge(null, vPropertyName, vPropertyValue, "is");
                            // connect the value to an  ExecutionEnvironment.
                            graph.addEdge(null, vExecutionEnvironment, vPropertyValue, "has");
                            // connect the value to a Component
                            graph.addEdge(null, vComponent, vPropertyValue, "has");
                        }

                    }

                }

            }

        }
        graph.shutdown();
        return "Data has been imported";
    }
}
