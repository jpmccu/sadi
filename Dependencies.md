To alleviate network problems cause by an entire roomful of people downloading dozens of files at the same time, please use the instructions on this page to pre-install various SADI dependencies.

# Java dependencies #

If you have Maven 2 working from the command line:
  1. download http://sadiframework.org/downloads/sadi-service-skeleton-0.1.0.zip
  1. unzip that file and open a shell in the resulting `sadi-services` directory
  1. execute the following command:
```
$ mvn ca.wilkinsonlab.sadi:sadi-generator:generate-service -DserviceName=hello -DserviceClass=com.example.HelloWorldService -DinputClass=http://sadiframework.org/examples/hello.owl#NamedIndividual  -DoutputClass=http://sadiframework.org/examples/hello.owl#GreetedIndividual -DcontactEmail=nobody@mailinator.com
```
> > (you've just built a SADI service)
  1. execute the following command:
```
$ mvn org.mortbay.jetty:jetty-maven-plugin:run
```
> > (you're now running a SADI service and will therefore have downloaded all the dependencies)

If you have Maven 2 working in Eclipse:
  1. download http://sadiframework.org/downloads/sadi-service-skeleton-0.1.0.zip
  1. import the project in that file into your workspace (using **File &raquo; Existing Projects into Workspace**)
  1. execute the **generate sadi service** run configuration (using **Run &raquo; Run Configurations...**)
  1. execute the **run sadi services in Jetty** run configuration (using **Run &raquo; Run Configurations...**)

Otherwise:
  1. download http://sadiframework.org/downloads/sadi.libraries.2011-05-19.zip
  1. unzip that file and move the contents of the resulting `repository` directory; if you can't figure this out, it's enough to have downloaded the file — an instructor can help you tomorrow.

# Perl dependencies #

  1. execute the following command (this is in an interactive process and can take a long time):
```
$ perl -MCPAN -e "install SADI"
```

# Ontologies #

  1. download http://sadiframework.org/downloads/training1105-ontologies.zip
  1. that file should contain the following ontologies:
    1. http://ontology.dumontierlab.com/biomedical-measure-primitive
    1. http://sadi-ontology.semanticscience.org/pharmacogenomics-primitive-sadi.owl
    1. http://sadi-ontology.semanticscience.org/pharmacogenomics-complex-sadi.owl
    1. http://sadi-ontology.semanticscience.org/pharmacogenomics-depression-sadi.owl
  1. unzip that file and put the ontologies somewhere you can easily find them