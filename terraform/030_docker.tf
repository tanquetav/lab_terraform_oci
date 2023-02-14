resource "null_resource" "logineegistry" {
  depends_on = [oci_artifacts_container_repository.registry]

  provisioner "local-exec" {
    command = "echo '${var.ocir_password}' |  docker login ${local.ocir_docker_repository} --username ${local.ocir_namespace}/${var.ocir_username} --password-stdin"
  }
}

resource "null_resource" "buildandpush" {
  depends_on = [null_resource.logineegistry]
  triggers = {
    version = join(",", [local.version])
  }

  provisioner "local-exec" {
    command     = "docker build -t ${local.ocir_docker_repository}/${local.ocir_namespace}/${local.ocirepo}/${var.function_name}:${local.version} . "
    working_dir = "../"
  }

  provisioner "local-exec" {
    command     = "docker push ${local.ocir_docker_repository}/${local.ocir_namespace}/${local.ocirepo}/${var.function_name}:${local.version}"
    working_dir = "../"
  }


}
