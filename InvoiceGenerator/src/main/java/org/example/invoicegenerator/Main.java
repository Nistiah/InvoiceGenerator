package org.example.invoicegenerator;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        List<Product> products = new ArrayList<>();
        products.add(new Product("Produkt 1", null, UnitOfMeasure.GRAM, 1, 100, 0.23));
        products.add(new Product("Produkt 2", null, UnitOfMeasure.GRAM, 2, 200, 0.23));
        products.add(new Product("Produkt 3", null, UnitOfMeasure.GRAM, 3.52, 300, 0.23));
        products.add(new Product("Produkt 4", null, UnitOfMeasure.GRAM, 4, 400, 0.23));
        products.add(new Product("Produkt 5", null, UnitOfMeasure.GRAM, 5, 500, 0.23));
        products.add(new Product("Produkt 6", null, UnitOfMeasure.GRAM, 6, 600, 0.23));
        products.add(new Product("Produkt 7", null, UnitOfMeasure.GRAM, 7.11, 700, 0.23));

        SellerInfo sellerInfo = new SellerInfo("Jan Kowalski", "ul. Kowalska 1", "Warszawa", "00-000", "1234567890", "12345678901234567890123456", "123456789");
        BuyerInfo buyerInfo = new BuyerInfo("Jan Nowak", "ul. Nowaka 1", "Warszawa", "00-000", "0987654321");
        ReceiverInfo receiverInfo = new ReceiverInfo("Dupsko", "ul. dupska 1", "pcim", "00-000");

        InvoiceGenerator invoiceGenerator = new InvoiceGenerator(LocalDate.of(2023, Month.JULY,22), 3712L, sellerInfo, buyerInfo, products);
        invoiceGenerator.generateInvoice("output.pdf");

        InvoiceGenerator invoiceGenerator2 = new InvoiceGenerator(LocalDate.of(2023, Month.JULY,22), 3712L, sellerInfo,buyerInfo, receiverInfo, products);
        invoiceGenerator2.generateInvoice("output2.pdf");
    }
}