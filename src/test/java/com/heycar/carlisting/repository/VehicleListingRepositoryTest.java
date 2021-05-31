package com.heycar.carlisting.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.heycar.carlisting.model.Vehicle;
import com.heycar.carlisting.model.VehicleList;
import java.util.List;
import org.junit.jupiter.api.Test;

class VehicleListingRepositoryTest {

  private final VehicleListingRepository vehicleListingRepository = new VehicleListingRepository();

  @Test
  public void shouldSaveVehicles() {

    Vehicle vehicle = new Vehicle();
    vehicle.setCode("1");
    vehicle.setColor("red");
    vehicle.setMake("mercedes");
    vehicle.setKw(123);
    vehicle.setModel("a180");
    vehicle.setYear(2016);

    VehicleList vehicleList = new VehicleList();
    vehicleList.setDealerId(123);
    vehicleList.setVehicles(List.of(vehicle));

    vehicleListingRepository.saveAll(vehicleList);
    var actualVehiclesById = vehicleListingRepository.findById(123);
    assertThat(actualVehiclesById).isNotNull().isNotEmpty().isEqualTo(List.of(vehicle));

    var actualVehicleList = vehicleListingRepository.findAll();
    assertThat(actualVehicleList).isNotNull().isNotEmpty().isEqualTo(List.of(vehicleList));
  }

  @Test
  public void shouldUpdateExisting() {

    Vehicle vehicle = new Vehicle();
    vehicle.setCode("1");
    vehicle.setColor("red");
    vehicle.setMake("mercedes");
    vehicle.setKw(123);
    vehicle.setModel("a180");
    vehicle.setYear(2016);

    Vehicle vehicle2 = new Vehicle();
    vehicle2.setCode("2");
    vehicle2.setColor("black");
    vehicle2.setMake("audi");
    vehicle2.setKw(123);
    vehicle2.setModel("a3");
    vehicle2.setYear(2014);

    VehicleList vehicleList = new VehicleList();
    vehicleList.setDealerId(123);
    vehicleList.setVehicles(List.of(vehicle, vehicle2));

    vehicleListingRepository.saveAll(vehicleList);

    // change color and year of same dealer's existing listing
    Vehicle vehicle3 = new Vehicle();
    vehicle3.setCode("2");
    vehicle3.setColor("white");
    vehicle3.setMake("audi");
    vehicle3.setKw(123);
    vehicle3.setModel("a3");
    vehicle3.setYear(2016);

    VehicleList vehicleList2 = new VehicleList();
    vehicleList2.setDealerId(123);
    vehicleList2.setVehicles(List.of(vehicle3));

    vehicleListingRepository.saveAll(vehicleList2);

    var actualVehiclesById = vehicleListingRepository.findById(123);
    assertThat(actualVehiclesById).isNotNull().isNotEmpty().isEqualTo(List.of(vehicle, vehicle3));
  }
}
