resource "oci_identity_policy" "policy" {
  compartment_id = oci_identity_compartment.compartment.id
  description    = var.function_name
  name           = var.function_name
  statements     = ["ALLOW any-user to use functions-family in compartment ${oci_identity_compartment.compartment.name} where ALL {request.principal.type= 'ApiGateway', request.resource.compartment.id = '${oci_identity_compartment.compartment.id}'}"]
}

resource "oci_apigateway_gateway" "gateway" {
  compartment_id = oci_identity_compartment.compartment.id
  display_name   = var.function_name
  subnet_id      = oci_core_subnet.sn.id
  endpoint_type  = "PUBLIC"
}

resource "oci_apigateway_deployment" "deployment" {
  compartment_id = oci_identity_compartment.compartment.id
  gateway_id     = oci_apigateway_gateway.gateway.id
  path_prefix    = "/"
  specification {
    routes {
      path    = "/"
      methods = ["GET"]
      backend {
        type        = "ORACLE_FUNCTIONS_BACKEND"
        function_id = oci_functions_function.func.id
      }
    }
  }
}

output "endpoint" {
  value = oci_apigateway_deployment.deployment.endpoint
}
