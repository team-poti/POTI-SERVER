resource "aws_db_instance" "prod" {
  identifier        = "poti-prod-db"
  engine            = "mysql"
  engine_version    = "8.4.8"
  instance_class    = "db.t4g.micro"

  username          = "poti"
  password          = var.db_password

  allocated_storage = 20
  storage_type      = "gp2"
  storage_encrypted = true

  multi_az               = false
  db_subnet_group_name   = "rds-ec2-db-subnet-group-2"
  vpc_security_group_ids = [
    aws_security_group.rds_to_ec2_6.id,
    aws_security_group.rds_to_ec2_7.id,
  ]

  deletion_protection = true # 실수로 destroy해도 AWS가 삭제 거부
  skip_final_snapshot = false # 삭제 시 스냅샷 생성 여부, true면 스냅샷 생성 안함
  final_snapshot_identifier = "poti-prod-db-final-snapshot"

  lifecycle {
    ignore_changes = [
      password,         # 패스워드는 Terraform 외부에서 관리
      engine_version,   # 마이너 버전 자동 업데이트 허용
    ]
  }

  tags = {
    Name = "poti-prod-db"
  }
}
