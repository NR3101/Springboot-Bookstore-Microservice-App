package com.neeraj.bookstorewebapp.clients.orders;

public record OrderConfirmationDTO(String orderNumber, OrderStatus status) {}
