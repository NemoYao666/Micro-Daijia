package com.atguigu.daijia.payment.service.impl;

import com.atguigu.daijia.common.constant.MqConst;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.common.service.RabbitService;
import com.atguigu.daijia.driver.client.DriverAccountFeignClient;
import com.atguigu.daijia.model.entity.payment.PaymentInfo;
import com.atguigu.daijia.model.enums.TradeType;
import com.atguigu.daijia.model.form.driver.TransferForm;
import com.atguigu.daijia.model.form.payment.PaymentInfoForm;
import com.atguigu.daijia.model.vo.order.OrderRewardVo;
import com.atguigu.daijia.model.vo.payment.WxPrepayVo;
import com.atguigu.daijia.order.client.OrderInfoFeignClient;
import com.atguigu.daijia.payment.config.WxPayV3Properties;
import com.atguigu.daijia.payment.mapper.PaymentInfoMapper;
import com.atguigu.daijia.payment.service.WxPayService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class WxPayServiceImpl implements WxPayService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

//    @Autowired
//    private RSAAutoCertificateConfig rsaAutoCertificateConfig;

    @Autowired
    private WxPayV3Properties wxPayV3Properties;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private OrderInfoFeignClient orderInfoFeignClient;

    @Autowired
    private DriverAccountFeignClient driverAccountFeignClient;

//    @Override
//    public WxPrepayVo createWxPayment(PaymentInfoForm paymentInfoForm) {
//        try {
//            //支付到一半退出的话，可能就存在记录
//            LambdaQueryWrapper<PaymentInfo> wrapper = new LambdaQueryWrapper<>();
//            wrapper.eq(PaymentInfo::getOrderNo, paymentInfoForm.getOrderNo());
//            PaymentInfo paymentInfo = paymentInfoMapper.selectOne(wrapper);
//            if(null == paymentInfo) {
//                paymentInfo = new PaymentInfo();
//                BeanUtils.copyProperties(paymentInfoForm, paymentInfo);
//                paymentInfo.setPaymentStatus(0);
//                paymentInfoMapper.insert(paymentInfo);
//            }
//
//            // 构建service
//            JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(rsaAutoCertificateConfig).build();
//
//            // request.setXxx(val)设置所需参数，具体参数可见Request定义
//            PrepayRequest request = new PrepayRequest();
//            Amount amount = new Amount();
//            amount.setTotal(paymentInfoForm.getAmount().intValue());
//            request.setAmount(amount);
//            request.setAppid(wxPayV3Properties.getAppid());
//            request.setMchid(wxPayV3Properties.getMerchantId());
//            //string[1,127]
//            String description = paymentInfo.getContent();
//            if(description.length() > 127) {
//                description = description.substring(0, 127);
//            }
//            request.setDescription(paymentInfo.getContent());
//            request.setNotifyUrl(wxPayV3Properties.getNotifyUrl());
//            request.setOutTradeNo(paymentInfo.getOrderNo());
//
//            //获取用户信息
//            Payer payer = new Payer();
//            payer.setOpenid(paymentInfoForm.getCustomerOpenId());
//            request.setPayer(payer);
//
//            //是否指定分账，不指定不能分账
//            SettleInfo settleInfo = new SettleInfo();
//            settleInfo.setProfitSharing(true);
//            request.setSettleInfo(settleInfo);
//
//            // 调用下单方法，得到应答
//            // response包含了调起支付所需的所有参数，可直接用于前端调起支付
//            PrepayWithRequestPaymentResponse response = service.prepayWithRequestPayment(request);
//            log.info("微信支付下单返回参数：{}", JSON.toJSONString(response));
//
//            WxPrepayVo wxPrepayVo = new WxPrepayVo();
//            BeanUtils.copyProperties(response, wxPrepayVo);
//            wxPrepayVo.setTimeStamp(response.getTimeStamp());
//            return wxPrepayVo;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
//        }
//    }

    //绕过微信支付
    @Override
    public WxPrepayVo createWxPayment(PaymentInfoForm paymentInfoForm) {
        try {
            //支付到一半退出的话，可能就存在记录
            LambdaQueryWrapper<PaymentInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PaymentInfo::getOrderNo, paymentInfoForm.getOrderNo());
            PaymentInfo paymentInfo = paymentInfoMapper.selectOne(wrapper);
            if(null == paymentInfo) {
                paymentInfo = new PaymentInfo();
                BeanUtils.copyProperties(paymentInfoForm, paymentInfo);
                paymentInfo.setPaymentStatus(0);
                paymentInfoMapper.insert(paymentInfo);
            }

            WxPrepayVo wxPrepayVo = new WxPrepayVo();
            wxPrepayVo.setAppId(UUID.randomUUID().toString().replace("-" , ""));
            wxPrepayVo.setTimeStamp(String.valueOf(new Date().getTime()));
            wxPrepayVo.setPackageVal(UUID.randomUUID().toString().replace("-" , ""));
            wxPrepayVo.setSignType(UUID.randomUUID().toString().replace("-" , ""));
            wxPrepayVo.setPaySign(UUID.randomUUID().toString().replace("-" , ""));

            return wxPrepayVo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
    }

//    @Override
//    public Boolean queryPayStatus(String orderNo) {
//        // 构建service
//        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(rsaAutoCertificateConfig).build();
//
//        QueryOrderByOutTradeNoRequest queryRequest = new QueryOrderByOutTradeNoRequest();
//        queryRequest.setMchid(wxPayV3Properties.getMerchantId());
//        queryRequest.setOutTradeNo(orderNo);
//
//        try {
//            Transaction transaction = service.queryOrderByOutTradeNo(queryRequest);
//            log.info(JSON.toJSONString(transaction));
//            if(null != transaction && transaction.getTradeState() == Transaction.TradeStateEnum.SUCCESS) {
//                //更改订单状态
//                this.handlePayment(transaction);
//                return true;
//            }
//        } catch (ServiceException e) {
//            // API返回失败, 例如ORDER_NOT_EXISTS
//            System.out.printf("code=[%s], message=[%s]\n", e.getErrorCode(), e.getErrorMessage());
//        }
//        return false;
//    }

    @Override
    public Boolean queryPayStatus(String orderNo) {
        this.handlePayment(orderNo);
        return true;
    }

    @Transactional
    @Override
    public void wxnotify(HttpServletRequest request) {
//        //1.回调通知的验签与解密
//        //从request头信息获取参数
//        //HTTP 头 Wechatpay-Signature
//        // HTTP 头 Wechatpay-Nonce
//        //HTTP 头 Wechatpay-Timestamp
//        //HTTP 头 Wechatpay-Serial
//        //HTTP 头 Wechatpay-Signature-Type
//        //HTTP 请求体 body。切记使用原始报文，不要用 JSON 对象序列化后的字符串，避免验签的 body 和原文不一致。
//        String wechatPaySerial = request.getHeader("Wechatpay-Serial");
//        String nonce = request.getHeader("Wechatpay-Nonce");
//        String timestamp = request.getHeader("Wechatpay-Timestamp");
//        String signature = request.getHeader("Wechatpay-Signature");
//        String requestBody = RequestUtils.readData(request);
//        log.info("wechatPaySerial：{}", wechatPaySerial);
//        log.info("nonce：{}", nonce);
//        log.info("timestamp：{}", timestamp);
//        log.info("signature：{}", signature);
//        log.info("requestBody：{}", requestBody);
//
//        //2.构造 RequestParam
//        RequestParam requestParam = new RequestParam.Builder()
//                .serialNumber(wechatPaySerial)
//                .nonce(nonce)
//                .signature(signature)
//                .timestamp(timestamp)
//                .body(requestBody)
//                .build();
//
//
//        //3.初始化 NotificationParser
//        NotificationParser parser = new NotificationParser(rsaAutoCertificateConfig);
//        //4.以支付通知回调为例，验签、解密并转换成 Transaction
//        Transaction transaction = parser.parse(requestParam, Transaction.class);
//        log.info("成功解析：{}", JSON.toJSONString(transaction));
//        if(null != transaction && transaction.getTradeState() == Transaction.TradeStateEnum.SUCCESS) {
//            //5.处理支付业务
//            this.handlePayment(transaction);
//        }
    }

    //支付成功后续处理
    //使用消息队列处理，在PaymentReceiver里调用
    @GlobalTransactional
    @Override
    public void handleOrder(String orderNo) {
        //1.更改订单支付状态
        orderInfoFeignClient.updateOrderPayStatus(orderNo);

        //2.处理系统奖励，打入司机账户
        OrderRewardVo orderRewardVo = orderInfoFeignClient.getOrderRewardFee(orderNo).getData();
        if(null != orderRewardVo.getRewardFee() && orderRewardVo.getRewardFee().doubleValue() > 0) {
            TransferForm transferForm = new TransferForm();
            transferForm.setTradeNo(orderNo);
            transferForm.setTradeType(TradeType.REWARD.getType());
            transferForm.setContent(TradeType.REWARD.getContent());
            transferForm.setAmount(orderRewardVo.getRewardFee());
            transferForm.setDriverId(orderRewardVo.getDriverId());
            driverAccountFeignClient.transfer(transferForm);
        }

        //TODO:分账
    }

//    public void handlePayment(Transaction transaction) {
//        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(
//                new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, transaction.getOutTradeNo()));
//        if (paymentInfo.getPaymentStatus() == 1) {
//            return;
//        }
//
//        //更新支付信息
//        paymentInfo.setPaymentStatus(1);
//        paymentInfo.setOrderNo(transaction.getOutTradeNo());
//        paymentInfo.setTransactionId(transaction.getTransactionId());
//        paymentInfo.setCallbackTime(new Date());
//        paymentInfo.setCallbackContent(JSON.toJSONString(transaction));
//        paymentInfoMapper.updateById(paymentInfo);
//        // 表示交易成功！
//
//        // 后续更新订单状态！ 使用消息队列！
//        rabbitService.sendMessage(MqConst.EXCHANGE_ORDER, MqConst.ROUTING_PAY_SUCCESS, paymentInfo.getOrderNo());
//    }

    public void handlePayment(String orderId) {

        LambdaQueryWrapper<PaymentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentInfo::getOrderNo, orderId);
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(wrapper);
        if (paymentInfo.getPaymentStatus() == 1) {
            return;
        }

        //更新支付信息
        paymentInfo.setPaymentStatus(1);
        paymentInfo.setOrderNo(orderId);
        paymentInfo.setTransactionId(UUID.randomUUID().toString().replace("-" , ""));
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(UUID.randomUUID().toString().replace("-" , ""));
        paymentInfoMapper.updateById(paymentInfo);
        // 表示交易成功！

        // 后续更新订单状态！ 使用消息队列！
        rabbitService.sendMessage(MqConst.EXCHANGE_ORDER, MqConst.ROUTING_PAY_SUCCESS, paymentInfo.getOrderNo());
    }

}
