package ca.bc.gov.nrs.cmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

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



  private SelectorSpec selector = null;

  @JsonProperty("selector")
  public SelectorSpec getSelector() { return selector; }
  public void setSelector( SelectorSpec selector ) {this.selector = selector; }

  private String quantifier = null;

  @JsonProperty("quantifier")
  public String getQuantifier() { return quantifier; }
  public void setQuantifier(String quantifier ) { this.quantifier = quantifier; }

  private String resolution = null;

  @JsonProperty("resolution")
  public String getResolution() { return resolution; }
  public void setResolution(String resolution ) { this.resolution = resolution; }

  private String[] expand = null;

  @JsonProperty("expand")
  public String[] getExpand() { return expand; }
  public void setExpand(String[] expand ) { this.expand = expand; }


  private String scope = null;
  @JsonProperty("scope")
  public String getScope() { return scope; }
  public void setScope(String scope ) { this.scope = scope; }

  private String version = null;

  public RequirementSpec version(String version) {
    this.version = version;
    return this;
  }


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
    return Objects.hash(quantifier, version);
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

