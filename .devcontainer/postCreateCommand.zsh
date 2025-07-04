#!/usr/bin/env zsh
sudo ln -snf /usr/share/zoneinfo/UTC /etc/localtime
echo UTC | sudo tee /etc/timezone > /dev/null 

mkdir /home/dnadev/.sbt
sudo cp /mnt/host.sbt/.credentials /home/dnadev/.sbt/.credentials
sudo chown -R dnadev:dnadev /home/dnadev/.sbt

(sbt compile || true)
echo 'Devcontainer setup completed'"