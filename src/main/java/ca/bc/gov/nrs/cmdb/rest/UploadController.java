/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.bc.gov.nrs.cmdb.rest;

import ca.bc.gov.nrs.cmdb.GraphTools;
import ca.bc.gov.nrs.cmdb.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.UUID;

import static ca.bc.gov.nrs.cmdb.GraphTools.*;

/**
 *
 * @author George
 */

@RestController
@RequestMapping("/upload")

public class UploadController {
    private String defaultName = "com.oracle.ofm";

    private static Gson gson;

    @Autowired
    private OrientGraphFactory factory;


    /***
     * Get an artifact template.
     * @return
     */

    
        @PostMapping
    public String Upload(@RequestBody String data)
    {
        gson = new Gson();

        String result = "[";

        OrientGraphNoTx graph =  factory.getNoTx();
        ObjectMapper mapper = new ObjectMapper();

        // first parse the data as an UploadSpec.

        JsonArray uploadData = (JsonArray) new JsonParser().parse(data);

        boolean first = true;

        for (JsonElement element : uploadData)
        {
            if (first == true)
            {
                first = false;
            }
            else
            {
                result += ",";
            }

            JsonObject jsonObject = element.getAsJsonObject();
            String kind = jsonObject.get("kind").getAsString();
            JsonElement value = jsonObject.get("value");

            try {

                if (kind.equalsIgnoreCase("artifact"))
                {
                    Artifact artifact = gson.fromJson(value, Artifact.class);
                    String key = artifact.getKey();
                    if (key == null || key.isEmpty())
                    {
                        artifact.setKey(UUID.randomUUID().toString());
                    }

                    GraphTools.CreateArtifactVertex (graph, artifact);
                    result += gson.toJson(artifact);
                }

                 else if (kind.equalsIgnoreCase("node")) {
                    Node node = gson.fromJson(value, Node.class);

                    String key = node.getKey();
                    if (key == null || key.isEmpty())
                    {
                        node.setKey(UUID.randomUUID().toString());
                    }

                    GraphTools.CreateNodeVertex (graph, node);
                    result += gson.toJson(node);
                }
            }
            catch (Exception e)
            {
                ErrorSpec error = new ErrorSpec();
                error.setCode("Error updating data");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);

                error.setMessage(e.toString() + sw.toString());
                result += gson.toJson(error);
            }
        }

        graph.shutdown();

        result += "]";

        return result;
    }
}
