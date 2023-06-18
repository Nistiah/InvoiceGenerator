package org.example.invoicegenerator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

record Product(String name, int quantity, double price, double vat) {}

record SellerInfo(String name, String street, String city, String postalCode, String NIP, String accountNumber) {}

record BuyerInfo(String name, String street, String city, String postalCode, String NIP) {}

public class InvoiceGenerator {

    LocalDate issueDate;
    Long invoiceNumber;
    SellerInfo sellerInfo;
    BuyerInfo buyerInfo;
    List<Product> products;

    public InvoiceGenerator(LocalDate issueDate, Long invoiceNumber, SellerInfo sellerInfo, BuyerInfo buyerInfo, List<Product> products) {
        this.issueDate = issueDate;
        this.invoiceNumber = invoiceNumber;
        this.sellerInfo = sellerInfo;
        this.buyerInfo = buyerInfo;
        this.products = products;
    }

    public void generateInvoice(String fileName) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float margin = 50;
            float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
            float yStart = page.getMediaBox().getHeight() - margin;
            float yPosition = yStart;
            float tableHeight = 100f;
            float cellMargin = 10f;

            String[][] content = {
                    {"Name", "Age", "City"},
                    {"John Doe", "30", "New York"},
                    {"Jane Smith", "25", "London"},
                    {"Bob Johnson", "40", "Paris"}
            };

            int rows = content.length;
            int cols = content[0].length;

            float rowHeight = tableHeight / (float) rows;
            float tableYLength = rowHeight * (float) rows;
            float cellWidth = tableWidth / (float) cols;
            float tableXLength = cellWidth * (float) cols;

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.setLineWidth(1f);
            contentStream.moveTo(margin, yStart);
            contentStream.lineTo(margin + tableXLength, yStart);
            contentStream.moveTo(margin, yStart - tableYLength);
            contentStream.lineTo(margin + tableXLength, yStart - tableYLength);

            // Draw table content
            for (int i = 0; i <= rows; i++) {
                contentStream.moveTo(margin, yPosition);
                contentStream.lineTo(margin + tableXLength, yPosition);
                yPosition -= rowHeight;
            }

            for (int i = 0; i <= cols; i++) {
                contentStream.moveTo(margin + (cellWidth * i), yStart);
                contentStream.lineTo(margin + (cellWidth * i), yStart - tableYLength);
            }

            contentStream.stroke();

            contentStream.setFont(PDType1Font.HELVETICA, 12);
            float textX, textY;
            textY = yStart - 15;
            for (String[] row : content) {
                textX = margin + cellMargin;
                for (String cell : row) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(textX, textY);
                    contentStream.showText(cell);
                    contentStream.endText();
                    textX += cellWidth;
                }
                textY -= rowHeight;
            }
            contentStream.close();
            document.save(fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
