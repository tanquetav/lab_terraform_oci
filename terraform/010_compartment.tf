resource "oci_identity_compartment" "compartment" {
  name          = var.compartment_name
  description   = var.compartment_name
  enable_delete = true
}

data "oci_identity_availability_domains" "oci" {
  compartment_id = oci_identity_compartment.compartment.id
}
