#!/bin/bash
set -e

echo ">>> Setting up Uyuni Master environment..."

echo 'deb http://download.opensuse.org/repositories/systemsmanagement:/Uyuni:/Master:/ContainerUtils/Ubuntu_24.04/ /' | sudo tee /etc/apt/sources.list.d/uyuni-utils.list
curl -fsSL https://download.opensuse.org/repositories/systemsmanagement:/Uyuni:/Master:/ContainerUtils/Ubuntu_24.04/Release.key | gpg --dearmor | sudo tee /etc/apt/trusted.gpg.d/uyuni-utils.gpg > /dev/null

sudo apt-get update
sudo apt-get install -y mgradm podman


FQDN="uyuni.ephemeral.local"
echo "127.0.0.1 $FQDN" | sudo tee -a /etc/hosts

cat <<EOF > /tmp/mgradm.yaml
image: registry.opensuse.org/uyuni/server:latest
pgsql:
  image: registry.opensuse.org/uyuni/server-postgresql:latest
admin:
  login: "admin"
  password: "admin"
  email: "admin@example.com"
ssl:
  password: "admin"
  organization: "UyuniTest"
  country: "DE"
  state: "Bavaria"
  city: "Nuremberg"
EOF

echo ">>> Installing Uyuni..."
sudo mgradm install podman --config /tmp/mgradm.yaml

echo ">>> DONE! Access https://localhost (Accept Self-Signed Cert)"
