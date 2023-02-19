package com.mycompany.app;

import com.hashicorp.cdktf.providers.oci.provider.OciProvider;
import software.constructs.Construct;

import com.hashicorp.cdktf.TerraformStack;

public class MainStack extends TerraformStack
{
    public MainStack(final Construct scope, final String id) {
        super(scope, id); 

        OciProvider provider = OciProvider.Builder.create(this, "ociprovider").build();

        CloudInput cloudInput = new CloudInput(this);
        // define resources here
        CloudCompartment cloudCompartment = new CloudCompartment(this, cloudInput.getCompartmentName());

        CloudRegistry cloudRegistry = new CloudRegistry(this, cloudInput.getFunctionName(), cloudCompartment.getCompartment());
    }
}