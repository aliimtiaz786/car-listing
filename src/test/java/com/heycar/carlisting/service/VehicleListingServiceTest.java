package com.heycar.carlisting.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.heycar.carlisting.model.Vehicle;
import com.heycar.carlisting.model.VehicleList;
import com.heycar.carlisting.repository.VehicleListingRepository;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class VehicleListingServiceTest {

  VehicleListingService vehicleListingService =
      new VehicleListingService(new VehicleListingRepository());

  @Test
  void testFilters() throws IOException {

    Vehicle vehicle = new Vehicle();
    vehicle.setCode("1");
    vehicle.setColor("black");
    vehicle.setMake("mercedes");
    vehicle.setKw(123);
    vehicle.setModel("a180");
    vehicle.setYear(2014);
    vehicle.setPrice(15950);

    MultipartFile file =
        new MockMultipartFile(
            "car_listing.csv", new ClassPathResource("car_listing.csv").getInputStream());
    vehicleListingService.save(file, 123);

    assertThat(vehicleListingService.getVehicleListings()).isNotNull().isNotEmpty();
    assertThat(vehicleListingService.getVehicleListings("mercedes", "a180", 2014, "black"))
        .isNotNull()
        .isNotEmpty()
        .extracting(VehicleList::getVehicles)
        .containsExactly(List.of(vehicle));
  }
}
