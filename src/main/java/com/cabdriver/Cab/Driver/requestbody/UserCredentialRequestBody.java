package com.cabdriver.Cab.Driver.requestbody;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserCredentialRequestBody {
    private String email;
    private String password;
}
