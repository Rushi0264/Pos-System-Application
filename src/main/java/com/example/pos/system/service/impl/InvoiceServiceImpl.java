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

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Font headingFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 11);

        // ================= HEADER =================

        Paragraph title = new Paragraph("POS SYSTEM INVOICE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));

        PdfPTable info = new PdfPTable(2);
        info.setWidthPercentage(100);
        info.setWidths(new float[]{1,2});

        addRow(info,"Invoice No","INV-" + order.getId());
        addRow(info,"Order ID",String.valueOf(order.getId()));

        addRow(info,"Store",
                order.getBranch()!=null &&
                        order.getBranch().getStore()!=null
                        ? order.getBranch().getStore().getName()
                        : "-");

        addRow(info,"Branch",
                order.getBranch()!=null
                        ? order.getBranch().getName()
                        : "-");

        addRow(info,"Customer",
                order.getCustomer()!=null
                        ? order.getCustomer().getFullName()
                        : "Walk-in Customer");

        addRow(info,"Phone",
                order.getCustomer()!=null
                        ? order.getCustomer().getPhone()
                        : "-");

        addRow(info,"Cashier",
                order.getCashier()!=null
                        ? order.getCashier().getFullName()
                        : "-");

        addRow(info,"Payment",
                order.getPaymentType().name());

        addRow(info,"Status",
                order.getStatus().name());

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");

        addRow(info,"Date",
                order.getCreatedAt().format(formatter));

        document.add(info);

        document.add(new Paragraph(" "));

        // ================= PRODUCT TABLE =================

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4,1,2,2});

        addHeader(table,"Product");
        addHeader(table,"Qty");
        addHeader(table,"Unit Price");
        addHeader(table,"Subtotal");

        for(OrderItem item : order.getItems()){

            table.addCell(item.getProduct().getName());

            table.addCell(String.valueOf(item.getQuantity()));

            table.addCell("₹" + item.getPrice());

            table.addCell(
                    "₹" + (item.getPrice() * item.getQuantity())
            );
        }

        document.add(table);

        document.add(new Paragraph(" "));

// ================= BILL SUMMARY =================

        PdfPTable summary = new PdfPTable(2);
        summary.setWidthPercentage(40);
        summary.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summary.setWidths(new float[]{2, 2});

        addRow(summary, "Subtotal", "₹" + String.format("%.2f", order.getSubtotal()));

        addRow(summary, "GST (18%)", "₹" + String.format("%.2f", order.getTaxAmount()));

        addRow(summary, "Discount", "₹" + String.format("%.2f", order.getDiscountAmount()));

        PdfPCell totalLabel = new PdfPCell(new Phrase("Grand Total", headingFont));
        PdfPCell totalValue = new PdfPCell(
                new Phrase("₹" + String.format("%.2f", order.getTotalAmount()), headingFont)
        );

        totalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
        totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

        summary.addCell(totalLabel);
        summary.addCell(totalValue);

        document.add(summary);

        document.add(new Paragraph(" "));

        //document.add(new Paragraph(" "));

        Paragraph thanks = new Paragraph(
                "Thank You For Shopping With Us!\nVisit Again.",
                headingFont
        );

        thanks.setAlignment(Element.ALIGN_CENTER);

        document.add(thanks);

        document.close();

        return out.toByteArray();
    }

    private void addHeader(PdfPTable table,String text){

        PdfPCell cell=new PdfPCell(new Phrase(text));

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);

        table.addCell(cell);
    }

    private void addRow(PdfPTable table,String key,String value){

        table.addCell(new Phrase(key));

        table.addCell(new Phrase(value));
    }
}