package com.bs.book.util;

import com.bs.book.config.HttpsClientRequestFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Util {
    public static String generateRamdonCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int pos = (int) (Math.random() * Constant.CHARS.length());
            code.append(Constant.CHARS.charAt(pos));
        }
        return code.toString();
    }

    public static String Md5(String content) throws NoSuchAlgorithmException {
        //生成实现指定摘要算法的 MessageDigest 对象。
        MessageDigest md = MessageDigest.getInstance("MD5");
        //使用指定的字节数组更新摘要。
        md.update(content.getBytes());
        //通过执行诸如填充之类的最终操作完成哈希计算。
        byte[] b = md.digest();
        //生成具体的md5密码到buf数组
        int i;
        StringBuilder buf = new StringBuilder("");
        for (byte b1 : b) {
            i = b1;
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }

    public static String httpsGet(String uri, Map<String, String> args)throws ServiceException{
        RestTemplate restTemplate = new RestTemplate(new HttpsClientRequestFactory());

        // URL builder
        StringBuilder uriBuilder = new StringBuilder(uri + "?");
        for(String k : args.keySet()){
            uriBuilder.append(k).append("=").append(args.get(k)).append("&");
        }
        uri = uriBuilder.toString();
        uri = uri.substring(0, uri.length() - 1);

        // Get
        log.info("URL: " + uri);
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    String.class);
        }catch (HttpClientErrorException.NotFound e){
            throw new ServiceException(ErrorEnum.ERROR_BOOK_NOT_FOUND);
        } catch (RestClientException e) {
            e.printStackTrace();
            throw new ServiceException(ErrorEnum.ERROR_GET_NO_RESPONSE_FROM_HOST);
        }
        return response.getBody();
    }

    public static String limitStringLength(String str, int length){
        if(str == null){
            str = "";
        }
        if(length > 0 && str.length() > length){
            str = str.substring(0, length);
        }
        return str;
    }

    public static void main(String[] args){
        try {
            // ItemZheng1234561 c84083b7a5ba2462783aab16fa52b9c8
            System.out.println(Md5("1234561"));
            // 1234561 aaa42296669b958c3cee6c0475c8093e

            String url = "https://api.douban.com/v2/book/isbn/9787550286870";
            Map<String, String> a = new HashMap<>();
            a.put("apikey", "0b2bdeda43b5688921839c8ecb20399b");
            System.out.println(httpsGet(url, a));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
