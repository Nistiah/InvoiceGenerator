# InvoiceGenerator

This Java class `InvoiceGenerator` is part of the `org.example.invoicegenerator` package. It is responsible for generating an invoice in PDF format based on the provided data.

## Class Structure

### Public Methods

- `generateInvoice(String fileName)`: Generates an invoice PDF file with the given file name. The method uses the provided data to populate the invoice content and layout.

### Constructors

- `InvoiceGenerator(LocalDate issueDate, Long invoiceNumber, SellerInfo sellerInfo, BuyerInfo buyerInfo, List<Product> products)`: Constructs an `InvoiceGenerator` object with the provided issue date, invoice number, seller information, buyer information, and a list of products.
- `InvoiceGenerator(LocalDate issueDate, Long invoiceNumber, SellerInfo sellerInfo, BuyerInfo buyerInfo, ReceiverInfo receiverInfo, List<Product> products)`: Constructs an `InvoiceGenerator` object with the provided issue date, invoice number, seller information, buyer information, receiver information, and a list of products.

### Fields

- `issueDate`: Represents the date of the invoice.
- `invoiceNumber`: Represents the invoice number.
- `sellerInfo`: Contains information about the seller.
- `buyerInfo`: Contains information about the buyer.
- `products`: Contains a list of products included in the invoice.
- `receiverInfo`: Contains information about the receiver (optional).

### Private Methods

- `generateHeader(PDPage page, PDPageContentStream contentStream)`: Generates the header section of the invoice, including invoice number, issue date, place of issue, seller information, buyer information, and receiver information (if provided).
- `generateBody(DecimalFormat decimalFormatInt, DecimalFormat decimalFormatDouble, PDPage page, PDPageContentStream contentStream)`: Generates the body section of the invoice, including the table with product details, net amount, VAT amount, and gross amount.
- `generateFooter(PDPageContentStream contentStream)`: Generates the footer section of the invoice, containing additional information and legal references.
- `writeLine(float lineWidth, float fromX, float toX, float y, PDPageContentStream contentStream)`: Draws a line on the PDF document from the specified starting position (`fromX`, `y`) to the ending position (`toX`, `y`).
- `writeText(float x, float y, PDPageContentStream contentStream, String text, PDType1Font font, int fontSize)`: Writes the specified text on the PDF document at the given coordinates (`x`, `y`) using the specified font and font size.
- `writeMultipleTextHorizontal(float[] x, float y, PDPageContentStream contentStream, String[] text, PDType1Font font, int fontSize)`: Writes multiple texts horizontally on the PDF document at the specified `y` coordinate, using the provided `x` coordinates for each text.
- `writeMultipleTextVertical(float x, float[] y, PDPageContentStream contentStream, String[] text, PDType1Font font, int fontSize)`: Writes multiple texts vertically on the PDF document at the specified `x` coordinate, using the provided `y` coordinates for each text.

## Dependencies

This class depends on the following external libraries:

- `org.apache.pdfbox` version X.X.X: Provides the functionality to create and manipulate PDF documents.

Please refer to the documentation of the `org.apache.pdfbox` library for more information on its usage and integration.

[output2.pdf](https://github.com/Nistiah/InvoiceGenerator/files/11781814/output2.pdf)

*Note: The image above shows an example of the generated invoice. The actual appearance may vary based
