package com.couchbase.mca.config;

import com.couchbase.client.core.service.ServiceType;
import com.couchbase.client.mc.ClusterSpec;
import com.couchbase.client.mc.coordination.Coordinator;
import com.couchbase.client.mc.coordination.Coordinators;
import com.couchbase.client.mc.coordination.IsolatedCoordinator;
import com.couchbase.client.mc.detection.*;
import com.couchbase.mca.integration.AbstractMultiClusterConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Configuration
public class MultiClusterDatabaseConfiguration extends AbstractMultiClusterConfiguration {

    @Value("${mca.couchbase.bootstrap-hosts}")
    private String bootstrap;
    @Value("${mca.couchbase.username:Administrator}")
    private String userName;
    @Value("${mca.couchbase.bucket.name:demo}")
    private String bucketName;
    @Value("${mca.couchbase.password:password}")
    private String userPass;
    @Value("${mca.couchbase.minFailureNodes:1}")
    private int minFailureNodes;

    @Bean
    @Override
    public String bucketName() {
       return bucketName;
    }

    @Bean
    @Override
    public String userName() {
        return userName;
    }

    @Bean
    @Override
    public String userPass() {
       return userPass;
    }


    private ClusterSpec from(String clusterSpec) {
        String []values = clusterSpec.split(";");
        return ClusterSpec.create(Stream.of(values[1].split(",")).collect(toSet()),values[0]);
    }


    public List<ClusterSpec> clustersList() {
        return Stream.of(bootstrap.split(" ")).map(this::from).collect(Collectors.toList());
    }


    @Bean
    @Override
    public Coordinator coordinator() {
        return Coordinators.isolated(new IsolatedCoordinator.Options()
                .clusterSpecs(clustersList())
                .activeEntries(1)
                .failoverNumNodes(this.minFailureNodes)
                .serviceTypes(new HashSet<>(Arrays.asList(ServiceType.BINARY, ServiceType.QUERY)))
        );

    }

    @Bean
    public FailureDetectorFactory<? extends FailureDetector> failureDetectorFactory() {
        return FailureDetectors.nodeHealth(coordinator(), NodeHealthFailureDetector.options().minFailedNodes(this.minFailureNodes));
        // return new NodeHealthFailureDetectorFactoryST(coordinator(), NodeHealthFailureDetectorST.options().minFailedNodes(this.minFailureNodes));
    }


}
