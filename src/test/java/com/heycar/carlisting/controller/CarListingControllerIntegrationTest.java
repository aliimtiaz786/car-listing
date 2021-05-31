package com.heycar.carlisting.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.heycar.carlisting.CarListingApplication;
import com.heycar.carlisting.message.ResponseMessage;
import com.heycar.carlisting.model.Vehicle;
import com.heycar.carlisting.model.VehicleList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

@SpringBootTest(classes = CarListingApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class CarListingControllerIntegrationTest {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate testRestTemplate;

  @Test
  void csvFileShouldBeUploadedSuccessfully() {
    LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
    parameters.add("file", new ClassPathResource("car_listing.csv"));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<LinkedMultiValueMap<String, Object>> entity =
        new HttpEntity<LinkedMultiValueMap<String, Object>>(parameters, headers);

    ResponseEntity<ResponseMessage> response =
        testRestTemplate.exchange(
            ("http://localhost:" + port + "/upload_csv/1"),
            HttpMethod.POST,
            entity,
            ResponseMessage.class,
            "");

    // Expect Ok
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .isNotNull()
        .extracting(ResponseMessage::getMessage)
        .isEqualTo("Uploaded the file successfully: car_listing.csv");

    List<VehicleList> vehicleLists = getVehicleListsByCallingSearch();
    assertThat(vehicleLists).isNotNull().isNotEmpty();
  }

  @Test
  void uploadVehicleListingThroughJSON() {

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

    ResponseEntity<ResponseMessage> response =
        testRestTemplate.postForEntity(
            ("http://localhost:" + port + "/vehicle_listing"), vehicleList, ResponseMessage.class);

    // Expect Ok
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .isNotNull()
        .extracting(ResponseMessage::getMessage)
        .isEqualTo("Vehicle Listing uploaded successfully for dealer_id: 123");

    List<VehicleList> vehicleLists = getVehicleListsByCallingSearch();
    assertThat(vehicleLists).isNotNull().isNotEmpty().contains(vehicleList);
  }

  private List<VehicleList> getVehicleListsByCallingSearch() {
    ResponseEntity<List<VehicleList>> searchResponse =
        testRestTemplate.exchange(
            "http://localhost:" + port + "/search",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<VehicleList>>() {});
    return searchResponse.getBody();
  }

  @Test
  void shouldThrowBadRequestIfDealerIdNotSend() {

    Vehicle vehicle = new Vehicle();
    vehicle.setCode("1");
    vehicle.setColor("red");
    vehicle.setMake("mercedes");
    vehicle.setKw(123);
    vehicle.setModel("a180");
    vehicle.setYear(2016);

    VehicleList vehicleList = new VehicleList();
    vehicleList.setVehicles(List.of(vehicle));

    ResponseEntity<ResponseMessage> response =
        testRestTemplate.postForEntity(
            ("http://localhost:" + port + "/vehicle_listing"), vehicleList, ResponseMessage.class);

    // Expect Ok
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
