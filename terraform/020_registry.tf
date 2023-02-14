resource "oci_artifacts_container_repository" "registry" {
  # note: repository = store for all images versions of a specific container image - so it included the function name
  compartment_id = oci_identity_compartment.compartment.id
  display_name   = "${local.ocirepo}/${var.function_name}"
  is_immutable   = false
  is_public      = false
}
