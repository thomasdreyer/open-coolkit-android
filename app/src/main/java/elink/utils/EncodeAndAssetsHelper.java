package elink.utils;

import android.content.Context;

import org.apache.http.util.EncodingUtils;

import java.io.InputStream;

/**
 * Created by yaoyi on 12/10/15.
 */
public class EncodeAndAssetsHelper {

    public String getFromAssets(Context context ,String fileName){
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            //获取文件的字节数
            int lenght = in.available();
            //创建byte数组
            byte[]  buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public  String decodeRightShift(String finalString){
        String[] strings= finalString.toString().split(",");
        byte[] origbyte=new byte[strings.length];

        for (int i=0;i<strings.length;i++){
            origbyte[i]=(byte)(Integer.parseInt(strings[i])>>2);
        }

        return new String(origbyte);
    }


    public  StringBuffer getDecodeDictionNaryPosition(String dictionNart,String recovered){
        String[] old=recovered.split(",");
        StringBuffer sb=new StringBuffer();
        for (int i = 0; i < old.length; i++) {
            sb.append(dictionNart.charAt(Integer.parseInt(old[i])));
        }
        return sb;
    }
}
