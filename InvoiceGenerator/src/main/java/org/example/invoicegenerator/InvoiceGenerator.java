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

record Product(String name, String pkwiu, UnitOfMeasure unitOfMeasure, double quantity, double pricePerUnit, double vat) {}
record SellerInfo(String name, String street, String city, String postalCode, String NIP, String accountNumber, String phoneNumber) {}
record BuyerInfo(String name, String street, String city, String postalCode, String NIP) {}
record ReceiverInfo(String name, String street, String city, String postalCode) {}

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
        DecimalFormat decimalFormatInt = new DecimalFormat("#");
        DecimalFormat decimalFormatDouble = new DecimalFormat("#.##");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            generateHeader(page, contentStream);
            generateBody(decimalFormatInt, decimalFormatDouble, page, contentStream);
            generateFooter(contentStream);

            contentStream.close();
            document.save(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateHeader(PDPage page, PDPageContentStream contentStream) throws IOException {
        //Numer faktury
        writeText((page.getMediaBox().getWidth() / 2) - (PDType1Font.HELVETICA_BOLD.getStringWidth("Faktura nr " + invoiceNumber + "/ " + issueDate.getYear()) / 1000 * 20 / 2), page.getMediaBox().getHeight() - 40, contentStream, "Faktura nr " + invoiceNumber + " / " + issueDate.getYear(), PDType1Font.HELVETICA_BOLD, 20);

        //Data wystawienia
        writeText(page.getMediaBox().getWidth() - PDType1Font.HELVETICA.getStringWidth("Data wystawienia: " + issueDate) / 1000 * 10 - 60, page.getMediaBox().getHeight() - 65, contentStream, "Data wystawienia: " + issueDate, PDType1Font.HELVETICA, 8);

        //Miejsce wystawienia
        writeText(page.getMediaBox().getWidth() - PDType1Font.HELVETICA.getStringWidth("Data wystawienia: " + issueDate) / 1000 * 10 - 60, page.getMediaBox().getHeight() - 85, contentStream, "Miejsce wystawienia: " + sellerInfo.city(), PDType1Font.HELVETICA, 8);

        //Linia pod nagłówkiem
        writeLine(1f, 50, page.getMediaBox().getWidth() - 50, page.getMediaBox().getHeight() - 100, contentStream);

        //Sprzedawca
        writeMultipleTextVertical(50, new float[]{page.getMediaBox().getHeight() - 120, page.getMediaBox().getHeight() - 132, page.getMediaBox().getHeight() - 144, page.getMediaBox().getHeight() - 156, page.getMediaBox().getHeight() - 168, page.getMediaBox().getHeight() - 180}, contentStream, new String[]{"Sprzedawca:", sellerInfo.name(), sellerInfo.street(), sellerInfo.postalCode() + " " + sellerInfo.city(), "NIP: " + sellerInfo.NIP(), "Tel: " + sellerInfo.phoneNumber()}, PDType1Font.HELVETICA, 8);

        //Nabywca
        writeMultipleTextVertical(page.getMediaBox().getWidth() - PDType1Font.HELVETICA.getStringWidth("Data wystawienia: " + issueDate) / 1000 * 10 - 60, new float[]{page.getMediaBox().getHeight() - 120, page.getMediaBox().getHeight() - 132, page.getMediaBox().getHeight() - 144, page.getMediaBox().getHeight() - 156, page.getMediaBox().getHeight() - 168},
                contentStream, new String[]{"Nabywca:", buyerInfo.name(), buyerInfo.street(), buyerInfo.postalCode() + " " + buyerInfo.city(), "NIP: " + buyerInfo.NIP()}, PDType1Font.HELVETICA, 8);

        //Odbiorca jezeli
        if (receiverInfo != null) {
            writeMultipleTextVertical(page.getMediaBox().getWidth() - PDType1Font.HELVETICA.getStringWidth("Data wystawienia: " + issueDate) / 1000 * 10 - 60, new float[]{page.getMediaBox().getHeight() - 190, page.getMediaBox().getHeight() - 202, page.getMediaBox().getHeight() - 214, page.getMediaBox().getHeight() - 226}, contentStream,
                    new String[]{"Odbiorca:", receiverInfo.name(), receiverInfo.street(), receiverInfo.postalCode() + " " + receiverInfo.city()}, PDType1Font.HELVETICA, 8);
        }
    }

    private void generateBody(DecimalFormat decimalFormatInt, DecimalFormat decimalFormatDouble, PDPage page, PDPageContentStream contentStream) throws IOException {
        //Tabela

        double bruttoPrice = 0;
        double nettoPrice = 0;
        double bruttoSum = 0;
        double nettoSum = 0;
        double vat = 0;
        double vatSum = 0;

        writeMultipleTextHorizontal(new float[]{50, 70, 190, 230, 270, 300, 350, 400, 450}, page.getMediaBox().getHeight() - 250, contentStream, new String[]{"Lp.", "Nazwa towaru lub uslugi", "PKWiU", "Jm", "Ilosc", "Cena jedn.", "Wartosc netto", "Stawka VAT", "Kwota VAT"}, PDType1Font.TIMES_ROMAN, 8);
        writeLine(1f, 50, page.getMediaBox().getWidth() - 50, page.getMediaBox().getHeight() - 260, contentStream);

        double sumOfIndividualVatSum = 0;
        double sumOfIndividualNettoSum = 0;
        int productMargin = 270;
        double sumOfIndividualBruttoSum = 0;
        int productDisplayed = 0;
        for (Product product : products) {

            vat = product.vat();
            bruttoPrice = product.pricePerUnit();
            nettoPrice = bruttoPrice / (1 + vat);
            nettoSum = nettoPrice * product.quantity();
            bruttoSum = bruttoPrice * product.quantity();
            vatSum = bruttoSum - nettoSum;

            sumOfIndividualVatSum += vatSum;
            sumOfIndividualNettoSum += nettoSum;
            sumOfIndividualBruttoSum += bruttoSum;

            writeMultipleTextHorizontal(new float[]{50, 70, 190, 230, 270, 300, 340, 390, 440, 495}, page.getMediaBox().getHeight() - productMargin, contentStream, new String[]{String.valueOf(productDisplayed), product.name(), product.pkwiu(), String.valueOf(product.unitOfMeasure()).toLowerCase(),
                    String.valueOf(product.quantity()), decimalFormatDouble.format(nettoPrice), decimalFormatDouble.format(nettoSum),
                    decimalFormatInt.format(vat * 100) + "%", decimalFormatDouble.format(vatSum), decimalFormatDouble.format(bruttoSum)}, PDType1Font.HELVETICA, 8);
            productMargin += 10;
            productDisplayed++;
        }

        writeLine(1f, 50, page.getMediaBox().getWidth() - 50, page.getMediaBox().getHeight() - productMargin, contentStream);
        productMargin += 20;

        writeMultipleTextHorizontal(new float[]{390, 440, 495}, page.getMediaBox().getHeight() - productMargin, contentStream, new String[]{"Suma netto", "Suma VAT", "Suma brutto"}, PDType1Font.HELVETICA, 8);
        productMargin += 10;

        writeLine(1f, 370, page.getMediaBox().getWidth() - 50, page.getMediaBox().getHeight() - productMargin, contentStream);
        productMargin += 10;

        writeMultipleTextHorizontal(new float[]{390, 440, 495}, page.getMediaBox().getHeight() - productMargin, contentStream, new String[]{decimalFormatDouble.format(sumOfIndividualNettoSum), decimalFormatDouble.format(sumOfIndividualVatSum), decimalFormatDouble.format(sumOfIndividualBruttoSum)}, PDType1Font.HELVETICA, 8);
        productMargin += 30;

        writeText(350, page.getMediaBox().getHeight() - productMargin, contentStream, "Razem do zaplaty: " + decimalFormatDouble.format(sumOfIndividualBruttoSum) + " PLN", PDType1Font.HELVETICA, 16);
    }

    private void generateFooter(PDPageContentStream contentStream) throws IOException {
        writeText(50, 50, contentStream, "Dokument wystawiony automatycznie nie wymagajacy podpisu", PDType1Font.HELVETICA, 8);
        writeText(50, 40, contentStream, "Podstawa prawna: Ustawa z dnia 11 marca 2014 o podatku od towarow i uslug art.106e", PDType1Font.HELVETICA, 8);
    }

    public void writeLine(float lineWidtg, float fromX, float toX, float y, PDPageContentStream contentStream) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
        contentStream.setLineWidth(lineWidtg);
        contentStream.moveTo(fromX, y);
        contentStream.lineTo(toX, y);
        contentStream.stroke();
    }

    public void writeText(float x, float y, PDPageContentStream contentStream, String text, PDType1Font font, int fontSize) throws IOException {
        try {
            contentStream.setFont(font, fontSize);
            contentStream.beginText();
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(text);
            contentStream.endText();
        } catch (NullPointerException e) {
            contentStream.endText();
            contentStream.setFont(font, fontSize);
            contentStream.beginText();
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(" ");
            contentStream.endText();
        }
    }

    public void writeMultipleTextHorizontal(float[] x, float y, PDPageContentStream contentStream, String[] text, PDType1Font font, int fontSize) throws IOException {
        for (int i = 0; i < x.length; i++) {
            writeText(x[i], y, contentStream, text[i], font, fontSize);
        }
    }

    public void writeMultipleTextVertical(float x, float[] y, PDPageContentStream contentStream, String[] text, PDType1Font font, int fontSize) throws IOException {
        for (int i = 0; i < y.length; i++) {
            writeText(x, y[i], contentStream, text[i], font, fontSize);
        }
    }
}




