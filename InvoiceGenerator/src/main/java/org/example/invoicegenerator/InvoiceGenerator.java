package org.example.invoicegenerator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;



record Product(String name, String pkwiu, UnitOfMeasure unitOfMeasure, double quantity, double pricePerUnit, double vat) {
}

record SellerInfo(String name, String street, String city, String postalCode, String NIP, String accountNumber, String phoneNumber) {
}

record BuyerInfo(String name, String street, String city, String postalCode, String NIP) {
}

record ReceiverInfo(String name, String street, String city, String postalCode) {
}

public class InvoiceGenerator {
    LocalDate issueDate;
    Long invoiceNumber;
    SellerInfo sellerInfo;
    BuyerInfo buyerInfo;
    List<Product> products;

    ReceiverInfo receiverInfo;

    public InvoiceGenerator(LocalDate issueDate, Long invoiceNumber, SellerInfo sellerInfo, BuyerInfo buyerInfo, List<Product> products) {
        this.issueDate = issueDate;
        this.invoiceNumber = invoiceNumber;
        this.sellerInfo = sellerInfo;
        this.buyerInfo = buyerInfo;
        this.products = products;
    }

    public InvoiceGenerator(LocalDate issueDate, Long invoiceNumber, SellerInfo sellerInfo, BuyerInfo buyerInfo, ReceiverInfo receiverInfo, List<Product> products) {
        this.issueDate = issueDate;
        this.invoiceNumber = invoiceNumber;
        this.sellerInfo = sellerInfo;
        this.buyerInfo = buyerInfo;
        this.products = products;
        this.receiverInfo = receiverInfo;
    }

    public void generateInvoice(String fileName) {
        int productDisplayed = 0;
        try (PDDocument document = new PDDocument()) {
            while (productDisplayed < products.size()) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                //Numer faktury
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.beginText();
                float textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth("Faktura nr " + invoiceNumber + "/ " + issueDate.getYear()) / 1000 * 20;
                float textX = ((page.getMediaBox().getWidth() / 2) - (textWidth / 2));
                float textY = page.getMediaBox().getHeight() - 40;
                contentStream.newLineAtOffset(textX, textY);
                contentStream.showText("Faktura nr " + invoiceNumber + " / " + issueDate.getYear());
                contentStream.endText();

                //Data wystawienia
                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.beginText();
                textWidth = PDType1Font.HELVETICA.getStringWidth("Data wystawienia: " + issueDate) / 1000 * 10;
                textX = (page.getMediaBox().getWidth() - textWidth - 60);
                textY = page.getMediaBox().getHeight() - 65;
                contentStream.newLineAtOffset(textX, textY);
                contentStream.showText("Data wystawienia: " + issueDate);
                contentStream.endText();

                //Miejsce wystawienia
                contentStream.beginText();
                textX = (page.getMediaBox().getWidth() - textWidth - 60);
                textY = page.getMediaBox().getHeight() - 85;
                contentStream.newLineAtOffset(textX, textY);
                contentStream.showText("Miejsce wystawienia: " + sellerInfo.city());
                contentStream.endText();

                //Linia pod nagłówkiem
                writeLine(1f, 50, page.getMediaBox().getWidth() - 50, page.getMediaBox().getHeight() - 100,  contentStream);


                //Sprzedawca
                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - 120);
                contentStream.showText("Sprzedawca:");
                contentStream.endText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - 132);
                contentStream.showText(sellerInfo.name());
                contentStream.endText();
                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - 144);
                contentStream.showText(sellerInfo.street());
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - 156);
                contentStream.showText(sellerInfo.postalCode() + " " + sellerInfo.city());
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - 168);
                contentStream.showText("NIP: " + sellerInfo.NIP());
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - 180);
                contentStream.showText("Tel: " + sellerInfo.phoneNumber());
                contentStream.endText();

                //Nabywca

                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - textWidth - 60, page.getMediaBox().getHeight() - 120);
                contentStream.showText("Nabywca:");
                contentStream.endText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - textWidth - 60, page.getMediaBox().getHeight() - 132);
                contentStream.showText(buyerInfo.name());
                contentStream.endText();
                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - textWidth - 60, page.getMediaBox().getHeight() - 144);
                contentStream.showText(buyerInfo.street());
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - textWidth - 60, page.getMediaBox().getHeight() - 156);
                contentStream.showText(buyerInfo.postalCode() + " " + buyerInfo.city());
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - textWidth - 60, page.getMediaBox().getHeight() - 168);
                contentStream.showText("NIP: " + buyerInfo.NIP());
                contentStream.endText();

                //Odbiorca jezeli
                if(receiverInfo!=null){
                    contentStream.setFont(PDType1Font.HELVETICA, 8);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(page.getMediaBox().getWidth() - textWidth - 60, page.getMediaBox().getHeight() - 190);
                    contentStream.showText("Odbiorca:");
                    contentStream.endText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(page.getMediaBox().getWidth() - textWidth - 60, page.getMediaBox().getHeight() - 202);
                    contentStream.showText(receiverInfo.name());
                    contentStream.endText();
                    contentStream.setFont(PDType1Font.HELVETICA, 8);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(page.getMediaBox().getWidth() - textWidth - 60, page.getMediaBox().getHeight() - 214);
                    contentStream.showText(receiverInfo.street());
                    contentStream.endText();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(page.getMediaBox().getWidth() - textWidth - 60, page.getMediaBox().getHeight() - 226);
                    contentStream.showText(receiverInfo.postalCode() + " " + receiverInfo.city());
                    contentStream.endText();
                }



                //Tabela
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - 250);
                contentStream.showText("Lp.");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(70, page.getMediaBox().getHeight() - 250);
                contentStream.showText("Nazwa towaru lub uslugi");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(190, page.getMediaBox().getHeight() - 250);
                contentStream.showText("PKWiU");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(230, page.getMediaBox().getHeight() - 250);
                contentStream.showText("Jm");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(270, page.getMediaBox().getHeight() - 250);
                contentStream.showText("Ilosc");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(300, page.getMediaBox().getHeight() - 250);
                contentStream.showText("Cena netto");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(340, page.getMediaBox().getHeight() - 250);
                contentStream.showText("Wartosc netto");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(390, page.getMediaBox().getHeight() - 250);
                contentStream.showText("Stawka VAT");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(440, page.getMediaBox().getHeight() - 250);
                contentStream.showText("Kwota VAT");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(495, page.getMediaBox().getHeight() - 250);
                contentStream.showText("Wartosc brutto");
                contentStream.endText();


                int productMargin = 260;

                writeLine(1f, 50, page.getMediaBox().getWidth() - 50, page.getMediaBox().getHeight() - productMargin,  contentStream);
                productMargin += 10;

                double bruttoPrice = 0;
                double nettoPrice = 0;
                double bruttoSum = 0;
                double nettoSum = 0;
                double vat = 0;
                double vatSum = 0;

                double sumOfIndividualVatSum = 0;
                double sumOfIndividualNettoSum = 0;
                double sumOfIndividualBruttoSum = 0;

                DecimalFormat decimalFormatInt = new DecimalFormat("#");
                DecimalFormat decimalFormatDouble = new DecimalFormat("#.##");

                for(Product product : products){
                    vat = product.vat();
                    bruttoPrice = product.pricePerUnit();
                    nettoPrice = bruttoPrice / (1 + vat);
                    nettoSum = nettoPrice * product.quantity() ;
                    bruttoSum = bruttoPrice * product.quantity();
                    vatSum = bruttoSum - nettoSum;

                    sumOfIndividualVatSum += vatSum;
                    sumOfIndividualNettoSum += nettoSum;
                    sumOfIndividualBruttoSum += bruttoSum;

                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, page.getMediaBox().getHeight() - productMargin);
                    contentStream.showText(String.valueOf(productDisplayed));
                    contentStream.endText();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(70, page.getMediaBox().getHeight() - productMargin);
                    contentStream.showText(product.name());
                    contentStream.endText();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(190, page.getMediaBox().getHeight() - productMargin);
                    try {
                        contentStream.showText(product.pkwiu());
                    } catch (Exception e) {
                        contentStream.showText(" ");
                    }
                    contentStream.endText();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(230, page.getMediaBox().getHeight() - productMargin);
                    contentStream.showText(String.valueOf(product.unitOfMeasure()).toLowerCase());
                    contentStream.endText();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(270, page.getMediaBox().getHeight() - productMargin);
                    contentStream.showText(String.valueOf(product.quantity()));
                    contentStream.endText();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(300, page.getMediaBox().getHeight() - productMargin);
                    contentStream.showText(String.valueOf(decimalFormatDouble.format(nettoPrice)));
                    contentStream.endText();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(340, page.getMediaBox().getHeight() - productMargin);
                    contentStream.showText(String.valueOf(decimalFormatDouble.format(nettoSum)));
                    contentStream.endText();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(390, page.getMediaBox().getHeight() - productMargin);
                    contentStream.showText(String.valueOf(decimalFormatInt.format(vat*100)+"%"));
                    contentStream.endText();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(440, page.getMediaBox().getHeight() - productMargin);
                    contentStream.showText(String.valueOf(decimalFormatDouble.format(vatSum)));
                    contentStream.endText();
                    contentStream.beginText();
                    contentStream.newLineAtOffset(495, page.getMediaBox().getHeight() - productMargin);
                    contentStream.showText(String.valueOf(decimalFormatDouble.format(bruttoSum)));
                    contentStream.endText();
                    productMargin+=10;
                    productDisplayed++;
                }

                writeLine(1f, 50, page.getMediaBox().getWidth() - 50, page.getMediaBox().getHeight() - productMargin,  contentStream);
                productMargin+=20;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(390, page.getMediaBox().getHeight() - productMargin);
                contentStream.showText("Suma netto");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(440, page.getMediaBox().getHeight() - productMargin);
                contentStream.showText("Suma VAT");
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(495, page.getMediaBox().getHeight() - productMargin);
                contentStream.showText("Suma brutto");
                contentStream.endText();

                productMargin+=10;

                writeLine(1f, 370, page.getMediaBox().getWidth() - 50, page.getMediaBox().getHeight() - productMargin,  contentStream);
                productMargin+=10;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(390, page.getMediaBox().getHeight() - productMargin);
                contentStream.showText(String.valueOf(decimalFormatDouble.format(sumOfIndividualNettoSum)));
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(440, page.getMediaBox().getHeight() - productMargin);
                contentStream.showText(String.valueOf(decimalFormatDouble.format(sumOfIndividualVatSum)));
                contentStream.endText();
                contentStream.beginText();
                contentStream.newLineAtOffset(495, page.getMediaBox().getHeight() - productMargin);
                contentStream.showText(String.valueOf(decimalFormatDouble.format(sumOfIndividualBruttoSum)));
                contentStream.endText();


                productMargin+=30;

                contentStream.setFont(PDType1Font.HELVETICA, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(350, page.getMediaBox().getHeight() - productMargin);
                contentStream.showText("Razem do zaplaty: "+decimalFormatDouble.format(sumOfIndividualBruttoSum)+" PLN");
                contentStream.endText();



                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 50);
                contentStream.showText("Dokument wystawiony automatycznie nie wymagajacy podpisu");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 8);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 40);
                contentStream.showText("Podstawa prawna: Ustawa z dnia 11 marca 2014 o podatku od towarow i uslug art.106e");
                contentStream.endText();

                contentStream.close();
            }
            document.save(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLine(float lineWidtg, float fromX, float toX, float y, PDPageContentStream contentStream) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
        contentStream.setLineWidth(lineWidtg);
        contentStream.moveTo(fromX, y);
        contentStream.lineTo(toX, y);
        contentStream.stroke();
    }



    }


