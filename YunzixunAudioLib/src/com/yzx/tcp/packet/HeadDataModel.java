package com.yzx.tcp.packet;

import java.util.HashMap;

import org.bson.BSON;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import com.yzx.tools.CustomLog;


/**
 * Э�鱨��ͷ
 * @author xiaozhenhua
 *
 */
public class HeadDataModel {
	private int sn = -1; // ��ˮ��

	public int type = -1; // ��Ϣ����

	public int op = -1; // ��������,�ӹ���������

	public int fsid = -1; // From:��̨����ʵ��ID;������Ϣ��ʹ�ã���̨����ʵ����Ψһ��ʶ��

	public long fuid = -1; // From:�û�UID;������Ϣ��ʹ�ã��ɽ�����������

	public int fpv = -1; // ������Ϣ��ʹ�ã��ɽ�����������

	public int fcsid = -1; // From:���������ID;������Ϣ��ʹ�ã��ɽ�����������������Ӧ��Ϣ�����ݴ�ֵ��·����Ϣ

	public int fconntp = -1;// From:������������;�ɽ����������ӣ����ӽ���ʱSession�л�ȡ���Ľ����������ͣ�

	public int tstp = -1; // To:��̨��������

	public int tsid = -1; // To:��̨����ʵ��ID

	public long tuid = -1; // To:�û�UID

	public int tpv = -1; // To:ƽ̨����

	public int tconntp = -1; // To:������������

	public int tcsid = -1; // To:���������ID

	public int ack = -1; // ��Ϣ�ַ���Ӧ��ʶ

	public int enc = -1; // ���ܷ�ʽ

	public int dsid = -1; // �ַ��������ID

	public int cpstp = -1; // ������ѹ������
	
	public int  bsid = -1; //��̨����ʵ��ID
	
	private long time = -1;
	
	private int resend = -1;//�Ƿ��ط�
	
	private int mtp = -1; //��Ϣý������ 1:����	2:ͼƬ	3:��Ƶ	4:��Ƶ	7:����λ��

	
	public BSONObject person = new BasicBSONObject();

	HashMap<String, Object> mList = new HashMap<String, Object>();
	public void setSn(int sn) {
		this.sn = sn;
	}

	public int getSn() {
		return sn;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setOp(int op) {
		this.op = op;
	}

	public int getOp() {
		return op;
	}

	public void setFsid(int fsid) {
		this.fsid = fsid;
	}

	public int getFsid() {
		return fsid;
	}

	public void setFuid(long fuid) {
		this.fuid = fuid;
	}

	public long getFuid() {
		return fuid;
	}

	public void setFpv(int fpv) {
		this.fpv = fpv;
	}

	public int getFpv() {
		return fpv;
	}

	public void setFcsid(int fcsid) {
		this.fcsid = fcsid;
	}

	public int getFcsid() {
		return fcsid;
	}

	public void setFconntp(int fconntp) {
		this.fconntp = fconntp;
	}

	public int getFconntp() {
		return fconntp;
	}

	public void setTstp(int tstp) {
		this.tstp = tstp;
	}

	public int getTstp() {
		return tstp;
	}

	public void setTsid(int tsid) {
		this.tsid = tsid;
	}

	public int getTsid() {
		return tsid;
	}

	public void setTuid(long tuid) {
		this.tuid = tuid;
	}

	public long getTuid() {
		return tuid;
	}

	public void setTpv(int tpv) {
		this.tpv = tpv;
	}

	public int getTpv() {
		return tpv;
	}

	public void setTconntp(int tconntp) {
		this.tconntp = tconntp;
	}

	public int getTconntp() {
		return tconntp;
	}

	public void setTcsid(int tcsid) {
		this.tcsid = tcsid;
	}

	public int getTcsid() {
		return tcsid;
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

	public int getAck() {
		return ack;
	}

	public void setEnc(int enc) {
		this.enc = enc;
	}

	public int getEnc() {
		return enc;
	}

	public void setDsid(int dsid) {
		this.dsid = dsid;
	}

	public int getDsid() {
		return dsid;
	}

	public void setCpstp(int cpstp) {
		this.cpstp = cpstp;
	}

	public int getCpstp() {
		return cpstp;
	}
	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}
	
	public void setBsid(int bsid) {
		this.bsid = bsid;
	}

	public int getBsid() {
		return bsid;
	}
	
	public int getResend(){
		return resend;
	}
	
	public void setResend(int resend){
		this.resend  = resend;
	}
	
	public void setMtp(int mtp) {
		this.mtp = mtp;
	}

	public int getMtp() {
		return mtp;
	}
	public byte[] toBsonByte() {

		if (getSn() >= 0) {
			person.put("sn", getSn());
		}
		if (getType() >= 0) {
			person.put("type", getType());
		}
		if (getOp() >= 0) {
			person.put("op", getOp());
		}
		
		if (getFsid() >= 0) {
			person.put("fsid", getFsid());
		}
		if (getFuid() >= 0) {
			person.put("fuid", getFuid());
		}
		if (getFpv() >= 0) {
			person.put("fpv", getFpv());
		}
		if (getFcsid() >= 0) {
			person.put("fcsid", getFcsid());
		}
		if (getFconntp() >= 0) {
			person.put("fconntp", getFconntp());
		}
		if (getTstp() >= 0) {
			person.put("tstp", getTstp());
		}
		if (getTsid() >= 0) {
			person.put("tsid", getTsid());
		}
		if (getTuid() >= 0) {
			person.put("tuid", getTuid());
		}

		if (getTpv() >= 0) {
			person.put("tpv", getTpv());
		}
		if (getTconntp() >= 0) {
			person.put("tconntp", getTconntp());
		}
		if (getTcsid() >= 0) {
			person.put("tcsid", getTcsid());
		}
		if (getAck() >= 0) {
			person.put("ack", getAck());
		}
		if(getEnc() >=0){
			person.put("enc", getEnc());
		}
		if(getMtp()>=0){
			person.put("mtp", getMtp());
		}
		
		if (this.mList.size() > 0) {
			for(String key:this.mList.keySet()){
				person.put(key, mList.get(key));
			}
		}
		if (getType() == PacketDfineAction.SOCKET_CALL) {
			CustomLog.v("TAG_UGoManager", "tcp write head:" + person);
		}
		
		return BSON.encode(person);
	}
	
	public long getLong(short userid,byte brand,byte resources,long uid){
		return ((long)userid  << 48) | ((long)brand << 40) |((long)resources  << 32) | (uid & 0xffffffff);
 	}
	
	public Short getUserid(long fuid){
		short userid = (short) (fuid >> 48 & 0xffff);
		return userid;
	}
	
	public byte getBrand(long fuid){
		byte brand = (byte) (fuid >> 40 & 0xff);
		return brand;
	}
	
	public byte getResources(long fuid){
		byte resourcesId = (byte) (fuid >> 32 & 0xff);
		return resourcesId;
	}
	
	public int getuid(long fuid){
		int uid = (int) (fuid & 0xffffffff);
		return uid;
	}

}
