# Questionnaire-WebService
Background 
--------
This web service has been developed as a part of the CloudSocket project (https://www.cloudsocket.eu) which receives research funding from the European Union's Horizon 2020 Framework Programme.

Purpose & General Operation
--------
This web service is the backend for the [Semantic-Annotation-Questionnaire-WebApp] (https://github.com/BPaaSModelling/Semantic-Annotation-Questionnaire-WebApp).
It contains several RDF files that structure the functional and non-functional requirements of cloud services.

The [Semantic-Annotation-Questionnaire-WebApp] (https://github.com/BPaaSModelling/Semantic-Annotation-Questionnaire-WebApp) calls the web service for

1. generating and selecting questions
2. discover services
3. select answers based on querying of concepts in the RDF model

The web service selects questions based on the domain dependency, attribute dependency and entropy which is based on the cloud services annotations in the ontology.

Used Namespaces
--------
1. BPAAS: Business Process as a Service Ontology [ [Repository Website](https://github.com/BPaaSModelling/BPaaS-Ontology) | [Turtle File](https://raw.githubusercontent.com/BPaaSModelling/BPaaS-Ontology/master/bpaas.ttl) ]
2. APQC: American Productivity and Quality Center Ontology [ [Repository Website](https://github.com/BPaaSModelling/APQC-Ontology) | [Turtle File](https://raw.githubusercontent.com/BPaaSModelling/APQC-Ontology/master/apqc.ttl) ]
3. FBPDO: Functional Business Process Description Ontology [ [Repository Website](https://github.com/BPaaSModelling/Functional-Business-Process-Description-Ontology) | [Turtle File](https://raw.githubusercontent.com/BPaaSModelling/Functional-Business-Process-Description-Ontology/master/fbpdo.ttl) ]
4. ARCHIMEO Ontology (meta ontology) [ [Repository Website](https://github.com/ikm-group/ArchiMEO) | [Turtle File](https://raw.githubusercontent.com/ikm-group/ArchiMEO/master/ARCHIMEO/ArchiMEO.ttl) ]
  1. TOP Ontology [ [Repository Website](https://github.com/ikm-group/ArchiMEO) | [Turtle File](https://raw.githubusercontent.com/ikm-group/ArchiMEO/master/ARCHIMEO/TOP/TOP.ttl) ]
  2. EO: Enterprise Object Ontology [ [Repository Website](https://github.com/ikm-group/ArchiMEO) | [Turtle File](https://raw.githubusercontent.com/ikm-group/ArchiMEO/master/ARCHIMEO/EO/EO.ttl) ]


Prerequisites
--------
* Java 1.8
* Internet connection
* tested on MAC OSX 10.11.15 and Windows 10 Education, Version 10.0.10240

Installation & Testing
--------
The installation and testing procedure has been described in the release description, that can be found here: https://github.com/BPaaSModelling/Questionnaire-WebService/releases/tag/0.0.2
