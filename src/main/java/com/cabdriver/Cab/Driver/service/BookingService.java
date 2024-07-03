package com.cabdriver.Cab.Driver.service;

import com.cabdriver.Cab.Driver.exceptions.InvalidOperationException;
import com.cabdriver.Cab.Driver.exceptions.ResourceDoesNotExistException;
import com.cabdriver.Cab.Driver.exceptions.UserNotFound;
import com.cabdriver.Cab.Driver.models.AppUser;
import com.cabdriver.Cab.Driver.models.Booking;
import com.cabdriver.Cab.Driver.models.Customer;
import com.cabdriver.Cab.Driver.models.Driver;
import com.cabdriver.Cab.Driver.repository.BookingRepository;
import com.cabdriver.Cab.Driver.repository.DriverRepository;
import com.cabdriver.Cab.Driver.responsebody.BookingResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    CustomerService customerService;

    @Autowired
    DriverService driverService;

    @Autowired
    BookingRepository bookingRepository;

    public void handleCustomerRequest(String startingLocation, String endingLocation, int customerId){

        Customer customer = customerService.getCustomerById(customerId);
        if(customer == null){
            throw new UserNotFound(String.format("User with id %s does not exist", customerId));
        }

        // if customer is present we need to create booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setStatus("Draft");
        booking.setBillAmount(0);
        booking.setStartingLocation(startingLocation);
        booking.setEndingLocation(endingLocation);

        bookingRepository.save(booking);
    }

    public List<BookingResponseBody> getBookingByState(String state){
        List<Booking> bookingList = bookingRepository.getBookingByStatus(state);

        List<BookingResponseBody> bookingResponseBodyList = new ArrayList<>();
        for(Booking booking : bookingList){
            BookingResponseBody bookingResponseBody = new BookingResponseBody();
            bookingResponseBody.setBookingID(booking.getId());
            bookingResponseBody.setCustomerID(booking.getCustomer().getId());
            bookingResponseBody.setCustomerName(booking.getCustomer().getFirstName());
            bookingResponseBody.setStartingLocation(booking.getStartingLocation());
            bookingResponseBody.setEndingLocation(booking.getEndingLocation());
            bookingResponseBody.setBillingAmount(booking.getBillAmount());
            bookingResponseBody.setStatus(booking.getStatus());
            bookingResponseBodyList.add(bookingResponseBody);
        }

        return bookingResponseBodyList;
    }

    public String updateBooking(String operation, String email, Integer bookingId){

        // we need to identify is this the customer email or is this a driver email
        Customer customer = customerService.getCustomerByEmail(email);
        Driver driver = driverService.getDriverByEmail(email);
        String userType = "";
        Integer userId = -1;
        AppUser user = null;
        if(customer != null){
            userId = customer.getId();
            userType = "CUSTOMER";
            user = customer;
        } else if (driver != null) {
            userId = driver.getId();
            userType = "DRIVER";
            user = driver;
        }else{
            throw new UserNotFound(String.format("User with id %d doesnt exist",userId));
        }

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if(booking == null){
            throw new ResourceDoesNotExistException(String.format("Booking with id %d does not exists in system", bookingId));
        }

        if(operation.equals("ACCEPT")){
            if(userType.equals("CUSTOMER")){
                throw new InvalidOperationException(String.format("Customer cannot accept rides"));
            }
            // driver to accept the ride
            booking.setDriver(driver);
            booking.setStatus("ACCEPTED");
            booking.setBillAmount(100);
            bookingRepository.save(booking);
            return String.format("Driver with id %d accepted the booking with id %d", userId, bookingId);
        }else if(operation.equals("CANCEL")){
            if(userType.equals("CUSTOMER")){
                if(booking.getCustomer().getId() == userId){
                    booking.setStatus("CANCELlED");
                    bookingRepository.save(booking);
                    return String.format("Customer with id %d cancelled ride with booking id %d", userId, bookingId);
                }else{
                    throw new InvalidOperationException(String.format("Customer with id %d is not allowed to cancel booking with id %d", userId, bookingId));
                }

            } else if (userType.equals("DRIVER")) {
                if(booking.getDriver().getId() == userId){
                    booking.setStatus("CANCELLED");
                    bookingRepository.save(booking);
                    return String.format("Driver with id %d cancelled booking with id %d", userId, bookingId);
                }else{
                    throw new InvalidOperationException(String.format("Driver with %d is not allowed to cancel booking with id%d", userId, bookingId));
                }
            }
        }
        return "";
    }
}
