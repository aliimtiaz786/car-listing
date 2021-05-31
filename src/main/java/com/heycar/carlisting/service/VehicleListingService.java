package com.heycar.carlisting.service;

import com.heycar.carlisting.helper.CSVHelper;
import com.heycar.carlisting.model.Vehicle;
import com.heycar.carlisting.model.VehicleList;
import com.heycar.carlisting.repository.VehicleListingRepository;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class VehicleListingService {
  @Autowired private final VehicleListingRepository repository;

  public void save(MultipartFile file, Integer dealerId) throws IOException {
    try {
      List<Vehicle> vehicles = CSVHelper.csvToVehicles(file.getInputStream());
      var vehicleListing = new VehicleList(dealerId, vehicles);
      repository.saveAll(vehicleListing);
    } catch (IOException e) {
      log.error("Error occurred while storing vehicle listings", e);
      throw e;
    }
  }

  public void save(VehicleList vehicleList) {
    repository.saveAll(vehicleList);
  }

  public List<VehicleList> getVehicleListings() {
    return repository.findAll();
  }

  public List<VehicleList> getVehicleListings(
      String make, String model, Integer year, String color) {
    var vehicleLists = getVehicleListings();

    if (vehicleLists.isEmpty()) {
      log.warn("No vehicle founds in storage ");
    }

    if (StringUtils.hasLength(make)
        || StringUtils.hasLength(model)
        || StringUtils.hasLength(color)
        || year != null) {
      return vehicleLists.stream()
          .map(
              p -> {
                var dealerId = p.getDealerId();

                var dealerVehicles = p.getVehicles();
                dealerVehicles = getFilteredVehiclesBasedOnMake(make, dealerVehicles);
                dealerVehicles = getFilteredVehiclesBasedOnModel(model, dealerVehicles);
                dealerVehicles = getFilteredVehiclesBasedOnYear(year, dealerVehicles);
                dealerVehicles = getFilteredVehiclesBasedOnColor(color, dealerVehicles);

                return new VehicleList(dealerId, dealerVehicles);
              })
          .collect(Collectors.toList());
    } else {
      return vehicleLists;
    }
  }

  private List<Vehicle> getFilteredVehiclesBasedOnMake(String make, List<Vehicle> vehicles) {
    if (StringUtils.hasLength(make)) {
      return vehicles.stream().filter(g -> g.getMake().equals(make)).collect(Collectors.toList());
    } else {
      return vehicles;
    }
  }

  private List<Vehicle> getFilteredVehiclesBasedOnModel(String model, List<Vehicle> vehicles) {
    if (StringUtils.hasLength(model)) {
      return vehicles.stream().filter(g -> g.getModel().equals(model)).collect(Collectors.toList());
    } else {
      return vehicles;
    }
  }

  private List<Vehicle> getFilteredVehiclesBasedOnYear(Integer year, List<Vehicle> vehicles) {
    if (year != null) {
      return vehicles.stream().filter(g -> g.getYear().equals(year)).collect(Collectors.toList());
    } else {
      return vehicles;
    }
  }

  private List<Vehicle> getFilteredVehiclesBasedOnColor(String color, List<Vehicle> vehicles) {
    if (StringUtils.hasLength(color)) {
      return vehicles.stream().filter(g -> g.getColor().equals(color)).collect(Collectors.toList());
    } else {
      return vehicles;
    }
  }
}
