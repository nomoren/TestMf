package cn.ssq.ticket.system.entity.SHSL;


import lombok.Data;

import java.io.Serializable;


/**
 * The type Order msg.
 */

@Data
public class AskPriceDTO implements Serializable{
    private static final long serialVersionUID = -4420072833988739933L;
    private String dpt;
    private String arr;
    private String date;
    private String flightNo;
    private String cabin;
    private String ticketPrice;


}
