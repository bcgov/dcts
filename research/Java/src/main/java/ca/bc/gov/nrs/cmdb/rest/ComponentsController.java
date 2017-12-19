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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static ca.bc.gov.nrs.cmdb.GraphTools.CreateEdgeIfNotExists;
import static ca.bc.gov.nrs.cmdb.GraphTools.CreateVertexIfNotExists;
import static ca.bc.gov.nrs.cmdb.GraphTools.CreateVertexTypeIfNotExists;

/**
 *
 * @author George
 */

@RestController
@RequestMapping("/components")

public class ComponentsController {
    
    private static Gson gson;

    @Autowired
    private OrientGraphFactory factory;




    /***
     * Create the model.
     * @return
     */
    @RequestMapping("/model")
    public String ComponentModel()
    {
        OrientGraphNoTx graph =  factory.getNoTx();

        /**
         * Component has Component
         * Component is manifested by Artifact
         * Component delivery instanced by component
         * Component is deployed to Execution Environment
         */

        CreateVertexTypeIfNotExists( graph, "Component");
        CreateVertexTypeIfNotExists( graph, "ExecutionEnvironment");
        CreateVertexTypeIfNotExists( graph, "Artifact");
        CreateVertexTypeIfNotExists( graph, "DeploymentSpec");
        CreateVertexTypeIfNotExists( graph, "DeviceNode");
        CreateVertexTypeIfNotExists( graph, "DeploymentSpec");
        CreateVertexTypeIfNotExists( graph, "PropertyValue");
        CreateVertexTypeIfNotExists( graph, "PropertyName");

        // Create some sample data.

        // IRS is a top level component
        OrientVertex vComponentIRS = CreateVertexIfNotExists (graph, "Component", "IRS");

        // Delivery:IRS is also a component.
        OrientVertex vComponentDeliveryIRS = CreateVertexIfNotExists (graph, "Component", "DELIVERY_IRS");

        // Create an edge between the top level component and the delivery component.
        CreateEdgeIfNotExists (graph, vComponentIRS, vComponentDeliveryIRS, "delivery instance");

        // Create an Artifact.
        OrientVertex vArtifactIRS = CreateVertexIfNotExists (graph, "Artifact", "IRS_EAR");

        // link it to the top level component.
        CreateEdgeIfNotExists(graph, vComponentIRS, vArtifactIRS, "Manifested by");

        // Create an Execution Environment for the Artifact.
        OrientVertex vExecutionEnvioronmentWLH01 = CreateVertexIfNotExists (graph, "ExecutionEnvironment", "WLH01");

        // link it to the artifact.
        CreateEdgeIfNotExists(graph, vArtifactIRS, vExecutionEnvioronmentWLH01, "Deployed to");

        // Create an execution environment for delivery
        OrientVertex vExecutionEnvironment = CreateVertexIfNotExists (graph, "ExecutionEnvironment", "DELIVERY_WLH01");

        // The delivery component is deploy to the execution environment.
        CreateEdgeIfNotExists(graph, vComponentDeliveryIRS, vExecutionEnvironment, "Deployed to");

        // There is also a link to the logical execution enviornment
        CreateEdgeIfNotExists (graph, vExecutionEnvioronmentWLH01, vExecutionEnvironment, "Instance of");

        // Create a Deployment Spec
        OrientVertex vDeploymentSpec = CreateVertexIfNotExists (graph, "DeploymentSpec", "DELIVERY_IRS_DEPLOYMENTSPEC");
        // link it to the delivery component.
        CreateEdgeIfNotExists (graph, vComponentDeliveryIRS, vDeploymentSpec, "has");

        // create a parent deployment spec.
        OrientVertex vIRSDeploymentSpec = CreateVertexIfNotExists (graph, "DeploymentSpec", "IRS_DEPLOYMENTSPEC");

        // link between the parent and child deployment spec
        CreateEdgeIfNotExists (graph, vIRSDeploymentSpec, vDeploymentSpec, "Instance of");

        // link the parent deployment spec to the artifact.
        CreateEdgeIfNotExists (graph, vArtifactIRS, vIRSDeploymentSpec, "Deployed to");

        // Create a device node.
        OrientVertex vDeviceNode = CreateVertexIfNotExists (graph, "DeviceNode", "BLEWIT");

        // The execution environment is hosted on a device node.
        CreateEdgeIfNotExists (graph, vExecutionEnvironment, vDeviceNode, "hosted by");

        graph.shutdown();
        return "Model has been created.";
    }
}
