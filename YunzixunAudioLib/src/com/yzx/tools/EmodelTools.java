package com.yzx.tools;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

import com.gl.softphone.EmodelInfo;
import com.gl.softphone.EmodelValue;
import com.gl.softphone.UGoAPIParam;
import com.gl.softphone.UGoManager;
import com.yzx.preference.UserData;

import android.content.Context;
import android.os.Build;

public class EmodelTools {
	
	public static void getEmodelValue(Context mContext){
		
		EmodelValue ev = new EmodelValue();
		ev.emodel_mos = new EmodelInfo();
		ev.emodel_ie = new EmodelInfo();
		ev.emodel_ppl = new EmodelInfo();
		ev.emodel_burstr = new EmodelInfo();
		ev.emodel_tr = new EmodelInfo();
		UGoManager.getInstance().pub_UGoGetEmodelValue(ev);
		
		JSONObject json = new JSONObject();
		try {
			json.put("ver", "yzx_"+Util.SDK_VERSION+"_"+UGoManager.getInstance().pub_UGoGetVersion());
			int worktype = NetWorkTools.getCurrentNetWorkType(mContext);
			switch(worktype){
			case NetWorkTools.NETWORK_3G:
				json.put("net","3g");
				break;
			case NetWorkTools.NETWORK_WIFI:
				json.put("net","wifi");
				break;
			//case NetWorkTools.NETWORK_ON:
			//case NetWorkTools.NETWORK_EDGE:
			//	break;
			default:
				json.put("net","ethernet");
				break;
			}
			json.put("pv","android_"+android.os.Build.VERSION.SDK_INT+"_"+Build.MODEL.replaceAll(" ", ""));
			json.put("caller", UserData.getClientId());
			json.put("callee", "");
			json.put("mcodec", "");
			json.put("cmode", UGoAPIParam.getInstance()!= null ? (UGoAPIParam.getInstance().stCallDialPara.mode == 6?0:1):0);
			json.put("mmode", 0);
			json.put("ctime", 0);
			json.put("cstate", 0);
			json.put("role", 0);
			json.put("snr", 0);
			json.put("frate", 0);
			
			json.put("mos_min", floatFormat(ev.emodel_mos.min));
			json.put("mos_max", floatFormat(ev.emodel_mos.max));
			json.put("mos_avg", floatFormat(ev.emodel_mos.average));
			
			json.put("loss_min", floatFormat(ev.emodel_ppl.min));
			json.put("loss_max", floatFormat(ev.emodel_ppl.max));
			json.put("loss_avg", floatFormat(ev.emodel_ppl.average));
			
			json.put("delay_min", floatFormat(ev.emodel_tr.min));
			json.put("delay_max", floatFormat(ev.emodel_tr.max));
			json.put("delay_avg", floatFormat(ev.emodel_tr.average));
			
			json.put("jitter_min", floatFormat(ev.emodel_burstr.min));
			json.put("jitter_max", floatFormat(ev.emodel_burstr.max));
			json.put("jitter_avg", floatFormat(ev.emodel_burstr.average));
			
			//CustomLog.v("CURRENT_EMODEL:"+json.toString());
			
			//FileTools.saveExLog(json.toString(),"YZX_emodel_");
			FileTools.uploadJson(mContext, json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将bouble格式化成float保留两位小数
	 * @param value
	 * @return
	 * @author: xiaozhenhua
	 * @data:2013-8-26 下午3:35:57
	 */
	private static BigDecimal floatFormat(double value){
		BigDecimal b = new BigDecimal(value);
		return b.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
}
