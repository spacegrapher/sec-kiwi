package com.kiwi.bubble.android.common.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

public class HttpPostUtil {
	/**
	 * 
	 * @param pageUrl : 전송 받을 Page URL 주소 형식
	 * @param requestMethod : 전송 받을 request형식 GET/POST
	 * @param param HashMap형식 put( String "Parameter이름", String "값" )
	 * @return 해당 URL PAGE에서  데이터 처리 후 HTML형식으로 Return
	 */
	public String httpPostData(String url, Map<String, String> param) throws ClientProtocolException, IOException {   
		// TODO Auto-generated method stub   
		HttpPost request = makeHttpPost( param, url ) ;
		HttpClient client = new DefaultHttpClient() ;  
		ResponseHandler<String> reshandler = new BasicResponseHandler() ;   
		String result = client.execute( request, reshandler ) ;   
		return result ;   
	}   

	
	private HttpEntity makeEntity( Vector<NameValuePair>  nameValue ) {   
		HttpEntity result = null ;   
		try {   
			result = new UrlEncodedFormEntity( nameValue, "EUC-KR" ) ;   
		} catch (UnsupportedEncodingException e) {   
			// TODO Auto-generated catch block   
//			e.printStackTrace();   
		}   
		return result ;   
	}   
	
	private HttpPost makeHttpPost(Map<String, String> param, String url) {   
		// TODO Auto-generated method stub   
		HttpPost request = new HttpPost( url ) ;
		Vector<NameValuePair> nameValue = new Vector<NameValuePair>() ;  
		
		if(param!=null){
			Set set = param.keySet();
			Iterator e = set.iterator();
			int cnt = 0;
			while(e.hasNext()){
				String name = (String)e.next();
				String value = (String)param.get(name);
				nameValue.add( new BasicNameValuePair( name, value ) ) ;   
			}
		}
		request.setEntity( makeEntity(nameValue) ) ;   
		return request;
	}
	
	public Document getDocument(String xml){
		Document doc = null;
        try{
        	// XML Document 를 생성
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(new String(xml));
            InputSource is = new InputSource(sr);
            doc = db.parse(is);
        }catch(Exception e){
//            e.printStackTrace();
        }
        return doc;
	}
	
	/**
	 * 
	 * @param xml : 지정된 xml의  NodeList를 출력
	 * @return : Document NodeList를 리턴
	 */
	public NodeList getElementsByTagNameNodeList(Document doc, String nodeName){
		NodeList result = null;
		if(doc!=null){
			result = doc.getElementsByTagName(nodeName);
		}
		return result;
	}
	
	public HashMap getChildHashMap(Document doc, String nodeName){
		HashMap resultData = new HashMap(); 
		NodeList nodeList = getElementsByTagNameNodeList(doc, nodeName);
		if(nodeList.getLength() > 0){
			//resultData = ; 
			for(int i=0; i < nodeList.getLength(); i++){
				Element element = (Element)nodeList.item(i);
				for(int j=0; j < element.getChildNodes().getLength(); j++){					
	       			 if(!"#text".trim().equals(element.getChildNodes().item(j).getNodeName())){
	       				if(element.getChildNodes().item(j).getFirstChild() != null){
	       					String s = element.getChildNodes().item(j).getFirstChild().getNodeValue();
	       				
	       					resultData.put(element.getChildNodes().item(j).getNodeName(), element.getChildNodes().item(j).getFirstChild().getNodeValue());
	       				}
	       			}
				}
			}
		}
		return resultData;
	}
	
	/**
	 * 
	 * @param xml : XML형식의 String 
	 * @param tagName : 지정한 태그의 하위 태그 모두를 HashMap으로 생성
	 * @return ArrayList : 생성된 HashMap을 ArrayList에 담아 return
	 * @throws IOException
	 */
	public HashMap getChildXmlHashMap(String xml, String tagName) throws IOException{
		HashMap resultData = null; 
		Document doc =  getDocument(xml);
		resultData= getChildHashMap(doc, tagName);
		
		return resultData;
	}
	ArrayList<Object> resultList;
	public void setResult(ArrayList<Object> resultList){
		this.resultList = resultList; 
	}
	
	/**
	 * 
	 * @param pageUrl : 데이터를 요청할 PAGE URL
	 * @param requestMethod : 전송 받을 request형식 GET/POST
	 * @param xml : XML형식의 String 
	 * @param tagName : 지정한 태그의 하위 태그 모두를 HashMap으로 생성
	 * @return ArrayList : 생성된 HashMap을 ArrayList에 담아 return
	 * @throws IOException
	 */
	public HashMap httpPostGetChild(String pageUrl, String tagName, Map<String, String> param) throws IOException{		
		HashMap resultData = null; 
		String xml = httpPostData(pageUrl, param);
		resultData = getChildXmlHashMap(xml, tagName);
		
		return resultData;
	}
}
