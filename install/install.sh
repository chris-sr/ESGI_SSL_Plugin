#!/bin/sh
sudo apt-get install gksu
gksu "apt-get clean"

echo "Installing wireshark..."
gksu "apt-get install wireshark"

echo "Installing dsniff..."
gksu "apt-get install dsniff"

#echo "Installing python..."
#gksu "apt-get install python"
#gksu "apt-get install python-twisted-web"
#gksu "python ./sslstrip-0.9/setup.py install"

echo "Installing sslstrip..."
gksu "apt-get install sslstrip"

gksu "apt-get clean"

echo "Create directories for the plugin..."
mkdir ~/.wireshark/
mkdir ~/.wireshark/plugins
mkdir ~/.wireshark/plugins/SSL_Wireshark_Plugin
mkdir ~/.wireshark/plugins/SSL_Wireshark_Plugin/Logs
mkdir ~/.wireshark/plugins/SSL_Wireshark_Plugin/Scripts
mkdir ~/.wireshark/plugins/SSL_Wireshark_Plugin/Images

echo "Copy plugin lua..."
cp -f ./src/lua/esgi_tool.lua ~/.wireshark/plugins/SSL_Wireshark_Plugin/

echo "Copy java application..."
cp -f ./src/java/SSLManager.jar ~/.wireshark/plugins/SSL_Wireshark_Plugin/

echo "Copy scripts..."
cp -f ./src/scripts/ipforwarding_enable.sh ~/.wireshark/plugins/SSL_Wireshark_Plugin/Scripts/
cp -f ./src/scripts/ipforwarding_disable.sh ~/.wireshark/plugins/SSL_Wireshark_Plugin/Scripts/
cp -f ./src/scripts/arpspoof.sh ~/.wireshark/plugins/SSL_Wireshark_Plugin/Scripts/
cp -f ./src/scripts/sslstrip.sh ~/.wireshark/plugins/SSL_Wireshark_Plugin/Scripts/

echo "Copy images..."
cp -f ./images/esgi.ico ~/.wireshark/plugins/SSL_Wireshark_Plugin/Images/
cp -f ./images/lock.ico ~/.wireshark/plugins/SSL_Wireshark_Plugin/Images/

echo "Done."

echo "Launching Wireshark !"
wireshark

