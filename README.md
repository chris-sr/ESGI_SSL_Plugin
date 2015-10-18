# SSL Wireshark Plugin

This Wireshark plugin allows you to launch a man in the middle attack with ARP poisoning method and trick the user with sslstrip to steal his login/password on websites without the SSL encryption.

The plugin launch a java application (Java 8) with a graphical user interface (GUI) made in JavaFX.
This application is aimed to beginners with very straight forward action to do the hacking. When the hacking is successful, you can read the login and password from the victim inside a table in real time.

## Installing

Running `install.sh` will install the dependencies, create the directories in your `$HOME/.wireshark`, then copy the lua script and the java application with his resources inside each proper directory.

## Dependencies

This plugin and its associated scripts and programs all depend on [nmap](http://nmap.org/), [arpspoof](http://www.monkey.org/~dugsong/dsniff/), [sslstrip](http://www.thoughtcrime.org/software/sslstrip/) and of course [wireshark](https://www.wireshark.org/).

Normally, all dependencies have been installed automatically during the installation process.

## Complimentary Documentation

Compatible and tested on Debian 7

