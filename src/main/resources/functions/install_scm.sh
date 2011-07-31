set -x
function install_scm() {
  yum install -y expect
  wget http://archive.cloudera.com/scm/installer/latest/scm-installer.bin
  chmod u+x scm-installer.bin
  
  # Need to use expect for the install since script expects user to hit enter
  # at the end
  cat >> install <<END
#!/usr/bin/expect -f
set timeout 300
spawn ./scm-installer.bin --ui=stdio --noprompt --noreadme --nooptions --i-agree-to-all-licenses
expect "*hit enter*"
send -- "\n"
expect EOF
END

  chmod +x install
  ./install
}
