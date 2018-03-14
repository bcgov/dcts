/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.bc.gov.nrs.cmdb.rest;

import com.google.gson.Gson;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static ca.bc.gov.nrs.cmdb.GraphTools.createEdgeIfNotExists;
import static ca.bc.gov.nrs.cmdb.GraphTools.createVertexIfNotExists;
import static ca.bc.gov.nrs.cmdb.GraphTools.createVertexTypeIfNotExists;

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

        createVertexTypeIfNotExists( graph, "Component");
        createVertexTypeIfNotExists( graph, "ExecutionEnvironment");
        createVertexTypeIfNotExists( graph, "Artifact");
        createVertexTypeIfNotExists( graph, "DeploymentSpecificationPlan");
        createVertexTypeIfNotExists( graph, "DeviceNode");
        createVertexTypeIfNotExists( graph, "DeploymentSpec");
        createVertexTypeIfNotExists( graph, "PropertyValue");
        createVertexTypeIfNotExists( graph, "PropertyName");

        // Create some sample data.

        // IRS is a top level component
        OrientVertex vComponentIRS = createVertexIfNotExists(graph, "Component", "IRS");

        // Delivery:IRS is also a component.
        OrientVertex vComponentDeliveryIRS = createVertexIfNotExists(graph, "Component", "DELIVERY_IRS");

        // Create an edge between the top level component and the delivery component.
        createEdgeIfNotExists(graph, vComponentIRS, vComponentDeliveryIRS, "delivery instance");

        // Create an Artifact.
        OrientVertex vArtifactIRS = createVertexIfNotExists(graph, "Artifact", "IRS_EAR");

        // link it to the top level component.
        createEdgeIfNotExists(graph, vComponentIRS, vArtifactIRS, "Manifested by");

        // Create an Execution Environment for the Artifact.
        OrientVertex vExecutionEnvioronmentWLH01 = createVertexIfNotExists(graph, "ExecutionEnvironment", "WLH01");

        // link it to the artifact.
        createEdgeIfNotExists(graph, vArtifactIRS, vExecutionEnvioronmentWLH01, "Deployed to");

        // Create an execution environment for delivery
        OrientVertex vExecutionEnvironment = createVertexIfNotExists(graph, "ExecutionEnvironment", "DELIVERY_WLH01");

        // The delivery component is deploy to the execution environment.
        createEdgeIfNotExists(graph, vComponentDeliveryIRS, vExecutionEnvironment, "Deployed to");

        // There is also a link to the logical execution enviornment
        createEdgeIfNotExists(graph, vExecutionEnvioronmentWLH01, vExecutionEnvironment, "Instance of");

        // Create a Deployment Spec
        OrientVertex vDeploymentSpec = createVertexIfNotExists(graph, "DeploymentSpec", "DELIVERY_IRS_DEPLOYMENTSPEC");
        // link it to the delivery component.
        createEdgeIfNotExists(graph, vComponentDeliveryIRS, vDeploymentSpec, "has");

        // create a parent deployment spec.
        OrientVertex vIRSDeploymentSpec = createVertexIfNotExists(graph, "DeploymentSpec", "IRS_DEPLOYMENTSPEC");

        // link between the parent and child deployment spec
        createEdgeIfNotExists(graph, vIRSDeploymentSpec, vDeploymentSpec, "Instance of");

        // link the parent deployment spec to the artifact.
        createEdgeIfNotExists(graph, vArtifactIRS, vIRSDeploymentSpec, "Deployed to");

        // Create a device node.
        OrientVertex vDeviceNode = createVertexIfNotExists(graph, "DeviceNode", "BLEWIT");

        // The execution environment is hosted on a device node.
        createEdgeIfNotExists(graph, vExecutionEnvironment, vDeviceNode, "hosted by");

        graph.shutdown();
        return "Model has been created.";
    }
}
