#!/usr/bin/env zsh
# Version 2
sudo ln -snf /usr/share/zoneinfo/UTC /etc/localtime
echo UTC | sudo tee /etc/timezone > /dev/null 

mkdir /home/dnadev/.sbt
sudo cp /mnt/host.home/.sbt/.credentials /home/dnadev/.sbt/.credentials
sudo cp /mnt/host.home/.gitconfig /home/dnadev/.gitconfig
sudo chown -R dnadev:dnadev /home/dnadev/.sbt
sudo chown dnadev:dnadev /home/dnadev/.gitconfig

(sbt compile || true)
echo 'Devcontainer setup completed'