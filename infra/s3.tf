resource "aws_s3_bucket" "main" {
  bucket = "poti-s3-bucket"

  tags = {
    Name = "poti-s3-bucket"
  }
}
