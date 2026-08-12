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

  tags = {
    Name = "PROD EC2"
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

  tags = {
    Name = "DEV EC2"
  }
}
