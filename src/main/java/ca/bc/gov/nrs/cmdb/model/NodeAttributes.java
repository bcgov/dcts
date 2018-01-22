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
public class NodeAttributes {

  @JsonProperty("fqdn")
  private String fqdn = null;
  public String getFqdn() {
    return fqdn;
  }
  public void setFqdn(String fqdn) {
    this.fqdn = fqdn;
  }

  @JsonProperty("all_ipv4_addresses")
  private String[] all_ipv4_addresses = null;
  public String[] getAll_Ipv4_Addresses() {
    return all_ipv4_addresses;
  }
  public void setAll_Ipv4_Addresses(String[] all_ipv4_addresses) {
    this.all_ipv4_addresses = all_ipv4_addresses;
  }

  @JsonProperty("all_ipv6_addresses")
  private String[] all_ipv6_addresses = null;
  public String[] getAll_Ipv6_Addresses() {
    return all_ipv6_addresses;
  }
  public void setAll_Ipv6_Addresses(String[] all_ipv6_addresses) {
    this.all_ipv6_addresses = all_ipv6_addresses;
  }


  @JsonProperty("default_ipv4")
  private String default_ipv4 = null;
  public String getDefault_Ipv4() {
    return default_ipv4;
  }
  public void setDefault_Ipv4(String default_ipv4) {
    this.default_ipv4 = default_ipv4;
  }

  @JsonProperty("default_ipv6")
  private String default_ipv6 = null;
  public String getDefault_Ipv6() {
    return default_ipv6;
  }
  public void setDefault_Ipv6(String default_ipv6) {
    this.default_ipv6 = default_ipv6;
  }

  @JsonProperty("devices")
  private HashMap<String, String> devices = null;
  public HashMap<String, String> getDevices() {
    return devices;
  }
  public void setDevices(HashMap<String, String> devices) {
    this.devices = devices;
  }

  @JsonProperty("os_family")
  private String os_family = null;
  public String getOs_Family() {
    return os_family;
  }
  public void setOs_Family(String os_family) {
    this.os_family = os_family;
  }

  @JsonProperty("os_name")
  private String os_name = null;
  public String getOs_Name() {
    return os_name;
  }
  public void setOs_Name(String os_name) {
    this.os_name = os_name;
  }

  @JsonProperty("os_version")
  private String os_version = null;
  public String getOs_version() {
    return os_version;
  }
  public void setOs_Version(String os_version) {
    this.os_version = os_version;
  }

  @JsonProperty("interfaces")
  private HashMap<String, InterfaceSpec> interfaces = null;
  public HashMap<String, InterfaceSpec> getInterfaces() {
    return interfaces;
  }
  public void setInterfaces(HashMap<String, InterfaceSpec> interfaces) {
    this.interfaces = interfaces;
  }

  @JsonProperty("environment")
  private String environment = null;
  public String getEnvironment() {
    return environment;
  }
  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  @JsonProperty("dc-provider")
  private String dc_provider = null;
  public String getDc_Provider() {
    return dc_provider;
  }
  public void setDc_Provider(String dc_provider) {
    this.dc_provider = dc_provider;
  }

  @JsonProperty("dc-region")
  private String dc_region = null;
  public String getDc_Region() {
    return dc_region;
  }
  public void setDc_Region(String dc_region) {
    this.dc_region = dc_region;
  }

  @JsonProperty("reserved-for")
  private String reserved_for = null;
  public String getReserved_For() {
    return reserved_for;
  }
  public void setReserved_For(String reserved_for) {
    this.reserved_for = reserved_for;
  }

}

