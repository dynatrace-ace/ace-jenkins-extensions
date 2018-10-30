# Dynatrace Innovation Lab Jenkins Global Script Library

This is a library of scripts that can be used to help build Jenins Pipeline jobs.
* More information on the Global Script Library can be found [here](https://github.com/jenkinsci/workflow-plugin/blob/master/cps-global-lib/README.md)
* More information on the Pipeline plugin can be found [here](https://github.com/jenkinsci/workflow-plugin/blob/master/README.md#introduction)
* Additional information including the very useful Refcard and tutorials [here](https://github.com/jenkinsci/workflow-plugin/blob/master/README.md#getting-started)
* Information on using docker commands inside Jenkins can be found at [cloudbees documentation](https://documentation.cloudbees.com/docs/cje-user-guide/docker-workflow.html) 

### Directory structure

The directory structure of the shared library repository is as follows:

    (root)
     +- src                     # groovy source files
     |   +- org
     |       +- foo
     |           +- Bar.groovy  # for org.foo.Bar class
     +- vars
         +- foo.groovy          # for global 'foo' variable/function
         +- foo.txt             # help for 'foo' variable/function

The `src` directory should look like standard Java source directory structure.
This directory is added to the classpath when executing Pipelines.

The `vars` directory hosts scripts that define global variables accessible from
Pipeline scripts.
