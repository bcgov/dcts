# Overview

# Definitions
* [Labels](https://kubernetes.io/docs/concepts/overview/working-with-objects/labels/) are key/value pairs that are attached to objects. Labels are intended to be used to specify identifying attributes of objects that are meaningful and relevant to users, but do not directly imply semantics to the core system. Labels can be used to organize and to select subsets of objects.
* [Annotations](https://kubernetes.io/docs/concepts/overview/working-with-objects/annotations/) are arbitrary non-identifying metadata attached to objects.

# Workflow
1. POST _Artifact Specification_ to `/deployments/start` which returns the _Deployment Specification Plan_ with a `plan id`
1. Parse _Deployment Specification Plan_, extract parameters/arguments and execute deployment.
1. POST the _Deployment Specification Plan_ to `/deployments/<plan id>/finish` indicating if it was successful or not.


# Artifact Specification

## artifactSpec
| Field | Description |
| :--- | :--- |
| system | |
| name | |
| version | |
| deploymentSpec | see [deploymentSpec](#deploymentSpec)|
| runtimeSpec | see [runtimeSpec](#runtimeSpec)|

## deploymentSpec
| Field | Description |
| :--- | :--- |
| provides | |
| requires | |

## runtimeSpec
| Field | Description |
| :--- | :--- |
| provides | |
| requires | |

# Deployment Specification


# References:
* [OSGi Import-Package](https://osgi.org/download/r6/osgi.core-6.0.0.pdf#page=50)
* [OSGi Export-Package](https://osgi.org/download/r6/osgi.core-6.0.0.pdf#page=50)
* [OSGi Version Range](https://osgi.org/download/r6/osgi.core-6.0.0.pdf#page=36)
* [How does maven sort version numbers?](https://stackoverflow.com/questions/13004443/how-does-maven-sort-version-numbers)
