package ca.bc.gov.nrs.cmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * Project Component
 **/
//import org.springframework.data.gremlin.annotation.*;

//@Vertex
public class DeploymentSpecificationPlan {



  /**
   * A system-generated unique identifier for a module
   **/
  @SerializedName("key")
  private String key = null;

  public DeploymentSpecificationPlan key(String key) {
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

  /* Deployment status
 * null - no attempt to deploy yet
 * true - deployed successfully
 * false - not deployed
 */
  @SerializedName("deployed")
  private Boolean deployed = null;

  @JsonProperty("deployed")
  public Boolean getDeployed() {
    return deployed;
  }
  public void setDeployed(Boolean deployed) {
    this.deployed = deployed;
  }

  // The Artifact specified as a parameter when the DeploymentSpecificationPlan was created.

  @SerializedName("artifacts")
  private Artifact[] artifacts;
  @JsonProperty("artifacts")
  public Artifact[] getArtifacts() {return artifacts;}
  public void setArtifacts(Artifact[] artifacts) {this.artifacts = artifacts;}

  /**
   * The name of the application.
   **/
  @SerializedName("Name")
  private String name = null;
  public DeploymentSpecificationPlan name(String name) {
    this.name = name;
    return this;
  }

  @JsonProperty("Name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  /**
   * System
   */
  @SerializedName("System")
  private String system = null;

  @JsonProperty("System")
  public String getSystem() {
    return system;
  }
  public void setSystem(String system) {
    this.system = system;
  }

  /**
   * SymbolicName
   */
  @SerializedName("SymbolicName")
  private String symbolicName = null;
  @JsonProperty("SymbolicName")
  public String getSymbolicName() {
    return symbolicName;
  }
  public void setSymbolicName(String symbolicName) {
    this.symbolicName = symbolicName;
  }


  /**
   * Description
   */
  @SerializedName("Description")
  private String description = null;
  @JsonProperty("Description")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Vendor
   */
  @SerializedName("Vendor")
  private String vendor = null;
  @JsonProperty("Vendor")
  public String getVendor() {
    return vendor;
  }
  public void setVendor(String vendor) {
    this.vendor = vendor;
  }

  /**
   * Vendor-Contact
   */
  @SerializedName("Vendor-Contact")
  private String vendorContact = null;
  @JsonProperty("Vendor-Contact")
  public String getVendorContact() {
    return vendorContact;
  }
  public void setVendorContact(String vendorContact) {
    this.vendorContact = vendorContact;
  }

  /**
   * Version
   */

  @SerializedName("version")
  private String version = null;
  @JsonProperty("version")
  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
  }

  @SerializedName("Export")
  private Export[] export = null;
  @JsonProperty("Export")
  public Export[] getExport() {
    return export;
  }
  public void setExport(Export[] export) {
    this.export = export;
  }

  @SerializedName("Import")
  private Import[] imports = null;
  @JsonProperty("Import")
  public Import[] getImport() {
    return imports;
  }
  public void setImport(Import[] imports) {
    this.imports = imports;
  }


  @SerializedName("componentEnvironment")
  private String componentEnvironment = null;
  @JsonProperty("componentEnvironment")
  public String getComponentEnvironment() {
    return componentEnvironment;
  }
  public void setComponentEnvironment(String componentEnvironment) {
    this.componentEnvironment = componentEnvironment;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeploymentSpecificationPlan deployment = (DeploymentSpecificationPlan) o;
    return Objects.equals(key, deployment.key) &&
        Objects.equals(name, deployment.name) ;
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
}

