package com.newgen.am.common;

import org.bson.BsonRegularExpression;
import org.bson.Document;

import javax.print.attribute.IntegerSyntax;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class RequestParamsParser {
    static class Filter {

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
            return "fieldName:" + fieldName + "|operator:" + operator + "|value:" + value;
        }
    }

    public static ArrayList<Filter> convertFilter(String input) {
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
                        if (operator.equals("in") || operator.equals("nin")) {
                            String[] valArr = nvp[1].split(",");
                            if (valArr.length > 1) {
                                String str = "";
                                for (String val : valArr) {
                                    str += "\"" + val + "\",";
                                }
                                filter.setValue(str);
                                filter.setValue(filter.getValue().substring(0, filter.getValue().length() - 1));
                            }
                        } else {
                            filter.setValue(nvp[1]);
                        }
                        arrList.add(filter);
                    }
                }
            }
        }
        return arrList;
    }

    public static ArrayList<Filter> convertSort(String input) {
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
        return 20;
    }

    public static int getOffset(String input) {
        for (String s : input.split("&")) {
            String[] nvp = s.split("=");
            if (nvp.length > 1) {
                if (nvp[0].equals("offset")) {
                    return Integer.parseInt(nvp[1]);
                }
            }
        }
        return 0;
    }

    public static Document buildQueryDocument(String reqParams) {
        ArrayList<Filter> listFilter = RequestParamsParser.convertFilter(reqParams);
        Document query = null;
        Iterator<RequestParamsParser.Filter> itFilter = listFilter.iterator();
        if (itFilter.hasNext()) {
            query = new Document();
            while (itFilter.hasNext()) {
                RequestParamsParser.Filter filter = itFilter.next();
                System.out.println("[FILTER]" + filter.toString());
                if (Constant.OPT_CONTAINS.equals(filter.getOperator())) {
                    String queryValue = String.format(".*%s.*", filter.getValue());
                    query.append(filter.getFieldName(), new BsonRegularExpression(queryValue, "i"));
                } else if (Constant.OPT_EQUALS.equals(filter.getOperator())) {
                    query.append(filter.getFieldName(), filter.getValue());
                } else if (Constant.OPT_IN.equals(filter.getOperator()) || Constant.OPT_NOT_IN.equals(filter.getOperator())) {
                    query.append(filter.getFieldName(), new Document()
                            .append(filter.getOperator(), Arrays.asList(filter.getValue())));
                } else {
                    query.append(filter.getFieldName(), new Document()
                            .append(filter.getOperator(), Long.parseLong(filter.getValue())));
                }
            }
        }

        return query;
    }

    public static Document buildSortDocument(String reqParams) {
        ArrayList<RequestParamsParser.Filter> listSort = RequestParamsParser.convertSort(reqParams);
        Document query = null;
        Iterator<Filter> itSort = listSort.iterator();
        if (itSort.hasNext()) {
            query = new Document();

            while (itSort.hasNext()) {
                Filter filter = itSort.next();
                System.out.println("[SORT]" + filter.toString());
                query.append(filter.getFieldName(), Integer.parseInt(filter.getValue()));
            }
        }

        return query;
    }
}
