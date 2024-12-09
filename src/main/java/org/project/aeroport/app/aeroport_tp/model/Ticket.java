package org.project.aeroport.app.aeroport_tp.model;

public class Ticket {
    private int ticketId;
    private String clientName;
    private String seatNumber;
    private String flightInfo;
    private String status;

    public Ticket(int ticketId, String clientName, String seatNumber, String flightInfo, String status) {
        this.ticketId = ticketId;
        this.clientName = clientName;
        this.seatNumber = seatNumber;
        this.flightInfo = flightInfo;
        this.status = status;
    }

    public int getTicketId() { return ticketId; }
    public String getClientName() { return clientName; }
    public String getSeatNumber() { return seatNumber; }
    public String getFlightInfo() { return flightInfo; }
    public String getStatus() { return status; }
}
