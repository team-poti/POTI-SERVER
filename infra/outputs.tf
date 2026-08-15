output "prod_ec2_public_ip" {
  value = aws_instance.prod.public_ip
}

output "dev_ec2_public_ip" {
  value = aws_instance.dev.public_ip
}

output "rds_endpoint" {
  value = aws_db_instance.prod.endpoint
}
