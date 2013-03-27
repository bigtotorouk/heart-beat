package com.slider.cn.app.bean.response;

import java.util.List;



public class ResponseBeats {

   private ResponseBeatsMeta meta;
   private List<ResponseBeat> objects;
	
   
   
   public ResponseBeatsMeta getMeta() {
	   return meta;
   }
   public void setMeta(ResponseBeatsMeta meta) {
	   this.meta = meta;
   }
   public List<ResponseBeat> getObjects() {
	   return objects;
   }
   public void setObjects(List<ResponseBeat> objects) {
	   this.objects = objects;
   }
   
   @Override
   public String toString() {
	   return "BeatsBean [meta=" + meta + ", objects=" + objects + "]";
   }
	   
}
