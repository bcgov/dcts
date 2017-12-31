package ca.bc.gov.nrs.cmdb;

import ca.bc.gov.nrs.cmdb.model.Artifact;
import ca.bc.gov.nrs.cmdb.model.RequirementSpec;
import ca.bc.gov.nrs.cmdb.model.SelectorSpec;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.apache.commons.lang3.tuple.Pair;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;

public class GraphTools {




    public static void updatedRequirements(OrientGraphNoTx graph, String edgeName, OrientVertex vArtifact, HashMap<String, RequirementSpec> requirementHash)
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

    public static void updatedRequirements(OrientGraphNoTx graph, String edgeName, OrientVertex vArtifact, ArrayList<Pair<String, RequirementSpec>> requirements)
    {
        if (requirements != null && requirements.size() > 0)
        {
            // start by verifying that the RequirementSpec exists.
            CreateVertexTypeIfNotExists( graph, "RequirementSpec");

            for (Pair<String, RequirementSpec> item : requirements)
            {
                String requirementType = item.getKey();
                RequirementSpec requirementSpec = item.getValue();
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

    public static Boolean HaveRequirement (OrientGraphNoTx graph, String requirementType, RequirementSpec requirementSpec)
    {
        Boolean result = false;
        // search the graph to determine if there is a suitable requirementSpec.

        OrientVertex vResult = null;
        // lookup the RequirementSpec.


        Iterable<Vertex> Components = graph.getVertices("RequirementSpec.key", requirementSpec.getKey(requirementType));
        if (Components != null && Components.iterator().hasNext())
        {
            vResult = (OrientVertex) Components.iterator().next();

            // determine if there is a vertex that provides this requirement.

            Iterable<Vertex> providers = vResult.getVertices(Direction.BOTH,"Provides");
            if (providers != null && providers.iterator().hasNext())
            {
                OrientVertex vProvider = (OrientVertex) providers.iterator().next();
                result = true;
            }

        }

        return result;
    }
}
