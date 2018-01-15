package ca.bc.gov.nrs.cmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

//@Vertex
public class DeploymentSpec {
//  @Id

  @JsonProperty("key")
  private String key = null;

  public DeploymentSpec key(String key) {
    this.key = key;
    return this;
  }


  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }


  /**
   * The name of the deployment spec.
   **/
  @JsonProperty("name")
  private String name = null;
  public DeploymentSpec name(String name) {
    this.name = name;
    return this;
  }


  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeploymentSpec deploymentSpec = (DeploymentSpec) o;
    return Objects.equals(key, deploymentSpec.key) &&
        Objects.equals(name, deploymentSpec.name) ;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Component {\n");
    
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
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
}

