package com.equipment.util;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 描述:
 * PDF导出工具类
 *
 * @author partner
 * @create 2018-07-20 12:08
 */
public class PDFUtil {
    /**
     *  表格默认样式:居中
     */
    private static final TextAlignment TEXT_ALIGN = TextAlignment.CENTER;
    /**
     * 表格每列默认百分比为：80
     */
    private static final float WIDTH_PERCENT=80;

    private static Logger logger = LoggerFactory.getLogger(PDFUtil.class);

    private static PdfFont getPdfFont() {
        InputStream inputStream = null;
        //设置字体为思源字体
        PdfFont font = null;
        try {
            inputStream = PDFUtil.class.getResourceAsStream("/simfang.ttf");
            font = PdfFontFactory.createFont(IOUtils.toByteArray(inputStream), PdfEncodings.IDENTITY_H, false);
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
        }finally {
            try {
                if(inputStream!=null) {
                    inputStream.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                logger.debug(e2.getMessage());
            }
        }
        return font;
    }

    public static byte[] exportPDF(String title, String [] headers, List<String []> listData, float [] cellsWidths) {
        return exportPDF(title, headers, listData, cellsWidths, WIDTH_PERCENT, TEXT_ALIGN);
    }

    public static byte[] exportPDF(String title,String [] headers,List<String []> listData,float [] cellsWidths,float widthPercent,TextAlignment textAlignment) {
        try {
            if (headers==null || headers.length <= 0) {
                logger.debug("headers不能为空!");
                throw new RuntimeException("headers不能为空！");
            }
            if (headers == null || cellsWidths == null || headers.length != cellsWidths.length) {
                logger.debug("headers和cellsWidths长度不一致!");
                throw new RuntimeException("headers和cellsWidths长度不一致!");
            }
            if (widthPercent == 0) {
                widthPercent=WIDTH_PERCENT;
            }
            if (textAlignment == null) {
                textAlignment = TEXT_ALIGN;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A2);
            DeviceRgb Gray = new DeviceRgb(242, 242, 242);
            Cell cell = new Cell();
            Table table1 = new Table(cellsWidths).setWidthPercent(widthPercent).setTextAlignment(textAlignment);
            table1.setFont(getPdfFont()).setMinHeight(18).setFixedLayout().setHorizontalAlignment(HorizontalAlignment.CENTER);
            for (int i = 0; i < headers.length; i++) {
                cell = new Cell().setBackgroundColor(Gray).add(new Paragraph(headers[i]).setBold());
                table1.addCell(cell);
            }
            for (int i = 0; i < listData.size(); i++) {
                String[] strings = listData.get(i);
                for (int j = 0; j < headers.length; j++) {
                    cell = new Cell().add(new Paragraph(!(strings[j]!=null) || "null".equals(strings[j])?"":strings[j]));
                    table1.addCell(cell);
                }
            }


            document.add(new Paragraph(title).setFont(getPdfFont()).setTextAlignment(TextAlignment.CENTER).setFontSize(23).setBold());
            document.add(table1);
            document.close();

            byte[] bytes = out.toByteArray();
            out.close();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e.getMessage());
            return null;
        }
    }

}
