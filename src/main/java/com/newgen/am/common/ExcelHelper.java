package com.newgen.am.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;

import com.newgen.am.dto.BrokerCSV;
import com.newgen.am.dto.CollaboratorCSV;
import com.newgen.am.dto.ExchangeSettingDTO;
import com.newgen.am.dto.InvestorCSV;
import com.newgen.am.dto.LoginAdminUsersDTO;
import com.newgen.am.dto.MarginTransCSV;
import com.newgen.am.dto.MemberCSV;
import com.newgen.am.exception.CustomException;

public class ExcelHelper {
	private static String CLASSNAME = "ExcelHelper";
	

	public static ByteArrayInputStream adminUsersToExcel(List<LoginAdminUsersDTO> adminUserList, long refId) {
		String methodName = "adminUsersToExcel";
		String[] HEADERs = {"Số TT", "Mã phòng ban", "Mã TVKD", "Mã Môi giới", "Mã CTV", "Tên đăng nhập", "Họ tên", "Email", "Điện thoại", "Trạng thái người dùng", "Trạng thái đăng nhập", "Số lần đăng nhập", "Ngày, giờ đăng nhập gần nhất"};
		String SHEET = "info";
		
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Sheet sheet = workbook.createSheet(SHEET);

			// Header
			Row headerRow = sheet.createRow(0);

			for (int col = 0; col < HEADERs.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(HEADERs[col]);
			}

			int rowIdx = 1;
			for (LoginAdminUsersDTO admUser : adminUserList) {
				Row row = sheet.createRow(rowIdx);
				row.createCell(0).setCellValue(rowIdx);
				row.createCell(1).setCellValue(admUser.getDeptCode());
				row.createCell(2).setCellValue(admUser.getMemberCode());
				row.createCell(3).setCellValue(admUser.getBrokerCode());
				row.createCell(4).setCellValue(admUser.getCollaboratorCode());
				row.createCell(5).setCellValue(admUser.getUsername());
				row.createCell(6).setCellValue(admUser.getFullName());
				row.createCell(7).setCellValue(admUser.getEmail());
				row.createCell(8).setCellValue(admUser.getPhoneNumber());
				row.createCell(9).setCellValue(Utility.getStatusVnStr(admUser.getStatus()));
				row.createCell(10).setCellValue(Utility.getLoginedVnStr(admUser.getLogined()));
				row.createCell(11).setCellValue(admUser.getLogonCounts());
				row.createCell(12).setCellValue(admUser.getLogonTimeStr());
				rowIdx++;
			}

			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			AMLogger.logError(CLASSNAME, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public static ByteArrayInputStream investorsToExcel(List<InvestorCSV> investorList, long refId) {
		String methodName = "investorsToExcel";
		String[] HEADERs = { "Số TT", "Mã TVKD", "Tên TVKD", "Mã Môi giới", "Tên Môi giới", "Mã CTV", "Tên CTV", "Mã TKGD", "Tên TKGD", "Ghi chú", "Trạng thái", "Ngày tham gia"};
		String SHEET = "info";
		
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Sheet sheet = workbook.createSheet(SHEET);

			// Header
			Row headerRow = sheet.createRow(0);

			for (int col = 0; col < HEADERs.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(HEADERs[col]);
			}

			int rowIdx = 1;
			for (InvestorCSV investor : investorList) {
				Row row = sheet.createRow(rowIdx);
				row.createCell(0).setCellValue(rowIdx);
				row.createCell(1).setCellValue(investor.getMemberCode());
				row.createCell(2).setCellValue(investor.getMemberName());
				row.createCell(3).setCellValue(investor.getBrokerCode());
				row.createCell(4).setCellValue(investor.getBrokerName());
				row.createCell(5).setCellValue(investor.getCollaboratorCode());
				row.createCell(6).setCellValue(investor.getCollaboratorName());
				row.createCell(7).setCellValue(investor.getInvestorCode());
				row.createCell(8).setCellValue(investor.getInvestorName());
				row.createCell(9).setCellValue(investor.getNote());
				row.createCell(10).setCellValue(Utility.getStatusVnStr(investor.getStatus()));
				row.createCell(11).setCellValue(investor.getCreatedDate());
				rowIdx++;
			}

			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			AMLogger.logError(CLASSNAME, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public static ByteArrayInputStream marginTransToExcel(List<MarginTransCSV> marginTransList, long refId) {
		String methodName = "marginTransToExcel";
		String[] HEADERs = {"Số TT", "Mã TVKD", "Tên TVKD", "Mã Môi giới", "Tên Môi giới", "Mã CTV", "Tên CTV", "Mã TKGD", "Tên TKGD", "Loại GD", "Số tiền nộp/rút", "Tiền tệ",
				"Người phê duyệt", "Ngày phê duyệt", "Người tạo", "Ngày tạo", "Ghi chú", "Ngày phiên"};
		String SHEET = "info";
		
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Sheet sheet = workbook.createSheet(SHEET);

			DataFormat format = workbook.createDataFormat();
			CellStyle numberStyle = workbook.createCellStyle();
			numberStyle.setDataFormat(format.getFormat("#,##0"));
			
			// Header
			Row headerRow = sheet.createRow(0);

			for (int col = 0; col < HEADERs.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(HEADERs[col]);
			}

			int rowIdx = 1;
			for (MarginTransCSV marginTran : marginTransList) {
				Row row = sheet.createRow(rowIdx);
				row.createCell(0).setCellValue(rowIdx);
				row.createCell(1).setCellValue(marginTran.getMemberCode());
				row.createCell(2).setCellValue(marginTran.getMemberName());
				row.createCell(3).setCellValue(marginTran.getBrokerCode());
				row.createCell(4).setCellValue(marginTran.getBrokerName());
				row.createCell(5).setCellValue(marginTran.getCollaboratorCode());
				row.createCell(6).setCellValue(marginTran.getCollaboratorName());
				row.createCell(7).setCellValue(marginTran.getInvestorCode());
				row.createCell(8).setCellValue(marginTran.getInvestorName());
				row.createCell(9).setCellValue(Utility.getTransTypeVnStr(marginTran.getTransactionType()));
				
				Cell cell = row.createCell(10);
				cell.setCellValue(marginTran.getAmount());
				cell.setCellStyle(numberStyle);
				
				row.createCell(11).setCellValue(marginTran.getCurrency());
				row.createCell(12).setCellValue(marginTran.getApprovalUser());
				row.createCell(13).setCellValue(marginTran.getApprovalDate());
				row.createCell(14).setCellValue(marginTran.getCreatedUser());
				row.createCell(15).setCellValue(marginTran.getCreatedDate());
				row.createCell(16).setCellValue(marginTran.getNote());
				row.createCell(17).setCellValue(marginTran.getSessionDate());
				rowIdx++;
			}

			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			AMLogger.logError(CLASSNAME, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public static ByteArrayInputStream membersToExcel(List<MemberCSV> memberList, long refId) {
		String methodName = "membersToExcel";
		String[] HEADERs = {"Số TT", "Mã TVKD", "Tên TVKD", "Ghi chú", "Trạng thái", "Ngày tham gia"};
		String SHEET = "info";
		
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Sheet sheet = workbook.createSheet(SHEET);

			// Header
			Row headerRow = sheet.createRow(0);

			for (int col = 0; col < HEADERs.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(HEADERs[col]);
			}

			int rowIdx = 1;
			for (MemberCSV member : memberList) {
				Row row = sheet.createRow(rowIdx);
				row.createCell(0).setCellValue(rowIdx);
				row.createCell(1).setCellValue(member.getCode());
				row.createCell(2).setCellValue(member.getName());
				row.createCell(3).setCellValue(member.getNote());
				row.createCell(4).setCellValue(Utility.getStatusVnStr(member.getStatus()));
				row.createCell(5).setCellValue(member.getCreatedDate());
				rowIdx++;
			}

			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			AMLogger.logError(CLASSNAME, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public static ByteArrayInputStream brokersToExcel(List<BrokerCSV> brokerList, long refId) {
		String methodName = "brokersToExcel";
		String[] HEADERs = {"Số TT", "Mã TVKD", "Tên TVKD", "Mã Môi giới", "Tên Môi giới", "Trạng thái", "Ghi chú", "Ngày tham gia"};
		String SHEET = "info";
		
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Sheet sheet = workbook.createSheet(SHEET);

			// Header
			Row headerRow = sheet.createRow(0);

			for (int col = 0; col < HEADERs.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(HEADERs[col]);
			}

			int rowIdx = 1;
			for (BrokerCSV broker : brokerList) {
				Row row = sheet.createRow(rowIdx);
				row.createCell(0).setCellValue(rowIdx);
				row.createCell(1).setCellValue(broker.getMemberCode());
				row.createCell(2).setCellValue(broker.getMemberName());
				row.createCell(3).setCellValue(broker.getCode());
				row.createCell(4).setCellValue(broker.getName());
				row.createCell(5).setCellValue(Utility.getStatusVnStr(broker.getStatus()));
				row.createCell(6).setCellValue(broker.getNote());
				row.createCell(7).setCellValue(broker.getCreatedDate());
				rowIdx++;
			}

			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			AMLogger.logError(CLASSNAME, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public static ByteArrayInputStream collaboratorsToExcel(List<CollaboratorCSV> collaboratorList, long refId) {
		String methodName = "collaboratorsToExcel";
		String[] HEADERs = {"Số TT", "Mã TVKD", "Tên TVKD", "Mã Môi giới", "Tên Môi giới", "Mã CTV", "Tên CTV", "Trạng thái", "Ghi chú", "Ngày tham gia"};
		String SHEET = "info";
		
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Sheet sheet = workbook.createSheet(SHEET);

			// Header
			Row headerRow = sheet.createRow(0);

			for (int col = 0; col < HEADERs.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(HEADERs[col]);
			}

			int rowIdx = 1;
			for (CollaboratorCSV collaborator : collaboratorList) {
				Row row = sheet.createRow(rowIdx);
				row.createCell(0).setCellValue(rowIdx);
				row.createCell(1).setCellValue(collaborator.getMemberCode());
				row.createCell(2).setCellValue(collaborator.getMemberName());
				row.createCell(3).setCellValue(collaborator.getBrokerCode());
				row.createCell(4).setCellValue(collaborator.getBrokerName());
				row.createCell(5).setCellValue(collaborator.getCode());
				row.createCell(6).setCellValue(collaborator.getName());
				row.createCell(7).setCellValue(Utility.getStatusVnStr(collaborator.getStatus()));
				row.createCell(8).setCellValue(collaborator.getNote());
				row.createCell(9).setCellValue(collaborator.getCreatedDate());
				rowIdx++;
			}

			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			AMLogger.logError(CLASSNAME, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public static ByteArrayInputStream userExchangesToExcel(List<ExchangeSettingDTO> userExchangeList, long refId) {
		String methodName = "userExchangesToExcel";
		String[] HEADERs = {"Số TT", "Mã phòng ban", "Tên phòng ban", "Mã TVKD", "Tên TVKD", "Mã Môi giới", "Tên Môi giới", "Mã CTV", "Tên CTV", "Mã TKGD", "Tên TKGD", "Tên đăng nhập", "Tên đầy đủ"};
		String SHEET = "info";
		
		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Sheet sheet = workbook.createSheet(SHEET);

			// Header
			Row headerRow = sheet.createRow(0);

			for (int col = 0; col < HEADERs.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(HEADERs[col]);
			}

			int rowIdx = 1;
			for (ExchangeSettingDTO userExchange : userExchangeList) {
				Row row = sheet.createRow(rowIdx);
				row.createCell(0).setCellValue(rowIdx);
				row.createCell(1).setCellValue(userExchange.getDeptCode());
				row.createCell(2).setCellValue(userExchange.getDeptName());
				row.createCell(3).setCellValue(userExchange.getMemberCode());
				row.createCell(4).setCellValue(userExchange.getMemberName());
				row.createCell(5).setCellValue(userExchange.getBrokerCode());
				row.createCell(6).setCellValue(userExchange.getBrokerName());
				row.createCell(7).setCellValue(userExchange.getCollaboratorCode());
				row.createCell(8).setCellValue(userExchange.getCollaboratorName());
				row.createCell(9).setCellValue(userExchange.getInvestorCode());
				row.createCell(10).setCellValue(userExchange.getInvestorName());
				row.createCell(11).setCellValue(userExchange.getUsername());
				row.createCell(12).setCellValue(userExchange.getFullName());
				rowIdx++;
			}

			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			AMLogger.logError(CLASSNAME, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
