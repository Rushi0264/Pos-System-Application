package com.example.pos.system.service.impl;


import com.example.pos.system.modal.Order;
import com.example.pos.system.modal.OrderItem;
import com.example.pos.system.repository.OrderRepository;
import com.example.pos.system.service.InvoiceService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final OrderRepository orderRepository;

    @Override
    public byte[] generateInvoice(Long orderId) throws Exception {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.GRAY);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
        Font valueBoldFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");

        // ================= HEADER =================

        Paragraph title = new Paragraph("Order #" + order.getId(), titleFont);
        title.setAlignment(Element.ALIGN_LEFT);
        document.add(title);

        document.add(new Paragraph(" "));

        // ================= ORDER INFO GRID (2 label-value pairs per row) =================

        PdfPTable info = new PdfPTable(4);
        info.setWidthPercentage(100);
        info.setWidths(new float[]{1.3f, 2f, 1.3f, 2f});
        info.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

        addPair(info, labelFont, valueFont,
                "Order ID", String.valueOf(order.getId()),
                "Status", order.getStatus().name());

        addPair(info, labelFont, valueFont,
                "Payment Type", order.getPaymentType().name(),
                "Subtotal", "\u20B9" + String.format("%.0f", order.getSubtotal()));

        addPair(info, labelFont, valueFont,
                "Tax", "\u20B9" + String.format("%.2f", order.getTaxAmount()),
                "Discount", "\u20B9" + String.format("%.0f", order.getDiscountAmount()));

        addPair(info, labelFont, valueBoldFont,
                "Total Amount", "\u20B9" + String.format("%.2f", order.getTotalAmount()),
                "Branch", order.getBranch() != null ? order.getBranch().getName() : "-");

        addPair(info, labelFont, valueFont,
                "Cashier",
                order.getCashier() != null
                        ? (order.getCashier().getFullName() != null
                        ? order.getCashier().getFullName()
                        : order.getCashier().getEmail())
                        : "-",
                "Customer",
                order.getCustomer() != null ? order.getCustomer().getFullName() : "Walk-in");

        addPair(info, labelFont, valueFont,
                "Customer Phone",
                order.getCustomer() != null ? order.getCustomer().getPhone() : "-",
                "Created At",
                order.getCreatedAt().format(formatter));

        document.add(info);

        document.add(new Paragraph(" "));

        // ================= PRODUCT TABLE =================

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 1.5f, 2, 2});

        addHeader(table, "Product", headerFont);
        addHeader(table, "Quantity", headerFont);
        addHeader(table, "Price", headerFont);
        addHeader(table, "Subtotal", headerFont);

        for (OrderItem item : order.getItems()) {

            table.addCell(new Phrase(item.getProduct().getName(), valueFont));
            table.addCell(new Phrase(String.valueOf(item.getQuantity()), valueFont));
            table.addCell(new Phrase("\u20B9" + String.format("%.0f", item.getPrice()), valueFont));
            table.addCell(new Phrase(
                    "\u20B9" + String.format("%.0f", item.getPrice() * item.getQuantity()),
                    valueFont));
        }

        document.add(table);

        document.add(new Paragraph(" "));

        // ================= PAYABLE AMOUNT SUMMARY =================

        PdfPTable summary = new PdfPTable(2);
        summary.setWidthPercentage(45);
        summary.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summary.setWidths(new float[]{2, 2});

        addSummaryRow(summary, "Subtotal",
                "\u20B9" + String.format("%.2f", order.getSubtotal()), labelFont, valueFont);

        addSummaryRow(summary, "Tax",
                "\u20B9" + String.format("%.2f", order.getTaxAmount()), labelFont, valueFont);

        addSummaryRow(summary, "Discount",
                "-\u20B9" + String.format("%.2f", order.getDiscountAmount()), labelFont, valueFont);

        PdfPCell totalLabel = new PdfPCell(new Phrase("Payable Amount", headerFont));
        PdfPCell totalValue = new PdfPCell(
                new Phrase("\u20B9" + String.format("%.2f", order.getTotalAmount()), headerFont)
        );

        totalLabel.setPadding(6);
        totalLabel.setBorderColor(BaseColor.LIGHT_GRAY);
        totalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);

        totalValue.setPadding(6);
        totalValue.setBorderColor(BaseColor.LIGHT_GRAY);
        totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

        summary.addCell(totalLabel);
        summary.addCell(totalValue);

        document.add(summary);

        document.close();

        return out.toByteArray();
    }

    private void addSummaryRow(PdfPTable table, String label, String value,
                               Font labelFont, Font valueFont) {

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(6);
        labelCell.setBorderColor(BaseColor.LIGHT_GRAY);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(6);
        valueCell.setBorderColor(BaseColor.LIGHT_GRAY);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addHeader(PdfPTable table, String text, Font font) {

        PdfPCell cell = new PdfPCell(new Phrase(text, font));

        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBackgroundColor(new BaseColor(245, 245, 245));
        cell.setPadding(6);

        table.addCell(cell);
    }

    private void addPair(PdfPTable table, Font labelFont, Font valueFont,
                         String label1, String value1,
                         String label2, String value2) {

        table.addCell(cell(label1, labelFont, false));
        table.addCell(cell(value1, valueFont, false));
        table.addCell(cell(label2, labelFont, false));
        table.addCell(cell(value2, valueFont, false));
    }

    private PdfPCell cell(String text, Font font, boolean center) {

        PdfPCell cell = new PdfPCell(new Phrase(text, font));

        cell.setPadding(6);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);

        if (center) {
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        }

        return cell;
    }
}