package ca.bc.gov.nrs.cmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * UploadSpec
 **/

public class UploadNodeSpec {
  private static Gson gson;

  private String kind = null;
  @JsonProperty("kind")
  public String getKind() { return kind; }
  public void setKind(String typeString ) { this.kind = typeString; }


  private Node value = null;

  @JsonProperty("value")
  public Node getValue() { return value; }
  public void setValue(Node value ) { this.value = value; }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UploadNodeSpec uploadSpec = (UploadNodeSpec) o;
    return Objects.equals(kind, uploadSpec.kind) && Objects.equals(value, uploadSpec.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kind, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UploadSpec {\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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