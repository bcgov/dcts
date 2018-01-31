package ca.bc.gov.nrs.cmdb;

import ca.bc.gov.nrs.cmdb.model.Artifact;
import ca.bc.gov.nrs.cmdb.model.Node;
import ca.bc.gov.nrs.cmdb.model.RequirementSpec;
import ca.bc.gov.nrs.cmdb.model.SelectorSpec;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class GraphTools {


    public static void updatedRequirements(OrientGraphNoTx graph, String edgeName, OrientVertex vArtifact, JsonObject requirementHash)
    {
        if (requirementHash != null)
        {
            // start by verifying that the RequirementSpec exists.
            CreateVertexTypeIfNotExists( graph, "RequirementSpec");

            // loop through the set of requirements.
            Set<Map.Entry<String,JsonElement>> requirements = requirementHash.entrySet();

            for (Map.Entry<String,JsonElement> requirement: requirements)
            {
                String key = UUID.randomUUID().toString();
                OrientVertex vRequirementSpec = CreateVertexIfNotExists (graph, "RequirementSpec", key);
                safeVertexPropertySet(vRequirementSpec, "type", requirement.getKey());
                // now add all of the other properties.
                JsonObject requirementProperties = (JsonObject) requirement.getValue();
                Set<Map.Entry<String,JsonElement>> properties = requirementProperties.entrySet();
                for (Map.Entry<String,JsonElement> property: properties)
                {
                    safeVertexPropertySet(vRequirementSpec, property.getKey(), property.getValue().getAsString());
                }
                // create an edge linking the artifact.
                CreateEdgeIfNotExists(graph,vArtifact,vRequirementSpec, edgeName);

            }
        }
    }


    public static void safeVertexPropertySet (OrientVertex vertex, String propertyName, String propertyValue)
    {
        if (propertyValue != null)
        {
            vertex.setProperty(propertyName, propertyValue);
        }
    }

    public static OrientVertex CreateArtifactVertex (OrientGraphNoTx graph, Artifact artifact)
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

    public static OrientVertex CreateNodeVertex (OrientGraphNoTx graph, Node node)
    {
        // create the item in the graph database.
        OrientVertex vNode = graph.addVertex("class:Node");

        safeVertexPropertySet(vNode, "key", node.getKey());
        safeVertexPropertySet(vNode, "name", node.getName());

        Set<Map.Entry<String,JsonElement>> attributes = node.getAttributes().entrySet();

        for (Map.Entry<String,JsonElement> attribute: attributes)
        {
            // add the attribute to the object.
            safeVertexPropertySet(vNode, attribute.getKey(), attribute.getValue().getAsString());
        }

        return vNode;
    }


    public static void CreateVertexTypeIfNotExists(OrientGraphNoTx graph, String name)
    {
        if (graph.getVertexType(name) == null)
        {
            graph.createVertexType(name);
        }
    }

    public static OrientVertex CreateVertexIfNotExists(OrientGraphNoTx graph, String vertexType, String key)
    {
        OrientVertex result = null;
        // lookup the Component.
        Iterable<Vertex> Components = graph.getVertices(vertexType + ".key", key);
        if (Components != null && Components.iterator().hasNext())
        {
            result = (OrientVertex) Components.iterator().next();
        }
        else
        {
            result = graph.addVertex("class:" + vertexType);
            result.setProperty("key", key);
        }
        return result;
    }


    public static void CreateEdgeIfNotExists (OrientGraphNoTx graph, OrientVertex vSource, OrientVertex vDestination, String edgeLabel)
    {
        // ensure there is an edge between the ExecutionEnvironment and the property.
        Iterable<com.tinkerpop.blueprints.Edge> edges = vSource.getEdges( vDestination, Direction.BOTH, edgeLabel);
        if (edges == null || ! edges.iterator().hasNext()) {
            graph.addEdge(null, vSource, vDestination, edgeLabel);
        }
    }

    public static OrientVertex GetVertex(OrientGraphNoTx graph, String vertexType, String key)
    {
        OrientVertex result = null;
        // lookup the Component.
        Iterable<Vertex> Components = graph.getVertices(vertexType + ".key", key);
        if (Components != null && Components.iterator().hasNext())
        {
            result = (OrientVertex) Components.iterator().next();
        }
        return result;
    }


    public static JsonObject HaveRequirement (OrientGraphNoTx graph, Map.Entry<String,JsonElement> requirement)
    {
        Boolean result = false;
        // search the graph to determine if there is a suitable requirementSpec.

        OrientVertex vResult = null;
        // lookup the RequirementSpec.

        JsonObject requirementSpec = requirement.getValue().getAsJsonObject();

        Iterable<Vertex> Components = graph.getVertices("RequirementSpec.type", requirement.getKey());
        if (Components != null && Components.iterator().hasNext())
        {
            vResult = (OrientVertex) Components.iterator().next();

            // determine if there is a vertex that provides this requirement.

            Iterable<Vertex> providers = vResult.getVertices(Direction.BOTH,"Provides");
            if (providers != null && providers.iterator().hasNext())
            {
                OrientVertex vProvider = (OrientVertex) providers.iterator().next();
                result = true;

                // update matches.
                JsonObject matches = new JsonObject();
                matches.add ("node-key", new JsonPrimitive(vProvider.getProperty("key").toString()));

                requirementSpec.add("matches", matches);

                // update expand.  expand is an array of strings.
                

            }
        }

        if (!result)
        {
            return null;
        }
        else
        {
            return requirementSpec;
        }
    }
}
