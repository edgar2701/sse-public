package com.edgar2701.sse.service;

import java.util.HashMap;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class sseService {

		private final HashMap<Integer, SseEmitter> currentSessions = new HashMap<>();

		public synchronized void openSession(Integer id, SseEmitter sseEmitter) {
			sseEmitter.onCompletion(()->{currentSessions.remove(id);});
			sseEmitter.onTimeout(()->{currentSessions.remove(id);});
			currentSessions.put(id, sseEmitter);		
		}

		public synchronized void removeSession(Integer id){
			currentSessions.remove(id);		
		}

		public void sendMessage(SseEmitter emitter, String event, String data) {					
			if (emitter!=null){
				try {
					emitter.send(SseEmitter.event().name(event).data(data));
					emitter.complete();				
				} catch (Exception e) {
					e.printStackTrace();
					emitter.completeWithError(e);
				}				
			}
		}
	
		public void sendMessageById(Integer id, String event, String data) {
			try {							
				SseEmitter e = currentSessions.get(id);
				sendMessage(e, event, data);													
			} catch (Throwable e) {
				e.printStackTrace();
			}	
			removeSession(id);	
		}
		
		public HashMap<Integer, SseEmitter> list(){
			HashMap<Integer, SseEmitter> result = new HashMap<>();
			try {			
				currentSessions.keySet().forEach(t->{					
					result.put(t, currentSessions.get(t));					
				});
			} catch (Exception e) {

			}				
			return result;
		}


	
}
