# If you have an existing SADI service webapp #

  1. [Install the OpenShift Client Tools](https://openshift.redhat.com/community/developers/install-the-client-tools)
  1. [Sign up for OpenShift](https://openshift.redhat.com/app/account/new)
  1. [Create a new Tomcat application](https://openshift.redhat.com/app/console/application_types/jbossews-1.0)
  1. Clone (check out) the new OpenShift application's repository according to the OpenShift instructions
  1. Add your SADI service webapp source code to the OpenShift application's repository, replacing the current `src` directory and `pom.xml` file. **Note**: before you replace the `pom.xml` file, you can copy part of it for use in the next step.
  1. Add the `openshift` profile to your `pom.xml` file (the `profiles` section follows the `build` section). You can copy this code from the original OpenShift `pom.xml` or from the snippet below:
```
	<profiles>
	  <profile>
	    <id>openshift</id>
	    <build>
	      <finalName>tomcat</finalName>
	      <plugins>
	        <plugin>
	          <artifactId>maven-war-plugin</artifactId>
	          <version>2.1.1</version>
	          <configuration>
	            <outputDirectory>webapps</outputDirectory>
	            <warName>ROOT</warName>
	          </configuration>
	        </plugin>
	      </plugins>
	    </build>
	  </profile>
	</profiles>
```
  1. Commit your changes to the Openshift application's repository:
```
      git add .
      git commit
```
  1. Push the OpenShift application's repository to deploy the application:
```
      git push
```
  1. When you make changes to your SADI service code, just commit and push those changes to redeploy the updated application (i.e.: `git add`, `git commit`, `git push` as necessary)

# If you are starting from scratch #

  1. [Install the OpenShift Client Tools](https://openshift.redhat.com/community/developers/install-the-client-tools)
  1. [Sign up for OpenShift](https://openshift.redhat.com/app/account/new)
  1. [Create a new Tomcat application](https://openshift.redhat.com/app/console/application_types/jbossews-1.0)
  1. Clone (check out) the new OpenShift application's repository according to the OpenShift instructions
  1. Add the SADI service quick start upstream repository and update:
```
      git remote add -m master SADI git://github.com/sadiframework/openshift-tomcat
      git pull -s recursive -X theirs SADI master # I don't think this master is necessary
```
  1. From here, you can proceed as if you'd just unpacked the SADI service skeleton (i.e.: just after [The SADI service skeleton in Building Services in Java](http://code.google.com/p/sadi/wiki/BuildingServicesInJava#The_SADI_service_skeleton))

# If you have problems #

  * [Open a new issue](http://code.google.com/p/sadi/issues/list).
  * Add a comment on this wiki page.
  * Post on the [sadi-dev Google group](http://groups.google.com/group/sadi-dev).