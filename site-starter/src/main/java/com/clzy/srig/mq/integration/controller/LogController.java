package com.clzy.srig.mq.integration.controller;

import com.clzy.geo.core.common.dto.common.JsonResponse;
import io.swagger.annotations.Api;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @Author: tangs
 * @Date: 2021/8/26 9:46
 */
@RestController()
@RequestMapping("/api/logs")
@Api(tags = "日志相关的接口")
public class LogController {

    @GetMapping
    public void getLogs(HttpServletRequest request, HttpServletResponse response) {

//        StringBuilder builder = new StringBuilder();
        File file = new File("../logs/mq");
        File[] files = file.listFiles();
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setCharacterEncoding("utf-8");
        if (files == null || files.length < 1) {
            return;
        }
        File outFile = files[files.length - 1].listFiles()[0];
        try {
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(outFile.getName(), "utf-8"));
            OutputStream out = response.getOutputStream();
            InputStream is = new FileInputStream(outFile);

            // 获取输出流 CommonsMultipartFile 中可以直接得到文件的流
//            OutputStream os = response.getOutputStream();
            IOUtils.copy(is, out);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(out);

//            XWPFDocument document = new XWPFDocument(OPCPackage.open(is));
//            document.write(out);
            out.flush();
//            out.close();
//            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try (RandomAccessFile randomAccessFile = new RandomAccessFile(files[files.length - 1].listFiles()[0], "r")) {
//            // 最后一次指针的位置,默认从头开始
//            // 每一行读取到的数据
//            String line = null;
//            // 持续监听文件
//            while (true) {
//                // 从最后一次读取到的数据开始读取
//                randomAccessFile.seek(lastPointer);
//                // 读取一行,遇到换行符停止,不包含换行符
//                while ((line = randomAccessFile.readLine()) != null) {
//                    builder.append(line + "\n");  // 打印日志消息，或者响应给客户端
//                }
//                // 读取完毕后,记录最后一次读取的指针位置
//                lastPointer = randomAccessFile.getFilePointer();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return JsonResponse.success(builder.toString());
    }

    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder();
        File file = new File("../logs/mq");
        File[] files = file.listFiles();
        long lastPointer = 0;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(files[files.length - 1].listFiles()[0], "r")) {
            // 最后一次指针的位置,默认从头开始
            // 每一行读取到的数据
            String line = null;
            // 持续监听文件
            while (true) {
                // 从最后一次读取到的数据开始读取
                randomAccessFile.seek(lastPointer);
                // 读取一行,遇到换行符停止,不包含换行符
                while ((line = randomAccessFile.readLine()) != null) {
                    builder.append(line + "\n");  // 打印日志消息，或者响应给客户端
                }
                // 读取完毕后,记录最后一次读取的指针位置
                lastPointer = randomAccessFile.getFilePointer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(builder.toString());
    }
}
