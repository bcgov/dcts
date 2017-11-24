# Overview

# Definitions
* [Labels](https://kubernetes.io/docs/concepts/overview/working-with-objects/labels/) are key/value pairs that are attached to objects. Labels are intended to be used to specify identifying attributes of objects that are meaningful and relevant to users, but do not directly imply semantics to the core system. Labels can be used to organize and to select subsets of objects.
* [Annotations](https://kubernetes.io/docs/concepts/overview/working-with-objects/annotations/) are arbitrary non-identifying metadata attached to objects.
* Artifact
* Component is the manifestation of deployed artifacts. A component can only be manifested by one, and only one, artifact.
* Server
* Interface

# Workflow
1. POST _Artifact Specification_ to `/deployments/start` which returns the _Deployment Specification Plan_ with a `plan id`
  * process all StringTemplate
  * Check if a `System` node exists matching `system` name
  * if `Artifact` node matching the `name` does not exist, create a `Artifact` node for the `System`
  * if `ArtifactVersion` node of type `name` does not exist, create a `ArtifactVersion` node for the `System`
  * Create `DeploymentSpec` node for the `System`, `ArtifactVersion` and `environment`
  * Process `runtimeSpec.provides`
    * if `Interface` node matching `interface` does not exist, creates a `Interface` node for the `System`
    * Create of Update a `Component` node for the `DeploymentSpec` node withe `Interface` node as the `interface` attribute
  * Process `runtimeSpec.requires`
  * Process `deploymentSpec`
1. Parse _Deployment Specification Plan_, extract parameters/arguments and execute deployment.
1. POST the _Deployment Specification Plan_ to `/deployments/<plan id>/finish` indicating if it was successful or not.

# Interfaces

# Artifact Specification

## artifactSpec
| Field | Type | Default |Description |
| :--- | :--- | :--- | :--- |
| system | | | |
| name | | | Uquiquely identify this artifact in the whole CMDB. It is equivalent to Maven `${groupId}.${artifactId}`|
| _version_ | [stringTemplate](#string-template) | `"${input.version}"` | (Parameter, Output) |
| _environment_ | [stringTemplate](#string-template) | `"${input.environment}"` | (Parameter, Output) |
| provides | array of [Provide](#provide) | |
| requires | map of [Require](#require) | |

## DeploymentSpec
key/value map where the value is of type [Selector](#selector)

## Selector
| Field | Type | Default |Description |
| :--- | :--- | :--- | :--- |
| selector | array of [MatchExpression](#MatchExpression) | `[{"key":"environment", "operator":"In", "value":["${input.environemnt}"]}]` | |
| quantifier | String | `1` | `+` (zero or one), `*` (zero or more), `?` (one or more), `n` (exact `n` times) |
| attributes | array of string | `[]` | Define which attributes to expanded/provided. Empty array means `all`.|
| _matches_ | array | `[]` | (Output) Upon resolving the selector, this field will contain an array of components matching the selector. |

## MatchExpression
Array of expressions are ANDed.

| Field | Type | Default |Description |
| :--- | :--- | :--- | :--- |
| key | string | | |
| operator | enum (string) | `"In"` | `"In"`, `"NotIn"` |
| values | array of [stringTemplate](#string-template) | `[]` | |

## Provide
| Field | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| interface | string | | The uniquely identified interface it provides | 
| layer | enum (string) | | `"server"`, `"router"`, `"client"` |
| attributes | map of [stringTemplate](#string-template) | | |
| labels | map of [stringTemplate](#string-template) | `{"interface":"$interface", "version":"$input.version", "environment":"$input.environment", "layer":"$layer"}` |  |

## Require
| Field | Type | Description |
| :--- | :--- | :--- |
| interface | string | The uniquely identified name of the interface it requires. Equivalent to the following [MatchExpression](#matchexpression): `{"key":"interface", "operator":"In", "value":[interface]}` |
| selector | array of [MatchExpression](#MatchExpression) | 
| scope | enum (string) | `"runtime"` (default), `"deployment"`|
| quantifier | string | `"1"` | `+` (zero or one), `*` (zero or more), `?` (one or more), `n` (exact `n` times) |
| _matches_ | array | `[]` | (Output) Upon resolving/finding the `interface`, this field will contain an array of components matching the interface. |

# Artifact Deployment Plan
| Field | Description |
| :--- | :--- |
| id | |
| artifactSpec | Resolved [artifactSpec](#artifactspec) |

# String Template
String templates are powered by [FreeMarker](http://freemarker.org/) (???)

# Reserved Label Keys
* interface
* name
* environment
* version
* layer

# References:
* [OSGi Import-Package](https://osgi.org/download/r6/osgi.core-6.0.0.pdf#page=50)
* [OSGi Export-Package](https://osgi.org/download/r6/osgi.core-6.0.0.pdf#page=50)
* [OSGi Version Range](https://osgi.org/download/r6/osgi.core-6.0.0.pdf#page=36)
* [How does maven sort version numbers?](https://stackoverflow.com/questions/13004443/how-does-maven-sort-version-numbers)
* [City Name Picker Service](http://names.drycodes.com/10?nameOptions=cities&combine=1&case=upper&separator=_)
