terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
}

provider "docker" {}

variable "mongo_host_port" {
  type        = number
  description = "Puerto en el host para MongoDB"
  default     = 27017
}

resource "docker_image" "mongo" {
  name         = "mongo:7.0"
  keep_locally = true
}

resource "docker_container" "mongo" {
  name  = "franquicias-mongo-tf"
  image = docker_image.mongo.image_id

  ports {
    internal = 27017
    external = var.mongo_host_port
  }
}

output "mongo_connection_string" {
  value       = "mongodb://127.0.0.1:${var.mongo_host_port}/franquicias_db"
  description = "URI para spring.data.mongodb.uri en desarrollo local"
}
