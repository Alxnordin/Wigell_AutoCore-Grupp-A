package com.wac.autocore.model;

public class InvoiceLine {

   private int invoiceLineId;
   private int invoiceId;
   private String serviceItemDescription;
   private double serviceItemPrice;
   private double discount;
   private double finalPricePerServiceAfterDiscount;

   public InvoiceLine(int invoiceLineId, int invoiceId, String serviceItemDescription,
                      double serviceItemPrice,
                      double discount,
                      double finalPricePerServiceAfterDiscount) {

       this.invoiceLineId = invoiceLineId;
       this.invoiceId = invoiceId;
       this.serviceItemDescription = serviceItemDescription;
       this.serviceItemPrice = serviceItemPrice;
       this.discount = discount;
       this.finalPricePerServiceAfterDiscount = finalPricePerServiceAfterDiscount;
   }

   public int getInvoiceId() {
       return invoiceId;
   }

   public void setInvoiceId(int invoiceId) {
       this.invoiceId = invoiceId;
   }

   public int getInvoiceLineId() {
       return invoiceLineId;
   }

   public void setInvoiceLineId(int invoiceLineId) {
       this.invoiceLineId = invoiceLineId;
   }

   public String getServiceItemDescription() {
        return serviceItemDescription;
   }

   public void setServiceItemDescription(String serviceItemDescription) {
        this.serviceItemDescription = serviceItemDescription;
   }

   public double getServiceItemPrice() {
       return serviceItemPrice;
   }

   public void setServiceItemPrice(double serviceItemPrice) {
       this.serviceItemPrice = serviceItemPrice;
   }

   public double getDiscount() {
       return discount;
   }

   public void setDiscount(double discount) {
       this.discount = discount;
   }

   public double getFinalPricePerServiceAfterDiscount() {
       return finalPricePerServiceAfterDiscount;
   }

   public void setFinalPricePerServiceAfterDiscount(double finalPricePerServiceAfterDiscount) {
       this.finalPricePerServiceAfterDiscount = finalPricePerServiceAfterDiscount;
   }


}
