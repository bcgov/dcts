package ca.bc.gov.nrs.cmdb;

import ca.bc.gov.nrs.cmdb.model.Artifact;
import ca.bc.gov.nrs.cmdb.model.DeploymentSpecificationPlan;
import ca.bc.gov.nrs.cmdb.model.Node;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GraphTools {


    public static void createComponentFromRequirement(OrientGraphNoTx graph, String edgeName, OrientVertex vArtifactDeploymentSpec, JsonObject requirementHash, String environment, OrientVertex vSystem) {
        if (requirementHash != null) {
            // start by verifying that the RequirementSpec exists.
            createVertexTypeIfNotExists(graph, "Component");

            // loop through the set of requirements.
            Set<Map.Entry<String, JsonElement>> requirements = requirementHash.entrySet();

            for (Map.Entry<String, JsonElement> requirement : requirements) {
                String key = UUID.randomUUID().toString();


                // now add all of the other properties.
                JsonObject requirementProperties = (JsonObject) requirement.getValue();


                String componentName = "unknown";

                if (requirementProperties.has("interface")) {
                    componentName = requirementProperties.get("interface").getAsString();
                }

                OrientVertex vComponent = createVertexIfNotExists(graph, "Component", componentName);
                safeVertexPropertySet(vComponent, "type", requirement.getKey());

                OrientVertex vComponentEnvironment = getComponentEnvironment(graph, environment, componentName);


                Set<Map.Entry<String, JsonElement>> properties = requirementProperties.entrySet();
                for (Map.Entry<String, JsonElement> property : properties) {
                    JsonElement propertyValue = property.getValue();
                    if (propertyValue.isJsonPrimitive()) {
                        safeVertexPropertySet(vComponent, property.getKey(), propertyValue.getAsString());
                    } else if (propertyValue.isJsonObject()) {
                        // create the object as a linked item.
                        createLinkedVertex(graph, "has", vComponent, property.getKey(), propertyValue.getAsJsonObject());
                    }

                }
                // create an edge linking the Artifact Deployment Spec to the Component.
                createEdgeIfNotExists(graph, vArtifactDeploymentSpec, vComponentEnvironment, edgeName);

                // create an edge linking the Component to the Component Environment.
                createEdgeIfNotExists(graph, vComponentEnvironment, vComponent, "Instance");

                // create an edge linking the Component to the Component Environment.
                createEdgeIfNotExists(graph, vSystem, vComponent, "Has");
            }
        }
    }

    public static void cleanupComponentInstanceLink(OrientGraphNoTx graph, JsonObject requirementHash, String environment) {
        if (requirementHash != null) {

            // loop through the set of requirements.
            Set<Map.Entry<String, JsonElement>> requirements = requirementHash.entrySet();

            for (Map.Entry<String, JsonElement> requirement : requirements) {

                JsonObject requirementProperties = (JsonObject) requirement.getValue();

                String componentName = "unknown";

                if (requirementProperties.has("interface")) {
                    componentName = requirementProperties.get("interface").getAsString();
                }

                OrientVertex vComponent = getVertexByName(graph, "Component", componentName);

                OrientVertex vComponentEnvironment = getVertexByName(graph, "ComponentEnvironment",  environment + "_" + componentName);

                // create an edge linking the Component to the Component Environment.
                removeEdgeIfExists(graph, vComponentEnvironment, vComponent, "Instance");
            }
        }
    }

    public static OrientVertex createVertexIfNotExists(OrientGraphNoTx graph, String vertexType, String key) {
        OrientVertex result = null;
        // lookup the Component.
        Iterable<Vertex> Components = graph.getVertices(vertexType + ".key", key);
        if (Components != null && Components.iterator().hasNext()) {
            result = (OrientVertex) Components.iterator().next();
        } else {
            result = graph.addVertex("class:" + vertexType);
            result.setProperty("key", key);
        }
        return result;
    }

    public static OrientVertex getComponentEnvironment(OrientGraphNoTx graph, String environment, String component) {
        // create the component environment if it does not exist
        OrientVertex vComponentEnvironment = null;

        String name = environment + "_" + component;
        Iterable<Vertex> vComponentEnvironments = graph.getVertices("ComponentEnvironment.name", name);
        if (vComponentEnvironments != null && vComponentEnvironments.iterator().hasNext()) {
            vComponentEnvironment = (OrientVertex) vComponentEnvironments.iterator().next();
        } else {
            vComponentEnvironment = graph.addVertex("class:ComponentEnvironment");
            vComponentEnvironment.setProperty("key", UUID.randomUUID().toString());
            vComponentEnvironment.setProperty("environment", environment);
            vComponentEnvironment.setProperty("name", name);
        }
        return vComponentEnvironment;
    }

    public static void removeEdgeIfExists(OrientGraphNoTx graph, OrientVertex vSource, OrientVertex vDestination, String edgeLabel) {
        if (vSource != null && vDestination != null)
        {
            // remove the edge if it exists.
            Iterable<com.tinkerpop.blueprints.Edge> edges = vSource.getEdges(vDestination, Direction.BOTH, edgeLabel);
            if (edges != null && edges.iterator().hasNext()) {
                graph.removeEdge(edges.iterator().next());
            }
        }

    }

    public static void createRequiresObjects(OrientGraphNoTx graph, String edgeName, OrientVertex vArtifactDeploymentSpec, JsonObject requirementHash) {
        if (requirementHash != null) {


            // loop through the set of requirements.
            Set<Map.Entry<String, JsonElement>> requirements = requirementHash.entrySet();

            for (Map.Entry<String, JsonElement> requirement : requirements) {
                String key = UUID.randomUUID().toString();


                // now add all of the other properties.
                JsonObject requirementProperties = (JsonObject) requirement.getValue();

                if (requirementProperties.has("matches")) {
                    JsonObject matches = requirementProperties.get("matches").getAsJsonObject();
                    if (matches.has("node-key")) {
                        String nodeKey = matches.get("node-key").getAsString();

                        // find the node.
                        OrientVertex vNode = findNode(graph, nodeKey);

                        // create an edge linking to the node.
                        createEdgeIfNotExists(graph, vArtifactDeploymentSpec, vNode, edgeName);
                    }
                }


            }
        }
    }

    public static OrientVertex findNode(OrientGraphNoTx graph, String nodeKey) {
        // create the component environment if it does not exist
        OrientVertex vResult = null;

        Iterable<Vertex> vResults = graph.getVertices("V.key", nodeKey);
        if (vResults != null && vResults.iterator().hasNext()) {
            vResult = (OrientVertex) vResults.iterator().next();
        }
        return vResult;
    }

    public static void createEdgeIfNotExists(OrientGraphNoTx graph, OrientVertex vSource, OrientVertex vDestination, String edgeLabel) {
        // ensure there is an edge between the ExecutionEnvironment and the property.
        Iterable<com.tinkerpop.blueprints.Edge> edges = vSource.getEdges(vDestination, Direction.BOTH, edgeLabel);
        if (edges == null || !edges.iterator().hasNext()) {
            graph.addEdge(null, vSource, vDestination, edgeLabel);
        }
    }

    public static void updatedRequirements(OrientGraphNoTx graph, String edgeName, OrientVertex vArtifact, JsonObject requirementHash) {
        if (requirementHash != null) {
            // start by verifying that the RequirementSpec exists.
            createVertexTypeIfNotExists(graph, "RequirementSpec");

            // loop through the set of requirements.
            Set<Map.Entry<String, JsonElement>> requirements = requirementHash.entrySet();

            for (Map.Entry<String, JsonElement> requirement : requirements) {
                String key = UUID.randomUUID().toString();
                OrientVertex vRequirementSpec = createVertexIfNotExists(graph, "RequirementSpec", key);
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
                createEdgeIfNotExists(graph, vArtifact, vRequirementSpec, edgeName);
            }
        }
    }

    public static JsonObject getRequirementFromVertex(OrientGraphNoTx graph, OrientVertex vRequirement) {
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
            // special case if the type is "Attribute".


            JsonObject requirement = getRequirementFromVertex(graph, vNext);
            if (type.equalsIgnoreCase("attribute")) {
                type = requirement.get("name").getAsString();
                requirement.remove("name");
            }
            result.add(type, requirement);
        }

        return result;

    }

    public static JsonObject getRequirementsFromArtifactVertex(OrientGraphNoTx graph, String edgeName, OrientVertex vArtifact) {
        JsonObject result = new JsonObject();

        // get the edges.

        Iterable<com.tinkerpop.blueprints.Edge> edges = vArtifact.getEdges(Direction.BOTH, edgeName);
        if (edges != null && edges.iterator().hasNext()) {
            // loop through the result.
            com.tinkerpop.blueprints.Edge edge = edges.iterator().next();
            OrientVertex vRequirement = (OrientVertex) edge.getVertex(Direction.IN);
            String type = vRequirement.getProperty("type");

            JsonObject requirement = getRequirementFromVertex(graph, vRequirement);

            result.add(type, requirement);
        }
        return result;
    }

    public static String getVersionFromArtifactVertex(OrientGraphNoTx graph, OrientVertex vArtifact) {
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

    public static OrientVertex getVersionVertexFromArtifactVertex(OrientGraphNoTx graph, OrientVertex vArtifact) {
        OrientVertex result = null;
        // get the edges.
        Iterable<com.tinkerpop.blueprints.Edge> edges = vArtifact.getEdges(Direction.BOTH, "has_version");
        if (edges != null && edges.iterator().hasNext()) {
            // loop through the result.
            com.tinkerpop.blueprints.Edge edge = edges.iterator().next();
            result = (OrientVertex) edge.getVertex(Direction.IN);
        }
        return result;
    }

    public static Artifact getArtifactFromGraph(OrientGraphNoTx graph, String name) {

        Artifact artifact = null;

        Iterable<Vertex> Artifacts = graph.getVertices("Artifact.name", name);
        if (Artifacts != null && Artifacts.iterator().hasNext())

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
            JsonObject artifactRequires = getRequirementsFromArtifactVertex(graph, "Requires", vArtifact);
            artifact.setRequires(artifactRequires);

            JsonObject artifactProvides = getRequirementsFromArtifactVertex(graph, "Provides", vArtifact);
            artifact.setProvides(artifactProvides);

            String version = getVersionFromArtifactVertex(graph, vArtifact);
            artifact.setVersion(version);
        }
        return artifact;
    }

    public static String getComponentEnvironmentFromVertex(OrientGraphNoTx graph, OrientVertex vNode) {
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

    public static void safeVertexPropertySet(OrientVertex vertex, String propertyName, String propertyValue) {
        if (propertyValue != null) {
            vertex.setProperty(propertyName, propertyValue);
        }
    }

    public static void createLinkedVertex(OrientGraphNoTx graph, String edgeName, OrientVertex vertex, String propertyName, JsonObject jsonObject) {
        if (jsonObject != null && propertyName != "matches") {
            String vertexClass = "Attribute";

            if (propertyName.equalsIgnoreCase("selector")) {
                vertexClass = "selector";
            }

            if (graph.getVertexType(vertexClass) == null) {
                graph.createVertexType(vertexClass);
            }

            // create the linked vertex.
            OrientVertex vNew = graph.addVertex("class:" + vertexClass);
            if (vertexClass.equalsIgnoreCase("attribute")) {
                vNew.setProperty("name", propertyName);
            }
            vertex.addEdge(edgeName, vNew);
            // now add all of the element properties to the new object.
            Set<Map.Entry<String, JsonElement>> attributes = jsonObject.entrySet();

            for (Map.Entry<String, JsonElement> attribute : attributes) {
                JsonElement element = attribute.getValue();
                // add the attribute to the object.
                String key = attribute.getKey();
                if (element.isJsonPrimitive()) {
                    String value = element.getAsString();
                    safeVertexPropertySet(vNew, key, value);
                } else if (element.isJsonObject()) {
                    createLinkedVertex(graph, "has", vNew, key, element.getAsJsonObject());
                } else if (element.isJsonArray()) {
                    JsonArray items = element.getAsJsonArray();
                    String values = "";
                    for (JsonElement item : items) {
                        if (item.isJsonObject()) {
                            createLinkedVertex(graph, "has", vNew, key, item.getAsJsonObject());
                        } else if (item.isJsonPrimitive()) {
                            if (values.length() > 0) {
                                values += ",";
                            }
                            values += item.getAsString();
                        }
                    }
                    if (values.length() > 0) {
                        safeVertexPropertySet(vNew, key, values);
                    }
                }
            }
        }
    }

    public static void createLinkedVersion(OrientGraphNoTx graph, OrientVertex vBase, String version) {
        createVertexTypeIfNotExists(graph, "Version");
        OrientVertex vVersion = graph.addVertex("class:Version");
        if (version != null) {
            vVersion.setProperty("version", version);
        }
        vBase.addEdge("has_version", vVersion);
    }

    public static void linkComponentEnvironment(OrientGraphNoTx graph, OrientVertex vBase, String name) {
        String vertexType = "ComponentEnvironment";
        OrientVertex vComponentEnvironment = null;
        createVertexTypeIfNotExists(graph, vertexType);
        Iterable<Vertex> ComponentEnvironments = graph.getVertices(vertexType + ".name", name);
        if (ComponentEnvironments != null && ComponentEnvironments.iterator().hasNext()) {
            vComponentEnvironment = (OrientVertex) ComponentEnvironments.iterator().next();
        } else {
            vComponentEnvironment = graph.addVertex("class:" + vertexType);
            vComponentEnvironment.setProperty("name", name);
        }


        if (vComponentEnvironment != null) {
            // check to see if there is already and edge connecting them.
            Iterable<Edge> edges = vBase.getEdges(Direction.IN, "is_deployed_to");
            if (edges == null || !edges.iterator().hasNext()) {
                // create the edge.
                vBase.addEdge("is_deployed_to", vComponentEnvironment);
            }
        }
    }

    public static void linkPreviousDeployments(OrientGraphNoTx graph, OrientVertex vDeployment, DeploymentSpecificationPlan deploymentSpecificationPlan)
    {
        // determine if there are similar deployments to link.
        Iterable<Vertex> deploymentSpecificationPlans = graph.getVertices("DeploymentSpecificationPlan.environment", deploymentSpecificationPlan.getComponentEnvironment());
        OrientVertex lastDeployment = null;

        for (Vertex c :   deploymentSpecificationPlans) {
            OrientVertex current = (OrientVertex) c;
            // check to see if we are not on the same deployment as is being processed now.
            String currentIdentity = current.getIdentity().toString();
            String deploymentIdentity = vDeployment.getIdentity().toString();
            if (! currentIdentity.equalsIgnoreCase(deploymentIdentity)) {

                Iterable<Edge> edges = current.getEdges(Direction.OUT, "After");
                if (edges == null || !edges.iterator().hasNext()) {
                    lastDeployment = current;
                }

            }
        }
        // check to see if we found a match.
        if (lastDeployment != null) {
            // add the After edge.
            lastDeployment.addEdge("After", vDeployment);
        }
    }


    public static void createVertexTypeIfNotExists(OrientGraphNoTx graph, String name) {
        if (graph.getVertexType(name) == null) {
            graph.createVertexType(name);
        }
    }

    public static OrientVertex createArtifactVertex(OrientGraphNoTx graph, Artifact artifact) {
        createVertexTypeIfNotExists(graph, "Version");
        // create the item in the graph database.
        OrientVertex vArtifact = graph.addVertex("class:Artifact");
        vArtifact.setProperty("key", artifact.getKey());
        vArtifact.setProperty("name", artifact.getName());
        safeVertexPropertySet(vArtifact, "system", artifact.getSystem());

        safeVertexPropertySet(vArtifact, "shortName", artifact.getShortName());
        safeVertexPropertySet(vArtifact, "description", artifact.getDescription());
        safeVertexPropertySet(vArtifact, "url", artifact.getUrl());
        safeVertexPropertySet(vArtifact, "vendor", artifact.getVendor());
        safeVertexPropertySet(vArtifact, "vendorContact", artifact.getVendorContact());
        String version = artifact.getVersion();
        createLinkedVersion(graph, vArtifact, version);

        /* requires and provides are stored as related vertexes with edges.  */

        updatedRequirements(graph, "Requires", vArtifact, artifact.getRequires());
        updatedRequirements(graph, "Provides", vArtifact, artifact.getProvides());

        return vArtifact;
    }

    public static void processAttribute(OrientGraphNoTx graph, OrientVertex vNode, Map.Entry<String, JsonElement> attribute) {
        JsonElement element = attribute.getValue();
        // add the attribute to the object.
        String key = attribute.getKey();
        if (element.isJsonPrimitive()) {
            String value = element.getAsString();
            safeVertexPropertySet(vNode, key, value);
        } else if (element.isJsonObject()) {
            createLinkedVertex(graph, "has", vNode, key, element.getAsJsonObject());
        } else if (element.isJsonArray()) {
            JsonArray items = element.getAsJsonArray();
            String values = "";
            for (JsonElement item : items) {
                if (item.isJsonObject()) {
                    createLinkedVertex(graph, "has", vNode, key, item.getAsJsonObject());
                } else if (item.isJsonPrimitive()) {
                    if (values.length() > 0) {
                        values += ",";
                    }
                    values += item.getAsString();
                }
            }
            if (values.length() > 0) {
                safeVertexPropertySet(vNode, key, values);
            }
        }
    }

    public static OrientVertex createNodeVertex(OrientGraphNoTx graph, Node node) {
        // create the item in the graph database.
        OrientVertex vNode = graph.addVertex("class:Node");

        safeVertexPropertySet(vNode, "key", node.getKey());
        safeVertexPropertySet(vNode, "name", node.getName());

        Set<Map.Entry<String, JsonElement>> attributes = node.getAttributes().entrySet();

        for (Map.Entry<String, JsonElement> attribute : attributes) {
            processAttribute(graph, vNode, attribute);
        }

        return vNode;
    }

    public static OrientVertex getVertex(OrientGraphNoTx graph, String vertexType, String key) {
        OrientVertex result = null;
        // lookup the Component.
        Iterable<Vertex> Components = graph.getVertices(vertexType + ".key", key);
        if (Components != null && Components.iterator().hasNext()) {
            result = (OrientVertex) Components.iterator().next();
        }
        return result;
    }

    public static OrientVertex getVertexByName(OrientGraphNoTx graph, String vertexType, String name) {
        OrientVertex result = null;
        // lookup the Component.
        Iterable<Vertex> Components = graph.getVertices(vertexType + ".name", name);
        if (Components != null && Components.iterator().hasNext()) {
            result = (OrientVertex) Components.iterator().next();
        }
        return result;
    }


    // true if there is a match

    public static JsonObject haveRequirement(OrientGraphNoTx graph, Map.Entry<String, JsonElement> requirement) {
        Boolean result = false;
        // search the graph to determine if there is a suitable requirementSpec.


        // lookup the RequirementSpec.

        JsonObject requirementSpec = requirement.getValue().getAsJsonObject();

        String requirementType = requirement.getKey();


        if (requirementType.equals("host")) {
            haveNode(graph, requirement, requirementSpec);
            if (!requirementSpec.has("matches") || requirementSpec.get("matches").getAsJsonObject() == null) {
                haveRequirementSpec(graph, requirement, requirementSpec);
            }
        } else {
            haveRequirementSpec(graph, requirement, requirementSpec);
        }

        if (requirementSpec.has("matches") && requirementSpec.get("matches").getAsJsonObject() != null) {
            return requirementSpec;
        } else {
            return null;
        }

    }

    public static void haveNode(OrientGraphNoTx graph, Map.Entry<String, JsonElement> requirement, JsonObject requirementSpec) {
        // At this stage the requirementSpec should contain the selector as a property.
        if (requirementSpec.has("selector") && requirementSpec.get("selector").isJsonObject()) {
            // get the requirementSpec's selector.
            JsonObject selector = requirementSpec.get("selector").getAsJsonObject();

            // get possible Nodes.
            Iterable<Vertex> Nodes = graph.getVerticesOfClass("Node");
            if (Nodes != null && Nodes.iterator().hasNext()) {
                OrientVertex vNode = (OrientVertex) Nodes.iterator().next();

                Boolean isMatch = matchAttributes(graph, vNode, selector);

                if (isMatch) {
                    JsonObject matches = new JsonObject();
                    matches.add("node-key", new JsonPrimitive(vNode.getProperty("key").toString()));

                    requirementSpec.add("matches", matches);

                    // update expand.  expand is an array of strings.
                }
            }
        }
    }

    public static void haveRequirementSpec(OrientGraphNoTx graph, Map.Entry<String, JsonElement> requirement, JsonObject requirementSpec) {
        OrientVertex vResult = null;

        Iterable<Vertex> Components = graph.getVertices("RequirementSpec.type", requirement.getKey());
        if (Components != null && Components.iterator().hasNext()) {
            vResult = (OrientVertex) Components.iterator().next();

            // determine if there is a vertex that provides this requirement.

            Iterable<Vertex> providers = vResult.getVertices(Direction.BOTH, "Provides");
            if (providers != null && providers.iterator().hasNext()) {
                OrientVertex vProvider = (OrientVertex) providers.iterator().next();

                // update matches.
                JsonObject matches = new JsonObject();
                matches.add("node-key", new JsonPrimitive(vProvider.getProperty("key").toString()));

                requirementSpec.add("matches", matches);

                // update expand.  expand is an array of strings.


            }
        }

    }

    public static Boolean matchAttributes(OrientGraphNoTx graph, OrientVertex vNode, JsonObject selector) {
        Boolean result = true;

        // Loop through all of the properties of the selector to see if we have a match.
        Set<Map.Entry<String, JsonElement>> properties = selector.entrySet();

        for (Map.Entry<String, JsonElement> property : properties) {
            Boolean foundSelector = false;
            String propertyKey = property.getKey();
            JsonElement propertyValue = property.getValue();

            if (propertyValue.isJsonPrimitive()) {
                String propertyValueString = propertyValue.getAsString();

                // now get the attribute's properties.
                Map<String, Object> attributes = vNode.getProperties();
                for (String attributeKey : attributes.keySet()) {
                    // discard key and name
                    if (attributeKey != null && !attributeKey.equalsIgnoreCase("name") && !attributeKey.equalsIgnoreCase("key")) {
                        String attributeValue = attributes.get(attributeKey).toString();
                        if (attributeKey.equalsIgnoreCase(propertyKey) && attributeValue != null && attributeValue.equalsIgnoreCase(propertyValueString)) {
                            foundSelector = true;
                        }
                    }
                }

                if (foundSelector == false) {
                    result = false;
                }
            }
        }

        return result;
    }

    public static void updateRequirementSpecNodeEdge(OrientGraphNoTx graph, String requirementSpecKey, String nodeKey) {
        OrientVertex vRequirementSpec = null;
        Iterable<Vertex> Requirements = graph.getVertices("RequirementSpec.key", requirementSpecKey);
        if (Requirements != null && Requirements.iterator().hasNext()) {
            vRequirementSpec = (OrientVertex) Requirements.iterator().next();
        }
        // search for the related node.
        Iterable<Vertex> Nodes = graph.getVertices("Node.key", nodeKey);
        if (vRequirementSpec != null && Nodes != null && Nodes.iterator().hasNext()) {
            OrientVertex vNode = (OrientVertex) Nodes.iterator().next();
            vRequirementSpec.addEdge("matches", vNode);
        }
    }

}