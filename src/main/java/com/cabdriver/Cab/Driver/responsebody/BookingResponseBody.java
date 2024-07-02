package com.cabdriver.Cab.Driver.responsebody;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseBody {
    private int bookingID;
    private int customerID;
    private String customerName;
    private String startingLocation;
    private String endingLocation;
    private int billingAmount;
    private String status;
}
