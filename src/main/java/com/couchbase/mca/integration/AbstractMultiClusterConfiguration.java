package com.couchbase.mca.integration;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.cluster.ClusterInfo;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.util.features.CouchbaseFeature;
import com.couchbase.client.java.util.features.Version;
import com.couchbase.client.mc.MultiClusterClient;
import com.couchbase.client.mc.coordination.Coordinator;
import com.couchbase.client.mc.coordination.SimpleTopologyAdmin;
import com.couchbase.client.mc.detection.FailureDetector;
import com.couchbase.client.mc.detection.FailureDetectorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.couchbase.config.CouchbaseConfigurationSupport;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.config.RepositoryOperationsMapping;

import java.util.List;

public abstract class AbstractMultiClusterConfiguration extends CouchbaseConfigurationSupport {

    @Bean
    public abstract String bucketName();

    @Bean
    public abstract String userName();

    @Bean
    public abstract String userPass();

    @Bean
    public abstract Coordinator coordinator();

    @Bean
    public abstract FailureDetectorFactory<? extends FailureDetector> failureDetectorFactory();

    @Bean
    public CouchbaseEnvironment couchbaseEnvironment() {
        return DefaultCouchbaseEnvironment.create();
    }

    @Bean
    public MultiClusterClient multiClusterClient() {
        MultiClusterClient client = new MultiClusterClient(coordinator(), failureDetectorFactory(), couchbaseEnvironment());
        client.authenticate(userName(), userPass());
        return client;
    }


    @Bean
    public SimpleTopologyAdmin topologyAdmin(Coordinator coordinator) {
        return SimpleTopologyAdmin.create(coordinator);
    }


    @Bean
    public Bucket multiClusterBucket() {
        return new SpringMcaBucket(multiClusterClient(), bucketName(), couchbaseEnvironment());
    }

    @Bean
    public ClusterInfo clusterInfo() {
        return new ClusterInfo() {
            @Override
            public JsonObject raw() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean checkAvailable(CouchbaseFeature feature) {
                return true;
            }

            @Override
            public Version getMinVersion() {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<Version> getAllVersions() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Bean
    public CouchbaseTemplate couchbaseTemplate() throws Exception {
        return new CouchbaseTemplate(
                clusterInfo(),
                multiClusterBucket(),
                mappingCouchbaseConverter(),
                translationService()
        );
    }

    @Bean
    public RepositoryOperationsMapping couchbaseRepositoryOperationsMapping(CouchbaseTemplate couchbaseTemplate) {
        //create a base mapping that associates all repositories to the default template
        RepositoryOperationsMapping baseMapping = new RepositoryOperationsMapping(couchbaseTemplate);
        //let the user tune it
        configureRepositoryOperationsMapping(baseMapping);
        return baseMapping;
    }

    /**
     * In order to customize the mapping between repositories/entity types to couchbase templates,
     * use the provided mapping's api (eg. in order to have different buckets backing different repositories).
     *
     * @param mapping the default mapping (will associate all repositories to the default template).
     */
    protected void configureRepositoryOperationsMapping(RepositoryOperationsMapping mapping) {
        //NO_OP
    }

}
