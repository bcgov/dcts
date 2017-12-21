/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.bc.gov.nrs.cmdb.rest;

import ca.bc.gov.nrs.cmdb.model.Artifact;
import ca.bc.gov.nrs.cmdb.model.RequirementSpec;
import ca.bc.gov.nrs.cmdb.model.SelectorSpec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphCommand;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;
import static ca.bc.gov.nrs.cmdb.GraphTools.CreateEdgeIfNotExists;
import static ca.bc.gov.nrs.cmdb.GraphTools.CreateVertexIfNotExists;
import static ca.bc.gov.nrs.cmdb.GraphTools.CreateVertexTypeIfNotExists;
/**
 *
 * @author George
 */

@RestController
@RequestMapping("/artifacts")

public class ArtifactsController {
    private String defaultName = "com.oracle.ofm";

    private static Gson gson;

    @Autowired
    private OrientGraphFactory factory;


    /***
     * Get an artifact template.
     * @return
     */



    private void updatedRequirements(OrientGraphNoTx graph, String edgeName, OrientVertex vArtifact, HashMap<String, RequirementSpec> requirementHash)
    {
        if (requirementHash != null)
        {
            // start by verifying that the RequirementSpec exists.
            CreateVertexTypeIfNotExists( graph, "RequirementSpec");

            for (String requirementType : requirementHash.keySet())
            {
                RequirementSpec requirementSpec = requirementHash.get(requirementType);
                OrientVertex vRequirementSpec = CreateVertexIfNotExists (graph, "RequirementSpec", requirementSpec.getKey(requirementType));
                // Set properties.

                vRequirementSpec.setProperty("quantifier", requirementSpec.getQuantifier());
                vRequirementSpec.setProperty("scope", requirementSpec.getScope());

                safeVertexPropertySet(vRequirementSpec, "version", requirementSpec.getVersion());

                // add the expand vector.

                // create an edge.
                CreateEdgeIfNotExists(graph,vArtifact,vRequirementSpec, edgeName);
            }
        }
    }

    private void safeVertexPropertySet (OrientVertex vertex, String propertyName, String propertyValue)
    {
        if (propertyValue != null)
        {
            vertex.setProperty(propertyName, propertyValue);
        }
    }

    private OrientVertex CreateArtifactVertex (OrientGraphNoTx graph, Artifact artifact)
    {
        // create the item in the graph database.
        OrientVertex vArtifact = graph.addVertex("class:Artifact");
        vArtifact.setProperty("key", artifact.getKey());
        vArtifact.setProperty("name", artifact.getName());
        safeVertexPropertySet(vArtifact, "system",artifact.getSystem());


        safeVertexPropertySet(vArtifact,"shortName",artifact.getShortName());
        safeVertexPropertySet(vArtifact,"description",artifact.getDescription());
        safeVertexPropertySet(vArtifact,"url",artifact.getUrl());
        safeVertexPropertySet(vArtifact,"vendor",artifact.getVendor());
        safeVertexPropertySet(vArtifact,"vendorContact",artifact.getVendorContact());
        safeVertexPropertySet(vArtifact,"version",artifact.getVersion());

        /* requires and provides are stored as related vertexes with edges.  */

        updatedRequirements (graph, "Requires", vArtifact, artifact.getRequires());
        updatedRequirements (graph, "Provides", vArtifact, artifact.getProvides());

        return vArtifact;
    }

    @RequestMapping("/resetTemplate")
    public String ResetTemplate()
    {
        OrientGraphNoTx graph =  factory.getNoTx();
        OrientVertex vArtifact = null;
        Artifact artifact = new Artifact();
        Iterable<Vertex> Artifacts = graph.getVertices("Artifact.name", defaultName);
        if (Artifacts != null && Artifacts.iterator().hasNext())
        {
            vArtifact = (OrientVertex) Artifacts.iterator().next();
            graph.removeVertex(vArtifact);
        }
        graph.shutdown();
        return "ok";
    }

    @RequestMapping("/getTemplate")
    public String GetTemplate()
    {
        gson = new Gson();


        OrientGraphNoTx graph =  factory.getNoTx();
        OrientVertex vArtifact = null;
        Artifact artifact = new Artifact();



        if (graph.getVertexType("Artifact") == null)
        {
            graph.createVertexType("Artifact");
        }

        Iterable<Vertex> Artifacts = graph.getVertices("Artifact.name", defaultName);
        if (Artifacts != null && Artifacts.iterator().hasNext())
        {
            vArtifact = (OrientVertex) Artifacts.iterator().next();
            artifact.setKey((String)vArtifact.getProperty("key"));
            artifact.setName((String)vArtifact.getProperty("name"));
            artifact.setSystem((String)vArtifact.getProperty("system"));
            artifact.setShortName((String)vArtifact.getProperty("shortName"));
            artifact.setDescription((String)vArtifact.getProperty("description"));
            artifact.setUrl((String)vArtifact.getProperty("url"));
            artifact.setVendor((String)vArtifact.getProperty("vendor"));
            artifact.setVendorContact((String)vArtifact.getProperty("vendorContact"));
            artifact.setVersion((String)vArtifact.getProperty("version"));

            // construct the requires.

            SelectorSpec selector = new SelectorSpec();
            selector.setName("com.oracle.weblogic.admin");
            selector.setVersion("[10,11)" );


            RequirementSpec host = new RequirementSpec();


            host.setQuantifier("?");
            host.setScope("deployment");
            String [] expandArray = new String[1];
            expandArray[0]="url";
            host.setExpand(expandArray);

            RequirementSpec credential = new RequirementSpec();

            SelectorSpec credentialSelector = new SelectorSpec();
            credentialSelector.setName ("com.oracle.weblogic.credential.deployer");


            credential.setQuantifier("?");
            credential.setScope("deployment");

            HashMap<String, RequirementSpec>  requiresHash = new HashMap<String, RequirementSpec> ();

            requiresHash.put ("host", host);
            requiresHash.put ("deployer_credential", credential);
            artifact.setRequires(requiresHash);

        }
        else // create the demo item.
        {
            // create the view model
            artifact.setKey(UUID.randomUUID().toString());
            artifact.setName(defaultName);
            artifact.setSystem("OFM");
            artifact.setShortName("Oracle Fusion Middleware");
            artifact.setDescription("Oracle's complete family of application infrastructure products—from the #1 Java application server to SOA and enterprise portals—are integrated with Oracle Applications and technologies to speed implementation and lower the cost of management and change. Best-of-breed offerings and unique hot-pluggable capabilities provide a foundation for innovation and extend the business value of existing investments.");
            artifact.setUrl("");
            artifact.setVendor("Oracle");
            artifact.setVendorContact("support@oracle.com");
            artifact.setVersion("11.1.1");


            // create a requirement.

            RequirementSpec requirementSpec = new RequirementSpec();
            requirementSpec.setQuantifier("?");

            String[] expand = new String[1];

            requirementSpec.setScope("deployment");

            // create the vertex.
            CreateArtifactVertex (graph, artifact);
        }

        graph.shutdown();

        // return the result
        //return result.toJson();//gson.toJson(result);
        ObjectMapper mapper = new ObjectMapper();
        String result = null;
        try {
            result = mapper.writeValueAsString(artifact);
        }
        catch (Exception e)
        {
            result = "ERROR" + e.toString();
        }
        return result;
    }

    @RequestMapping("/getDemoArtifact")
    public String GetDemoUpload()
    {
        gson = new Gson();


        OrientGraphNoTx graph =  factory.getNoTx();
        OrientVertex vArtifact = null;
        Artifact artifact = new Artifact();



        // create the view model
        artifact.setKey(UUID.randomUUID().toString());
        artifact.setName("BEAVERTON");
        artifact.setVersion("11.1.1");

            // create a requirement.

            RequirementSpec requirementSpec = new RequirementSpec();
            requirementSpec.setQuantifier("?");

            String[] expand = new String[1];

            requirementSpec.setScope("deployment");

        // construct the requires.

        SelectorSpec selector = new SelectorSpec();
        selector.setName("com.oracle.weblogic.admin");
        selector.setVersion("[10,11)" );


        RequirementSpec host = new RequirementSpec();


        host.setQuantifier("?");
        host.setScope("deployment");
        String [] expandArray = new String[1];
        expandArray[0]="url";
        host.setExpand(expandArray);

        RequirementSpec credential = new RequirementSpec();

        SelectorSpec credentialSelector = new SelectorSpec();
        credentialSelector.setName ("com.oracle.weblogic.credential.deployer");


        credential.setQuantifier("?");
        credential.setScope("deployment");

        HashMap<String, RequirementSpec>  requiresHash = new HashMap<String, RequirementSpec> ();

        requiresHash.put ("host", host);
        requiresHash.put ("deployer_credential", credential);
        artifact.setProvides(requiresHash);


        // return the result
        //return result.toJson();//gson.toJson(result);
        ObjectMapper mapper = new ObjectMapper();
        String result = null;
        try {
            result = mapper.writeValueAsString(artifact);
        }
        catch (Exception e)
        {
            result = "\"ERROR" + e.toString() + "\"";
        }
        return result;



        }


        @PostMapping
    public String CreateArtifact(@RequestBody Artifact artifact)
    {
        OrientGraphNoTx graph =  factory.getNoTx();
        CreateArtifactVertex (graph, artifact);
        graph.shutdown();
        ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            result = mapper.writeValueAsString(artifact);
        }
        catch (Exception e)
        {
            result = "\"ERROR" + e.toString() + "\"";
        }
        return result;
    }


    @RequestMapping("/resetArtifacts")
    public String ResetArtifacts()
    {
        String result = "";

        OrientGraphNoTx graph =  factory.getNoTx();
        OrientVertex vArtifact = null;
        try {

            OCommandRequest command = graph.command( new OCommandSQL( "DELETE Vertex Artifact"));
            command.execute();
            command = graph.command( new OCommandSQL( "DELETE Vertex RequirementSpec"));
            command.execute();
            result = "\"Artifacts cleared.\"";
        }
        catch (Exception e)
        {
            result = "\"ERROR" + e.toString() + "\"";
        }

        /*

        Iterable<Vertex> Artifacts = graph.getVerticesOfClass("Artifact");
        if (Artifacts != null && Artifacts.iterator().hasNext())
        {
            vArtifact = (OrientVertex) Artifacts.iterator().next();
            graph.removeVertex(vArtifact);
        }
        */
        graph.shutdown();
        return result;

    }

}
