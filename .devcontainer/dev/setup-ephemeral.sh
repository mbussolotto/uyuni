#!/usr/bin/env bash
#
# Copyright (c) 2025 SUSE LLC
#
# This software is licensed to you under the GNU General Public License,
# version 2 (GPLv2). There is NO WARRANTY for this software, express or
# implied, including the implied warranties of MERCHANTABILITY or FITNESS
# FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
# along with this software; if not, see
# http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
set -e

echo ">>> Waiting for Systemd..."
until systemctl is-system-running --wait >/dev/null 2>&1 || [ $? -eq 1 ]; do
  sleep 1
done

FQDN="uyuni.ephemeral.local"
echo "127.0.0.1 $FQDN" | sudo tee -a /etc/hosts
# Try to set hostname, but don't fail script if container permissions block it
sudo hostnamectl set-hostname $FQDN || true

cat <<EOF > /tmp/mgradm.yaml
# Note: These are the stable rolling images. 
# If you want the strict 'Master' branch images, change to:
# image: registry.opensuse.org/systemsmanagement/uyuni/master/containers/uyuni/server
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
sudo mgradm install podman \
    --config /tmp/mgradm.yaml

echo ">>> DONE! Access https://localhost (Accept Self-Signed Cert)"
