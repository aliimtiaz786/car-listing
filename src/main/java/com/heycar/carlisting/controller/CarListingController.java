package com.heycar.carlisting.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.heycar.carlisting.helper.CSVHelper;
import com.heycar.carlisting.message.ResponseMessage;
import com.heycar.carlisting.model.VehicleList;
import com.heycar.carlisting.service.VehicleListingService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CarListingController {

  @Autowired private final VehicleListingService vehicleListingService;

  @PostMapping(path = "/upload_csv/{dealer_id}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ResponseMessage> uploadVehicleListingsViaCSV(
      @PathVariable("dealer_id") Integer dealerId, @RequestParam("file") MultipartFile file) {
    var message = "";

    if (dealerId == null) {
      message = "dealer_id cannot be null or empty!";
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }

    if (CSVHelper.hasCSVFormat(file)) {
      try {
        vehicleListingService.save(file, dealerId);
        message = "Uploaded the file successfully: " + file.getOriginalFilename();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
      } catch (Exception e) {
        log.error("Error occurred while upload file ", e);
        message = "Could not upload the file: " + file.getOriginalFilename() + "!";
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
            .body(new ResponseMessage(message));
      }
    }

    message = "Please upload a csv file!";
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
  }

  @PostMapping(path = "/vehicle_listing", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ResponseMessage> uploadVehicleListingsViaJSONRequest(
      @Validated @RequestBody VehicleList vehicleList) {

    var message = "";
    if (!isValidRequest(vehicleList)) {
      message = "dealer_id cannot be null or invalid request sent!";
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }
    try {
      vehicleListingService.save(vehicleList);
      message = "Vehicle Listing uploaded successfully for dealer_id: " + vehicleList.getDealerId();
      return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    } catch (Exception e) {
      log.error(
          "Error occurred while upload vehile listing for dealer_id : {} ",
          vehicleList.getDealerId(),
          e);
      message =
          "Could not upload vehicle listing for dealer_id: " + vehicleList.getDealerId() + "!";
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
          .body(new ResponseMessage(message));
    }
  }

  @GetMapping(path = "/search", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<List<VehicleList>> getAllVehicleListing(
      @RequestParam(value = "make", required = false) String make,
      @RequestParam(value = "model", required = false) String model,
      @RequestParam(value = "year", required = false) Integer year,
      @RequestParam(value = "color", required = false) String color) {
    try {

      List<VehicleList> vehicleListings =
          vehicleListingService.getVehicleListings(make, model, year, color);
      if (vehicleListings.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(vehicleListings, HttpStatus.OK);
    } catch (Exception e) {
      log.error("Error occurred while getting all the vehicle listings ", e);
      return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private boolean isValidRequest(VehicleList vehicleList) {
    return vehicleList != null
        && vehicleList.getDealerId() != null
        && !CollectionUtils.isEmpty(vehicleList.getVehicles());
  }
}
