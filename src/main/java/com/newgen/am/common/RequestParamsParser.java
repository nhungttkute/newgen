package com.newgen.am.common;

import org.bson.BsonRegularExpression;
import org.bson.Document;
import org.springframework.stereotype.Component;

import javax.print.attribute.IntegerSyntax;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Component
public class RequestParamsParser {
	private String className = "RequestParamsParser";

	class Filter {

		protected String fieldName;
		protected String operator;
		protected String value;

		public Filter() {
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "fieldName:" + fieldName + ", operator:" + operator + ", value:" + value;
		}
	}

	public class SearchCriteria {
		private int skip;
		private int limit;
		private Document query;
		private Document sort;

		public int getSkip() {
			return skip;
		}

		public void setSkip(int skip) {
			this.skip = skip;
		}

		public int getLimit() {
			return limit;
		}

		public void setLimit(int limit) {
			this.limit = limit;
		}

		public Document getQuery() {
			return query;
		}

		public void setQuery(Document query) {
			this.query = query;
		}

		public Document getSort() {
			return sort;
		}

		public void setSort(Document sort) {
			this.sort = sort;
		}
	}

	private ArrayList<Filter> convertFilter(String input) {
		ArrayList<Filter> arrList = new ArrayList<>();
		for (String s : input.split("&")) {
			String[] nvp = s.split("=");
			if (nvp.length > 1) {
				if (nvp[0].endsWith("]")) {
					String[] tmp = nvp[0].split("\\[");
					if (tmp.length > 1) {
						Filter filter = new Filter();
						filter.setFieldName(tmp[0]);
						String operator = tmp[1].substring(0, tmp[1].length() - 1);
						filter.setOperator("$" + operator);
						filter.setValue(nvp[1]);
						arrList.add(filter);
					}
				}
			}
		}
		return arrList;
	}

	private ArrayList<Filter> convertSort(String input) {
		ArrayList<Filter> arrList = new ArrayList<>();
		for (String s : input.split("&")) {
			String[] nvp = s.split("=");
			if (nvp.length > 1) {
				if (nvp[0].equals("sort_by")) {
					for (String item : nvp[1].split(",")) {
						Filter filter = new Filter();
						if (item.startsWith("asc(") && item.endsWith(")")) {
							filter.setFieldName(item.substring(4, item.length() - 1));
							filter.setValue("1");
						} else if (item.startsWith("desc(") && item.endsWith(")")) {
							filter.setFieldName(item.substring(5, item.length() - 1));
							filter.setValue("-1");
						}
						arrList.add(filter);
					}
					break;
				}
			}
		}
		return arrList;
	}

	public static int getLimit(String input) {
		for (String s : input.split("&")) {
			String[] nvp = s.split("=");
			if (nvp.length > 1) {
				if (nvp[0].equals("limit")) {
					return Integer.parseInt(nvp[1]);
				}
			}
		}
		return Constant.PAGINATION_DEFAULT_LIMIT;
	}

	private int getOffset(String input) {
		for (String s : input.split("&")) {
			String[] nvp = s.split("=");
			if (nvp.length > 1) {
				if (nvp[0].equals("offset")) {
					return Integer.parseInt(nvp[1]);
				}
			}
		}
		return Constant.PAGINATION_DEFAULT_OFFSET;
	}

	private List<String> convertStringToArray(String input) {
		String[] valArr = input.split(",");
		List<String> result = new ArrayList<String>();
		if (valArr.length > 0) {
			for (String val : valArr) {
				result.add(val);
			}
		}
		return result;
	}

	private Document buildQueryDocument(Document query, String reqParams, long refId) {
		String methodName = "buildQueryDocument";
		ArrayList<Document> queryList = new ArrayList<Document>();
		
		ArrayList<Filter> listFilter = convertFilter(reqParams);
		Iterator<RequestParamsParser.Filter> itFilter = listFilter.iterator();
		if (itFilter.hasNext()) {
			while (itFilter.hasNext()) {
				RequestParamsParser.Filter filter = itFilter.next();
				AMLogger.logMessage(className, methodName, refId, "[FILTER]: " + filter.toString());
				if (Constant.OPT_CONTAINS.equals(filter.getOperator())) {
					String queryValue = String.format(".*%s.*", filter.getValue());
					Document queryDoc = new Document();
					queryDoc.append(filter.getFieldName(), new BsonRegularExpression(queryValue, "i"));
					queryList.add(queryDoc);
				} else if (Constant.OPT_NOT_CONTAINS.equals(filter.getOperator())) {
					String queryValue = String.format(".*%s.*", filter.getValue());
					Document queryDoc = new Document();
					queryDoc.append(filter.getFieldName(), new Document().append("$not", new BsonRegularExpression(queryValue, "i")));
					queryList.add(queryDoc);
				} else if (Constant.OPT_START.equals(filter.getOperator())) {
					String queryValue = String.format("^%s.*", filter.getValue());
					Document queryDoc = new Document();
					queryDoc.append(filter.getFieldName(), new BsonRegularExpression(queryValue, "i"));
					queryList.add(queryDoc);
				} else if (Constant.OPT_END.equals(filter.getOperator())) {
					String queryValue = String.format(".*%s$", filter.getValue());
					Document queryDoc = new Document();
					queryDoc.append(filter.getFieldName(), new BsonRegularExpression(queryValue, "i"));
					queryList.add(queryDoc);
				} else if (Constant.OPT_EQUALS.equals(filter.getOperator())) {
					Document queryDoc = new Document();
					if (Utility.getNumberQueryFieldNames().contains(filter.getFieldName())) {
						queryDoc.append(filter.getFieldName(), Long.valueOf(filter.getValue()));
					} else {
						queryDoc.append(filter.getFieldName(), filter.getValue());
					}
					queryList.add(queryDoc);
				} else if (Constant.OPT_IN.equals(filter.getOperator())
						|| Constant.OPT_NOT_IN.equals(filter.getOperator())) {
					Document queryDoc = new Document();
					queryDoc.append(filter.getFieldName(),
							new Document().append(filter.getOperator(), convertStringToArray(filter.getValue())));
					queryList.add(queryDoc);
				} else {
					Document queryDoc = new Document();
					queryDoc.append(filter.getFieldName(),
							new Document().append(filter.getOperator(), Long.parseLong(filter.getValue())));
					queryList.add(queryDoc);
				}
			}
			query.append("$and", queryList);
		}
		
		return query;
	}

	private Document buildSortDocument(String reqParams, long refId) {
		String methodName = "";
		ArrayList<RequestParamsParser.Filter> listSort = convertSort(reqParams);
		Document sort = null;
		Iterator<Filter> itSort = listSort.iterator();
		if (itSort.hasNext()) {
			sort = new Document();

			while (itSort.hasNext()) {
				Filter filter = itSort.next();
				AMLogger.logMessage(className, methodName, refId, "[SORT]: " + filter.toString());
				sort.append(filter.getFieldName(), Double.parseDouble(filter.getValue()));
			}
		}

		return sort;
	}

	public SearchCriteria getSearchCriteria(String queryString, String nestedObjName, long refId) throws Exception {
		int skip = Constant.PAGINATION_DEFAULT_OFFSET;
		int limit = Constant.PAGINATION_DEFAULT_LIMIT;
		Document defaultSort = new Document();
		defaultSort.append(nestedObjName + Constant.SORT_DETAUL_FIELD, -1d);
		Document query = new Document();
		if (Utility.isNotNull(queryString)) {
			String reqParams = Utility.decode(queryString);
			skip = getOffset(reqParams);
			limit = getLimit(reqParams);

			// build sort
			Document sort = buildSortDocument(reqParams, refId);
			if (sort != null) {
				defaultSort = sort;
			}

			// build query
			buildQueryDocument(query, reqParams, refId);
		}
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setLimit(limit);
		searchCriteria.setSkip(skip);
		searchCriteria.setQuery(query);
		searchCriteria.setSort(defaultSort);
		return searchCriteria;
	}
}
