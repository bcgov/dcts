package ca.bc.gov.nrs.cmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * SchemaSpec
 **/

public class SchemaSpec {
  private static Gson gson;

  @JsonProperty("artifact")
  private Artifact artifact = null;


  @JsonProperty ("deploymentSpecificationPlan")
  private DeploymentSpecificationPlan deploymentSpec=null;

  @JsonProperty ("requirementspec")
  private RequirementSpec requirementSpec=null;

  @JsonProperty ("selectorspec")
  private SelectorSpec selectorSpec=null;

  @JsonProperty ("errorspec")
  private ErrorSpec errorSpec=null;


  public String toJson() {
      if (gson == null)
      {
          gson = new Gson();
      }
     return gson.toJson(this);
  }

}