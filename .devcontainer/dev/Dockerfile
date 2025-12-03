# SPDX-FileCopyrightText: 2025 SUSE LLC
#
# SPDX-License-Identifier: Apache-2.0
FROM registry.opensuse.org/opensuse/tumbleweed:latest

ENV ZYPPER_CMD="zypper --non-interactive"

# Installing all packages
RUN $ZYPPER_CMD addrepo --refresh --no-gpgcheck "https://download.opensuse.org/repositories/systemsmanagement:/Uyuni:/Utils/openSUSE_Tumbleweed systemsmanagement:uyuni:utils" && \
    $ZYPPER_CMD addrepo --refresh --no-gpgcheck "https://download.opensuse.org/repositories/systemsmanagement:/Uyuni:/Master:/ContainerUtils/openSUSE_Tumbleweed/" uyuni-utils && \
    $ZYPPER_CMD refresh && \
RUN $ZYPPER_CMD install --no-recommends \
    # Core System & Network
    systemd systemd-sysvinit shadow \
    glibc-locale iproute2 hostname \
    iputils sudo curl wget openssh \
    # Development Tools & Editors
    vim neovim tar gzip awk git \
    make man rsync cpio expect \
    bash-completion intltool \
    # Java Stack
    java-17-openjdk-devel \
    java-25-openjdk-devel \
    ant ant-junit ant-junit5 \
    apache-ivy maven servletapi5 \
    obs-to-maven \
    # Web & Python Stack
    nodejs22 npm22 python3 \
    python3-pip python3-PyYAML \
    python3-setuptools \
    # Uyuni Tools
    mgradm podman \
    && $ZYPPER_CMD clean -a

# Setting Java 17 as system default
RUN /usr/sbin/alternatives --set javac /usr/lib64/jvm/java-17-openjdk/bin/javac && \
        /usr/sbin/alternatives --set java /usr/lib64/jvm/jre-17-openjdk/bin/java && \
        /usr/sbin/alternatives --set jre_openjdk /usr/lib64/jvm/jre-17-openjdk && \
        /usr/sbin/alternatives --set java_sdk_openjdk /usr/lib64/jvm/java-17-openjdk

# Creating regular user
RUN useradd --create-home --shell /bin/bash uyuni
RUN echo "uyuni ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

USER root
COPY --chown=uyuni --chmod=755 initialize.sh /home/uyuni/initialize.sh

USER uyuni

COPY initialize.sh /home/uyuni/initialize.sh
