package cn.ssq.ticket.system.entity.pp;

import cn.ssq.ticket.system.util.HuiFuUtil;

import java.io.Serializable;

public class HuiFuPay implements Serializable{

	private static final long serialVersionUID = 1L;
	
	
	private String Version="10" ;
	
	private String CmdId= "Buy";
	
	private String  EtclientFlag="Y";
	
	private String  EtclientUsrId= HuiFuUtil.payUsrId;
	
	private String  TrueGateId="61";
	
	private String  MerId=HuiFuUtil.merId;
	
	private String CurCode="RMB";
	
	private String  RetUrl="http://www.airpp.net/airs/common/vepayRtn!chinapnrPayNotifySync.shtml";
	
	private String  BgRetUrl="http://www.airpp.net/airs/common/vepayRtn!chinapnrPayNotify.shtml";

	private String OrdId;
	
	private String OrdAmt;
	
	private String MerPriv="";
	
	private String  ChkValue="";
	
	private String  PayInfo;
	
	private String  PartnerCode="F0";

	private String PnrNum;
	
	
	public String getBgRetUrl() {
		return BgRetUrl;
	}

	public void setBgRetUrl(String bgRetUrl) {
		BgRetUrl = bgRetUrl;
	}

	public String getPnrNum() {
		return PnrNum;
	}

	public void setPnrNum(String pnrNum) {
		PnrNum = pnrNum;
	}

	public String getMerPriv() {
		return MerPriv;
	}

	public void setMerPriv(String merPriv) {
		MerPriv = merPriv;
	}

	public String getCurCode() {
		return CurCode;
	}

	public void setCurCode(String curCode) {
		CurCode = curCode;
	}

	public String getVersion() {
		return Version;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public String getCmdId() {
		return CmdId;
	}

	public void setCmdId(String cmdId) {
		CmdId = cmdId;
	}

	public String getEtclientFlag() {
		return EtclientFlag;
	}

	public void setEtclientFlag(String etclientFlag) {
		EtclientFlag = etclientFlag;
	}

	public String getEtclientUsrId() {
		return EtclientUsrId;
	}

	public void setEtclientUsrId(String etclientUsrId) {
		EtclientUsrId = etclientUsrId;
	}

	public String getTrueGateId() {
		return TrueGateId;
	}

	public void setTrueGateId(String trueGateId) {
		TrueGateId = trueGateId;
	}

	public String getMerId() {
		return MerId;
	}

	public void setMerId(String merId) {
		MerId = merId;
	}

	public String getOrdId() {
		return OrdId;
	}

	public void setOrdId(String ordId) {
		OrdId = ordId;
	}

	public String getOrdAmt() {
		return OrdAmt;
	}

	public void setOrdAmt(String ordAmt) {
		OrdAmt = ordAmt;
	}

	public String getRetUrl() {
		return RetUrl;
	}

	public void setRetUrl(String retUrl) {
		RetUrl = retUrl;
	}

	public String getChkValue() {
		return ChkValue;
	}

	public void setChkValue(String chkValue) {
		ChkValue = chkValue;
	}

	public String getPayInfo() {
		return PayInfo;
	}

	public void setPayInfo(String payInfo) {
		PayInfo = payInfo;
	}

	public String getPartnerCode() {
		return PartnerCode;
	}

	public void setPartnerCode(String partnerCode) {
		PartnerCode = partnerCode;
	}

	@Override
	public String toString() {
		return "HuiFuPay [Version=" + Version + ", CmdId=" + CmdId + ", EtclientFlag=" + EtclientFlag
				+ ", EtclientUsrId=" + EtclientUsrId + ", TrueGateId=" + TrueGateId + ", MerId=" + MerId + ", CurCode="
				+ CurCode + ", RetUrl=" + RetUrl + ", BgRetUrl=" + BgRetUrl + ", OrdId=" + OrdId + ", OrdAmt=" + OrdAmt
				+ ", MerPriv=" + MerPriv + ", ChkValue=" + ChkValue + ", PayInfo=" + PayInfo + ", PartnerCode="
				+ PartnerCode + ", PnrNum=" + PnrNum + "]";
	}
	
}
