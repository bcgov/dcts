package ca.bc.gov.nrs.cmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * Artifact
 **/
//import org.springframework.data.gremlin.annotation.*;

//@Vertex
public class Export {
  private static Gson gson;
//  @Id
  private String key = null;
//  @Property("module_name")
  private String name = null;
  private String version = null;

  public Export key(String key) {
    this.key = key;
    return this;
  }

  @JsonProperty("key")
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }

  /**
   * The name of the Export
   **/
  public Export name(String name) {
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
    Export artifact = (Export) o;
    return Objects.equals(key, artifact.key) &&
        Objects.equals(name, artifact.name) ;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Component {\n");
    
    sb.append("    id: ").append(toIndentedString(key)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");

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

