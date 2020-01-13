package com.equipment.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Author: JavaTansanlin
 * @Description: 二维码下载工具
 * @Date: Created in 18:46 2018/9/6
 * @Modified By:
 */
public class QrcodeDown {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int  BUFFER_SIZE = 2 * 1024;

    /**
     * 生成二维码图片
     * */
    public static void createListCodeFile(String wxCode ,String aliCode ,String oneCode ,String code ,String fileFole){
        try {
            if (wxCode!=null && aliCode!=null && aliCode!=null && code!=null && fileFole!=null){
                String path = fileFole + "/" + code + "/";
                File folder = new File(path);
                //判断文件夹是否存在
                if (!folder.exists() && !folder.isDirectory()) {
                    folder.mkdirs();//创建文件夹
                }

                int width = 300; // 二维码图片宽度
                int height = 300; // 二维码图片高度
                String format = "jpg";// 二维码的图片格式

                Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
                hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // 内容所使用字符集编码

                // 生成微信的二维码
                BitMatrix wx = new MultiFormatWriter().encode(wxCode,
                        BarcodeFormat.QR_CODE, width, height, hints);
                File wxFile = new File(path + "WX-" + code +".jpg");
                writeToFile(wx, format, wxFile);

                if (oneCode!=null && !"".equals(oneCode)){
                    //生成二码合一
                    BitMatrix one = new MultiFormatWriter().encode(oneCode,
                            BarcodeFormat.QR_CODE, width, height, hints);
                    File oneFile = new File(path + "one-" + code +".jpg");
                    writeToFile(one, format, oneFile);
                }

                // 生成支付宝的二维码
                String  pa = path + "ali-" + code +".jpg";
                downloadPicture(aliCode ,pa);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /** 生成指定格式的二维码图片 */
    public static void createPic(String code ,String path ,String fileName){
        try {
            if (code!=null && path!=null && !"".equals(code) && !"".equals(path) && fileName!=null && !"".equals(fileName)){
                File folder = new File(path);
                //判断文件夹是否存在
                if (!folder.exists() && !folder.isDirectory()) {
                    folder.mkdirs();//创建文件夹
                }
                //判断文件是否存在
                File file = new File(path+fileName+".jpg");
                if(!file.exists()){
                    int width = 300; // 二维码图片宽度
                    int height = 300; // 二维码图片高度
                    String format = "jpg";// 二维码的图片格式

                    Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
                    hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // 内容所使用字符集编码

                    BitMatrix one = new MultiFormatWriter().encode(code,
                            BarcodeFormat.QR_CODE, width, height, hints);
                    File oneFile = new File(path + fileName +".jpg");
                    writeToFile(one, format, oneFile);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    public static void writeToFileWithId(String id,BitMatrix matrix, String format, File file)
            throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        //得到画笔对象
        Graphics g = image.getGraphics();
        //设置颜色。
        g.setColor(Color.BLACK);
        //最后一个参数用来设置字体的大小
        Font f = new Font("宋体",Font.BOLD,20);
        Color mycolor = Color.BLACK;//new Color(0, 0, 255);
        g.setColor(mycolor);
        g.setFont(f);
        //10,20 表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。
        g.drawString(id,110,285);
        g.dispose();
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format "
                    + format + " to " + file);
        }
    }


    public static void writeToFile(BitMatrix matrix, String format, File file)
            throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format "
                    + format + " to " + file);
        }
    }

    public static void writeToStream(BitMatrix matrix, String format,
                                     OutputStream stream) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }

    //链接url下载图片
    public static void downloadPicture(String urlList,String path) {
        URL url = null;
        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            deleteAll(new File("D:/qrcode/777777"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 压缩成ZIP
     * @param srcDir 压缩文件夹路径
     * @param fileDirAndName    文件输出地址
     * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构;
     *                          false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String srcDir, String fileDirAndName, boolean KeepDirStructure){
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null ;
        try {
            FileOutputStream fos1 = new FileOutputStream(new File(fileDirAndName));
            zos = new ZipOutputStream(fos1);
            File sourceFile = new File(srcDir);
            compress(sourceFile,zos,sourceFile.getName(),KeepDirStructure);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 递归压缩方法
     * @param sourceFile 源文件
     * @param zos        zip输出流
     * @param name       压缩后的名称
     * @param KeepDirStructure  是否保留原来的目录结构,true:保留目录结构;
     * false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception{
        byte[] buf = new byte[BUFFER_SIZE];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if(KeepDirStructure){
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            }else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(),KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(),KeepDirStructure);
                    }
                }
            }
        }
    }

    /** 删除文件夹或者文件 */
    public static void deleteAll(File file){
        if(file.isFile() || file.list().length ==0){
            file.delete();
        }else{
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteAll(files[i]);
                files[i].delete();
            }
            if(file.exists())         //如果文件本身就是目录 ，就要删除目录
                file.delete();
        }

    }

}
