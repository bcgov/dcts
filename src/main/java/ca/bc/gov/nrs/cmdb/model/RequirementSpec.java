package ca.bc.gov.nrs.cmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Objects;

/**
 * Artifact
 **/
//import org.springframework.data.gremlin.annotation.*;

//@Vertex
public class RequirementSpec {
  private static Gson gson;
//  @Id

//  @Property("module_name")

  private String quantifier = null;

  @JsonProperty("quantifier")
  public String getQuantifier() { return quantifier; }
  public void setQuantifier(String quantifier ) { this.quantifier = quantifier; }

  private String resolution = null;

  @JsonProperty("resolution")
  public String getResolution() { return resolution; }
  public void setResolution(String resolution ) { this.resolution = resolution; }

  private String artifactKey = null;

  @JsonProperty("artifact-key")
  public String getArtifactKey() { return artifactKey; }
  public void setArtifactKey(String artifactKey ) { this.artifactKey = artifactKey; }

  private String[] expand = null;

  @JsonProperty("expand")
  public String[] getExpand() { return expand; }
  public void setExpand(String[] expand ) { this.expand = expand; }


  private String scope = null;
  @JsonProperty("scope")
  public String getScope() { return scope; }
  public void setScope(String scope ) { this.scope = scope; }

  private String version = null;

  private ErrorSpec error = null;
  @JsonProperty("error")
  public ErrorSpec getError() { return error; }
  public void setError(ErrorSpec error ) { this.error = error; }

  private HashMap<String, String> matches;
  @JsonProperty("matches")
  public HashMap<String, String> getMatches() { return matches; }
  public void setMatches(HashMap<String, String> matches ) { this.matches = matches; }

  /**
   * The version of the Export
   **/

  @JsonProperty("version")
  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
  }

  public RequirementSpec version(String version) {
    this.version = version;
    return this;
  }



  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RequirementSpec selector = (RequirementSpec) o;
    return Objects.equals(quantifier, selector.quantifier) &&
        Objects.equals(version, selector.version) ;
  }

  @Override
  public int hashCode() {
    return Objects.hash(quantifier, version, scope, resolution);
  }

  public String getKey (String keyType)
  {
    return keyType + ((Integer)hashCode()).toString();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Selector {\n");

    sb.append("    quantifier: ").append(toIndentedString(quantifier)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");

    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

  public String toJson() {
     return gson.toJson(this);
  }

}

