import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.baoyuan.service.weixin.WxCardBindService;
import com.baoyuan.service.weixin.WxConfigService;
import com.baoyuan.service.weixin.WxMessageRequestService;
import com.baoyuan.util.SpringUtils;
import com.baoyuan.weixin.bean.Article;
import com.baoyuan.weixin.bean.EventType;
import com.baoyuan.weixin.bean.Message;
import com.baoyuan.weixin.bean.MessageType;
import com.baoyuan.weixin.utils.DesUtils;
import com.baoyuan.entity.weixin.WxCardBind;
import com.baoyuan.http.DefaultOpenApi;


class DefaultHandleRequest {

	private WxMessageRequestService wxMessageRequestService;

	private WxConfigService wxConfigService;

	private WxCardBindService wxCardBindService;
	
	private DefaultOpenApi defaultOpenApi;
	
	WxMessageRequestService getWxMessageRequestService(){
		if(wxMessageRequestService==null){
			wxMessageRequestService = SpringUtils.getApplicationContext().getBean(WxMessageRequestService.class);
		}

		return wxMessageRequestService;
	}

	WxConfigService getWxConfigService(){
		if(wxConfigService==null){
			wxConfigService = SpringUtils.getApplicationContext().getBean(WxConfigService.class);
		}

		return wxConfigService;
	}

	WxCardBindService getWxCardBindService(){
		if(wxCardBindService==null){
			wxCardBindService = SpringUtils.getApplicationContext().getBean(WxCardBindService.class);
		}

		return wxCardBindService;
	}
	
	DefaultOpenApi getDefaultOpenApi(){
		if(defaultOpenApi==null){
			defaultOpenApi = SpringUtils.getApplicationContext().getBean(DefaultOpenApi.class);
		}

		return defaultOpenApi;
	}
	
	 
	String handleRequest(Map<String,Object> args) {
		String wid = args.get("wid");
		Message request = args.get("message");
		println(request.toXML());
		
		println(wid);
		MessageType type = request.getMessageType();
		
		Message response = null;
		switch(type){
			/*case MessageType.TEXT:
				response = createTextMessage(request.getToUser(),request.getFromUser(),"��ã����ǲ�����Ϣ");
				break;*/
			case MessageType.EVENT:
				String context="<a href='https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxb901d155b2df7485&redirect_uri=http://zzbaoyuan.f3322.org/weixin-web/web/wx/oauth/authorization/187363843322675200&response_type=code&scope=snsapi_userinfo#wechat_redirect'>OAuth��֤</a>";
				response = createTextMessage(request.getToUser(),request.getFromUser(),context);

				EventType eventType = request.getEventType();
				if (eventType == EventType.SUBSCRIBE) {
					response = createTextMessage(request.getToUser(),request.getFromUser(),"��ã���ӭ��עϲӯ��΢�źţ�");
				}
				if (eventType == EventType.UNSUBSCRIBE) {
					response = createTextMessage(request.getToUser(),request.getFromUser(),"��ã��˶���Ϣ");
				}
				if (eventType == EventType.SCAN) {
					response = createTextMessage(request.getToUser(),request.getFromUser(),"��ã�ɨ����Ϣ");
				}
				if (eventType == EventType.LOCATION) {
					response = createTextMessage(request.getToUser(),request.getFromUser(),"��ã�λ����Ϣ");
				}
				if (eventType == EventType.CLICK) {
					WxCardBind cardBind = getWxCardBindService().find("weixin", "165617974705651712", "fromUserName", request.getFromUser());
					
					if(cardBind==null){
						String template = "���ã�Ϊ�˱�֤�����ÿ���ȫ,���Ƚ��������֤.�����֤�ɹ���������:\n\n1.����˵������֡����\n2.�����˵�������֪ͨ����\n3.�������޸ġ���ʧ�����\n-----------------\n  [OK]<a href='http://116.255.227.139/weixin/web/wx/card/login/234758759493140480?user="+request.getFromUser()+"&card=&passwd='>��������󶨿���</a>.";
						response =  createTextMessage(request.getToUser(),request.getFromUser(),template);
						
					}else {
					 
				     if ("M_VIP_01".equals(request.getEventKey())) {
					     //�����
						String template ="���ѳɹ��󶨿���"+cardBind.getCardNo()+",��������:\n\n1.����˵������֡����\n2.�����˵�������֪ͨ����\n3.�������޸ġ���ʧ�����\n-----------------\n [OK]<a href='http://116.255.227.139/weixin/web/wx/card/login/234758759493140480?user="+request.getFromUser()+"&card=&passwd='>������������</a>.";
							
						response =  createTextMessage(request.getToUser(),request.getFromUser(),template);
						
					}else if("M_VIP_02".equals(request.getEventKey())){
					    //������
				     	String uniqueCode = cardBind.getCardNo().substring(6, 11);
					    String token = getDefaultOpenApi().getToken(uniqueCode);
						
					    Map<String, Object> history =getDefaultOpenApi().cardYueJf(cardBind.getCardNo(),DesUtils.decrypt(cardBind.getCardPasswd()), token);
						
				    	String template="����:"+cardBind.getCardNo()+"\n-----------------\n�����:"+history.get("totalMoney")+"Ԫ\n�������:"+history.get("consumeMoney")+"Ԫ\n�ܻ���:"+history.get("totalJifen")+"\n���û���:"+history.get("consumeJifen")+"\n-----------------\n�����������:"+history.get("maxMoney")+"Ԫ";
					   
					   response =  createTextMessage(request.getToUser(),request.getFromUser(),template);
					}
					else if("M_VIP_03".equals(request.getEventKey())){
						//������ʷ
					  String uniqueCode = cardBind.getCardNo().substring(6, 11);
					  String token = getDefaultOpenApi().getToken(uniqueCode);
					  Map<String, Object> data = getDefaultOpenApi().consumeTj(cardBind.getCardNo(), token);
						
					  String template="�𾴵��û�,���!��"+cardBind.getCardNo()+"���7���������ʷ����:\n-----------------\n�����ܴ���:"+data.get("totalCount")+"\n�����ܽ��:"+data.get("sumMoney")+"\n-----------------\n [OK]<a href='http://116.255.227.139/weixin/web/wx/card/history/234758759493140480?user="+request.getFromUser()+"&card="+cardBind.getCardNo()+"&pageSize="+data.get("totalCount")+"'>����鿴������ϸ</a>";
					 
					  response =  createTextMessage(request.getToUser(),request.getFromUser(),template);
					   
					}else if("M_VIP_04".equals(request.getEventKey())) {
					   //��������
					  String uniqueCode = cardBind.getCardNo().substring(6, 11);
				   	  String token = getDefaultOpenApi().getToken(uniqueCode);
					  Map<String, Object> info = getDefaultOpenApi().cardDetail(cardBind.getCardNo(),token);
					
					  String template="����:"+cardBind.getCardNo()+"\n������:"+info.get("cardType")+"\n���ȼ�:"+info.get("cardLevel")+"\n��״̬:"+info.get("cardStatus")+"\n-----------------\n����:" +info.get("name")+"\n�Ա�:"+info.get("sex")+"\n��ϵ�绰:"+info.get("phone")+"\n��ϵ��ַ:"+info.get("address")+"\n����:"+info.get("nation")+"\n-----------------\n�����ŵ�:"+info.get("shopName")+"\n��������:"+info.get("createDate");
					  
					  response =  createTextMessage(request.getToUser(),request.getFromUser(),template);
						
					}else if("M_VIP_05".equals(request.getEventKey())){
						//�޸�����
						String template="[õ��]��ܰ����:���������޸����ĳֿ�����,���μ����޸ĺ������,���������������Ҫ���鷳![���]\n-----------------\n  [OK]<a href='http://116.255.227.139/weixin/web/wx/card/change/234758759493140480?user="+request.getFromUser()+"'>��������޸�����</a>";
						
						response =  createTextMessage(request.getToUser(),request.getFromUser(),template);
				   }else if("K_SER_01".equals(request.getEventKey())){
					 //����ʧ 
					 String template="[õ��]��ܰ����:����������ʧ���󶨵�"+cardBind.getCardNo()+"�Ŀ�,����ʧ���޷����в�ѯ�����ѵ�ҵ��,���������������Ҫ����ʧ![���]\n-----------------\n  [OK]<a href='http://116.255.227.139/weixin/web/wx/card/loss/234758759493140480?user="+request.getFromUser()+"'>���������ʧ</a>";
					
					 response =  createTextMessage(request.getToUser(),request.getFromUser(),template);
				  }else if("K_SER_02".equals(request.getEventKey())){  
				   //����� 
				    String template="[õ��]��ܰ����:��������������󶨵�"+cardBind.getCardNo()+"�Ŀ�,��Һ󼴿�����:\n\n1.����˵������֡����\n2.�����˵�������֪ͨ����\n3.�������޸ġ���ʧ�����\n-----------------\n  [OK]<a href='http://116.255.227.139/weixin/web/wx/card/found/234758759493140480?user="+request.getFromUser()+"'>����������</a>.";
				     
					response=createTextMessage(request.getToUser(),request.getFromUser(),template);
				  }else if("K_SER_03".equals(request.getEventKey())){
				    //���߳�ֵ
				    String template="[Ʋ��]��Ǹ,���߳�ֵ������Ҫ��˽���,���µ�400-602-3332��ȡ��������,��л���Ĺ�ע!";
					
					response=createTextMessage(request.getToUser(),request.getFromUser(),template);
				  }
				}
				}		
				break;
			/*case MessageType.IMAGE:
				response = createArticleMessage(request.getToUser(),request.getFromUser());
				break;
			case MessageType.LINK:
				response = createTextMessage(request.getToUser(),request.getFromUser(),"��ã����ǲ�����Ϣ");
				break;
			case MessageType.LOCATION:
				response = createTextMessage(request.getToUser(),request.getFromUser(),"��ã����ǲ�����Ϣ");
				break;
			case MessageType.VIDEO:
				response = createTextMessage(request.getToUser(),request.getFromUser(),"��ã����ǲ�����Ϣ");
				break;
			case MessageType.VOICE:
				response = createTextMessage(request.getToUser(),request.getFromUser(),"��ã����ǲ�����Ϣ");
				break;
			case MessageType.MUSIC:
				response = createTextMessage(request.getToUser(),request.getFromUser(),"��ã����ǲ�����Ϣ");
				break;
			case MessageType.NEWS:
				response = createTextMessage(request.getToUser(),request.getFromUser(),"��ã����ǲ�����Ϣ");
				break;
			default:
				String context="<a href='https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxb901d155b2df7485&redirect_uri=http://zzbaoyuan.f3322.org/weixin-web/web/wx/oauth/authorization/187363843322675200&response_type=code&scope=snsapi_userinfo#wechat_redirect'>OAuth��֤</a>";
				
				response = createTextMessage(request.getToUser(),request.getFromUser(),context);
				break;
				*/
		}

		if(response == null){
			return null;
		}
		return response.toXML();
	}
	
	Message createTextMessage(String toUser,String fromUser,String context){
		Message result = new Message();
		
		result.setFromUser(toUser);
		result.setToUser(fromUser);
		result.setMessageType(MessageType.TEXT);
		result.setCreateTime(new Date());
		result.setContent(context);
		
		return result;
	}
	
	Message createArticleMessage(String toUser,String fromUser){
		Message result = new Message();
		
		result.setFromUser(toUser);
		result.setToUser(fromUser);
		result.setMessageType(MessageType.NEWS);
		result.setCreateTime(new Date());
		
		List<Article> articles = new ArrayList<Article>();
		
		Article item = new Article();
		item.setTitle("������Title");
		item.setPicUrl("http://www.shop8188.com/shop/template/web/img/graph.png")
		item.setUrl("http://www.wp99.cn/vipeng/");
		item.setDescription("��ע˵����Ϣ");
		articles.add(item);
		
		item = new Article();
		item.setTitle("������Title");
		item.setPicUrl("http://www.shop8188.com/shop/template/web/img/graph.png")
		item.setUrl("http://www.wp99.cn/vipeng/");
		item.setDescription("��ע˵����Ϣ");
		
		articles.add(item);
		
		item = new Article();
		item.setTitle("������Title");
		item.setPicUrl("http://www.shop8188.com/shop/template/web/img/graph.png")
		item.setUrl("http://www.wp99.cn/vipeng/");
		item.setDescription("��ע˵����Ϣ");
		
		articles.add(item);
		
		item = new Article();
		item.setTitle("������Title");
		item.setPicUrl("http://www.shop8188.com/shop/template/web/img/graph.png")
		item.setUrl("http://www.wp99.cn/vipeng/");
		item.setDescription("��ע˵����Ϣ");
		
		articles.add(item);
		
		item = new Article();
		item.setTitle("������Title");
		item.setPicUrl("http://www.shop8188.com/shop/template/web/img/graph.png")
		item.setUrl("http://www.wp99.cn/vipeng/");
		item.setDescription("��ע˵����Ϣ");
		
		articles.add(item);
		
		result.setArticles(articles)
		
		println(result.toXML());
		
		return result;
	}
}
