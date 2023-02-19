package com.mycompany.app;

import com.hashicorp.cdktf.TerraformVariable;
import com.hashicorp.cdktf.providers.null_provider.resource.Resource;
import com.hashicorp.cdktf.providers.null_provider.resource.ResourceConfig;
import com.hashicorp.cdktf.providers.oci.artifacts_container_repository.ArtifactsContainerRepository;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.HashMap;

public class CloudDocker extends Construct {
    private final Resource buildandpush;

    public CloudDocker(Construct construct, TerraformVariable functionName, TerraformVariable ocirUsername, TerraformVariable ocirPassword, ArtifactsContainerRepository registry) {
        super(construct, "ocidocker");
/*
        Resource loginregistry = new Resource(this, "login", ResourceConfig.builder()
                .build());

        loginregistry.addOverride("provisioner", Arrays.asList(
                new HashMap() {
                    {
                        put("local-exec", new HashMap<String, String>() {
                            {
                                put("command", "echo '" + ocirPassword.getStringValue() + "' |  docker login " + OciConstants.ocirDockerRepository + " --username " + OciConstants.ocirNamespace + "/" + ocirUsername.getStringValue() + " --password-stdin");
                            }
                        });
                    }
                }
                ));

        try {
            Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c ", "echo '" + ocirPassword.getStringValue() + "' |  docker login " + OciConstants.ocirDockerRepository + " --username " + OciConstants.ocirNamespace + "/" + ocirUsername.getStringValue() + " --password-stdin"});
        } catch (IOException e) {
        }
*/
        // String imageAddress = OciConstants.ocirDockerRepository + "/" + OciConstants.ocirNamespace + "/" + OciConstants.ociRepo + "/" + functionName.getStringValue() + ":" + OciConstants.version;
        String imageAddress = String.format("%s/%s/%s/%s:%s", OciConstants.ocirDockerRepository , OciConstants.ocirNamespace , OciConstants.ociRepo , functionName.getStringValue() , OciConstants.version);

        buildandpush = new Resource(this, "buildandpush", ResourceConfig.builder()
                .dependsOn(Arrays.asList(registry))
                .triggers(new HashMap() {
                    {
                        put("version", OciConstants.version);
                    }
                })
                .build());

        buildandpush.addOverride("provisioner", Arrays.asList(
                new HashMap() {
                    {
                        put("local-exec", new HashMap<String, String>() {
                            {
                                put("command", "docker build -t " + imageAddress + " .");
                                put("working_dir", OciConstants.workDir);
                            }
                        });

                    }
                },
                new HashMap() {
                    {
                        put("local-exec", new HashMap<String, String>() {
                            {
                                put("command", "docker push " + imageAddress);
                                put("working_dir", OciConstants.workDir);
                            }
                        });
                    }
                }));
    }

    public Resource getBuildandpush() {
        return buildandpush;
    }
}
