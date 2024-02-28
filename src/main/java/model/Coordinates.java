package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Coordinates {
    @JsonProperty("name")
    private String name;
    @JsonProperty("lat")
    private double latitude;
    @JsonProperty("lon")
    private double longitude;
}
