packer {
  required_plugins {
    amazon = {
      version = ">= 1.0.0, <2.0.0"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

variable "aws_region" {
  description = "The AWS region where resources will be deployed"
  type        = string
  default     = "us-east-1"
}

variable "source_ami" {
  type    = string
  default = "ami-0866a3c8686eaeeba" # Ubuntu 24.04 LTS us-east-1
}

variable "ssh_username" {
  type    = string
  default = "ubuntu"
}

variable "subnet_id" {
  type    = string
  default = "subnet-0cb34451b17710366"
}

variable "postgres_password" {
  type    = string
  default = "qwertyuiop"
}

variable "postgres_username" {
  type    = string
  default = "csye6225"
}

source "amazon-ebs" "my-ami" {
  region          = "${var.aws_region}"
  ami_name        = "csye6225_f24_app_${formatdate("YYYY_MM_DD_HH_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225"

  ami_regions = [
    "us-east-1",
  ]

  aws_polling {
    delay_seconds = 120
    max_attempts  = 50
  }

  instance_type = "t2.small"
  source_ami    = "${var.source_ami}"
  ssh_username  = "${var.ssh_username}"
  subnet_id     = "${var.subnet_id}"

  launch_block_device_mappings {
    delete_on_termination = true
    device_name           = "/dev/sda1"
    volume_size           = 8
    volume_type           = "gp2"
  }
}

build {
  sources = [
    "source.amazon-ebs.my-ami"
  ]

  provisioner "shell-local" {
    inline = ["cd .. && ./gradlew clean build"]
  }

  provisioner "shell" {
    script = "updateOS.sh"
  }

  provisioner "shell" {
    script = "appDirSetup.sh"
  }

  provisioner "shell" {
    script = "javaSetup.sh"
  }

  provisioner "shell" {
    script = "dbSetup.sh"
  }

  provisioner "file" {
    source      = "../build/libs/webapp-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/app.jar"
  }

  provisioner "shell" {
    script = "appSetup.sh"
  }

  provisioner "shell" {
    script = "appStart.sh"
  }
}

