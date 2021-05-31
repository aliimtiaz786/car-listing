package com.heycar.carlisting.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleList {

  private Integer dealerId;
  private List<Vehicle> vehicles;

}
