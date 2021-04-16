package cn.ssq.ticket.system.service;

import cn.ssq.ticket.system.entity.TOrderCtrip;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TOrderCtripService extends IService<TOrderCtrip> {

    // 添加
    int add(TOrderCtrip orderCtrip);
}
