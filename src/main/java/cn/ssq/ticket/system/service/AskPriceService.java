package cn.ssq.ticket.system.service;


import cn.ssq.ticket.system.entity.SHSL.AskPriceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 深航商旅报价Feign客户端
 */
@FeignClient(value = "ASK-PRICE")
public interface AskPriceService {

    @RequestMapping("/askPrice")
    @ResponseBody
    AskPriceDTO askPrice(@RequestParam("askPriceDTOJsonStr") String askPriceDTOJsonStr);

//    @RequestMapping(value = "/dept/list", method = RequestMethod.GET)
//     String list();
}
