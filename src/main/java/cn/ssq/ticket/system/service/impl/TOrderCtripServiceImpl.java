package cn.ssq.ticket.system.service.impl;

import cn.ssq.ticket.system.entity.TOrderCtrip;
import cn.ssq.ticket.system.mapper.TOrderCtripMapper;
import cn.ssq.ticket.system.service.TOrderCtripService;
import cn.ssq.ticket.system.util.LimitQueue;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class TOrderCtripServiceImpl extends ServiceImpl<TOrderCtripMapper, TOrderCtrip> implements TOrderCtripService {

    // 携程订单队列
    public static LimitQueue<String> queue = new LimitQueue<String>(200);

    /**
     * 添加数据
     *
     * @param orderCtrip
     * @return
     */
    @Override
    public int add(TOrderCtrip orderCtrip) {
        // 去重复处理
        if (queue.contains(orderCtrip.getCtripId())) return 0;
        int insert = baseMapper.insert(orderCtrip);
        queue.offer(orderCtrip.getCtripId());
        return insert;
    }
}
