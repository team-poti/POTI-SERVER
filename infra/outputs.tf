output "prod_ec2_public_ip" {
  value = aws_eip.prod.public_ip
}

output "dev_ec2_public_ip" {
  value = aws_eip.dev.public_ip
}

output "rds_endpoint" {
  value = aws_db_instance.prod.endpoint
}
