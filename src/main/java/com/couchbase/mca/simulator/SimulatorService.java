package com.couchbase.mca.simulator;

import com.couchbase.client.mc.coordination.SimpleTopologyAdmin;
import com.couchbase.client.mc.coordination.TopologyEntry;
import org.springframework.data.couchbase.repository.CouchbaseRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class SimulatorService<T> {
    private final SimpleTopologyAdmin topologyAdmin;
    private final CouchbaseRepository<T,String> repository;


    public SimulatorService(SimpleTopologyAdmin topologyAdmin, CouchbaseRepository<T, String> repository) {
        this.topologyAdmin = topologyAdmin;
        this.repository = repository;
    }

    public SimulatorResponse<T> get(String id) {
        //SpringMcaBucket bucket = ((SpringMcaBucket)repository.getCouchbaseOperations().getCouchbaseBucket());
        SimulatorResponse<T> response;
        Set<String> active = topologyAdmin.currentTopology().active().stream().map(TopologyEntry::identifier).collect(Collectors.toSet());
        long init = System.nanoTime();
        try {
            T value = repository.findById(id).orElse(null);
            float latency = (System.nanoTime() - init)/1000000.0f;
            response = SimulatorResponse.<T>builder().value(value).latency(latency).activeClusters(active).type(SimulatorResponse.Operation.READ).timestamp(LocalDateTime.now()).build();
        } catch (RuntimeException ex) {
            float latency = (System.nanoTime() - init)/1000000.0f;
            response = SimulatorResponse.<T>builder().latency(latency).type(SimulatorResponse.Operation.READ).status(SimulatorResponse.Status.ERROR).message(ex.toString()).timestamp(LocalDateTime.now()).build();
        }
        return response;
    }

    public SimulatorResponse<T> save(T value) {
        // Also, you can use the SDK SpringMcaBucket bucket = ((SpringMcaBucket)repository.getCouchbaseOperations().getCouchbaseBucket());
        SimulatorResponse<T> response;
        Set<String> active = topologyAdmin.currentTopology().active().stream().map(TopologyEntry::identifier).collect(Collectors.toSet());
        long init = System.nanoTime();
        try {
            T val = repository.save(value);
            float latency = (float)(System.nanoTime() - init)/(1000000.0f);
            response = SimulatorResponse.<T>builder().value(val).latency(latency).activeClusters(active).type(SimulatorResponse.Operation.WRITE).timestamp(LocalDateTime.now()).build();
        } catch (RuntimeException ex) {
            float latency = (float)(System.nanoTime() - init)/(1000000.0f);
            response = SimulatorResponse.<T>builder().latency(latency).type(SimulatorResponse.Operation.WRITE).status(SimulatorResponse.Status.ERROR).message(ex.toString()).timestamp(LocalDateTime.now()).build();
        }
        return response;
    }

    public SimulatorResponse<Iterable<T>> getAll() {
        SimulatorResponse<Iterable<T>> response;
        Set<String> active = topologyAdmin.currentTopology().active().stream().map(TopologyEntry::identifier).collect(Collectors.toSet());
        long init = System.nanoTime();
        try {
            Iterable<T> content = repository.findAll();
            float latency = (float)(System.nanoTime() - init)/(1000000.0f);
            response = SimulatorResponse.<Iterable<T>>builder().value(content).latency(latency).activeClusters(active).type(SimulatorResponse.Operation.QUERY).timestamp(LocalDateTime.now()).build();
        } catch (RuntimeException ex) {
            float latency = (float)(System.nanoTime() - init)/(1000000.0f);
            response = SimulatorResponse.<Iterable<T>>builder().latency(latency).type(SimulatorResponse.Operation.QUERY).status(SimulatorResponse.Status.ERROR).message(ex.toString()).timestamp(LocalDateTime.now()).build();
        }
        return response;
    }


}
