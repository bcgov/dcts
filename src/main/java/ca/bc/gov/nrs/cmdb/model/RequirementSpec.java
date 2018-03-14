package ca.bc.gov.nrs.cmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Objects;

/**
 * Artifact
 **/
//import org.springframework.data.gremlin.annotation.*;

//@Vertex
public class RequirementSpec {
  private static Gson gson;


  @SerializedName("key")
  private String key = null;
  public String getKey() { return key; }
  public void setKey(String key ) { this.key = key; }

  @SerializedName("quantifier")
  private String quantifier = null;
  public String getQuantifier() { return quantifier; }
  public void setQuantifier(String quantifier ) { this.quantifier = quantifier; }

  @SerializedName("interface")
  private String _interface = null;
  public String getInterface() { return _interface; }
  public void setInterface(String interfaceText ) { this._interface = interfaceText; }

  @SerializedName("resolution")
  private String resolution = null;
  public String getResolution() { return resolution; }
  public void setResolution(String resolution ) { this.resolution = resolution; }

  @SerializedName("artifact-key")
  private String artifactKey = null;
  public String getArtifactKey() { return artifactKey; }
  public void setArtifactKey(String artifactKey ) { this.artifactKey = artifactKey; }

  @SerializedName("expand")
  private String[] expand = null;
  public String[] getExpand() { return expand; }
  public void setExpand(String[] expand ) { this.expand = expand; }


  @SerializedName("scope")
  private String scope = null;
  public String getScope() { return scope; }
  public void setScope(String scope ) { this.scope = scope; }

  /**
   * The version of the RequirementSpec
   **/
  @SerializedName("version")
  private  String version = null;
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

  @SerializedName("error")
  private ErrorSpec error = null;
  public ErrorSpec getError() { return error; }
  public void setError(ErrorSpec error ) { this.error = error; }

  @SerializedName("matches")
  private HashMap<String, String> matches;
  public HashMap<String, String> getMatches() { return matches; }
  public void setMatches(HashMap<String, String> matches ) { this.matches = matches; }




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

