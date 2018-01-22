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
public class IPAddress {


  @JsonProperty("address")
  private String address= null;
  public String getAddress() {
    return address;
  }
  public void setAddress(String address) {
    this.address = address;
  }

  @JsonProperty("netmask")
  private String netmask= null;
  public String getNetmask() {
    return netmask;
  }
  public void setNetmask(String netmask) {
    this.netmask = netmask;
  }

  @JsonProperty("network")
  private String network= null;
  public String getNetwork() {
    return network;
  }
  public void setNetwork(String network) {
    this.network = network;
  }



}

