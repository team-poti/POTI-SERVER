terraform {
  required_version = ">= 1.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    bucket = "poti-terraform-state"
    key    = "poti/terraform.tfstate"
    region = "ap-northeast-2"
  }
}

provider "aws" {
  region = "ap-northeast-2"
}
