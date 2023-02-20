package com.mycompany.app;

import com.hashicorp.cdktf.providers.oci.core_default_route_table.CoreDefaultRouteTable;
import com.hashicorp.cdktf.providers.oci.core_default_route_table.CoreDefaultRouteTableConfig;
import com.hashicorp.cdktf.providers.oci.core_default_route_table.CoreDefaultRouteTableRouteRules;
import com.hashicorp.cdktf.providers.oci.core_default_security_list.CoreDefaultSecurityList;
import com.hashicorp.cdktf.providers.oci.core_default_security_list.CoreDefaultSecurityListConfig;
import com.hashicorp.cdktf.providers.oci.core_default_security_list.CoreDefaultSecurityListEgressSecurityRules;
import com.hashicorp.cdktf.providers.oci.core_default_security_list.CoreDefaultSecurityListIngressSecurityRules;
import com.hashicorp.cdktf.providers.oci.core_default_security_list.CoreDefaultSecurityListIngressSecurityRulesTcpOptions;
import com.hashicorp.cdktf.providers.oci.core_internet_gateway.CoreInternetGateway;
import com.hashicorp.cdktf.providers.oci.core_internet_gateway.CoreInternetGatewayConfig;
import com.hashicorp.cdktf.providers.oci.core_subnet.CoreSubnet;
import com.hashicorp.cdktf.providers.oci.core_subnet.CoreSubnetConfig;
import com.hashicorp.cdktf.providers.oci.core_vcn.CoreVcn;
import com.hashicorp.cdktf.providers.oci.core_vcn.CoreVcnConfig;
import com.hashicorp.cdktf.providers.oci.identity_compartment.IdentityCompartment;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CloudNetwork extends Construct {
    private final CoreVcn vcn;
    private final CoreInternetGateway ig;
    private final CoreDefaultRouteTable routeTable;
    private final CoreDefaultSecurityList sl;
    private final CoreSubnet subnet;

    public CloudNetwork(Construct construct, IdentityCompartment compartment) {
        super(construct, "cloudnetwork");

        vcn = new CoreVcn(this, "vcn", CoreVcnConfig.builder()
                .compartmentId(compartment.getId())
                .cidrBlock(OciConstants.cidrBlock)
                .build());

        ig = new CoreInternetGateway(this, "ig", CoreInternetGatewayConfig.builder()
                .compartmentId(compartment.getId())
                .vcnId(vcn.getId())
                .build());

        routeTable = new CoreDefaultRouteTable(this, "routetable", CoreDefaultRouteTableConfig.builder()
                .manageDefaultResourceId(vcn.getDefaultRouteTableId())
                .routeRules(Arrays.asList(
                        CoreDefaultRouteTableRouteRules.builder()
                                .destination("0.0.0.0/0")
                                .destinationType("CIDR_BLOCK")
                                .networkEntityId(ig.getId())
                                .build()
                ))
                .build());

        Stream<Integer> ports = Stream.of(80, 443, 22);

        sl = new CoreDefaultSecurityList(this, "sl", CoreDefaultSecurityListConfig.builder()
                .manageDefaultResourceId(vcn.getDefaultSecurityListId())
                .ingressSecurityRules(ports.map(x -> CoreDefaultSecurityListIngressSecurityRules.builder()
                    .protocol("6")
                    .tcpOptions(CoreDefaultSecurityListIngressSecurityRulesTcpOptions.builder()
                            .min(x)
                            .max(x)
                            .build())
                    .source("0.0.0.0/0")
                    .build()).collect(Collectors.toList()))
                .egressSecurityRules(Arrays.asList(
                        CoreDefaultSecurityListEgressSecurityRules.builder()
                                .protocol("all")
                                .destination("0.0.0.0/0")
                                .build()
                ))
                .build());
        subnet = new CoreSubnet(this, "subnet", CoreSubnetConfig.builder()
                .compartmentId(compartment.getId())
                .cidrBlock("10.0.0.0/24")
                .vcnId(vcn.getId())
                .routeTableId(routeTable.getId())
                .securityListIds(Arrays.asList(sl.getId()))
                .build());
    }

    public CoreVcn getVcn() {
        return vcn;
    }

    public CoreInternetGateway getIg() {
        return ig;
    }

    public CoreDefaultRouteTable getRouteTable() {
        return routeTable;
    }

    public CoreDefaultSecurityList getSl() {
        return sl;
    }

    public CoreSubnet getSubnet() {
        return subnet;
    }
}
