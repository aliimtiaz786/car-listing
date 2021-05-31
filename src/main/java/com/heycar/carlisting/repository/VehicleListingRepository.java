package com.heycar.carlisting.repository;

import com.heycar.carlisting.model.Vehicle;
import com.heycar.carlisting.model.VehicleList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class VehicleListingRepository {

  private final ConcurrentHashMap<Integer, List<Vehicle>> storage = new ConcurrentHashMap<>();

  public void saveAll(VehicleList vehicleList) {

    var newVehicles = vehicleList.getVehicles();
    var newVehicleDealerId = vehicleList.getDealerId();
    // if storage already contains we will update existing with respect to code
    if (storage.containsKey(vehicleList.getDealerId())) {
      var existingVehicles = storage.get(vehicleList.getDealerId());
      var existingVehicleMap =
          existingVehicles.stream()
              .map(p -> Map.entry(p.getCode(), p))
              .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

      // update existing or add new
      for (Vehicle vehicle : newVehicles) {
        existingVehicleMap.put(vehicle.getCode(), vehicle);
      }

      storage.put(newVehicleDealerId, existingVehicleMap.values().stream().toList());

    } else {
      storage.put(newVehicleDealerId, newVehicles);
    }
  }

  public List<VehicleList> findAll() {

    return storage.entrySet().stream()
        .map(
            p -> {
              var dealerId = p.getKey();
              var vehicles = p.getValue();
              return new VehicleList(dealerId, vehicles);
            })
        .collect(Collectors.toList());
  }

  public List<Vehicle> findById(Integer dealerId) {

    return Optional.ofNullable(storage.get(dealerId)).orElse(Collections.emptyList());
  }
}
