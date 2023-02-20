package com.mycompany.app;

import com.hashicorp.cdktf.TerraformOutput;
import com.hashicorp.cdktf.TerraformOutputConfig;
import com.hashicorp.cdktf.TerraformVariable;
import com.hashicorp.cdktf.providers.oci.apigateway_api.ApigatewayApi;
import com.hashicorp.cdktf.providers.oci.apigateway_api.ApigatewayApiConfig;
import com.hashicorp.cdktf.providers.oci.apigateway_deployment.ApigatewayDeployment;
import com.hashicorp.cdktf.providers.oci.apigateway_deployment.ApigatewayDeploymentConfig;
import com.hashicorp.cdktf.providers.oci.apigateway_deployment.ApigatewayDeploymentSpecification;
import com.hashicorp.cdktf.providers.oci.apigateway_deployment.ApigatewayDeploymentSpecificationRoutes;
import com.hashicorp.cdktf.providers.oci.apigateway_deployment.ApigatewayDeploymentSpecificationRoutesBackend;
import com.hashicorp.cdktf.providers.oci.apigateway_gateway.ApigatewayGateway;
import com.hashicorp.cdktf.providers.oci.apigateway_gateway.ApigatewayGatewayConfig;
import com.hashicorp.cdktf.providers.oci.core_subnet.CoreSubnet;
import com.hashicorp.cdktf.providers.oci.functions_function.FunctionsFunction;
import com.hashicorp.cdktf.providers.oci.identity_compartment.IdentityCompartment;
import com.hashicorp.cdktf.providers.oci.identity_policy.IdentityPolicy;
import com.hashicorp.cdktf.providers.oci.identity_policy.IdentityPolicyConfig;
import software.constructs.Construct;

import java.util.Arrays;

public class CloudApiGateway extends Construct {
    private final IdentityPolicy policy;
    private final ApigatewayGateway apiGateway;
    private final ApigatewayDeployment deployment;
    private final TerraformOutput endpoint;

    public CloudApiGateway(Construct construct, TerraformVariable functionName, IdentityCompartment compartment, CoreSubnet subnet, FunctionsFunction function) {
        super(construct, "cloudApiGateway");

        policy = new IdentityPolicy(this, "policy", IdentityPolicyConfig.builder()
                .compartmentId(compartment.getId())
                .name(functionName.getStringValue())
                .description(functionName.getStringValue())
                .statements(Arrays.asList(
                    "ALLOW any-user to use functions-family in compartment "+compartment.getName()+" where ALL {request.principal.type= 'ApiGateway', request.resource.compartment.id = '"+compartment.getId()+"'}"
                ))
                .build());

        apiGateway = new ApigatewayGateway(this, "apigateway", ApigatewayGatewayConfig.builder()
                .compartmentId(compartment.getId())
                .displayName(functionName.getStringValue())
                .subnetId(subnet.getId())
                .endpointType("PUBLIC")
                .build());

        deployment = new ApigatewayDeployment(this, "deployment", ApigatewayDeploymentConfig.builder()
                .compartmentId(compartment.getId())
                .gatewayId(apiGateway.getId())
                .pathPrefix("/")
                .specification(ApigatewayDeploymentSpecification.builder()
                        .routes(Arrays.asList(
                                ApigatewayDeploymentSpecificationRoutes.builder()
                                        .path("/")
                                        .methods(Arrays.asList("GET"))
                                        .backend(ApigatewayDeploymentSpecificationRoutesBackend.builder()
                                                .type("ORACLE_FUNCTIONS_BACKEND")
                                                .functionId(function.getId())
                                                .build())
                                        .build()
                        ))
                        .build())
                .build());

        endpoint = new TerraformOutput(this, "endpoint", TerraformOutputConfig.builder()
                .value(deployment.getEndpoint())
                .build());
    }

    public IdentityPolicy getPolicy() {
        return policy;
    }

    public ApigatewayGateway getApiGateway() {
        return apiGateway;
    }

    public ApigatewayDeployment getDeployment() {
        return deployment;
    }

    public TerraformOutput getEndpoint() {
        return endpoint;
    }
}
