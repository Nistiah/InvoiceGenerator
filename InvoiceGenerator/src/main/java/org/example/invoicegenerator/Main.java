package org.example.invoicegenerator;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        InvoiceGenerator invoiceGenerator = new InvoiceGenerator(null, null, null, null, null);
        invoiceGenerator.generateInvoice("output.pdf");
    }
}