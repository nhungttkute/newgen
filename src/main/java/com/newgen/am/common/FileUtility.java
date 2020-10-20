/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.common;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.http.HttpStatus;

import com.newgen.am.exception.CustomException;

/**
 *
 * @author nhungtt
 */
public class FileUtility {

	public final static String CREATE_NEW_USER_EMAIL_FILE = "CREATE_NEW_USER_EMAIL_FILE";
	public final static String CREATE_NEW_USER_EMAIL_SUBJECT = "MXV M-System - Thông báo tạo mới user đăng nhập thành công";
	public final static String CHANGE_PASSWORD_EMAIL_FILE = "CHANGE_PASSWORD_EMAIL_FILE";
	public final static String CHANGE_PASSWORD_EMAIL_SUBJECT = "MXV M-System - Thông báo thay đổi thành công mật khẩu user đăng nhập";
	public final static String CHANGE_PIN_EMAIL_FILE = "CHANGE_PIN_EMAIL_FILE";
	public final static String CHANGE_PIN_EMAIL_SUBJECT = "MXV M-System - Thông báo thay đổi thành công số PIN của user đăng nhập";

	private static String className = "FileUtility";

	public static String loadFileContent(String fileName, long refId) {
		String methodName = "loadFileContent";
		StringBuilder content = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
			// read line by line
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			reader.close();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
		}
		return content.toString();
	}

	public static long getFileSize(String fileName) {
		File file = new File(fileName);
		return file.length();
	}

	public static String compressImage(String fileName) throws IOException {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		ImageWriter imageWriter = null;
		ImageOutputStream imageOutputStream = null;
		FileInputStream compressedFileStreamReader = null;
		
		try {
			File imageFile = new File(fileName);
			File compressedImageFile = new File("temp.jpg");

			inputStream = new FileInputStream(imageFile);
			outputStream = new FileOutputStream(compressedImageFile);

			BufferedImage bufferedImage = ImageIO.read(inputStream);
			long imageBytesSize = imageFile.length();
			float imageQuality = (float) Utility.roundAvoid(100000d / imageBytesSize, 2);

			// Get image writers
			Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("jpg");

			if (!imageWriters.hasNext())
				throw new IllegalStateException("Writers Not Found!!");

			imageWriter = (ImageWriter) imageWriters.next();
			imageOutputStream = ImageIO.createImageOutputStream(outputStream);
			imageWriter.setOutput(imageOutputStream);

			ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();

			// Set the compress quality metrics
			imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			imageWriteParam.setCompressionQuality(imageQuality);

			// Created image
			imageWriter.write(null, new IIOImage(bufferedImage, null, null), imageWriteParam);
			
			compressedFileStreamReader = new FileInputStream(compressedImageFile);
            byte[] bytes = new byte[(int)compressedImageFile.length()];
            compressedFileStreamReader.read(bytes);
            return Base64.getEncoder().encodeToString(bytes);
		} catch (Exception e) {
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			// close all streams
			inputStream.close();
			outputStream.close();
			imageOutputStream.close();
			compressedFileStreamReader.close();
			imageWriter.dispose();
		}
	}
}
