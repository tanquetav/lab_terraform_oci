package com.mycompany.app;

import com.hashicorp.cdktf.TerraformVariable;
import com.hashicorp.cdktf.providers.oci.identity_compartment.IdentityCompartment;
import com.hashicorp.cdktf.providers.oci.identity_compartment.IdentityCompartmentConfig;
import software.constructs.Construct;

public class CloudCompartment extends Construct {
    private final IdentityCompartment compartment;

    public CloudCompartment(Construct scope, TerraformVariable compartmentName) {
        super(scope, "cloudcompartment");
        compartment = new IdentityCompartment(this, "ocicompartment", IdentityCompartmentConfig.builder()
                .name(compartmentName.getStringValue())
                .description(compartmentName.getStringValue())
                .build());
    }

    public IdentityCompartment getCompartment() {
        return compartment;
    }
}
