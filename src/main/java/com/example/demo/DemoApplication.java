package com.example.demo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static java.text.MessageFormat.format;

@SpringBootApplication
@RestController
@EnableSwagger2
@Api(value = "some demo api")
public class DemoApplication {

    @Value("${image.path}")
    private String path;


    @PostMapping("/upload")
    @ApiOperation(value="上传图片", notes="根据url的id来获取用户详细信息")
    public String uploadImage(@RequestParam String appId,
                              @RequestParam String imageType,
                              HttpServletRequest request, HttpServletResponse response) throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartRequest.getFile("upload");
        InputStream is = multipartFile.getInputStream();
        BufferedImage buffImg = ImageIO.read(is);
        String urlPath = getFilePathName(appId, UUID.randomUUID().toString().replace("-", ""), "png");
        File outPutFile = new File(urlPath);
        createDirIfNotExist(outPutFile.getParentFile());
        writeFile(buffImg, imageType, outPutFile);
        return outPutFile.getPath();
    }


    private void createDirIfNotExist(final File outPutFile) {
        if (!outPutFile.exists() && !outPutFile.mkdirs()) {
            throw new RuntimeException("image save fail");
        }
    }

    private void writeFile(BufferedImage buffImg, String imageType, File outPutFile) {
        try (FileOutputStream output = new FileOutputStream(outPutFile)) {
            ImageIO.write(buffImg, imageType, output);
        } catch (Exception ioe) {
            throw new RuntimeException("image save fail");
        }
    }

    private String getFilePathName(final String appId, final String uid, final String imageType) {
        return format(path, appId, uid, imageType);
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
