package com.company.app;

import com.hashicorp.cdktf.App;
import com.hashicorp.cdktf.TerraformStack;
import com.hashicorp.cdktf.Testing;
import com.hashicorp.cdktf.TestingAppConfig;
import com.hashicorp.cdktf.providers.oci.core_default_security_list.CoreDefaultSecurityList;
import com.hashicorp.cdktf.providers.oci.core_subnet.CoreSubnet;
import com.hashicorp.cdktf.providers.oci.core_vcn.CoreVcn;
import com.hashicorp.cdktf.providers.oci.identity_compartment.IdentityCompartment;
import com.mycompany.app.CloudCompartment;
import com.mycompany.app.CloudInput;
import com.mycompany.app.CloudNetwork;
import com.mycompany.app.OciConstants;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CloudNetworkTest {

    @Test
    void shouldCreateNetwork() {
        App app = Testing.app(TestingAppConfig.builder()
                .stubVersion(Boolean.FALSE)
                .build());

        TerraformStack stack = new TerraformStack(app, "app");
        CloudInput cloudInput = new CloudInput(stack);
        CloudCompartment compartment = new CloudCompartment(stack, cloudInput.getCompartmentName());
        new CloudNetwork(stack, compartment.getCompartment());
        String synthesized = Testing.synth(stack);

        assertTrue(Testing.toHaveResource(synthesized, IdentityCompartment.TF_RESOURCE_TYPE));
        assertTrue(Testing.toHaveResourceWithProperties(synthesized, CoreVcn.TF_RESOURCE_TYPE, new HashMap(){
            {
                put("cidr_block", OciConstants.cidrBlock);
            }
        }));
        assertTrue(Testing.toHaveResourceWithProperties(synthesized, CoreSubnet.TF_RESOURCE_TYPE, new HashMap(){
            {
                put("cidr_block", "10.0.0.0/24");
            }
        }));
    }
}
