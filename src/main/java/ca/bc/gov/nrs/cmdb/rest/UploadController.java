/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.bc.gov.nrs.cmdb.rest;

import ca.bc.gov.nrs.cmdb.GraphTools;
import ca.bc.gov.nrs.cmdb.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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



        String result = "";

        OrientGraphNoTx graph =  factory.getNoTx();
        ObjectMapper mapper = new ObjectMapper();

        // first parse the data as an UploadSpec.

        UploadSpec uploadSpec = gson.fromJson(data, UploadSpec.class);

        if (uploadSpec.getKind().equalsIgnoreCase("artifact"))
        {
            UploadArtifactSpec uploadArtifactSpec = gson.fromJson(data, UploadArtifactSpec.class);

            Artifact artifact = (Artifact) uploadArtifactSpec.getValue();
            GraphTools.CreateArtifactVertex (graph, artifact);
            try {
                result = mapper.writeValueAsString(artifact);
            }
            catch (Exception e)
            {
                result = "\"ERROR" + e.toString() + "\"";
            }
        }

        graph.shutdown();

        return result;

    }

}
