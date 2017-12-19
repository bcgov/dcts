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
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.springframework.beans.factory.annotation.Autowired;
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
                vRequirementSpec.setProperty("selector", requirementSpec.getSelector());
                vRequirementSpec.setProperty("quantifier", requirementSpec.getQuantifier());
                vRequirementSpec.setProperty("scope", requirementSpec.getScope());
                vRequirementSpec.setProperty("version", requirementSpec.getVersion());

                // add the expand vector.

                // create an edge.
                CreateEdgeIfNotExists(graph,vArtifact,vRequirementSpec, edgeName);
            }
        }


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

            host.setSelector(selector);
            host.setQuantifier("?");
            host.setScope("deployment");
            String [] expandArray = new String[1];
            expandArray[0]="url";
            host.setExpand(expandArray);

            RequirementSpec credential = new RequirementSpec();

            SelectorSpec credentialSelector = new SelectorSpec();
            credentialSelector.setName ("com.oracle.weblogic.credential.deployer");

            credential.setSelector(credentialSelector);
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

            // create the item in the graph database.
            vArtifact = graph.addVertex("class:Artifact");
            vArtifact.setProperty("key", artifact.getKey());
            vArtifact.setProperty("name", artifact.getName());
            vArtifact.setProperty("system",artifact.getSystem());
            vArtifact.setProperty("shortName",artifact.getShortName());
            vArtifact.setProperty("description",artifact.getDescription());
            vArtifact.setProperty("url",artifact.getUrl());
            vArtifact.setProperty("vendor",artifact.getVendor());
            vArtifact.setProperty("vendorContact",artifact.getVendorContact());
            vArtifact.setProperty("version",artifact.getVersion());

            /* requires and provides are stored as related vertexes with edges.  */

            updatedRequirements (graph, "Requires", vArtifact, artifact.getRequires());
            updatedRequirements (graph, "Provides", vArtifact, artifact.getProvides());

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

}
