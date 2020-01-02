sudo apt install python3
sudo apt install python3-pip
pip3 install colorama
pip3 install requests
clear
sudo mv wisdnote /usr/bin/
if grep -q "complete -W 'add edit list delete relogin help search' wisdnote" "/etc/bash.bashrc"; then
  echo 
else
    sudo echo "complete -W 'add edit list delete relogin help search' wisdnote">>/etc/bash.bashrc
fi
echo Succesfully Installed !
