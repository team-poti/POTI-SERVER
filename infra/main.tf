terraform {
  required_version = ">= 1.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    bucket       = "poti-terraform-state"
    key          = "poti/terraform.tfstate"
    region       = "ap-northeast-2"
    use_lockfile = true # 동시 apply 방지 (S3 네이티브 잠금)
  }
}

provider "aws" {
  region = "ap-northeast-2"
}
