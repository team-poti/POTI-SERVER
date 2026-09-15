resource "aws_instance" "prod" {
  ami           = "ami-0e4ab31f1847c850c"
  instance_type = "t3.micro"
  key_name      = "poti-key"
  subnet_id     = aws_subnet.ec2.id

  vpc_security_group_ids = [
    aws_security_group.prod_ec2.id,
    aws_security_group.ec2_to_rds_6.id,
    aws_security_group.ec2_to_rds_7.id,
  ]

  user_data = <<-EOF
    #!/bin/bash
    set -e

    # Docker 설치
    apt-get update -y
    apt-get install -y ca-certificates curl gnupg
    install -m 0755 -d /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    chmod a+r /etc/apt/keyrings/docker.gpg
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
    apt-get update -y
    apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    usermod -aG docker ubuntu
    systemctl enable docker

    # nginx + certbot 설치
    apt-get install -y nginx certbot python3-certbot-nginx
    systemctl enable nginx

    # 디렉토리 구성
    mkdir -p /home/ubuntu/app
    mkdir -p /var/www/poti/.well-known
    chown -R ubuntu:ubuntu /home/ubuntu/app

    # docker 네트워크 생성
    docker network create poti-net || true

    # Redis 컨테이너 실행
    docker run -d --name redis --network poti-net --restart always -v /home/ubuntu/app/redis_data:/data redis:alpine
  EOF

  lifecycle {
    ignore_changes = [user_data]
  }

  tags = {
    Name = "PROD EC2"
  }

  metadata_options {
    http_tokens = "required" # IMDSv2 강제 (SSRF 시 자격증명 탈취 방어)
  }
}

resource "aws_eip" "prod" {
  instance = aws_instance.prod.id
  domain   = "vpc"

  tags = {
    Name = "poti-prod-eip"
  }
}

resource "aws_instance" "dev" {
  ami           = "ami-0e4ab31f1847c850c"
  instance_type = "t3.small"
  key_name      = "poti-key"
  subnet_id     = aws_subnet.ec2.id

  vpc_security_group_ids = [
    aws_security_group.dev_ec2.id,
  ]

  lifecycle {
    ignore_changes = [user_data]
  }

  tags = {
    Name = "DEV EC2"
  }

  metadata_options {
    http_tokens = "required" # IMDSv2 강제 (SSRF 시 자격증명 탈취 방어)
  }
}

resource "aws_eip" "dev" {
  instance = aws_instance.dev.id
  domain   = "vpc"

  tags = {
    Name = "poti-dev-eip"
  }
}
