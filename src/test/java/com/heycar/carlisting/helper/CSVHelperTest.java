package com.heycar.carlisting.helper;

import static org.assertj.core.api.Assertions.assertThat;

import com.heycar.carlisting.model.Vehicle;
import java.io.IOException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class CSVHelperTest {

  @Test
  void shouldReadAndParseCSVFileCorrectly() throws IOException {

    var vehicles =
        CSVHelper.csvToVehicles(new ClassPathResource("car_listing.csv").getInputStream());
    assertThat(vehicles)
        .isNotNull()
        .isNotEmpty()
        .hasSize(4)
        .extracting(Vehicle::getCode, Vehicle::getMake, Vehicle::getColor)
        .contains(Tuple.tuple("1", "mercedes", "black"), Tuple.tuple("2", "audi", "white"));
  }
}
