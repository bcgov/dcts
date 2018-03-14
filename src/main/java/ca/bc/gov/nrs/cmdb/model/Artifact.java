package ca.bc.gov.nrs.cmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.tuple.Pair;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Artifact
 **/
//import org.springframework.data.gremlin.annotation.*;

//@Vertex
public class Artifact {
  private static Gson gson;


//  @Property("module_name")


  public Artifact key(String key) {
    this.key = key;
    return this;
  }

  @SerializedName("key")
  private String key = null;
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

  @SerializedName("name")
  private String name = null;
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Short Name
   */
  @SerializedName("shortName")
  private String shortName = null;
  public String getShortName() {
    return shortName;
  }
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  /**
   * System
   */
  @SerializedName("system")
  private String system = null;
  public String getSystem() {
    return system;
  }
  public void setSystem(String system) {
    this.system = system;
  }

  /**
   * Description
   */
  @SerializedName("description")
  private String description = null;

  public String getDescription() { return description;  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Url
   */
  @SerializedName("url")
  private String url = null;

  public String getUrl() { return url;  }
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Vendor
   */
  @SerializedName("vendor")
  private String vendor = null;

  public String getVendor() { return vendor;  }
  public void setVendor(String vendor) {
    this.vendor = vendor;
  }

  /**
   * VendorContact
   */
  @SerializedName("vendorContact")
  private String vendorContact = null;
  public String getVendorContact() { return vendorContact;  }
  public void setVendorContact(String vendorContact) {
    this.vendorContact = vendorContact;
  }

  /**
   * Version
   */
  @SerializedName("version")
  private String version = null;
  public String getVersion() { return version;  }
  public void setVersion(String version) {
    this.version = version;
  }

  @SerializedName("provides")
  private JsonObject provides;
  public JsonObject getProvides() {
    return provides;
  }
  public void setProvides(JsonObject provides) {
    this.provides = provides;
  }

  @SerializedName("requires")
  private JsonObject requires;
  public JsonObject  getRequires() {
    return requires;
  }
  public void setRequires (JsonObject  requires) {
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

