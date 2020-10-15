package com.edgar2701.sse.controller;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.edgar2701.sse.service.sseService;

@Controller
public class SseController {
	
	@Autowired
	sseService sse;
	Integer id = 0;
	
	@GetMapping("/list")
	public String list(Model model) {
		model.addAttribute("list", sse.list());
		return "list";
	}
	
	@CrossOrigin
	@GetMapping("/register")
	public SseEmitter register(@RequestParam String id) {
		SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
		sse.openSession(Integer.valueOf(id), sseEmitter);
		return sseEmitter;
	}

	@GetMapping("/")
	public String home(HttpSession session) {
		if (session.getAttribute("id")==null) {
			session.setAttribute("id", id++);
		}
		return "home";
	}
	
	@PostMapping("/sendDataOneByOne")
	public ResponseEntity<String> sendMessage(@RequestParam Integer id, @RequestParam String event, @RequestParam String data) {
		try {
			sse.sendMessageById(id, event, data);
			return ResponseEntity.ok("ok");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getLocalizedMessage());
		}
	}
	
	@PostMapping("/sendDataAll")
	public ResponseEntity<String> sendMessages(@RequestParam String event, @RequestParam String data) {
		try {
			HashMap<Integer, SseEmitter> all = sse.list();
			for (SseEmitter tmp: all.values()) {
				sse.sendMessage(tmp, event, data);
			}
			return ResponseEntity.ok("ok");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getLocalizedMessage());
		}		
	}
}
