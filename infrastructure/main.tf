terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.16"
    }
  }

  required_version = ">= 1.2.0"
  backend "s3" {
    bucket = "gurukul-terraform-tutorial"
    key    = "sankar/terraform.tfstate"
    region = "ap-south-1"
  }
}

provider "aws" {
  region = "ap-south-1"
}

data "aws_key_pair" "key-pair" {
  key_name = "gurukul-2023"
}

data "aws_security_group" "security-group" {
  vpc_id = "vpc-e4bc548f"
}

resource "aws_instance" "app_server" {
  ami             = "ami-0e742cca61fb65051"
  instance_type   = "t2.micro"
  key_name        = data.aws_key_pair.key-pair.key_name
  security_groups = data.aws_security_group.security-group.vpc_id

  tags = {
    Name  = "SankarSampleEsopApp"
    Owner = "Sankar"
  }
}
