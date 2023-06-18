package org.example.invoicegenerator;

public record ProductGen(String name, String pkwiu, UnitOfMeasure unitOfMeasure, double quantity, double pricePerUnit,
                         double vat) {
}
