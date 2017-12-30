package ca.bc.gov.nrs.cmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Project Component
 **/

import java.util.Objects;
//import org.springframework.data.gremlin.annotation.*;

//@Vertex
public class Component {
//  @Id
  private Integer id = null;
//  @Property("module_name")
  private String name = null;
//  @Link("part_of_application")
  private Project module = null;

  /**
   * A system-generated unique identifier for a module
   **/
  public Component id(Integer id) {
    this.id = id;
    return this;
  }

  @JsonProperty("id")
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * The name of the application.
   **/
  public Component name(String name) {
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
   * A foreign key reference to the Project.
   **/
  public Component module(Project module) {
    this.module = module;
    return this;
  }

  @JsonProperty("module")
  public Project getModule() {
    return module;
  }
  public void setModule(Project module) {
    this.module = module;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Component component = (Component) o;
    return Objects.equals(id, component.id) &&
        Objects.equals(name, component.name) &&
        Objects.equals(component, component.module);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, module);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Component {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    module: ").append(toIndentedString(module)).append("\n");
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

