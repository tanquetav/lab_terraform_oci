resource "oci_functions_application" "application" {
  #Required
  compartment_id = oci_identity_compartment.compartment.id
  display_name   = var.function_name
  subnet_ids     = [oci_core_subnet.sn.id]

}

resource "oci_functions_function" "func" {
  depends_on     = [null_resource.buildandpush]
  application_id = oci_functions_application.application.id
  display_name   = var.function_name
  image          = "${local.ocir_docker_repository}/${local.ocir_namespace}/${local.ocirepo}/${var.function_name}:${local.version}"
  memory_in_mbs  = "128"
}
