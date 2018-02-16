package ca.bc.gov.nrs.cmdb;

import ca.bc.gov.nrs.cmdb.model.Artifact;
import ca.bc.gov.nrs.cmdb.model.Node;
import ca.bc.gov.nrs.cmdb.model.RequirementSpec;
import ca.bc.gov.nrs.cmdb.model.SelectorSpec;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class GraphTools {

    public static void updatedRequirements(OrientGraphNoTx graph, String edgeName, OrientVertex vArtifact, JsonObject requirementHash) {
        if (requirementHash != null) {
            // start by verifying that the RequirementSpec exists.
            CreateVertexTypeIfNotExists(graph, "RequirementSpec");

            // loop through the set of requirements.
            Set<Map.Entry<String, JsonElement>> requirements = requirementHash.entrySet();

            for (Map.Entry<String, JsonElement> requirement : requirements) {
                String key = UUID.randomUUID().toString();
                OrientVertex vRequirementSpec = CreateVertexIfNotExists(graph, "RequirementSpec", key);
                safeVertexPropertySet(vRequirementSpec, "type", requirement.getKey());
                // now add all of the other properties.
                JsonObject requirementProperties = (JsonObject) requirement.getValue();
                Set<Map.Entry<String, JsonElement>> properties = requirementProperties.entrySet();
                for (Map.Entry<String, JsonElement> property : properties) {
                    JsonElement propertyValue = property.getValue();
                    if (propertyValue.isJsonPrimitive()) {
                        safeVertexPropertySet(vRequirementSpec, property.getKey(), propertyValue.getAsString());
                    } else if (propertyValue.isJsonObject()) {
                        // create the object as a linked item.
                        createLinkedVertex(graph, "has", vRequirementSpec, property.getKey(), propertyValue.getAsJsonObject());
                    }

                }
                // create an edge linking the artifact.
                CreateEdgeIfNotExists(graph, vArtifact, vRequirementSpec, edgeName);
            }
        }
    }

    public static JsonObject GetRequirementFromVertex(OrientGraphNoTx graph, OrientVertex vRequirement) {
        JsonObject result = new JsonObject();

        Map<String, Object> properties = vRequirement.getProperties();

        for (String propertyKey : properties.keySet()) {
            // discard type as it is obtained one level up.
            if (propertyKey != null && !propertyKey.equalsIgnoreCase("type")) {
                String propertyValue = properties.get(propertyKey).toString();
                result.add(propertyKey, new JsonPrimitive(propertyValue));
            }
        }

        // Now add items in related edges.
        Iterable<com.tinkerpop.blueprints.Edge> edges = vRequirement.getEdges(Direction.OUT, "has");
        if (edges != null && edges.iterator().hasNext()) {
            // loop through the result.
            com.tinkerpop.blueprints.Edge edge = edges.iterator().next();
            OrientVertex vNext = (OrientVertex) edge.getVertex(Direction.IN);
            String type = vNext.getType().getName();
            JsonObject requirement = GetRequirementFromVertex(graph, vNext);
            result.add(type, requirement);
        }

        return result;

    }

    public static JsonObject GetRequirementsFromArtifactVertex(OrientGraphNoTx graph, String edgeName, OrientVertex vArtifact) {
        JsonObject result = new JsonObject();

        // get the edges.

        Iterable<com.tinkerpop.blueprints.Edge> edges = vArtifact.getEdges(Direction.BOTH, edgeName);
        if (edges != null && edges.iterator().hasNext()) {
            // loop through the result.
            com.tinkerpop.blueprints.Edge edge = edges.iterator().next();
            OrientVertex vRequirement = (OrientVertex) edge.getVertex(Direction.IN);
            String type = vRequirement.getProperty("type");

            JsonObject requirement = GetRequirementFromVertex(graph, vRequirement);

            result.add(type, requirement);
        }
        return result;
    }

    public static String GetVersionFromArtifactVertex(OrientGraphNoTx graph, OrientVertex vArtifact) {
        String result = null;
        // get the edges.
        Iterable<com.tinkerpop.blueprints.Edge> edges = vArtifact.getEdges(Direction.BOTH, "has_version");
        if (edges != null && edges.iterator().hasNext()) {
            // loop through the result.
            com.tinkerpop.blueprints.Edge edge = edges.iterator().next();
            OrientVertex vVersion = (OrientVertex) edge.getVertex(Direction.IN);
            result = vVersion.getProperty("version");
        }
        return result;
    }

    public static Artifact GetArtifactFromGraph(OrientGraphNoTx graph, String name) {

        Artifact artifact = null;

        Iterable<Vertex> Artifacts = graph.getVertices("Artifact.name", name);
        if(Artifacts !=null&&Artifacts.iterator().hasNext())

        {
            artifact = new Artifact();
            OrientVertex vArtifact = (OrientVertex) Artifacts.iterator().next();
            artifact.setKey((String) vArtifact.getProperty("key"));
            artifact.setName((String) vArtifact.getProperty("name"));
            artifact.setSystem((String) vArtifact.getProperty("system"));
            artifact.setShortName((String) vArtifact.getProperty("shortName"));
            artifact.setDescription((String) vArtifact.getProperty("description"));
            artifact.setUrl((String) vArtifact.getProperty("url"));
            artifact.setVendor((String) vArtifact.getProperty("vendor"));
            artifact.setVendorContact((String) vArtifact.getProperty("vendorContact"));
            artifact.setVersion((String) vArtifact.getProperty("version"));

            // get the requires and provides from the graph database.
            JsonObject artifactRequires = GetRequirementsFromArtifactVertex(graph, "Requires", vArtifact);
            artifact.setRequires(artifactRequires);

            JsonObject artifactProvides = GetRequirementsFromArtifactVertex(graph, "Provides", vArtifact);
            artifact.setProvides(artifactProvides);

            String version = GetVersionFromArtifactVertex(graph, vArtifact);
            artifact.setVersion(version);
        }
        return artifact;
    }

    public static String GetComponentEnvironmentFromVertex (OrientGraphNoTx graph, OrientVertex vNode)
    {
        String result = null;

        // get the edges.

        Iterable<com.tinkerpop.blueprints.Edge> edges = vNode.getEdges(Direction.BOTH, "is_deployed_to");
        if (edges != null && edges.iterator().hasNext()) {
            // loop through the result.
            com.tinkerpop.blueprints.Edge edge = edges.iterator().next();
            OrientVertex vVersion = (OrientVertex) edge.getVertex(Direction.IN);
            result = vVersion.getProperty("name");
        }
        return result;
    }



    public static void safeVertexPropertySet (OrientVertex vertex, String propertyName, String propertyValue)
    {
        if (propertyValue != null)
        {
            vertex.setProperty(propertyName, propertyValue);
        }
    }


    public static void createLinkedVertex (OrientGraphNoTx graph, String edgeName, OrientVertex vertex, String propertyName, JsonObject jsonObject)
    {
        if (jsonObject != null)
        {
            // create the linked vertex.
            OrientVertex vNew = graph.addVertex("class:" + propertyName);
            vertex.addEdge(edgeName, vNew);
            // now add all of the element properties to the new object.
            Set<Map.Entry<String,JsonElement>> attributes = jsonObject.entrySet();

            for (Map.Entry<String,JsonElement> attribute: attributes)
            {
                JsonElement element = attribute.getValue();
                // add the attribute to the object.
                String key = attribute.getKey();
                if (element.isJsonPrimitive())
                {
                    String value = element.getAsString();
                    safeVertexPropertySet(vNew, key, value);
                } else if (element.isJsonObject())
                {
                    createLinkedVertex(graph, "has", vNew, key, element.getAsJsonObject());
                } else if (element.isJsonArray())
                {
                    JsonArray items = element.getAsJsonArray();
                    String values = "";
                    for (JsonElement item : items)
                    {
                        if (item.isJsonObject())
                        {
                            createLinkedVertex(graph, "has", vNew, key, item.getAsJsonObject());
                        }
                        else if (item.isJsonPrimitive())
                        {
                            if (values.length() > 0)
                            {
                                values += ",";
                            }
                            values += item.getAsString();
                        }
                    }
                    if (values.length() > 0)
                    {
                        safeVertexPropertySet(vNew, key, values);
                    }
                }
            }
        }
    }


    public static void CreateLinkedVersion (OrientGraphNoTx graph, OrientVertex vBase, String version)
    {
        CreateVertexTypeIfNotExists( graph, "Version");
        OrientVertex vVersion = graph.addVertex("class:Version");
        if (version != null)
        {
            vVersion.setProperty("version",version);
        }
        vBase.addEdge("has_version", vVersion);
    }

    public static void LinkComponentEnvironment (OrientGraphNoTx graph, OrientVertex vBase, String name)
    {
        String vertexType = "ComponentEnvironment";
        OrientVertex vComponentEnvironment = null;
        CreateVertexTypeIfNotExists( graph, vertexType);
        Iterable<Vertex> ComponentEnvironments = graph.getVertices(vertexType + ".name", name);
        if (ComponentEnvironments != null && ComponentEnvironments.iterator().hasNext())
        {
            vComponentEnvironment = (OrientVertex) ComponentEnvironments.iterator().next();
        }
        else
        {
            vComponentEnvironment = graph.addVertex("class:" + vertexType);
            vComponentEnvironment.setProperty("name", name);
        }


        if (vComponentEnvironment != null)
        {
            // check to see if there is already and edge connecting them.
            Iterable<Edge> edges = vBase.getEdges(Direction.IN, "is_deployed_to");
            if (edges == null || !edges.iterator().hasNext())
            {
                // create the edge.
                vBase.addEdge("is_deployed_to", vComponentEnvironment);
            }
        }
    }


    public static OrientVertex CreateArtifactVertex (OrientGraphNoTx graph, Artifact artifact)
    {
        CreateVertexTypeIfNotExists( graph, "Version");
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
        String version = artifact.getVersion();
        CreateLinkedVersion(graph, vArtifact, version);

        /* requires and provides are stored as related vertexes with edges.  */

        updatedRequirements (graph, "Requires", vArtifact, artifact.getRequires());
        updatedRequirements (graph, "Provides", vArtifact, artifact.getProvides());

        return vArtifact;
    }

    public static void ProcessAttribute (OrientGraphNoTx graph, OrientVertex vNode, Map.Entry<String,JsonElement> attribute)
    {
        JsonElement element = attribute.getValue();
        // add the attribute to the object.
        String key = attribute.getKey();
        if (element.isJsonPrimitive())
        {
            String value = element.getAsString();
            safeVertexPropertySet(vNode, key, value);
        } else if (element.isJsonObject())
        {
            createLinkedVertex(graph, "has", vNode, key, element.getAsJsonObject());
        } else if (element.isJsonArray())
        {
            JsonArray items = element.getAsJsonArray();
            String values = "";
            for (JsonElement item : items)
            {
                if (item.isJsonObject())
                {
                    createLinkedVertex(graph, "has", vNode, key, item.getAsJsonObject());
                }
                else if (item.isJsonPrimitive())
                {
                    if (values.length() > 0)
                    {
                        values += ",";
                    }
                    values += item.getAsString();
                }
            }
            if (values.length() > 0)
            {
                safeVertexPropertySet(vNode, key, values);
            }
        }
    }

    public static OrientVertex CreateNodeVertex (OrientGraphNoTx graph, Node node)
    {
        // create the item in the graph database.
        OrientVertex vNode = graph.addVertex("class:Node");

        safeVertexPropertySet(vNode, "key", node.getKey());
        safeVertexPropertySet(vNode, "name", node.getName());

        Set<Map.Entry<String,JsonElement>> attributes = node.getAttributes().entrySet();

        for (Map.Entry<String,JsonElement> attribute: attributes) {
            ProcessAttribute(graph, vNode, attribute);
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

    // true if there is a match

    public static Boolean matchAttributes (OrientGraphNoTx graph, OrientVertex vNode, JsonObject selector )
    {
        Boolean result = true;

        // Loop through all of the properties of the selector to see if we have a match.
        Set<Map.Entry<String,JsonElement>> properties =  selector.entrySet();

        for (Map.Entry<String, JsonElement> property : properties)
        {
            Boolean foundSelector = false;
            String propertyKey = property.getKey();
            JsonElement propertyValue = property.getValue();

            if (propertyValue.isJsonPrimitive())
            {
                String propertyValueString = propertyValue.getAsString();

                // now get the attribute's properties.
                Map<String, Object> attributes = vNode.getProperties();
                for (String attributeKey: attributes.keySet())
                {
                    // discard key and name
                    if (attributeKey != null && !attributeKey.equalsIgnoreCase("name") && !attributeKey.equalsIgnoreCase("key"))
                    {
                        String attributeValue = attributes.get(attributeKey).toString();
                        if (attributeKey.equalsIgnoreCase(propertyKey) && attributeValue != null && attributeValue.equalsIgnoreCase(propertyValueString))
                        {
                            foundSelector = true;
                        }
                    }
                }

                if (foundSelector == false)
                {
                    result = false;
                }
            }
        }

        return result;
    }

    public static JsonObject HaveNode(OrientGraphNoTx graph, Map.Entry<String,JsonElement> requirement, JsonObject requirementSpec)
    {
        // At this stage the requirementSpec should contain the selector as a property.
        if (requirementSpec.has("selector") && requirementSpec.get("selector").isJsonObject())
        {
            // get the requirementSpec's selector.
            JsonObject selector = requirementSpec.get("selector").getAsJsonObject();

            // get possible Nodes.
            Iterable<Vertex> Nodes = graph.getVerticesOfClass("Node");
            if (Nodes != null && Nodes.iterator().hasNext())
            {
                OrientVertex vNode = (OrientVertex) Nodes.iterator().next();

                Boolean isMatch = matchAttributes (graph, vNode, selector);

                if (isMatch)
                {
                        JsonObject matches = new JsonObject();
                        matches.add ("node-key", new JsonPrimitive(vNode.getProperty("key").toString()));

                        requirementSpec.add("matches", matches);



                        // update expand.  expand is an array of strings.
                }
            }
        }
        return requirementSpec;
    }

    public static JsonObject HaveRequirementSpec(OrientGraphNoTx graph, Map.Entry<String,JsonElement> requirement, JsonObject requirementSpec)
    {
        OrientVertex vResult = null;

        Iterable<Vertex> Components = graph.getVertices("RequirementSpec.type", requirement.getKey());
        if (Components != null && Components.iterator().hasNext())
        {
            vResult = (OrientVertex) Components.iterator().next();

            // determine if there is a vertex that provides this requirement.

            Iterable<Vertex> providers = vResult.getVertices(Direction.BOTH,"Provides");
            if (providers != null && providers.iterator().hasNext())
            {
                OrientVertex vProvider = (OrientVertex) providers.iterator().next();

                // update matches.
                JsonObject matches = new JsonObject();
                matches.add ("node-key", new JsonPrimitive(vProvider.getProperty("key").toString()));

                requirementSpec.add("matches", matches);

                // update expand.  expand is an array of strings.


            }
        }
        return requirementSpec;
    }
    public static JsonObject HaveRequirement (OrientGraphNoTx graph, Map.Entry<String,JsonElement> requirement)
    {
        Boolean result = false;
        // search the graph to determine if there is a suitable requirementSpec.


        // lookup the RequirementSpec.

        JsonObject requirementSpec = requirement.getValue().getAsJsonObject();

        String requirementType = requirement.getKey();


        if (requirementType.equals("host"))
        {
            requirementSpec = HaveNode(graph, requirement, requirementSpec);
            if (!requirementSpec.has("matches") || requirementSpec.get("matches").getAsJsonObject() == null)
            {
                requirementSpec = HaveRequirementSpec(graph, requirement, requirementSpec);
            }
        }
        else
        {
            requirementSpec = HaveRequirementSpec(graph, requirement, requirementSpec);
        }

        if (requirementSpec.has("matches") && requirementSpec.get("matches").getAsJsonObject() != null)
        {
            return requirementSpec;
        }
        else
        {
            return null;
        }

    }

    public static void UpdateRequirementSpecNodeEdge(OrientGraphNoTx graph, String requirementSpecKey , String nodeKey)
    {
        OrientVertex vRequirementSpec = null;
        Iterable<Vertex> Requirements = graph.getVertices("RequirementSpec.key", requirementSpecKey);
        if (Requirements != null && Requirements.iterator().hasNext()) {
            vRequirementSpec = (OrientVertex) Requirements.iterator().next();
        }
        // search for the related node.
        Iterable<Vertex> Nodes = graph.getVertices("Node.key", nodeKey);
        if (vRequirementSpec!= null && Nodes != null && Nodes.iterator().hasNext()) {
            OrientVertex vNode = (OrientVertex) Nodes.iterator().next();
            vRequirementSpec.addEdge("matches",vNode);
        }
    }

}
