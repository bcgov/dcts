
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.bc.gov.nrs.cmdb.rest;

import ca.bc.gov.nrs.cmdb.model.Artifact;
import ca.bc.gov.nrs.cmdb.model.RequirementSpec;
import ca.bc.gov.nrs.cmdb.model.SelectorSpec;
import ca.bc.gov.nrs.cmdb.model.UploadSpec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphCommand;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;

import static ca.bc.gov.nrs.cmdb.GraphTools.*;

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

        if (graph.getVertexType("Artifact") == null)
        {
            graph.createVertexType("Artifact");
        }

        Artifact artifact = GetArtifactFromGraph(graph, defaultName);


        if (artifact == null) // create the demo item.
        {
            artifact = new Artifact();
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

            JsonObject  requiresHash = new JsonObject ();

            // convert the host object to a JsonObject
            JsonObject hostObject = gson.fromJson( gson.toJson(requirementSpec), JsonObject.class);

            JsonObject selectorObject = new JsonObject();
            selectorObject.add("os_family", new JsonPrimitive("SunOS"));
            selectorObject.add("os_name", new JsonPrimitive("SunOS"));

            hostObject.add( "selector", selectorObject );

            requiresHash.add ("host", hostObject);

            artifact.setRequires(requiresHash);

            // add provides.

            RequirementSpec serverSpec1 = new RequirementSpec();
            serverSpec1.setInterface("com.oracle.weblogic");
            serverSpec1.setVersion("10.3.6");

            RequirementSpec serverSpec2 = new RequirementSpec();
            serverSpec2.setInterface("com.oracle.forms");
            serverSpec2.setVersion("???");

            JsonObject providesList = new JsonObject();

            JsonObject serverSpec1Object = gson.fromJson( gson.toJson(serverSpec1), JsonObject.class);
            JsonObject serverSpec2Object = gson.fromJson( gson.toJson(serverSpec2), JsonObject.class);

            providesList.add ("server", serverSpec1Object);
            providesList.add ("server", serverSpec2Object);

            artifact.setProvides( providesList );

            // create the vertex.
            CreateArtifactVertex (graph, artifact);
            // ensure we have all data from the graph.
            artifact = GetArtifactFromGraph(graph, defaultName);
        }

        graph.shutdown();

        String result = null;
        try {

            Artifact[] artifacts = new Artifact[1];
            artifacts[0] = artifact;
            result = gson.toJson(artifacts);
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
        artifact.setName("ZEUS");
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


        JsonObject providesList = new JsonObject();
        JsonObject hostObject = gson.fromJson( gson.toJson(host), JsonObject.class);

        JsonObject selectorObject = new JsonObject();
        selectorObject.add("os_family", new JsonPrimitive("SunOS"));
        selectorObject.add("os_name", new JsonPrimitive("SunOS"));

        hostObject.add( "selector", selectorObject );

        JsonObject credentialObject = gson.fromJson( gson.toJson(credential), JsonObject.class);

        providesList.add ("host", hostObject);
        providesList.add ("deployerCredentials", credentialObject);

        artifact.setProvides(providesList);

        // setup an upload spec.

        UploadSpec[] uploadSpec = new UploadSpec[1];

        uploadSpec[0] = new UploadSpec();
        uploadSpec[0].setKind("artifact");
        uploadSpec[0].setValue(artifact);



        String result = gson.toJson(uploadSpec);
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
