# Introduction #

SADI for GMOD is a collection of ready-made SADI services for accessing sequence feature data in RDF form. The services were developed as an add-on for the GMOD (Generic Model Organism Database) project, which is a popular toolkit for building model organism databases and their associated websites (e.g. FlyBase).

For more background info, see:

  * http://sadiframework.org
  * http://gmod.org

# The Services #

SADI for GMOD provides five SADI services for accessing sequence feature data:

| **Service Name** | **Input** | **Relationship** | **Output** |
|:-----------------|:----------|:-----------------|:-----------|
| get\_feature\_info | database identifier | is about         | feature description |
| get\_features\_overlapping\_region | genomic coordinates | overlaps         | collection of feature descriptions |
| get\_sequence\_for\_region | genomic coordinates | is represented by | DNA, RNA, or amino acid sequence |
| get\_child\_features | feature description | has part / derives into | collection of feature descriptions |
| get\_parent\_feature | feature description | is part of / derives from |  collection of feature descriptions |


# Structure of Service Input/Output RDF #

The main entities of the service input/output data are features and genomic coordinates. For a detailed discussion of how these entities have been modeled, see [Modeling Sequence Features in RDF](ModelingSequenceFeaturesInRDF.md).

# Setting up the Services #

The services are implemented as CGI Perl scripts.

These instructions assume working apache and mysql installations. To set up the services:

  1. **Load your GFF files into a** `Bio::DB::Sequence::Store` **database (mysql)**.  There are instructions for doing that on the GBrowse wiki: http://gmod.org/wiki/GBrowse_Backends#Using_Bio::DB::SeqFeature::Store_with_the_MySQL_Backend.
  1. **Install the SADI for GMOD dependencies with CPAN or your OS package manager**:
    * Bio::DB::SeqFeature::Store
    * Template
    * RDF::Trine
    * RDF::Query
    * OBO::Core::Ontology
    * URI
    * constant::boolean
    * Config::IniFiles
  1. **Download the SADI for GMOD tarball and unpack into** `cgi-bin`: http://sadi.googlecode.com/files/sadi.gmod-0.1.2.tar.gz. A subdirectory will be created called `sadi.gmod`.
  1. **Set `Bio::DB::SeqFeature::Store` connection parameters in** `cgi-bin/sadi.gmod/sadi.gmod.conf`. sadi.gmod.conf uses the GBrowse configuration format for configuring data sources.  Synopsis:
```
[GENERAL]
db_adaptor = Bio::DB::SeqFeature::Store
db_args = -adaptor DBI::mysql
          -dsn dbi:mysql:database=flybase
base_url = http://flybase.org/cgi-bin/sadi.gmod/
```
  1. **Configure database crossreference (dbxref) mappings in** `cgi-bin/sadi.gmod/dbxref.conf`. The mappings indicate how dbxref prefixes in the local database should be translated to standard namespaces in the LSRN (Life Science Resource Name) registry. Synopsis:
```
[DBXREF_TO_LSRN]
SwissProt = UniProt
UniProtKB = UniProt
SwissProt/TrEMBL = UniProt
...
```
  1. **Register the services in the public SADI registry**. (TODO: A script will be provided for this purpose.)

# SVN access #

The source for SADI for GMOD can be checked out (read-only) using:

```
svn co http://sadi.googlecode.com/svn/trunk/Perl/sadi.gmod
```