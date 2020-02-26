package live.yremp.qrcode.controoller;

import com.alibaba.fastjson.JSONObject;
import live.yremp.qrcode.utils.CreateQRCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

@Controller
public class ApiController {
    @Autowired
    private CreateQRCode createQRCode;
    @Value("${server.path}")
    private String serverPath;
    @Value("${view.path}")
    private String webPath;

    @RequestMapping("/api/generateCode")
    @ResponseBody
    public Object generateCode(@RequestBody JSONObject object, HttpServletRequest request) throws IOException {
        String string = object.get("string").toString();
        JSONObject result = (JSONObject) createQRCode.CreataQRCode(string, request);
        if (result.getInteger("status") == 200) return result;
        else {
            JSONObject error = new JSONObject();
            error.put("status", 100);
            error.put("message", "未知错误");
            return error;
        }

    }

    @RequestMapping("/api/download/{filename}")
    public Object downloadQRCode(@PathVariable("filename") String fileName, HttpServletResponse response, HttpServletRequest request) {
        String path = request.getServletPath();
        System.out.println(path);
        String URL = serverPath+webPath+fileName;
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        ServletOutputStream outputStream = null;
        try {
            URL url = new URL(URL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            int responseCode = httpURLConnection.getResponseCode();
            response.setContentType("application/x-download");
            response.addHeader("Content-Disposition","attachment;filename=" + fileName);
            if (responseCode == 200) {
                inputStream =httpURLConnection.getInputStream();
                outputStream = response.getOutputStream();
                // 在http响应中输出流
                byte[] cache = new byte[1024];
                int nRead = 0;
                while ((nRead = inputStream.read(cache) ) != -1) {
                    outputStream.write(cache, 0, nRead);
                }
                outputStream.flush();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
      return null;
    }
}
