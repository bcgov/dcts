package ca.bc.gov.nrs.cmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * Error
 **/

public class ErrorSpec {
  private static Gson gson;

//  @Property("module_name")

  @JsonProperty("quantifier")
  private String code = null;


  public String getCode() { return code; }
  public void setCode(String code ) { this.code = code; }

  @JsonProperty("message")
  private String message = null;

  public String getMessage() { return message; }
  public void setMessage(String message ) { this.message = message; }

  @JsonProperty("target")
  private String target = null;

  public String getTarget() { return target; }
  public void setTarget(String target ) { this.target = target; }

  @JsonProperty("details")
  private ErrorSpec[] details = null;


  public ErrorSpec[] getDetails() { return details; }
  public void setDetails(ErrorSpec[] details ) { this.details = details; }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorSpec error = (ErrorSpec) o;
    return Objects.equals(code, error.code ) &&
        Objects.equals(message, error.message ) ;
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, target);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Error {\n");

    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    target: ").append(toIndentedString(target)).append("\n");

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