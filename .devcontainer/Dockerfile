FROM mcr.microsoft.com/devcontainers/base:ubuntu-22.04

USER root

# ----------------------------------------------------------------------
# Certificates
# ----------------------------------------------------------------------
COPY certificates  /usr/local/share/ca-certificates/
RUN echo "Import certificates in /usr/local/share/ca-certificates/" \
    && update-ca-certificates

# ----------------------------------------------------------------------
# Base packages
# ----------------------------------------------------------------------
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl gnupg lsb-release \
    wget ssh \
    ca-certificates \
    git git-lfs\
    zip unzip gzip \
    # sudo \
    bash-completion \
    apt-transport-https \
    && rm -rf /var/lib/apt/lists/*

# ----------------------------------------------------------------------
# TimeZone
# ----------------------------------------------------------------------
RUN ln -snf /usr/share/zoneinfo/UTC /etc/localtime && \
    echo UTC | tee /etc/timezone > /dev/null 


# ----------------------------------------------------------------------
# Prepare Installation Files
# ----------------------------------------------------------------------
WORKDIR /home/vscode/
RUN mkdir -p ./.sbt
RUN mkdir -p ./tmp
COPY scripts ./tmp
RUN chmod -R 775 ./tmp
RUN chown -R vscode .

# ----------------------------------------------------------------------
# Install as user
# ----------------------------------------------------------------------

USER vscode
RUN ./tmp/gen_proxy_env.sh /home/vscode/tmp/.proxy.env
RUN ./tmp/clis.sh
RUN ./tmp/coursier.sh
RUN ./tmp/java_cert.sh
RUN ./tmp/tofu.sh


WORKDIR /workspaces
