package com.cabdriver.Cab.Driver.service;

import com.cabdriver.Cab.Driver.exceptions.UserNotFound;
import com.cabdriver.Cab.Driver.models.Booking;
import com.cabdriver.Cab.Driver.models.Customer;
import com.cabdriver.Cab.Driver.repository.BookingRepository;
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
}
