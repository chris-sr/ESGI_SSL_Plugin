#!/bin/sh
cat /proc/sys/net/ipv4/ip_forward
gksu "echo 0 > /proc/sys/net/ipv4/ip_forward"
cat /proc/sys/net/ipv4/ip_forward

