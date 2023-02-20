package com.mycompany.app;

import com.hashicorp.cdktf.TerraformVariable;
import com.hashicorp.cdktf.providers.null_provider.resource.Resource;
import com.hashicorp.cdktf.providers.oci.core_subnet.CoreSubnet;
import com.hashicorp.cdktf.providers.oci.functions_application.FunctionsApplication;
import com.hashicorp.cdktf.providers.oci.functions_application.FunctionsApplicationConfig;
import com.hashicorp.cdktf.providers.oci.functions_function.FunctionsFunction;
import com.hashicorp.cdktf.providers.oci.functions_function.FunctionsFunctionConfig;
import com.hashicorp.cdktf.providers.oci.identity_compartment.IdentityCompartment;
import software.constructs.Construct;

import java.util.Arrays;

public class CloudFunction extends Construct {

    private final FunctionsApplication application;
    private final FunctionsFunction function;

    public CloudFunction(Construct mainStack, TerraformVariable functionName, IdentityCompartment compartment, CoreSubnet subnet, Resource buildandpush) {
        super(mainStack, "cloudFunction");

        application = new FunctionsApplication(this, "application", FunctionsApplicationConfig.builder()
                .compartmentId(compartment.getId())
                .displayName(functionName.getStringValue())
                .subnetIds(Arrays.asList(subnet.getId()))
                .build());

        String imageAddress = String.format("%s/%s/%s/%s:%s", OciConstants.ocirDockerRepository , OciConstants.ocirNamespace , OciConstants.ociRepo , functionName.getStringValue() , OciConstants.version);

        function = new FunctionsFunction(this, "function", FunctionsFunctionConfig.builder()
                .dependsOn(Arrays.asList(buildandpush))
                .applicationId(application.getId())
                .displayName(functionName.getStringValue())
                .image(imageAddress)
                .memoryInMbs("128")
                .build());
    }

    public FunctionsApplication getApplication() {
        return application;
    }

    public FunctionsFunction getFunction() {
        return function;
    }
}
