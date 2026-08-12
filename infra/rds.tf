resource "aws_db_instance" "prod" {
  identifier        = "poti-prod-db"
  engine            = "mysql"
  instance_class    = "db.t4g.micro"
  db_name           = "poti"

  # 실제 값은 환경변수 또는 AWS Secrets Manager에서 관리
  username = var.db_username
  password = var.db_password

  vpc_security_group_ids = [
    aws_security_group.rds_to_ec2_6.id,
    aws_security_group.rds_to_ec2_7.id,
  ]

  skip_final_snapshot = true

  tags = {
    Name = "poti-prod-db"
  }
}
