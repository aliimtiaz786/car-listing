package com.heycar.carlisting.helper;

import com.heycar.carlisting.model.Vehicle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class CSVHelper {
  private static final String TYPE = "text/csv";

  public static boolean hasCSVFormat(MultipartFile file) {
    return TYPE.equals(file.getContentType());
  }

  public static List<Vehicle> csvToVehicles(InputStream is) throws IOException {
    try (var fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        var csvParser =
            new CSVParser(
                fileReader,
                CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim()); ) {

      List<Vehicle> vehicles = new ArrayList<>();

      Iterable<CSVRecord> csvRecords = csvParser.getRecords();

      for (CSVRecord csvRecord : csvRecords) {

        if (csvRecord.size() >= csvParser.getHeaderMap().size()) {

          var makeModel = csvRecord.get("make/model");
          var splittedModelMake = makeModel.split("/");
          var make = splittedModelMake[0];
          var model = splittedModelMake[1];
          var vehicle =
              new Vehicle(
                  csvRecord.get("code"),
                  make,
                  model,
                  Integer.parseInt(csvRecord.get("power-in-ps")),
                  Integer.parseInt(csvRecord.get("year")),
                  csvRecord.get("color"),
                  Integer.parseInt(csvRecord.get("price")));

          vehicles.add(vehicle);
        }
      }

      return vehicles;
    } catch (IOException e) {
      log.error("Error occurred while parsing csv file to object", e);
      throw e;
    }
  }
}
