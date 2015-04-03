package com.yzx.tcp.packet;

import java.util.Iterator;

import org.bson.BSONObject;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Э������
 * @author xiaozhenhua
 *
 */
public abstract class DataPacket {

	public int headLength = -1; //����ͷ����
	public int length = -1;//�����峤��
	
	public HeadDataModel  mHeadDataPacket;//����ͷ����
	
	public String headJson = "";

	protected DataPacket() {
		mHeadDataPacket = new HeadDataModel();
	}

	/**
	 * ���ݴ��ݵ������ι����봴����Ӧ�����ݰ�
	 * @param mType ��������
	 * @param timeVer �ι�����
	 * @return
	 * @author: xiaozhenhua
	 * @data:2014-4-14 ����8:20:12
	 */
	public static DataPacket CreateDataPack(byte mType, byte timeVer) {
		DataPacket dataPack = null;
		
		switch(mType){
			case PacketDfineAction.SOCKET_INTERNAL_TYPE:{
					// ��ҵ��������󣬱����¼������
					switch (timeVer) {
					case PacketDfineAction.SOCKET_LOGIN_STATUS_OP:
						dataPack = new LoginPacket();
						break;
					case PacketDfineAction.SOCKET_PING_OP: // ����������
						dataPack = new PingPacket();
						break;
					case PacketDfineAction.SOCKET_LOGINCOT_OFF_LINE_OP: //�ǳ���
						dataPack = new LogoutPacket();
						break;
					case PacketDfineAction.SOCKET_VERSION:
						dataPack = new VersionPacket();
						break;
			       }
			}
		    break;
			case PacketDfineAction.SOCKET_REALTIME_TYPE: //ʵʱ��Ϣ����
				switch (timeVer) {
				case PacketDfineAction.SOCKET_INPUT_OP://��������
					//dataPack = new InputStatusPacket();
					break;
				case PacketDfineAction.SOCKET_LOGIN_STATUS_OP:
					
					break;
				default:
					break;
				}
			      
			 break;
			case PacketDfineAction.SOCKET_STORAGE_TYPE: {  //�洢��Ϣ 
				if(timeVer == PacketDfineAction.SOCKET_REEIVER_OP
						|| timeVer == PacketDfineAction.SOCKET_LOGINCOT_OFF_LINE_OP){//����������Ӧ��
					dataPack = new ResponseReceiverMessagePacket();
				}else{//IM ���Ͱ�
					dataPack = new UcsMessage();
				}
			 }
			break;
			case PacketDfineAction.SOCKET_SEVERPUSH_TYPE://����������ҵ��
				switch(timeVer){
				case PacketDfineAction.SOCKET_LOGIN_STATUS_OP:
					dataPack = new ResponseCallBackPacket();
					break;
				}
				break;
			case PacketDfineAction.SOCKET_USERSTATUS_TYPE:
				switch (timeVer) {
				case PacketDfineAction.SOCKET_PING_OP://״̬��ѯ
					dataPack = new QueryStatus();
					break;
				}
				break;
		}

		if (dataPack != null) {
			if (PacketDfineAction.SN > 20000){
				PacketDfineAction.SN = 0;
			}
			dataPack.mHeadDataPacket.setSn(PacketDfineAction.SN);
			PacketDfineAction.SN++;
		}
		return dataPack;
	}

	public abstract String toJSON();	
	
	public void setHead(BSONObject person) {
		if (person == null)
			return;
		if (person.containsField("sn") && person.get("sn")!=null) {
			mHeadDataPacket.setSn(Integer.parseInt(person.get("sn").toString()));
		}
		if (person.containsField("type") && person.get("type")!=null) {
			mHeadDataPacket.setType(Integer.parseInt(person.get("type").toString()));
		}
		if (person.containsField("op") && person.get("op")!=null) {
			mHeadDataPacket.setOp(Integer.parseInt(person.get("op").toString()));
		}
		if (person.containsField("fsid") && person.get("fsid")!=null) {
			mHeadDataPacket.setFsid(Integer.parseInt(person.get("fsid").toString()));
		}
		if (person.containsField("fuid")&&person.get("fuid")!=null) {
			mHeadDataPacket.setFuid(Long.parseLong(person.get("fuid").toString()));
		}
		if (person.containsField("fpv")&&person.get("fpv")!=null) {
			mHeadDataPacket.setFpv(Integer.parseInt(person.get("fpv").toString()));
		}
		if (person.containsField("fcsid")&&person.get("fcsid")!=null) {
			mHeadDataPacket.setFcsid(Integer.parseInt(person.get("fcsid").toString()));
		}
		if (person.containsField("fconntp")&&person.get("fconntp")!=null) {
			mHeadDataPacket.setFconntp(Integer.parseInt(person.get("fconntp").toString()));
		}
		if (person.containsField("tstp")&&person.get("tstp")!=null) {
			mHeadDataPacket.setTstp(Integer.parseInt(person.get("tstp").toString()));
		}
		if (person.containsField("tsid")&&person.get("tsid")!=null) {
			mHeadDataPacket.setTsid(Integer.parseInt(person.get("tsid").toString()));
		}
		if (person.containsField("tuid")&&person.get("tuid")!=null) {
			mHeadDataPacket.setTuid(Long.parseLong(person.get("tuid").toString()));
		}
		if (person.containsField("tpv")&&person.get("tpv")!=null) {
			mHeadDataPacket.setTpv(Integer.parseInt(person.get("tpv").toString()));
		}
		if (person.containsField("fcsid")&&person.get("fcsid")!=null) {
			mHeadDataPacket.setFcsid(Integer.parseInt(person.get("fcsid").toString()));
		}
		if (person.containsField("ack")&&person.get("ack")!=null) {
			mHeadDataPacket.setAck(Integer.parseInt(person.get("ack").toString()));
		}
		if (person.containsField("enc")&&person.get("enc")!=null) {
			mHeadDataPacket.setEnc(Integer.parseInt(person.get("enc").toString()));
		}
		if (person.containsField("dsid")&&person.get("dsid")!=null) {
			mHeadDataPacket.setDsid(Integer.parseInt(person.get("dsid").toString()));
		}
		if (person.containsField("cpstp") && person.get("cpstp")!=null) {
			mHeadDataPacket.setCpstp(Integer.parseInt(person.get("cpstp").toString()));
		}
		if(person.containsField("time") && person.get("time")!=null){
			mHeadDataPacket.setTime(Long.parseLong(person.get("time").toString()));
		}
		if(person.containsField("resend") && person.get("resend")!=null){
			mHeadDataPacket.setResend(Integer.parseInt(person.get("resend").toString()));
		}
		this.headJson = person.toString();
	}
	
	public void setServer(String headJson){
		if(headJson != null && headJson.length() > 0){
			JSONObject headObject;
			try {
				headObject = new JSONObject(headJson);
				Iterator it = headObject.keys();  
	            while (it.hasNext()) {  
	                String key = (String) it.next();  
	                if(key.equalsIgnoreCase("fconntp")){
	                	this.mHeadDataPacket.setTconntp(headObject.getInt(key));
	                }else if(key.equalsIgnoreCase("fcsid")){
	        			this.mHeadDataPacket.setTcsid(headObject.getInt(key));
	        		}else  if(key.equalsIgnoreCase("fpv")){
	        			this.mHeadDataPacket.setTpv(headObject.getInt(key));
	        		}else if(key.equalsIgnoreCase("fsid")){
	        			this.mHeadDataPacket.setTsid(headObject.getInt(key));
	        		}else if(key.equalsIgnoreCase("tconntp")){
	        			this.mHeadDataPacket.setFconntp(headObject.getInt(key));
	        		}else if(key.equalsIgnoreCase("tcsid")){
	        			this.mHeadDataPacket.setFcsid(headObject.getInt(key));
	        		}else if(key.equalsIgnoreCase("tpv")){
	        			this.mHeadDataPacket.setFpv(headObject.getInt(key));
	        		}else if(key.equalsIgnoreCase("tsid")){
	        			this.mHeadDataPacket.setFsid(headObject.getInt(key));
	        		}else if(key.equalsIgnoreCase("tuid")||key.equalsIgnoreCase("fuid")||key.equalsIgnoreCase("time")||key.equalsIgnoreCase("sn")||key.equalsIgnoreCase("type")||key.equalsIgnoreCase("op")){
	        			//��Щ��������ӵ�����ͷ�б�
	        		}else{
	        			this.mHeadDataPacket.mList.put(key, headObject.get(key));
	        		}
	            }  
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
