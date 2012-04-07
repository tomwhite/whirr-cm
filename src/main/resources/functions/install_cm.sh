set -x
function install_cm() {
  yum install -y expect
  wget http://archive.cloudera.com/cm4/installer/latest/cloudera-manager-installer.bin
  chmod u+x cloudera-manager-installer.bin
  
  # Use expect for the install to make it appear interactive
  cat >> install <<END
#!/usr/bin/expect -f
set timeout 300
spawn ./cloudera-manager-installer.bin --ui=stdio --noprompt --noreadme --nooptions --i-agree-to-all-licenses
expect EOF
END

  chmod +x install
  ./install
}
