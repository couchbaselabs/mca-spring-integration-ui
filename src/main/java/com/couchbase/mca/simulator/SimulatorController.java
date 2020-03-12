package com.couchbase.mca.simulator;

import com.couchbase.mca.airport.Airport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("simulator")
public class SimulatorController {
    private final Logger LOGGER = LoggerFactory.getLogger(SimulatorController.class);
    private final SimulatorService<Airport> service;

    @Autowired
    public SimulatorController(SimulatorService service) {
        this.service = service;
    }

    @GetMapping("airports/{id}")
    public ResponseEntity<SimulatorResponse<Airport>> get(@PathVariable String id) {
      //  LOGGER.info("reading {}...",id);
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping("airports")
    public ResponseEntity<SimulatorResponse<Airport>> save(@RequestBody Airport airport) {
       // LOGGER.info("Saving...");
        return ResponseEntity.ok(service.save(airport));
    }

    @GetMapping("airports")
    public ResponseEntity<SimulatorResponse<Iterable<Airport>>> getAll() {
        // LOGGER.info("Querying...");
        return ResponseEntity.ok(service.getAll());
    }

}
