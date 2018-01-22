package ca.bc.gov.nrs.cmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Objects;

/**
 * Artifact
 **/
//import org.springframework.data.gremlin.annotation.*;

//@Vertex
public class InterfaceSpec {


  @JsonProperty("macaddress")
  private String macaddress;
  public String getMacaddress() {
    return macaddress;
  }
  public void setMacaddress(String macaddress) {
    this.macaddress = macaddress;
  }

  public IPAddress ipv4 = null;
  @JsonProperty("ipv4")
  public IPAddress getIpv4() {
    return ipv4;
  }
  public void setIpv4(IPAddress ipv4) {
    this.ipv4 = ipv4;
  }




}

