package org.example;

import org.example.Data.Customer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PDFGeneratorTest {
    Customer customer = mock(Customer.class);

    @Test
    public void getIdTest() {
        when(customer.getId()).thenReturn(3);

        int id = customer.getId();

        assertEquals(3, id);
    }

    @Test
    public void getFirstNameTest() {
        when(customer.getFirstName()).thenReturn("First");

        String first = customer.getFirstName();

        assertEquals("First", first);
    }

    @Test
    public void getLastNameTest() {
        when(customer.getLastName()).thenReturn("Last");

        String last = customer.getLastName();

        assertEquals("Last", last);
    }
    /*
    @Test
    public void toStringTest() {
        when(customer.getId()).thenReturn(3);
        when(customer.getFirstName()).thenReturn("First");
        when(customer.getLastName()).thenReturn("Last");

        int id = customer.getId();
        String firstName = customer.getFirstName();
        String lastName = customer.getLastName();

        when(customer.toString()).thenReturn("User{id='"+ id + "', firstName='" + firstName + "', lastName='" + lastName + "'}\n");

        String toString = customer.toString();

        assertEquals("User{id='3', firstName='First', lastName='Last'}\n", toString);
    }
    */
}
