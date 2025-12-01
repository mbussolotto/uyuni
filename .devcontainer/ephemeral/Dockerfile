FROM registry.opensuse.org/opensuse/tumbleweed:latest

# Prevent zypper from asking for confirmations
ENV ZYPPER_CMD="zypper --non-interactive"

# 1. Update and Install Systemd & Basic Tools
# 'systemd-sysvinit' is crucial for /sbin/init to work as PID 1
RUN $ZYPPER_CMD refresh && \
    $ZYPPER_CMD install --no-recommends \
        systemd \
        systemd-sysvinit \
        openssh \
        sudo \
        curl \
        wget \
        vim \
        tar \
        gzip \
        iproute2 \
        hostname \
        shadow \
        glibc-locale && \
    $ZYPPER_CMD clean -a

# 2. Configure Systemd
# Mask services that fail in containers to keep logs clean
RUN systemctl mask systemd-remount-fs.service dev-hugepages.mount \
    sys-fs-fuse-connections.mount systemd-logind.service getty.target console-getty.service

# 3. Create 'ubuntu' user (to match your previous config)
# We give it passwordless sudo access
RUN useradd -m -s /bin/bash -G users uyuni && \
    echo "uyuni:uyuni" | chpasswd && \
    echo 'uyuni ALL=(ALL) NOPASSWD: ALL' > /etc/sudoers.d/uyuni && \
    chmod 440 /etc/sudoers.d/uyuni

# 4. Set Locale (Optional but recommended for Tumbleweed)
ENV LANG=en_US.UTF-8

# 5. Init
VOLUME [ "/sys/fs/cgroup" ]
CMD ["/usr/sbin/init"]
