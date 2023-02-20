package com.company.app;

import com.hashicorp.cdktf.App;
import com.hashicorp.cdktf.TerraformStack;
import com.hashicorp.cdktf.Testing;
import com.hashicorp.cdktf.TestingAppConfig;
import com.hashicorp.cdktf.providers.oci.identity_compartment.IdentityCompartment;
import com.mycompany.app.CloudCompartment;
import com.mycompany.app.CloudInput;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CloudCompartmentTest {

    @Test
    void shouldCreateCompartment() {
        App app = Testing.app(TestingAppConfig.builder()
                .stubVersion(Boolean.FALSE)
                .build());

        TerraformStack stack = new TerraformStack(app, "app");
        CloudInput cloudInput = new CloudInput(stack);
        new CloudCompartment(stack, cloudInput.getCompartmentName());
        String synthesized = Testing.synth(stack);
        System.out.println(synthesized);
        assertTrue(Testing.toHaveResource(synthesized, IdentityCompartment.TF_RESOURCE_TYPE));
    }
}
