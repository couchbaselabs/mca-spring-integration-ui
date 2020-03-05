package com.couchbase.mca.config;

import com.couchbase.client.mc.coordination.SimpleTopologyAdmin;
import com.couchbase.mca.airport.Airport;
import com.couchbase.mca.airport.AirportRepository;
import com.couchbase.mca.simulator.SimulatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimulatorConfig {

    @Bean
    @Autowired
    public SimulatorService<Airport> simulatorService(SimpleTopologyAdmin admin, AirportRepository repository) {
        return new SimulatorService(admin, repository);
    }
}
