package com.equipment.util;

import com.equipment.model.equipmanager.EquipManagePage;
import com.equipment.util.excel.Excel;
import com.equipment.util.excel.ExcelException;
import com.equipment.util.excel.ExcelRow;
import com.equipment.util.excelRd.ExcelRd;
import com.equipment.util.excelRd.ExcelRdException;
import com.equipment.util.excelRd.ExcelRdRow;
import com.equipment.util.excelRd.ExcelRdTypeEnum;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * Excel导入导出工具类
 *
 * @author partner
 * @create 2018-07-19 15:39
 */
public class ExcelCommon {
    /**
     * @param map path:保存的路径,fileName：文件名，title：标题,createBy：创建者,header[] 行头,listData 要导出的数据（可选填）
     *            必须严格按照指定的列名传入，作用于导出模板和导出数据
     */
    public static String createExcelModul(Map map) throws ExcelException {
        String path = (String) map.get("path");
        String fileName = (String) map.get("fileName");
        //判断文件夹是否存在
        if (!new File(path).exists() || !new File(path).isDirectory()) {
            new File(path).mkdir();
        }
        String title = (String) map.get("title");
        String createBy = (String) map.get("createBy");
        String[] header = (String[]) map.get("header");
        List<Object[]> listData = (List<Object[]>) map.get("listData");

        return createExcelModul(path, fileName, title, createBy, header, listData);
    }

    /**
     * @param path     保存的路径+文件名
     * @param fileName 保存的路径+文件名
     * @param title    标题
     * @param createBy 创建者
     * @param header   行头
     * @param listData 要导出的数据
     * @return 保存地址
     * @throws ExcelException 异常
     */
    public static String createExcelModul(String path, String fileName, String title, String createBy, String[] header, List<Object[]> listData) throws ExcelException {
        //判断文件夹是否存在
        if (!new File(path).exists() || !new File(path).isDirectory()) {
            new File(path).mkdirs();
        }
        String savePath = path + File.separator + fileName;

        Excel excel = new Excel();
        if (VerifyData.strIsNotNull(title)) {
            excel.setTitle(title);
        }
        if (!VerifyData.strIsNotNull(savePath)) {
            throw new ExcelException("保存路径不能为空");
        } else {
            excel.setSavePath(savePath);
        }
        if (VerifyData.strIsNotNull(createBy)) {
            excel.setCreateBy(createBy);
        }
        if (header!=null && header.length>0) {
            excel.setHeader(header);
        }
        for (Object[] dataRow : listData) {
            ExcelRow row = excel.createRow();
            for (Object dataCell : dataRow) {
                row.addCell(!VerifyData.strIsNotNull(dataCell+"") || "null".equals(dataCell+"")?"":dataCell);
            }
        }
        try {
            return excel.createXlsx();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 简单的专门返回数据的excel信息
     *
     * @param pathFile    目录对象,支持url路径,支持绝对和抽象路径
     * @param startRowNum 指定起始行，从1开始
     * @param startColNum 指定起始列，从1开始
     * @throws IOException
     * @throws ExcelRdException
     */
    public static List<Object[]> excelRd(File pathFile, Integer startRowNum, Integer startColNum, ExcelRdTypeEnum[] types) throws IOException, ExcelRdException {
        List<Object[]> data = new ArrayList<>();

        ExcelRd excelRd = new ExcelRd(pathFile);
        excelRd.setStartRow(startRowNum - 1);
        excelRd.setStartCol(startColNum - 1);
        // 指定每列的类型
        excelRd.setTypes(types);

        List<ExcelRdRow> rows = excelRd.analysisXlsx();

        Iterator<ExcelRdRow> iterator = rows.iterator();
        while (iterator.hasNext()) {
            ExcelRdRow next = iterator.next();
            List<Object> row = next.getRow();
            data.add(row.toArray());
        }
        return data;
    }

    public static List<List<Object[]>> excelRdList(String pathFile,Integer startRowNum,Integer startColNum,ExcelRdTypeEnum [] typeEnums){
        ExcelRd excelRd = new ExcelRd(pathFile);
        excelRd.setStartRow(startRowNum);
        excelRd.setStartCol(startColNum);
        excelRd.setTypes(typeEnums);
        List<List<Object[]>> listObjects = new ArrayList<>();
        List<List<ExcelRdRow>> lists=null;
        try {
            lists=excelRd.analysisXlsxMultiTable();
            for (int i = 0; i < lists.size(); i++) {
                List<Object[]> objectArray = new ArrayList<>();
                Iterator<ExcelRdRow> iterator = lists.get(i).iterator();
                while (iterator.hasNext()) {
                    ExcelRdRow excelRdRow =iterator.next();
                    List<Object> objects = excelRdRow.getRow();
                    objectArray.add(objects.toArray());
                }
                listObjects.add(objectArray);
            }
        } catch (ExcelRdException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listObjects;
    }


    private static void mergedRegionNowRow(XSSFSheet sheet, XSSFRow row, int firstCol, int lastCol) {
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), firstCol - 1, lastCol - 1));
    }


}
