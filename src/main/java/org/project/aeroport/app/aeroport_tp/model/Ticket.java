package org.project.aeroport.app.aeroport_tp.model;

public class Ticket {
    private int ticketId;
    private String clientName;
    private String clientPassportData;
    private int seatNumber;
    private String flightInfo;
    private String status;

    public Ticket(int ticketId, String clientName, String clientPassportData, int seatNumber, String flightInfo, String status) {
        this.ticketId = ticketId;
        this.clientName = clientName;
        this.clientPassportData = clientPassportData;
        this.seatNumber = seatNumber;
        this.flightInfo = flightInfo;
        this.status = status;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientPassportData() {
        return clientPassportData;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getFlightInfo() {
        return flightInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setClientPassportData(String clientPassportData) {
        this.clientPassportData = clientPassportData;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setFlightInfo(String flightInfo) {
        this.flightInfo = flightInfo;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
