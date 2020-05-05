package com.newgen.am.controller;

import java.io.IOException;
import java.util.Base64;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.am.dto.MemberDTO;
import com.newgen.am.model.Photo;
import com.newgen.am.service.PhotoService;

@RestController
public class PhotoController {
	@Autowired
	PhotoService photoService;
	
	@Autowired
	ModelMapper modelMapper;
	
	@PostMapping(value = "/admin/photo/add", consumes = {"multipart/form-data"})
	public String addPhoto(@RequestParam("memberInfoJson") String memberInfoJson, @RequestParam("image") MultipartFile image)
			throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		MemberDTO memberDto = objectMapper.readValue(memberInfoJson, MemberDTO.class);
		System.out.println("memberInfoJson: " + memberInfoJson);
		System.out.println("MemberDTO: " + objectMapper.writeValueAsString(memberDto));
		String id = photoService.addPhoto("nhung_test", image);
		return "redirect:/photos/" + id;
	}
	
	@GetMapping("/admin/photo/{id}")
	public String getPhoto(@PathVariable String id, Model model) {
	    Photo photo = photoService.getPhoto(id);
	    return Base64.getEncoder().encodeToString(photo.getImage().getData());
	}
}
