/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.utils;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author wesam
 */
public class ZipUtil {

    public static void zip(String source, String dest) throws IOException {

        // ZIP
        // new way of compressing to a gzip file instead of zip
        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(dest));
        FileInputStream in = new FileInputStream(source);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.finish();
        out.close();

        // Old way of compressing to a file of type zip
//        File destFile = new File(dest);
//        File sourceFile = new File(source);
//        ZipFile zipFile = new ZipFile(destFile);
//        ZipParameters parameters = new ZipParameters();
//        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
//
//        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
//        //Adding file
//        zipFile.addFile(sourceFile, parameters);
//        byte[] buffer = new byte[1024];
//
//        try {
//
//            GZIPOutputStream gzos =
//                    new GZIPOutputStream(new FileOutputStream(dest));
//
//            FileInputStream in =
//                    new FileInputStream(source);
//
//            int len;
//            while ((len = in.read(buffer)) > 0) {
//                gzos.write(buffer, 0, len);
//            }
//
//            in.close();
//
//            gzos.finish();
//            gzos.close();
//
//            System.out.println("Done");
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    public static void unzip(String source, String dest) throws IOException {

//        CommonLogger.businessLogger.info("Starting UNZIP File["+source+"] Into File["+dest+"]");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting unzip Files")
                .put(GeneralConstants.StructuredLogKeys.SOURCE_FILE, source)
                .put(GeneralConstants.StructuredLogKeys.DESTINATION_FILE, dest).build());

        long startTime = System.currentTimeMillis();
        //UNZIP
        GZIPInputStream in = new GZIPInputStream(new FileInputStream(source));
        OutputStream out = new FileOutputStream(dest);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();

//        CommonLogger.businessLogger.info("Finished UNZIP File[" + source + "] Into File[" + dest + "] in " + (System.currentTimeMillis() - startTime) + "]msec");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Finished unzip Files")
                .put(GeneralConstants.StructuredLogKeys.SOURCE_FILE, source)
                .put(GeneralConstants.StructuredLogKeys.DESTINATION_FILE, dest)
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

//            ZipFile zipFile = new ZipFile(source);
//
//            zipFile.extractAll(dest);
//        byte[] buffer = new byte[1024];
//
//        try {
//
//            GZIPInputStream gzis =
//                    new GZIPInputStream(new FileInputStream(source));
//
//            FileOutputStream out =
//                    new FileOutputStream(dest);
//
//            int len;
//            while ((len = gzis.read(buffer)) > 0) {
//                out.write(buffer, 0, len);
//            }
//
//            gzis.close();
//            out.close();
//
//            System.out.println("Done");
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    public static void main(String[] args) throws IOException {
        //  try {
        //        Date date = getBillCycleDate(15);
        //        System.out.println(date);

//            String sourceFile = "D:\\Dalia.zip";
//            String destFilele = "D:\\Dalia.csv";
////
//            unzip(sourceFile, destFilele);
        ///    zip("D:\\Dalia.txt", "D:\\Dalia1.zip");
//        } catch (ZipException ex) {
//            Logger.getLogger(ZipUtil.class.getName()).log(Level.SEVERE, null, ex);
//        }
        //      zip("D:\\DWH\\Zipping\\File.txt","D:\\DWH\\Zipping\\File.gzip");
    }
}
