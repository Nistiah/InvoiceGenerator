package org.example.invoicegenerator;

public record Product(String name, String pkwiu, UnitOfMeasure unitOfMeasure, double quantity, double pricePerUnit,
                      double vat) {
}
