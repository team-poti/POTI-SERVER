variable "db_username" {
  description = "RDS 마스터 유저명"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "RDS 마스터 패스워드"
  type        = string
  sensitive   = true
}
