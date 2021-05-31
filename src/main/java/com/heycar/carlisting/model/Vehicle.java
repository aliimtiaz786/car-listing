package com.heycar.carlisting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Vehicle {
  @NonNull private String code;
  private String make;
  private String model;
  private Integer kw;
  private Integer year;
  private String color;
  private Integer price;
}
