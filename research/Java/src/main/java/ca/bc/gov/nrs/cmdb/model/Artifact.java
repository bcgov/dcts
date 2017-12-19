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
public class Artifact {
  private static Gson gson;

  private String key = null;
//  @Property("module_name")
  private String name = null;

  public Artifact key(String key) {
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
   * The name of the Artifact
   **/
  public Artifact name(String name) {
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
   * Short Name
   */
  private String shortName = null;
  @JsonProperty("shortName")
  public String getShortName() {
    return shortName;
  }
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  /**
   * System
   */
  private String system = null;
  @JsonProperty("system")
  public String getSystem() {
    return system;
  }
  public void setSystem(String system) {
    this.system = system;
  }

  /**
   * Description
   */
  private String description = null;
  @JsonProperty("description")
  public String getDescription() { return description;  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Url
   */
  private String url = null;
  @JsonProperty("url")
  public String getUrl() { return url;  }
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Vendor
   */
  private String vendor = null;
  @JsonProperty("vendor")
  public String getVendor() { return vendor;  }
  public void setVendor(String vendor) {
    this.vendor = vendor;
  }

  /**
   * VendorContact
   */
  private String vendorContact = null;
  @JsonProperty("vendorContact")
  public String getVendorContact() { return vendorContact;  }
  public void setVendorContact(String vendorContact) {
    this.vendorContact = vendorContact;
  }

  /**
   * Version
   */
  private String version = null;
  @JsonProperty("version")
  public String getVersion() { return version;  }
  public void setVersion(String version) {
    this.version = version;
  }


  private HashMap<String, RequirementSpec> provides = null;
  @JsonProperty("Provides")
  public HashMap<String, RequirementSpec>  getProvides() {
    return provides;
  }
  public void setProvides(HashMap<String, RequirementSpec>  provides) {
    this.provides = provides;
  }

  private HashMap<String, RequirementSpec> requires = null;
  @JsonProperty("Requires")
  public HashMap<String, RequirementSpec>  getRequires() {
    return requires;
  }
  public void setRequires (HashMap<String, RequirementSpec>  requires) {
    this.requires = requires;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Artifact artifact = (Artifact) o;
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

