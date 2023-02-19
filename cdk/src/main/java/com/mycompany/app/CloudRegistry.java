package com.mycompany.app;

import com.hashicorp.cdktf.TerraformVariable;
import com.hashicorp.cdktf.providers.oci.artifacts_container_repository.ArtifactsContainerRepository;
import com.hashicorp.cdktf.providers.oci.artifacts_container_repository.ArtifactsContainerRepositoryConfig;
import com.hashicorp.cdktf.providers.oci.identity_compartment.IdentityCompartment;
import software.constructs.Construct;

public class CloudRegistry extends Construct {
    private final ArtifactsContainerRepository registry;

    public CloudRegistry(Construct construct, TerraformVariable functionName, IdentityCompartment compartment) {
        super(construct, "cloudregistry");

        registry = new ArtifactsContainerRepository(this, "ociregistry", ArtifactsContainerRepositoryConfig.builder()
                .compartmentId(compartment.getId())
                .displayName(OciConstants.ociRepo+"/"+functionName.getStringValue())
                .isImmutable(Boolean.FALSE)
                .isPublic(Boolean.FALSE)
                .build());
    }

    public ArtifactsContainerRepository getRegistry() {
        return registry;
    }
}
