package com.mycompany.app;

import com.hashicorp.cdktf.TerraformOutput;
import com.hashicorp.cdktf.TerraformVariable;
import com.hashicorp.cdktf.TerraformVariableConfig;
import software.constructs.Construct;

public class CloudInput extends Construct{
    private final TerraformVariable compartmentName;
    private final TerraformVariable functionName;
    private final TerraformVariable ocirUsername;
    private final TerraformVariable ocirPassword;

    public CloudInput(Construct mainStack) {
        super(mainStack, "cloudInput");
        compartmentName = new TerraformVariable(this, "compartment_name", TerraformVariableConfig.builder()
                .type("string")
                .defaultValue("funcdemo")
                .build()
        );

        functionName = new TerraformVariable(this, "function_name", TerraformVariableConfig.builder()
                .type("string")
                .defaultValue("func")
                .build()
        );
        ocirUsername = new TerraformVariable(this, "ocir_username", TerraformVariableConfig.builder()
                .type("string")
                .defaultValue("tavares.george@gmail.com")
                .build()
        );
        ocirPassword = new TerraformVariable(this, "ocir_password", TerraformVariableConfig.builder()
                .type("string")
                .defaultValue("")
                .build()
        );
    }

    public TerraformVariable getCompartmentName() {
        return compartmentName;
    }

    public TerraformVariable getFunctionName() {
        return functionName;
    }

    public TerraformVariable getOcirUsername() {
        return ocirUsername;
    }

    public TerraformVariable getOcirPassword() {
        return ocirPassword;
    }
}
