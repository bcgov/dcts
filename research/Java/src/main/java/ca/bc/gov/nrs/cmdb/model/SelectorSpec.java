package ca.bc.gov.nrs.cmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * Artifact
 **/
//import org.springframework.data.gremlin.annotation.*;

//@Vertex
public class SelectorSpec{
  private static Gson gson;
//  @Id

//  @Property("module_name")
  private String name = null;

  private String version = null;

  public SelectorSpec version(String version) {
    this.version = version;
    return this;
  }


  /**
   * The name of the Artifact
   **/
  public SelectorSpec name(String name) {
    this.name = name;
    return this;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
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
    SelectorSpec selector = (SelectorSpec) o;
    return Objects.equals(name, selector.name) &&
        Objects.equals(version, selector.version) ;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, version);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SelectorSpec {\n");

    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

