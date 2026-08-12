resource "aws_vpc" "main" {
  cidr_block = "172.31.0.0/16"

  tags = {
    Name = "poti-vpc"
  }
}

resource "aws_subnet" "ec2" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "172.31.32.0/20"
  availability_zone = "ap-northeast-2c"

  tags = {
    Name = "poti-ec2-subnet"
  }
}

# RDS 프라이빗 서브넷
resource "aws_subnet" "rds_a" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "172.31.1.128/25"
  availability_zone = "ap-northeast-2a"

  tags = {
    Name = "RDS-Pvt-subnet-5"
  }
}

resource "aws_subnet" "rds_b" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "172.31.64.128/25"
  availability_zone = "ap-northeast-2b"

  tags = {
    Name = "RDS-Pvt-subnet-2"
  }
}

resource "aws_subnet" "rds_c" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "172.31.0.128/25"
  availability_zone = "ap-northeast-2c"

  tags = {
    Name = "RDS-Pvt-subnet-3"
  }
}

resource "aws_subnet" "rds_d" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "172.31.1.0/25"
  availability_zone = "ap-northeast-2d"

  tags = {
    Name = "RDS-Pvt-subnet-4"
  }
}

# PROD EC2 보안그룹
resource "aws_security_group" "prod_ec2" {
  name        = "launch-wizard-5"
  description = "launch-wizard-5 created 2026-07-03T06:39:59.027Z"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name = "poti-prod-ec2-sg"
  }
}

# DEV EC2 보안그룹
resource "aws_security_group" "dev_ec2" {
  name        = "launch-wizard-4"
  description = "launch-wizard-4 created 2026-07-03T06:25:15.687Z"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name = "poti-dev-ec2-sg"
  }
}

# EC2 → RDS 연결 보안그룹
resource "aws_security_group" "ec2_to_rds_6" {
  name        = "ec2-rds-6"
  description = "Security group attached to instances to securely connect to poti-prod-db."
  vpc_id      = aws_vpc.main.id
}

resource "aws_security_group" "ec2_to_rds_7" {
  name        = "ec2-rds-7"
  description = "Security group attached to instances to securely connect to poti-prod-db."
  vpc_id      = aws_vpc.main.id
}

# RDS → EC2 연결 보안그룹
resource "aws_security_group" "rds_to_ec2_6" {
  name        = "rds-ec2-6"
  description = "Security group attached to poti-prod-db to allow EC2 instances to connect."
  vpc_id      = aws_vpc.main.id
}

resource "aws_security_group" "rds_to_ec2_7" {
  name        = "rds-ec2-7"
  description = "Security group attached to poti-prod-db to allow EC2 instances to connect."
  vpc_id      = aws_vpc.main.id
}

# Dozzle 보안그룹
resource "aws_security_group" "dozzle" {
  name        = "dozzle"
  description = "dozzle-connect"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name = "poti-dozzle-sg"
  }
}
