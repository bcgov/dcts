/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.bc.gov.nrs.cmdb.rest;

import ca.bc.gov.nrs.cmdb.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientEdge;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ca.bc.gov.nrs.cmdb.GraphTools.*;

/**
 *
 * @author George
 */

@RestController
@RequestMapping("/deployments")

public class DeploymentsController {

    private static Gson gson;

    @Autowired
    private OrientGraphFactory factory;

    public DeploymentsController()
    {
        gson = new Gson();
    }


    /***
     * Get a new deployment specification plan.
     * @return
     */
    @PostMapping("/start")

    public ResponseEntity<String> StartDeployment( @RequestParam(required = true) String componentEnvironmentName, @RequestParam(required = true) String version, @RequestBody String rawData)
    {
        Artifact[] artifacts = gson.fromJson(rawData,Artifact[].class);
        OrientGraphNoTx graph =  factory.getNoTx();

        String result = null;
        // before we setup the deployment specification plan, check to see if we meet the requirements.

        Boolean haveRequirements = false;
        ArrayList<ErrorSpec> errorList = new ArrayList<ErrorSpec>();


        for(Artifact input : artifacts)
        {
            JsonObject requirementHash = input.getRequires();

            JsonObject updatedRequirements = new JsonObject();

            // loop through the set of requirements.
            Set<Map.Entry<String,JsonElement>> requirements = requirementHash.entrySet();

            for (Map.Entry<String,JsonElement> requirement: requirements)
            {
                // determine if we have a match for the requirement spec.
                JsonObject haveRequirement = haveRequirement(graph, requirement);

                if (haveRequirement == null)
                {
                    int errorCount = errorList.size() + 1;

                    ErrorSpec newError = new ErrorSpec();
                    newError.setCode("RequirementNotMet");
                    newError.setMessage("Requirement " + requirement.getKey() + " not met.");
                    newError.setTarget("Deployment-Error-" + errorCount);
                    errorList.add(newError);

                    JsonObject unmetRequirementObject = requirement.getValue().getAsJsonObject();
                    JsonObject errorObject = gson.fromJson(gson.toJson(newError),JsonObject.class);
                    unmetRequirementObject.add("error", errorObject);

                    updatedRequirements.add (requirement.getKey(), unmetRequirementObject);

                }
                else  // update the object to indicate that the requirement is met.
                {
                    updatedRequirements.add (requirement.getKey(), haveRequirement);
                }

            }
            // swap in the updatedRequirements.
            input.setRequires(updatedRequirements);
        }

        if (errorList.size() == 0)
        {
            // setup the new deployment specification plan.
            DeploymentSpecificationPlan deploymentSpecificationPlan = new DeploymentSpecificationPlan();

            deploymentSpecificationPlan.setKey(UUID.randomUUID().toString());
            deploymentSpecificationPlan.setComponentEnvironment(componentEnvironmentName);

            DateFormat dateFormat = new SimpleDateFormat("yy/mm/dd-hh:mm");
            Calendar cal = Calendar.getInstance();

            deploymentSpecificationPlan.setName("New Deployment Specification Plan " + dateFormat.format(cal.getTime()));
            deploymentSpecificationPlan.setArtifacts(artifacts);
            deploymentSpecificationPlan.setDeployed(false);
            deploymentSpecificationPlan.setVersion (version);
            deploymentSpecificationPlan.setSystem("OFM");

            if (graph.getVertexType("DeploymentSpecificationPlan") == null)
            {
                graph.createVertexType("DeploymentSpecificationPlan");
            }

            if (graph.getVertexType("ArtifactDeploymentSpec") == null)
            {
                graph.createVertexType("ArtifactDeploymentSpec");
            }

            if (graph.getVertexType("System") == null)
            {
                graph.createVertexType("System");
            }

            if (graph.getVertexType("ComponentEnvironment") == null)
            {
                graph.createVertexType("ComponentEnvironment");
            }

            if (graph.getVertexType("Environment") == null)
            {
                graph.createVertexType("Environment");
            }

            OrientVertex vDeploymentSpecificationPlan = graph.addVertex("class:DeploymentSpecificationPlan" );
            vDeploymentSpecificationPlan.setProperty("key", deploymentSpecificationPlan.getKey());
            vDeploymentSpecificationPlan.setProperty("name", deploymentSpecificationPlan.getName());
            vDeploymentSpecificationPlan.setProperty("environment", deploymentSpecificationPlan.getComponentEnvironment());


            // create the system if it does not exist
            OrientVertex vSystem = null;
            Iterable<Vertex> vSystems = graph.getVertices("System.name", deploymentSpecificationPlan.getSystem());
            if (vSystems != null && vSystems.iterator().hasNext()) {
                vSystem = (OrientVertex) vSystems.iterator().next();
            }
            else
            {
                vSystem =graph.addVertex("class:System" );
                vSystem.setProperty("key", UUID.randomUUID().toString());
                vSystem.setProperty("name", deploymentSpecificationPlan.getSystem());
            }

            // create the environment if it does not exist
            OrientVertex vEnvironment = null;
            Iterable<Vertex> vEnvironments = graph.getVertices("Environment.name", deploymentSpecificationPlan.getComponentEnvironment());
            if (vEnvironments != null && vEnvironments.iterator().hasNext()) {
                vEnvironment = (OrientVertex) vEnvironments.iterator().next();
            }
            else
            {
                vEnvironment = graph.addVertex("class:Environment" );
                vEnvironment.setProperty("key", UUID.randomUUID().toString());
                vEnvironment.setProperty("name", deploymentSpecificationPlan.getComponentEnvironment());
            }

            createEdgeIfNotExists(graph,vSystem, vEnvironment, "Has");

            // update the graph
            for(Artifact input : artifacts) {
                OrientVertex vArtifactDeploymentSpec = null;
                OrientVertex vArtifact = null;
                Iterable<Vertex> vArtifacts = graph.getVertices("Artifact.key", input.getKey());
                if (vArtifacts != null && vArtifacts.iterator().hasNext()) {
                    vArtifact = (OrientVertex) vArtifacts.iterator().next();
                    // create the Artifact Deployment Spec
                    vArtifactDeploymentSpec = graph.addVertex("class:ArtifactDeploymentSpec" );
                    vArtifactDeploymentSpec.setProperty("name", input.getName());
                    vArtifactDeploymentSpec.setProperty("key", UUID.randomUUID().toString());


                    // get the link for the Artifact Version.

                    OrientVertex artifactVersion = getVersionVertexFromArtifactVertex(graph, vArtifact);


                    createEdgeIfNotExists(graph,vArtifactDeploymentSpec, artifactVersion, "Using");


                    createEdgeIfNotExists(graph,vDeploymentSpecificationPlan, vArtifactDeploymentSpec, "Has");



                    // create the provides components.
                    JsonObject provides = input.getProvides();
                    if (provides != null)
                    {
                        createComponentFromRequirement(graph, "Provides", vArtifactDeploymentSpec, provides, deploymentSpecificationPlan.getComponentEnvironment(), vSystem  );
                    }

                    // create the requires components.
                    JsonObject requires = input.getRequires();
                    if (requires != null)
                    {
                        createRequiresObjects(graph, "Requires", vArtifactDeploymentSpec, requires  );
                    }
                }
            }


            // done with the graph
            graph.shutdown();

            try {
                result = gson.toJson(deploymentSpecificationPlan);
            }
            catch (Exception e)
            {
                ErrorSpec newError = new ErrorSpec();
                newError.setCode("Exception");
                newError.setMessage(e.toString());
                newError.setTarget("ObjectMapper");
                result = gson.toJson(newError);
            }
        }
        else
        {
            ErrorSpec newError = new ErrorSpec();
            newError.setCode ("RequirementsMissing");
            newError.setMessage ("Unable to locate requirements for the deployment.  See the details element for more information.");
            newError.setTarget ("startDeployment");
            newError.setDetails (errorList.toArray(new ErrorSpec[0]));
            // create an error spec.
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError (newError);

            errorResponse.setArtifacts(artifacts);
            // convert to json.
            result = errorResponse.toJson();

        }

        // return the result
        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(result, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/{deploymentId}/finish")
    public ResponseEntity<String> FinishDeployment(@PathVariable("deploymentId") String deploymentId, @RequestParam(required = true) Boolean success, @RequestBody String rawData )
    {
        // use gson to parse as fasterjackson cannot understand the JsonObject data.

        DeploymentSpecificationPlan deploymentSpecificationPlan = gson.fromJson(rawData, DeploymentSpecificationPlan.class);
        OrientGraphNoTx graph =  factory.getNoTx();


        if (graph.getVertexType("DeploymentSpecificationPlan") == null)
        {
            graph.createVertexType("DeploymentSpecificationPlan");
        }

        // find the associated OrientDb object
        OrientVertex vDeploymentSpecificationPlan = getVertex(graph, "DeploymentSpecificationPlan", deploymentId);

        if (vDeploymentSpecificationPlan != null) {
            // update the status
            vDeploymentSpecificationPlan.setProperty("deployment-successful", success.toString());
            // update other properties from the input data.
            if (deploymentSpecificationPlan.getSystem() != null) { vDeploymentSpecificationPlan.setProperty("System", deploymentSpecificationPlan.getSystem()); }
            if (deploymentSpecificationPlan.getSymbolicName() != null) { vDeploymentSpecificationPlan.setProperty("SymbolicName", deploymentSpecificationPlan.getSymbolicName()); }
            if (deploymentSpecificationPlan.getDescription() != null) { vDeploymentSpecificationPlan.setProperty("Description", deploymentSpecificationPlan.getDescription()); }
            if (deploymentSpecificationPlan.getVendor() != null) { vDeploymentSpecificationPlan.setProperty("Vendor", deploymentSpecificationPlan.getVendor()); }
            if (deploymentSpecificationPlan.getVendorContact() != null) { vDeploymentSpecificationPlan.setProperty("Vendor-Contact", deploymentSpecificationPlan.getVendorContact()); }



            deploymentSpecificationPlan.setDeployed(success);

            deploymentSpecificationPlan.setKey((String)vDeploymentSpecificationPlan.getProperty("key"));
            deploymentSpecificationPlan.setName((String)vDeploymentSpecificationPlan.getProperty("name"));
            // see if there are any edges.
            Iterable<com.tinkerpop.blueprints.Edge> edges = vDeploymentSpecificationPlan.getEdges( Direction.BOTH, "Deploys");
            if (edges != null && edges.iterator().hasNext()) {
                OrientEdge vEdge = (OrientEdge) edges.iterator().next();
                OrientVertex vArtifact = graph.getVertex( vEdge.getInVertex());
                Artifact artifact = new Artifact();
                artifact.setKey((String) vArtifact.getProperty("key"));
                artifact.setName((String) vArtifact.getProperty("name"));
            }

            // remove instance links from Components.
            Artifact[] artifacts = deploymentSpecificationPlan.getArtifacts();

            for(Artifact input : artifacts) {
                OrientVertex vArtifact = null;
                JsonObject providesHash = input.getProvides();
                cleanupComponentInstanceLink(graph,providesHash,deploymentSpecificationPlan.getComponentEnvironment());
            }

            // Create a link to Previous deployments.
            linkPreviousDeployments (graph, vDeploymentSpecificationPlan, deploymentSpecificationPlan);


        }
        else
        {
            throw new ResourceNotFoundException(deploymentId);
        }

        // done with the graph
        graph.shutdown();


        String result = gson.toJson(deploymentSpecificationPlan);

        // return the result
        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(result, httpHeaders, HttpStatus.OK);

    }
}