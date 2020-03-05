package com.couchbase.mca.simulator;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Set;

@Value
@Builder
public class SimulatorResponse<T> {
    Operation type;
    Set<String> activeClusters;
    LocalDateTime timestamp;
    T value;
    Float latency;
    @Builder.Default
    Status status = Status.SUCCESS;
    String message;

    enum Operation {
        WRITE,
        READ
    }

    enum Status {
        SUCCESS,
        ERROR
    }


}
