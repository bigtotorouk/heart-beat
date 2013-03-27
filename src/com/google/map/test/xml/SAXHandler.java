package com.google.map.test.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class SAXHandler extends DefaultHandler{
	private static final String TAG = "SAXHandler";
	private String preTAG;
	
	private StringBuilder addressBuilder;
	private StringBuilder cityBuilder;
	private StringBuilder countryNameCode;
	
	public SAXHandler(){
		super();
		addressBuilder = new StringBuilder();
		cityBuilder = new StringBuilder();
		countryNameCode = new StringBuilder();
	}
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {	
		preTAG = localName;
		System.out.println("startElement -->> preTAG: "+ preTAG);
		super.startElement(uri, localName, qName, attributes);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		preTAG = "";
		System.out.println("endElement -->> preTAG: "+ preTAG);
		super.endElement(uri, localName, qName);
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String info = new String(ch,start,length);  
		if(preTAG.equals("address")){
			addressBuilder.append(info);
		}else if(preTAG.equals("LocalityName")){
			cityBuilder.append(info);
		}else if(preTAG.equals("CountryNameCode")){
			countryNameCode.append(info);
		}
		System.out.println("characters -->> addressBuilder: "+ addressBuilder.toString()+" cityBuilder"+cityBuilder.toString());
		super.characters(ch, start, length);
	}
	
	
	public String getCity(){
		return cityBuilder.toString();
	}
	public String getAddress(){
		return addressBuilder.toString();
	}
	public String getCountryCode(){
		return countryNameCode.toString();
	}
	
	
}
