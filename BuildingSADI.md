# Requirements #

SVN is required to check code out of the Google Code source repository.

Maven 2 (mvn) is required to build SADI.

Some features of the SADI code base are optimized for use with the Eclipse Java IDE.  These features have been tested with Eclipse 3.5 and 3.6 (with the Maven Integration for Eclipse plugin), but the code base is being currently being updated to align with Eclipse 3.7 and its built-in Maven support.

# Install required software #

  1. Download Eclipse 3.7.1 (Indigo SR1) from http://www.eclipse.org/downloads/.
  1. Open Eclipse and install the Subversive SVN Team Provider
    1. **Help → Install new software...**
    1. Select the Indigo update site
    1. Select the Subversive SVN Team Provider from the list of available software
    1. Click Finish and restart Eclipse when asked

# Configure SVN #
  1. Add the SADI Google Code repository
    1. **Window → Open perspective → Other...** and choose SVN Repository exploring
    1. **File → New → Repository Location**
    1. Type `https://sadi.googlecode.com/svn/` in the **URL** field
    1. Type your Google Code User and Password (note that your Google Code password will not be the same as your regular Google account password; you can find it at [Google Project Hosting settings](https://code.google.com/hosting/settings)
    1. Click Finish

# Check out SADI #

Check out the SADI projects you want to build from the trunk/ directory of the SADI Google Code repository.  Every SADI project should build independently, so you only need to check out the projects you directly intend to edit (code from projects you haven't checked out will still be available in Eclipse through the associated Maven source artifacts).

# Troubleshooting #

## Build errors ##
If a project has build errors, try the following:
  1. Make sure it has been successfully identified as a Maven project (indicated by a tiny "M" on the project folder icon in the **Package Explorer** View).  If it has not, right-click the project folder and choose **Configure → Convert to Maven project** (Eclipse 3.7) or **Maven → Enable Dependency Management** (Eclipse 3.6 or earlier).
  1. Make sure the required artifacts have finished downloading.  Eclipse can take a long time to update the Maven central indexes.  If there is nothing happening in the **Progress** View and there are still missing artifacts, right-click the project folder in the **Package Explorer** View and choose **Maven → Update Dependencies**.
  1. If the project you're building depends on a SNAPSHOT version of any SADI artifact, you'll need to add the Sonatype snapshot repository to your maven settings.xml. Here's an example:
```
<settings>
  <profiles>
    <profile>
      <id>sonatype-snapshots</id>
      <repositories>
        <repository>
          <id>sonatype-nexus-snapshots</id>
          <name>Sonatype Nexus Snapshots</name>
          <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>sonatype-nexus-snapshots</id>
          <name>Sonatype Nexus Snapshots</name>
          <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>sonatype-snapshots</activeProfile>
  </activeProfiles>
</settings>
```

## Missing run configurations ##
The Run configuration XML syntax changed between Eclipse 3.6 and Eclipse 3.7, so configurations created with one will not be usable by the other.  Fortunately, the syntax is easily corrected to work with the version of Eclipse you happen to be running.
Run configurations for SADI are found in the `doc/eclipse` directory of each project.  If one of these files is not working in your version of Eclipse, open it and change the `<launchConfiguration>` element like so:
  * for Eclipse 3.7, it should be: `<launchConfiguration type="org.eclipse.m2e.Maven2LaunchConfigurationType">`
  * for Eclipse 3.6, it should be: `<launchConfiguration type="org.maven.ide.eclipse.Maven2LaunchConfigurationType">`