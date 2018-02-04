/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.bc.gov.nrs.cmdb.rest;

import ca.bc.gov.nrs.cmdb.GraphTools;
import ca.bc.gov.nrs.cmdb.model.Artifact;
import ca.bc.gov.nrs.cmdb.model.ErrorSpec;
import ca.bc.gov.nrs.cmdb.model.Node;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author George
 */

@RestController
@RequestMapping("/graph")

public class GraphController {
    private String defaultName = "com.oracle.ofm";

    private static Gson gson;

    @Autowired
    private OrientGraphFactory factory;


    /***
     * Reset the graph; deletes everything.
     * @return
     */


    @RequestMapping("/reset")
    public String Reset()
    {
        String result = "";

        OrientGraphNoTx graph =  factory.getNoTx();
        OrientVertex vArtifact = null;
        try {

            OCommandRequest command = graph.command( new OCommandSQL( "DELETE Edge E"));
            command.execute();
            command = graph.command( new OCommandSQL( "DELETE Vertex V"));
            command.execute();
            result = "\"Graph cleared.\"";
        }
        catch (Exception e)
        {
            result = "\"ERROR" + e.toString() + "\"";
        }

        graph.shutdown();
        return result;
    }
}
