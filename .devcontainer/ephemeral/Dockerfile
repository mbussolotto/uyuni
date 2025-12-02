FROM registry.opensuse.org/opensuse/tumbleweed:latest

ENV LANG=en_US.UTF-8
ENV FQDN="uyuni.ephemeral.local"
ENV ZYPPER_CMD="zypper --non-interactive"

RUN $ZYPPER_CMD addrepo --refresh --no-gpgcheck "https://download.opensuse.org/repositories/systemsmanagement:/Uyuni:/Master:/ContainerUtils/openSUSE_Tumbleweed/" uyuni-utils

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
        git-core \
        glibc-locale \
        mgradm \
        podman && \
    $ZYPPER_CMD clean -a

RUN systemctl mask systemd-remount-fs.service dev-hugepages.mount \
    sys-fs-fuse-connections.mount systemd-logind.service getty.target console-getty.service

RUN useradd -m -s /bin/bash -G users uyuni && \
    echo "uyuni:uyuni" | chpasswd && \
    echo 'uyuni ALL=(ALL) NOPASSWD: ALL' > /etc/sudoers.d/uyuni && \
    chmod 440 /etc/sudoers.d/uyuni

VOLUME [ "/sys/fs/cgroup" ]
CMD ["/usr/sbin/init"]
