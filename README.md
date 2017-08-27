# DrivenByMoss
Bitwig Studio extensions to support several controllers

### Building and Installing the extension

Users should download and install the version from the
[main site](http://www.mossgrabers.de/Software/Bitwig/Bitwig.html).
These directions are for developers to test changes prior to release.

1. Install Maven and dependences, either [from here](https://maven.apache.org/install.html)
or if on Linux, using the distro package manager, e.g. `yum install maven` or
`apt-get install maven`.
1. Run `mvn package` in this repo's root.
1. cd to `target` and rename `DrivenByMoss-LOCAL-<number>.jar` to
`DrivenByMoss.bwextension`.
1. Follow [installation instructions](https://github.com/git-moss/DrivenByMoss/wiki/Installation)
for further steps.
